/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.Clearing;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.entity.PlatformCategory;
import com.pcitech.iLife.modules.mod.entity.ProfitShareItem;
import com.pcitech.iLife.modules.mod.entity.ProfitShareScheme;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.mod.service.ClearingService;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.modules.mod.service.ProfitShareItemService;
import com.pcitech.iLife.modules.mod.service.ProfitShareSchemeService;
import com.pcitech.iLife.util.NumberUtil;
import com.pcitech.iLife.util.Util;

/**
 * 清算Controller
 * @author qchzhu
 * @version 2019-10-14
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/clearing")
public class ClearingController extends BaseController {

	@Autowired
	private ClearingService clearingService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private BrokerService brokerService;
	@Autowired
	private ProfitShareSchemeService profitShareSchemeService;
	@Autowired
	private ProfitShareItemService profitShareItemService;
	
	@ModelAttribute
	public Clearing get(@RequestParam(required=false) String id) {
		Clearing entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = clearingService.get(id);
		}
		if (entity == null){
			entity = new Clearing();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:clearing:view")
	@RequestMapping(value = {"list", ""})
	public String list(Clearing clearing, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Clearing> page = clearingService.findPage(new Page<Clearing>(request, response), clearing); 
		model.addAttribute("page", page);
		return "modules/mod/clearingList";
	}

	@RequiresPermissions("mod:clearing:view")
	@RequestMapping(value = "form")
	public String form(Clearing clearing, Model model) {
		model.addAttribute("clearing", clearing);
		return "modules/mod/clearingForm";
	}

	@RequiresPermissions("mod:clearing:edit")
	@RequestMapping(value = "save")
	public String save(Clearing clearing, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, clearing)){
			return form(clearing, model);
		}
		clearingService.save(clearing);
		addMessage(redirectAttributes, "保存清算成功");
		return "redirect:"+Global.getAdminPath()+"/mod/clearing/?repage";
	}
	
	@RequiresPermissions("mod:clearing:edit")
	@RequestMapping(value = "delete")
	public String delete(Clearing clearing, RedirectAttributes redirectAttributes) {
		clearingService.delete(clearing);
		addMessage(redirectAttributes, "删除清算成功");
		return "redirect:"+Global.getAdminPath()+"/mod/clearing/?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/money/byOrder/{brokerId}", method = RequestMethod.GET)
	public List<Clearing> getMoneyList(@PathVariable String brokerId,@RequestParam int offset,@RequestParam int size,HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("brokerId", brokerId);
		map.put("offset", offset);
		map.put("size", size);
		return clearingService.findListByBroker(map);
	}

	/**
	 * 完成订单清分：根据订单ID完成清分，并且发送通知。清分逻辑：
	 * 1，根据orderId获取指定订单
	 * 2，根据订单platform获取清分规则
	 * 3，逐条计算并保存清分记录
	 * 4，发送通知
	 * 5，更新订单清分状态为ready
	 * 
	 * @param orderId 订单ID
	 * @param json 订单数据。预留。当前未使用该参数
	 * @return
	 */
	@Transactional(readOnly = false)
	@ResponseBody
	@RequestMapping(value = "rest/clear/{orderId}", method = RequestMethod.POST)
	public JSONObject clearing(@PathVariable String orderId, @RequestBody JSONObject json) {
		JSONObject result = new JSONObject();
		result.put("success",false);
		
		//获取原始订单
		Order order = orderService.get(orderId);
		if(order == null || !"pending".equalsIgnoreCase(order.getStatus())) {
			result.put("msg", "order doesnot exist or order status is not pending.");
			return result;
		}
		
		//根据订单获取推广达人信息，包括推广达人、上级达人、上上级达人
		Broker system = brokerService.get("system");//注意：作为兜底达人。必须存在！！
		Broker broker = brokerService.get(order.getBroker());
		if(broker==null)broker = system;//如果无推广达人，则归因于平台
		Broker parent = brokerService.get(broker.getParent());
		if(parent==null)parent=broker;//如果未设置上级达人则归因于推广达人
		Broker grandpa = brokerService.get(parent.getParent());
		if(grandpa==null)grandpa=parent;//如果无上上级达人，则归因于上级达人
		
		//查询得到分润规则，并根据分润明细进行清分
		ProfitShareScheme profitShareScheme = new ProfitShareScheme();
		profitShareScheme.setCategory("default");//注意：当前统一根据默认分润规则，不考虑按类目分润
		profitShareScheme.setPlatform(order.getPlatform());
		profitShareScheme.setType("order");//默认只查找订单分润的规则：注意，这个值是在字典里定义的，不能弄错了
		profitShareScheme = profitShareSchemeService.getByQuery(profitShareScheme);
		
		if(profitShareScheme==null) {//如果没有分润规则，那也没法算钱啊。快找运营去设置
			result.put("msg", "no profit share scheme");
			return result;
		}else {//那就找分润明细，并计算达人分润和上级分润
			ProfitShareItem profitShareItem = new ProfitShareItem();
			profitShareItem.setScheme(profitShareScheme);
			List<ProfitShareItem> shareItems = profitShareItemService.findListByQuery(profitShareItem);//查询对应shareScheme下的所有有效分润明细
			for(ProfitShareItem shareItem:shareItems) {//逐条处理，每一条对应生成一条清分记录、
				String status = "pending";//清分记录的状态：默认为pending，需要根据分润类型及团队条件设置
				String person = system.getId();//判定对应的分润受益人：是一个具体人员，默认为平台用户
				//判断分润类型是团队还是个人
				if("team".equalsIgnoreCase(shareItem.getBeneficiaryType())) {//团队分润
					status = "pending";//需要二次清分
        	   		person = shareItem.getBeneficiary();//对于团队绩效，直接使用团队标记
				}else {//个人分润：包括达人、上级、上上级
					status = "cleared";//默认个人收益直接为已清算，表示可申请提现
					if("broker".equalsIgnoreCase(shareItem.getBeneficiary())) {
						person = broker.getId();
					}else if("parent".equalsIgnoreCase(shareItem.getBeneficiary())) {
						person = parent.getId();
						//如果未达到团队约束，则设置为锁定。仅考虑直接团队人数
						if(brokerService.countChilds(person)<15)
							status = "locked";
					}else if("grandpa".equalsIgnoreCase(shareItem.getBeneficiary())) {
						person = grandpa.getId();
						//如果未达到团队约束，则设置为锁定。仅考虑直接团队人数
						if(brokerService.countChilds(person)<15)
							status = "locked";
					}else {
						//不知道出什么错误。不应该出现此类受益人。直接忽略
					}
				}
				//建立清分记录
				DecimalFormat df = new DecimalFormat("#.00");//保留两位小数
				Clearing clearing = new Clearing();
				clearing.setIsNewRecord(true);
				clearing.setId(Util.md5(order.getId()+profitShareScheme.getId()+shareItem.getId()+shareItem.getBeneficiaryType()+shareItem.getBeneficiary()+person));//指定ID：订单ID-分润规则ID-分润明细ID-受益方类型-受益方-对应person
				clearing.setOrder(order);
				clearing.setPlatform(order.getPlatform());
				clearing.setOrderTime(order.getOrderTime());
				clearing.setItem(order.getItem());
				clearing.setAmountOrder(df.format(order.getAmount()));
				clearing.setAmountCommission(df.format(order.getCommissionSettlement()));
				clearing.setAmountProfit(df.format(order.getCommissionSettlement()*shareItem.getShare()*0.01));
				clearing.setScheme(profitShareScheme);
				clearing.setSchemeItem(shareItem);
				clearing.setShare(""+shareItem.getShare());
				clearing.setBeneficiary(person);
				clearing.setBeneficiaryType(shareItem.getBeneficiaryType());
				clearing.setStatusClear(status);
				clearing.setCreateDate(new Date());
				clearing.setUpdateDate(new Date());
				clearingService.save(clearing);
				
				//发送通知：通过BrokerClearingNotifyTask完成。这里不主动发送
			}
		}
		
		//更新订单的清分状态为cleared
		order.setStatus("cleared");
		orderService.save(order);

		//搞定
		result.put("success",true);
		result.put("msg", "order cleared.");
		return result;
	}
	
}