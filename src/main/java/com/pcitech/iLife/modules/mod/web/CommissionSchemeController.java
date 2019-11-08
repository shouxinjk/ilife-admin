/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.CommissionScheme;
import com.pcitech.iLife.modules.mod.entity.CpsLinkScheme;
import com.pcitech.iLife.modules.mod.entity.CreditScheme;
import com.pcitech.iLife.modules.mod.entity.ProfitShareItem;
import com.pcitech.iLife.modules.mod.entity.ProfitShareScheme;
import com.pcitech.iLife.modules.mod.entity.TraceCode;
import com.pcitech.iLife.modules.mod.service.CommissionSchemeService;
import com.pcitech.iLife.modules.mod.service.CreditSchemeService;
import com.pcitech.iLife.modules.mod.service.ProfitShareItemService;
import com.pcitech.iLife.modules.mod.service.ProfitShareSchemeService;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * 佣金规则设置Controller
 * @author qchzhu
 * @version 2019-10-14
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/commissionScheme")
public class CommissionSchemeController extends BaseController {
	@Autowired
	private DictService dictService;
	@Autowired
	private CommissionSchemeService commissionSchemeService;
	@Autowired
	private CreditSchemeService creditSchemeService;
	@Autowired
	private ProfitShareSchemeService profitShareSchemeService;
	@Autowired
	private ProfitShareItemService profitShareItemService;
	
