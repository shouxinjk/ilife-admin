/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.wx.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.modules.wx.entity.WxAccount;
import com.pcitech.iLife.modules.wx.entity.WxSubscribes;
import com.pcitech.iLife.modules.wx.service.WxAccountService;
import com.pcitech.iLife.modules.wx.service.WxSubscribesService;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;
import com.pcitech.iLife.util.WxHelper;

/**
 * 微信公众号管理Controller
 * @author ilife
 * @version 2022-03-28
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxAccount")
public class WxAccountController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private WxSubscribesService wxSubscribesService;
	
	@Autowired
	private BrokerService brokerService;
	
	@Autowired
	private DictService dictService;
	
	@Autowired
	private WxAccountService wxAccountService;
	
	@ModelAttribute
	public WxAccount get(@RequestParam(required=false) String id) {
		WxAccount entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxAccountService.get(id);
		}
		if (entity == null){
			entity = new WxAccount();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxAccount:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxAccount wxAccount, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxAccount> page = wxAccountService.findPage(new Page<WxAccount>(request, response), wxAccount); 
		model.addAttribute("page", page);
		return "modules/wx/wxAccountList";
	}

	@RequiresPermissions("wx:wxAccount:view")
	@RequestMapping(value = "form")
	public String form(WxAccount wxAccount, Model model) {
		model.addAttribute("wxAccount", wxAccount);
		return "modules/wx/wxAccountForm";
	}

	@RequiresPermissions("wx:wxAccount:edit")
	@RequestMapping(value = "save")
	public String save(WxAccount wxAccount, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxAccount)){
			return form(wxAccount, model);
		}
		wxAccountService.save(wxAccount);
		addMessage(redirectAttributes, "保存微信公众号成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxAccount/?repage";
	}
	
	@RequiresPermissions("wx:wxAccount:edit")
	@RequestMapping(value = "delete")
	public String delete(WxAccount wxAccount, RedirectAttributes redirectAttributes) {
		wxAccountService.delete(wxAccount);
		addMessage(redirectAttributes, "删除微信公众号成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxAccount/?repage";
	}

	
	/**
	 * 获取待阅读公众号列表:支持根据发布者 openid 过滤
	 * 包括置顶公众号及普通公众号两部分，在获取第一页时，优先获取指定列表，
	 * 然后分页获取普通待阅读公众号列表
	 * 注意：公众号的status字段保持的是置顶信息或顶一下信息。前端需要转换为数字，如果大于1为置顶，等于1为顶一下，0则为普通公众号
	 */
	@ResponseBody
	@RequestMapping(value = "rest/pending-accounts", method = RequestMethod.GET)
	public List<WxAccount> listPagedPendingAccounts( @RequestParam(required=true) int from,@RequestParam(required=true) int to,@RequestParam String openid,@RequestParam String publisherOpenid) {
		List<WxAccount> list = Lists.newArrayList();
		//组织参数
		Map<String,Object> params = Maps.newHashMap();
		params.put("from", from);
		params.put("to", to);
		if(openid!=null && openid.trim().length()>0) {
			params.put("openid", openid);
		}
		if(publisherOpenid!=null && publisherOpenid.trim().length()>0) {
			params.put("publisherOpenid", publisherOpenid);
		}
		//先查询获取指定公众号列表，仅在第一页时加载
		if(from==0) {
			list.addAll(wxAccountService.findToppingList(params));
		}
		
		//然后获取普通公众号列表
		list.addAll(wxAccountService.findPendingList(params));
		return list;
	}
	
	/**
	 * 根据openid获取已发布公众号列表
	 */
	@ResponseBody
	@RequestMapping(value = "rest/my-accounts/{openid}", method = RequestMethod.GET)
	public List<WxAccount> findMyAccounts(@PathVariable String openid, @RequestParam(required=true) int from,@RequestParam(required=true) int to) {
		Map<String,Object> params = Maps.newHashMap();
		params.put("openid", openid);
		params.put("from", from);
		params.put("to", to);
		return wxAccountService.findMyAccounts(params);
	}
	
	/**
	 * 根据openid获取已发布公众号总数
	 */
	@ResponseBody
	@RequestMapping(value = "rest/total-accounts/{openid}", method = RequestMethod.GET)
	public Map<String,Object> countMyAccounts(@PathVariable String openid) {
		logger.debug("try to count accounts by openid.[openid]"+openid);
		Map<String, Object> result = Maps.newHashMap();
		result.put("status",true);
		result.put("count",wxAccountService.countMyAccounts(openid));
		return result;
	}	
	
	/**
	 * 发布公众号。数据：
	 * 	original：公众号地址
	 * 	broker：id或openid至少传递一个，在传递openid时需要同时传递nickname
	 */
	@ResponseBody
	@RequestMapping(value = "rest/account", method = RequestMethod.POST)
	public Map<String, Object> publishAccount(@RequestBody WxAccount account){
		logger.debug("try to publish account.[data]"+account);
		Map<String, Object> result = Maps.newHashMap();
		Broker broker = null;
		
		//设置ID，并检查是否存在：
		String id = Util.md5(account.getOriginalId());
		WxAccount account2 = wxAccountService.get(id);
		if(account2!=null) {//表示存在，直接返回，不扣除阅豆
			result.put("status",true);
			result.put("description","Account exists.");
			result.put("code", "duplicate");//特殊标记，对于已经存在的公众号不会扣减阅豆，需要提示发布者
			result.put("data", account2);//将公众号作为数据返回
			return result;
		}
		
		//检查达人信息，如果不存在则静默创建
		if(account.getBroker()!=null && account.getBroker().getId()!=null) {
			broker = brokerService.get(account.getBroker().getId());
		}else if(account.getBroker()!=null && account.getBroker().getOpenid()!=null) {
			broker = brokerService.getByOpenid(account.getBroker().getOpenid());//先根据openid查询
			if(broker == null) {//如果不存在则直接默认创建
				//如果不存在，表示未注册的情况下直接发了链接，默认注册达人
				String parentdBrokerId = Global.getConfig("default_parent_broker_id");//固定达人ID 
				broker = new Broker();
				broker.setId(Util.md5(account.getBroker().getOpenid()));
				broker.setIsNewRecord(true);
				broker.setParent(brokerService.get(parentdBrokerId));
				broker.setOpenid(account.getBroker().getOpenid());
				broker.setPoints(20);//默认设置
				String nickname = "流量主达人";
				if(account.getBroker().getNickname()!=null&&account.getBroker().getNickname().trim().length()>0) {
					nickname = account.getBroker().getNickname();
				}
				broker.setNickname(nickname);
				brokerService.save(broker);
				//发送通知到上级达人
				JSONObject json = new JSONObject();
				json.put("title", "新流量主自动注册");
				json.put("name", nickname);
				json.put("openid", Global.getConfig("default_parent_broker_openid"));//固定达人openid
				HttpClientHelper.getInstance().post(
						Global.getConfig("wechat.templateMessenge")+"/notify-parent-broker", 
						json,null);//推送上级达人通知
			}
		}else {//直接提示出错了
			result.put("status",false);
			result.put("description","No broker info.");
		}
		
		//检查虚拟豆
		if(broker == null || broker.getId() == null || broker.getId().trim().length()==0 ) {
			result.put("status",false);
			result.put("description","Cannot find broker. "+account.getBroker());
		}else if(broker.getPoints()>0) {
			//发布公众号
			account.setId(id);
			account.setIsNewRecord(true);//直接作为新公众号发布
			account.setCreateDate(new Date());
			account.setUpdateDate(new Date());
			account.setStatus("active");
//			account.setChannel("manual");//公众号不支持静默创建
			account.setBroker(broker);
			wxAccountService.save(account);
			result.put("status",true);
			result.put("description","Account created successfully");
			result.put("data", wxAccountService.get(id));//将公众号作为数据返回
			
			//扣除虚拟豆
			Dict dict = new Dict();
			dict.setType("publisher_point_cost");//查找流量主虚拟豆字典设置
			List<Dict> points = dictService.findList(dict);
			int pointsCost = 5;//默认为5个
			for(Dict point:points) {
				if("publish-account".equalsIgnoreCase(point.getValue())) {
					try {
						pointsCost = Integer.parseInt(point.getLabel());
					}catch(Exception ex) {
						//do nothing
					}
					break;
				}
			}
			broker.setPoints(broker.getPoints()-pointsCost);//TODO：查询扣除虚拟豆数量
			brokerService.save(broker);
			result.put("points", pointsCost);	
			result.put("pointsRemain", broker.getPoints());	
			result.put("description","Account created successfully and points charged");
			
			//推送公众号到企业微信群，便于群发：由于通过微信，无前端接入，只能从后端完成推送
			String img = "https://open.weixin.qq.com/qr/code?username="+account.getOriginalId();
			JSONObject webhookItem = new JSONObject();
			webhookItem.put("title" , account.getName());
			webhookItem.put("description" , "有新公众号发布，我们去粉吧~~");
			webhookItem.put("url" , img);//TODO：需要进入公众号列表界面，当前直接跳转到公众号本身
			webhookItem.put("picurl" , img);//采用默认图片
			JSONArray webhookItems = new JSONArray();
			webhookItems.add(webhookItem);
			JSONObject webhookAccounts = new JSONObject();
			webhookAccounts.put("accounts", webhookItems);
			JSONObject webhookMsg = new JSONObject();
			webhookMsg.put("msgtype", "news");
			webhookMsg.put("news", webhookAccounts);
			logger.debug("try to post webhook msg."+webhookMsg);
			Map<String,String> headers = Maps.newHashMap();
			headers.put("Content-Type", "application/json");
			HttpClientHelper.getInstance().post(Global.getConfig("webHookUrlPrefix")+Global.getConfig("webHookCompanyBroker"), webhookMsg,headers);//推送到微信达人运营群webhook
		}else {
			result.put("status",false);
			result.put("description","Canceld. Points not enough.");
		}
		return result;
	}
	
	/**
	 * 上架、下架公众号
	 */
	@ResponseBody
	@RequestMapping(value = "rest/account/{id}/{status}", method = RequestMethod.POST)
	public Map<String, Object> changeAccountStatus(@PathVariable String id,@PathVariable String status){
		Map<String, Object> result = Maps.newHashMap();
		WxAccount account = wxAccountService.get(id);
		
		//检查公众号
		if(account == null) {
			result.put("status",false);
			result.put("description","Cannot find account by id:"+id);
		}else{
			//修改公众号状态
			account.setStatus(status);
			wxAccountService.save(account);
			result.put("status",true);
			result.put("data",account);
			result.put("description","Account status changed.[new]"+status);
		}
		return result;
	}
	
	/**
	 * 有效阅读，扣除阅豆
	 * 扣除阅豆，并返回本次消耗
	 */
	@ResponseBody
	@RequestMapping(value = "rest/subscribe/{id}/{openid}", method = RequestMethod.POST)
	public Map<String, Object> onAccountViewed(@PathVariable String id,@PathVariable String openid){
		Map<String, Object> result = Maps.newHashMap();
		WxAccount account = wxAccountService.get(id);
		
		//检查公众号
		if(account == null) {
			result.put("status",false);
			result.put("description","Cannot find account by id:"+id);
		}else{
			//获取公众号发布达人
			Broker broker = brokerService.get(account.getBroker().getId());//重要：必须重新获取，否则会导致其他信息丢失
			//扣除虚拟豆
			Dict dict = new Dict();
			dict.setType("publisher_point_cost");//查找流量主虚拟豆字典设置
			List<Dict> points = dictService.findList(dict);
			int pointsCost = 5;//默认为5个
			for(Dict point:points) {
				if("subscribe-account".equalsIgnoreCase(point.getValue())) {
					try {
						pointsCost = Integer.parseInt(point.getLabel());
					}catch(Exception ex) {
						//do nothing
					}
					break;
				}
			}
			//扣除公众号发布者阅豆
			broker.setPoints(broker.getPoints()-pointsCost);//查询扣除虚拟豆数量
			brokerService.save(broker);
			//增加阅读者阅豆
			Broker subscriber = brokerService.getByOpenid(openid);
			if(subscriber==null || subscriber.getId()==null) {
				//直接忽略
				result.put("description","Cannot find broker info for current subscriber");
			}else {
				subscriber.setPoints(subscriber.getPoints()+pointsCost);//增加奖励
				brokerService.save(subscriber);
			}
			//写入阅读记录
			WxSubscribes wxSubscribes = new WxSubscribes();
			wxSubscribes.setBroker(broker);
			wxSubscribes.setOpenid(openid);
			wxSubscribes.setAccount(account);
			wxSubscribes.setCreateDate(new Date());
			wxSubscribes.setUpdateDate(new Date());
			wxSubscribesService.save(wxSubscribes);
			
			//需要设置返回信息：发布者达人ID、openId、logo、消耗阅豆、
			result.put("openid",broker.getOpenid());
			result.put("brokerId",broker.getId());
			result.put("nickname",broker.getName());
			//result.put("avatarUrl",broker.getOpenid()); //TODO 需要补充
			result.put("points",pointsCost);
			result.put("pointsRemain",broker.getPoints());
			result.put("status",true);
			result.put("description","Account exposure pionts cosumed.[cost]"+pointsCost+"[remain]"+broker.getPoints());
		}
		return result;
	}
	
}