	@ModelAttribute
	public CommissionScheme get(@RequestParam(required=false) String id) {
		CommissionScheme entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = commissionSchemeService.get(id);
		}
		if (entity == null){
			entity = new CommissionScheme();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:commissionScheme:view")
	@RequestMapping(value = {"list", ""})
	public String list(CommissionScheme commissionScheme, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CommissionScheme> page = commissionSchemeService.findPage(new Page<CommissionScheme>(request, response), commissionScheme); 
		model.addAttribute("page", page);
		return "modules/mod/commissionSchemeList";
	}

	@RequiresPermissions("mod:commissionScheme:view")
	@RequestMapping(value = "form")
	public String form(CommissionScheme commissionScheme, Model model) {
		model.addAttribute("commissionScheme", commissionScheme);
		return "modules/mod/commissionSchemeForm";
	}

	@RequiresPermissions("mod:commissionScheme:edit")
	@RequestMapping(value = "save")
	public String save(CommissionScheme commissionScheme, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, commissionScheme)){
			return form(commissionScheme, model);
		}
		commissionSchemeService.save(commissionScheme);
		addMessage(redirectAttributes, "保存佣金规则设置成功");
		return "redirect:"+Global.getAdminPath()+"/mod/commissionScheme/?repage";
	}
	
	@RequiresPermissions("mod:commissionScheme:edit")
	@RequestMapping(value = "delete")
	public String delete(CommissionScheme commissionScheme, RedirectAttributes redirectAttributes) {
		commissionSchemeService.delete(commissionScheme);
		addMessage(redirectAttributes, "删除佣金规则设置成功");
		return "redirect:"+Global.getAdminPath()+"/mod/commissionScheme/?repage";
	}
	
	/**
	 * 根据指定item的佣金及积分。返回结果为：
	 * {
	 * 	order:x,
	 * 	team:y,
	 * 	credit:z
	 * }
	 * 
	 * 参数：source，category，price。其中积分不根据category区分
	 * 
	 * 计算逻辑：
	 * 1，根据source、category查询佣金规则。根据price计算得到总佣金额 amount
	 * 2，根据source、category查询得到分润规则，默认分润类型为 order
	 * 3，查询分润类型明细，得到 推广达人、上级分润规则。根据分润规则及amount计算店返及团返
	 * 4，根据source查询积分规则。并根据price调用脚本计算得到积分
	 * 
	 * 前端展现逻辑：
	 * 0，获取页面分享brokerId，如果存在broker则传递source、category、price请求获取profit信息
	 * 1，得到返回结果后进行显示。如果有店返、团返则不显示积分
	 */
	@ResponseBody
	@RequestMapping(value = "rest/profit", method = RequestMethod.GET)
	public Map<String, Object> getProfit(@RequestParam String source, @RequestParam double price, @RequestParam String category,HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> map = Maps.newHashMap();
		
		//查询佣金规则
		CommissionScheme commission = new CommissionScheme();
		commission.setCategory(category);
		commission.setPlatform(source);
		commission = commissionSchemeService.getByQuery(commission);
		
		//如果没有根据类目设置佣金则默认使用平台佣金规则
		if(commission == null) {
			map.put("warn-commission", "use platform default commission scheme");
			commission = new CommissionScheme();
			commission.setPlatform(source);
			commission = commissionSchemeService.getByQuery(commission);
		}
		
		if(commission == null) {//如果没有佣金规则，那当然就不算钱了，但是积分还是要算的
			map.put("warn-commission", "no commission scheme");
		}else {//如果有佣金规则，就计算佣金总额
			double amount  = 0;
			if("absolute".equalsIgnoreCase(commission.getType())) {//如果是固定金额，则直接用固定金额
				amount = commission.getAmount();
			}else {//如果是百分比，则计算得到。作为默认规则
				amount = commission.getAmount()*price/100;
			}
			
			//查询分润规则并进行二次计算
			ProfitShareScheme profitShareScheme = new ProfitShareScheme();
			profitShareScheme.setCategory(category);
			profitShareScheme.setPlatform(source);
			profitShareScheme.setType("order");//默认只查找订单分润的规则：注意，这个值是在字典里定义的，不能弄错了
			profitShareScheme = profitShareSchemeService.getByQuery(profitShareScheme);
			
			//如果无针对指定类别的分润规则，则直接该平台的默认分润规则
			if(profitShareScheme==null) {//如果无针对指定类别的分润规则，则直接该平台的默认分润规则
				map.put("warn-profit", "use platform default profit share scheme");
				profitShareScheme = new ProfitShareScheme();
				profitShareScheme.setPlatform(source);//仅根据source查找分润规则
				profitShareScheme.setType("order");//默认只查找订单分润的规则：注意，这个值是在字典里定义的，不能弄错了
				profitShareScheme = profitShareSchemeService.getByQuery(profitShareScheme);
			}
			
			if(profitShareScheme==null) {//如果没有分润规则，那也没法算钱啊。快找运营去设置
				map.put("warn-profit", "no profit share scheme");
			}else {//那就找分润明细，并计算达人分润和上级分润
				ProfitShareItem profitShareItem = new ProfitShareItem();
				profitShareItem.setScheme(profitShareScheme);
				profitShareItem.setBeneficiaryType("person");//个人分润
				profitShareItem.setBeneficiary("broker");//特定给推广者
				//查找并计算达人分润
				ProfitShareItem brokerShare = profitShareItemService.getByQuery(profitShareItem);
				if(brokerShare ==null) {//我还能说什么，运营把推广达人的分润都忘到九霄云网了，去找他
					map.put("warn-broker", "no broker profit share item");
				}else {//计算达人的分润金额，并设置店返
					double shareAmount = amount*brokerShare.getShare()/100;
					map.put("order", shareAmount);
				}
				//查找并计算上级达人分润
				profitShareItem.setBeneficiary("parent");//特定给上级达人
				ProfitShareItem parentBrokerShare = profitShareItemService.getByQuery(profitShareItem);
				if(brokerShare ==null) {//彻底无语了，上级达人的分润也没设置
					map.put("warn-parent", "no parent broker profit share item");
				}else {//计算上级达人的分润金额，并设置店返
					double shareAmount = amount*parentBrokerShare.getShare()/100;
					map.put("team", shareAmount);
				}
			}
		}
		
		//查询积分规则，并进行脚本计算，样例如下：
		//注意：脚本中字符串操作需要用单引号，双引号表示模板消息，会出现解析错误
		//return price*0.01
		CreditScheme creditScheme = new CreditScheme();
		creditScheme.setPlatform(source);
		creditScheme.setCategory(category);
		creditScheme = creditSchemeService.getByQuery(creditScheme);
		String script = "return price";//默认积分与价格相同
		
		//如果没有特定品类积分规则，则使用平台积分规则
		if(creditScheme == null) {
			map.put("warn-credit", "no credit scheme. use platform default credit scheme");
			creditScheme = new CreditScheme();
			creditScheme.setPlatform(source);
			creditScheme = creditSchemeService.getByQuery(creditScheme);
		}
		
		if(creditScheme == null) {//不用说了，运营没设置积分规则。直接采用与价格相等作为积分
			map.put("warn-credit", "no credit scheme. use default one let credit=price");
		}else {//否则就获取脚本算积分吧
			script = creditScheme.getScript();
		}
		Binding binding = new Binding();
		binding.setVariable("source",source);
		binding.setVariable("category",category);
		binding.setVariable("price",price);
		try {
	        GroovyShell shell = new GroovyShell(binding);
	        Object value = shell.evaluate(script);//计算得到积分
	        map.put("credit", Double.parseDouble(value.toString()));
		}catch(Exception ex) {//如果计算发生错误也使用默认链接
			map.put("error-script", ex.getMessage());
		}
		
        return map;
	}
	/**
	 * 获得指定商品的佣金及积分。返回结果为：
	 * {
	 * 	order:x,
	 * 	team:y,
	 * 	credit:z
	 * }
	 * 
	 * 参数：source，category，amount，price。其中amount为2方分润佣金额，price为商品售价。积分不根据category区分
	 * 
	 * 计算逻辑：
	 * 1，获取佣金总额 amount：注意佣金总额由前端传入
	 * 2，根据source、category查询得到分润规则，默认分润类型为 order
	 * 3，查询分润类型明细，得到 推广达人、上级分润规则。根据分润规则及amount计算店返及团返
	 * 4，根据source查询积分规则。并根据amount调用脚本计算得到积分
	 * 
	 * 前端展现逻辑：
	 * 0，获取页面分享brokerId，如果存在broker则传递source、category、price请求获取profit信息
	 * 1，得到返回结果后进行显示。如果有店返、团返则不显示积分
	 */
	@ResponseBody
	@RequestMapping(value = "rest/profit-2-party", method = RequestMethod.GET)
	public Map<String, Object> getProfit2Party(@RequestParam String source, @RequestParam double price, @RequestParam double amount, @RequestParam String category,HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> map = Maps.newHashMap();
		
		//查询分润规则并根据2方分润计算3方分润
		ProfitShareScheme profitShareScheme = new ProfitShareScheme();
		profitShareScheme.setCategory(category);
		profitShareScheme.setPlatform(source);
		profitShareScheme.setType("order");//默认只查找订单分润的规则：注意，这个值是在字典里定义的，不能弄错了
		profitShareScheme = profitShareSchemeService.getByQuery(profitShareScheme);
		
		//如果无针对指定类别的分润规则，则直接该平台的默认分润规则
		if(profitShareScheme==null) {//如果无针对指定类别的分润规则，则直接该平台的默认分润规则
			map.put("warn-profit", "use platform default profit share scheme");
			profitShareScheme = new ProfitShareScheme();
			profitShareScheme.setPlatform(source);//仅根据source查找分润规则
			profitShareScheme.setType("order");//默认只查找订单分润的规则：注意，这个值是在字典里定义的，不能弄错了
			profitShareScheme = profitShareSchemeService.getByQuery(profitShareScheme);
		}
		
		if(profitShareScheme==null) {//如果没有分润规则，那也没法算钱啊。快找运营去设置
			map.put("warn-profit", "no profit share scheme");
		}else {//那就找分润明细，并计算达人分润和上级分润
			ProfitShareItem profitShareItem = new ProfitShareItem();
			profitShareItem.setScheme(profitShareScheme);
			profitShareItem.setBeneficiaryType("person");//个人分润
			profitShareItem.setBeneficiary("broker");//特定给推广者
			//查找并计算达人分润
			ProfitShareItem brokerShare = profitShareItemService.getByQuery(profitShareItem);
			if(brokerShare ==null) {//我还能说什么，运营把推广达人的分润都忘到九霄云网了，去找他
				map.put("warn-broker", "no broker profit share item");
			}else {//计算达人的分润金额，并设置店返
				double shareAmount = amount*brokerShare.getShare()/100;
				map.put("order", shareAmount);
			}
			//查找并计算上级达人分润
			profitShareItem.setBeneficiary("parent");//特定给上级达人
			ProfitShareItem parentBrokerShare = profitShareItemService.getByQuery(profitShareItem);
			if(brokerShare ==null) {//彻底无语了，上级达人的分润也没设置
				map.put("warn-parent", "no parent broker profit share item");
			}else {//计算上级达人的分润金额，并设置店返
				double shareAmount = amount*parentBrokerShare.getShare()/100;
				map.put("team", shareAmount);
			}
		}
		
		//查询积分规则，并进行脚本计算，样例如下：
		//注意：脚本中字符串操作需要用单引号，双引号表示模板消息，会出现解析错误
		//return price*0.01
		CreditScheme creditScheme = new CreditScheme();
		creditScheme.setPlatform(source);
		creditScheme.setCategory(category);
		creditScheme = creditSchemeService.getByQuery(creditScheme);
		String script = "return price";//默认积分与价格相同
		
		//如果没有特定品类积分规则，则使用平台积分规则
		if(creditScheme == null) {
			map.put("warn-credit", "no credit scheme. use platform default credit scheme");
			creditScheme = new CreditScheme();
			creditScheme.setPlatform(source);
			creditScheme = creditSchemeService.getByQuery(creditScheme);
		}
		
		if(creditScheme == null) {//不用说了，运营没设置积分规则。直接采用与价格相等作为积分
			map.put("warn-credit", "no credit scheme. use default one let credit=price");
		}else {//否则就获取脚本算积分吧
			script = creditScheme.getScript();
		}
		Binding binding = new Binding();
		binding.setVariable("source",source);
		binding.setVariable("category",category);
		binding.setVariable("price",price);
		try {
	        GroovyShell shell = new GroovyShell(binding);
	        Object value = shell.evaluate(script);//计算得到积分
	        map.put("credit", Double.parseDouble(value.toString()));
		}catch(Exception ex) {//如果计算发生错误也使用默认链接
			map.put("error-script", ex.getMessage());
		}
		
        return map;
	}
}