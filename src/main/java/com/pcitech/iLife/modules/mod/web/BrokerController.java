/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
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

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.Util;

import me.chanjar.weixin.common.error.WxErrorException;


/**
 * 达人管理Controller
 * @author qchzhu
 * @version 2019-10-14
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/broker")
public class BrokerController extends BaseController {

	//注意：当前未启用，需要提供WxMpService实现类
	//@Autowired
	//private WxMpService wxMpService;
	
	@Autowired
	private BrokerService brokerService;
	
	@ModelAttribute
	public Broker get(@RequestParam(required=false) String id) {
		Broker entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = brokerService.get(id);
		}
		if (entity == null){
			entity = new Broker();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:broker:view")
	@RequestMapping(value = {"list", ""})
	public String list(Broker broker, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Broker> page = brokerService.findPage(new Page<Broker>(request, response), broker); 
		model.addAttribute("page", page);
		return "modules/mod/brokerList";
	}

	@RequiresPermissions("mod:broker:view")
	@RequestMapping(value = "form")
	public String form(Broker broker, Model model) {
		model.addAttribute("broker", broker);
		return "modules/mod/brokerForm";
	}

	@RequiresPermissions("mod:broker:edit")
	@RequestMapping(value = "save")
	public String save(Broker broker, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, broker)){
			return form(broker, model);
		}
		//检查虚拟豆
		if(broker.getPoints()==0) {
			broker.setPoints(20);//默认虚拟豆：固化写死
		}
		//检查达人等级：强制达人等级=parent等级+1
		Broker parent = brokerService.get(broker.getParent().getId());//注意需要另外获取，broker.parent内不包含hierarchy信息
		broker.setHierarchy(parent==null?1:parent.getHierarchy()+1);
		
		brokerService.save(broker);
		addMessage(redirectAttributes, "保存推广达人成功");
		return "redirect:"+Global.getAdminPath()+"/mod/broker/?repage";
	}
	
	@RequiresPermissions("mod:broker:edit")
	@RequestMapping(value = "delete")
	public String delete(Broker broker, RedirectAttributes redirectAttributes) {
		brokerService.delete(broker);
		addMessage(redirectAttributes, "删除推广达人成功");
		return "redirect:"+Global.getAdminPath()+"/mod/broker/?repage";
	}
	
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		Broker broker = new Broker();
		List<Broker> list = brokerService.findList(broker);
		for (int i=0; i<list.size(); i++){
			Broker e = list.get(i);
			//if (StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParent().getId());
				map.put("name", e.getName());
				mapList.add(map);
			//}
		}
		return mapList;
	}

	/**
	 * 根据openid获取指定达人信息
	 */
	@ResponseBody
	@RequestMapping(value = "rest/brokerByOpenid/{openid}", method = RequestMethod.GET)
	public Map<String, Object> getBrokerByOpenid(@PathVariable String openid) {
		Map<String, Object> result = Maps.newHashMap();
		Broker broker = brokerService.getByOpenid(openid);//根据openid获取达人
		if(broker == null) {//如果未找到对应的达人直接返回空
			//result.put("status", false);
			//20220416：修复因为avatarUrl不为空导致的达人创建失败问题：因为没有达人导致前端一直在加载状态，此处临时创建达人。
			Broker tmpBroker = new Broker();
			Broker parentBroker = brokerService.get(Global.getConfig("default_parent_broker_id"));
			tmpBroker.setId(Util.md5(openid));
			tmpBroker.setIsNewRecord(true);
			tmpBroker.setParent(parentBroker);
			tmpBroker.setLevel("流量主");
			tmpBroker.setPoints(20);//默认
			tmpBroker.setRemarks("修复20220416错误自动创建");
			tmpBroker.setStatus("ready");
			tmpBroker.setHierarchy(parentBroker.getHierarchy()+1);
			tmpBroker.setOpenid(openid);
			tmpBroker.setCreateDate(new Date());
			tmpBroker.setUpdateDate(new Date());
			brokerService.save(tmpBroker);
			broker = brokerService.getByOpenid(openid);//重新根据openid获取达人
			if(broker == null) {
				result.put("status", false);
				result.put("msg", "该死，修复bug后还是没能获取broker");
			}else {
				result.put("status", true);
				result.put("data", broker);
				result.put("msg", "该死，这是临时修复bug自动创建的broker");
			}
			//20220416 临时修复结束
		}else {
			result.put("status", true);
			result.put("data", broker);
		}
		return result;
	}
	
	/**
	 * 更新达人nickname及avatarUrl：
	 * 如果带有nickname，则同时更新nickname
	 * 如果带有avatarUrl，则同时更新avatarUrl
	 */
	@ResponseBody
	@RequestMapping(value = "rest/sync/{openid}", method = RequestMethod.POST)
	public Map<String, Object> syncBrokerInfo(@PathVariable String openid, @RequestBody JSONObject json) {
		Map<String, Object> result = Maps.newHashMap();
		Broker broker = brokerService.getByOpenid(openid);//根据openid获取达人
		if(broker == null) {//如果未找到对应的达人直接返回空
			result.put("status", false);
		}else {
			//更新状态为ready：将之前取关的收回来
			broker.setStatus("ready");
			//检查并更新nickname与avatarUrl
			boolean updateBroker = false;
			if(json.getString("nickname")!=null && json.getString("nickname").trim().length()>0) {
				broker.setNickname(json.getString("nickname"));
				updateBroker = true;
			}
			if(json.getString("avatarUrl")!=null && json.getString("avatarUrl").trim().length()>0) {
				broker.setAvatarUrl(json.getString("avatarUrl"));
				updateBroker = true;
			}
			if(updateBroker) {
				brokerService.save(broker);
			}
			result.put("status", true);
			result.put("data", broker);
		}
		return result;
	}
	
	/**
	 * 根据brokerId获取指定达人信息
	 */
	@ResponseBody
	@RequestMapping(value = "rest/brokerById/{brokerId}", method = RequestMethod.GET)
	public Map<String, Object> getBrokerByBrokerid(@PathVariable String brokerId, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = Maps.newHashMap();
		Broker broker = brokerService.get(brokerId);//根据brokerId获取达人
		if(broker == null) {//如果未找到对应的达人直接返回空
			result.put("status", false);
		}else {
			result.put("status", true);
			result.put("data", broker);
		}
		return result;
	}
	
	/**
	 * 根据openid获取下级达人列表
	 */
	@ResponseBody
	@RequestMapping(value = "rest/brokersByOpenid/{openid}", method = RequestMethod.GET)
	public List<Broker> listBrokersByOpenid(@PathVariable String openid, @RequestParam int from, @RequestParam int to) {
		List<Broker> mapList = Lists.newArrayList();
		Broker parent = brokerService.getByOpenid(openid);//根据openid获取父级达人
		if(parent == null)//如果未找到对应的达人直接返回空
			return mapList;
		Broker broker = new Broker();
		broker.setParent(parent);
//		return brokerService.findList(broker);
		Map<String,Object> params = Maps.newHashMap();
		params.put("parentId", parent.getId());
		params.put("from", from);
		params.put("to", to);
		return brokerService.findChildList(params);
	}
	
	/**
	 * 根据openid设置状态，在取消关注时使用
	 */
	@ResponseBody
	@RequestMapping(value = "rest/disableBrokersByOpenid/{openid}", method = RequestMethod.POST)
	public Map<String,Object> disableBroker(@PathVariable String openid, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String,Object> map = Maps.newHashMap();
		map.put("success", false);
		map.put("message", "the use  is not a broker.");
		Broker broker = brokerService.getByOpenid(openid);//根据openid获取父级达人
		if(broker == null)//如果未找到对应的达人直接返回空
			return map;
		broker.setStatus("offline");
		try {
			brokerService.save(broker);
			map.put("success", true);
			map.put("message", "the broker is now disabled.");
		}catch(Exception ex) {
			map.put("success", false);
			map.put("message", "error occured while disable broker.[openid]"+openid);
		}
		return map;
	}
	
	/**
	 * 根据id获取下级达人列表
	 */
	@ResponseBody
	@RequestMapping(value = "rest/{id}", method = RequestMethod.GET)
	public List<Broker> listBrokers(@PathVariable String id, HttpServletRequest request, HttpServletResponse response, Model model) {
		Broker parent = new Broker();
		parent.setId(id);
		Broker broker = new Broker();
		broker.setParent(parent);
		List<Broker> list =brokerService.findList(broker);
		return list;
	}
	
	/**
	 * 给指定ID的达人添加下级达人
	 */
	@ResponseBody
	@RequestMapping(value = "rest/{id}", method = RequestMethod.POST)
	public Map<String, Object> registerBroker(@PathVariable String id,@RequestBody Broker broker, HttpServletRequest request, HttpServletResponse response, Model model) throws WxErrorException{
		Map<String, Object> result = Maps.newHashMap();
		Broker parent = brokerService.get(id);
		if(parent == null) {
			result.put("status",false);
			result.put("description","Cannot find parent broker by id:"+id);
		}else {
			broker.setParent(parent);
			broker.setHierarchy(parent.getHierarchy()+1);//默认设置为上级达人层级+1
			brokerService.save(broker);
			result.put("status",true);
			result.put("description","Broker created successfully");
			Broker newbroker = brokerService.get(broker);
			result.put("data", newbroker);
		}
		//检查虚拟豆
		if(broker.getPoints()==0) {
			broker.setPoints(20);//默认虚拟豆：固化写死
		}
		return result;
	}
	
	//静默注册：根据openid查找是否有达人信息，如果没有则注册
	@ResponseBody
	@RequestMapping(value = "rest/silent-broker-check", method = RequestMethod.POST)
	public Broker registerBrokerSilent(@RequestBody Broker broker) {
		if(broker.getId()!=null && broker.getId().trim().length()>0){//表示已经存在，不需处理，直接返回
			Broker b = brokerService.get(broker);
			if(broker.getNickname() !=null && broker.getNickname().trim().length()>0) {
				b.setNickname(broker.getNickname());//由于不能静默获取微信用户信息，需要更新
				brokerService.save(b);
			}
			return b;
		}else if(broker.getOpenid()!=null && broker.getOpenid().trim().length()>0) {
			Broker b = brokerService.getByOpenid(broker.getOpenid());
			if(b!=null){//如果存在则直接返回
				return b;
			}else {//否则执行静默注册
				//如果不存在，表示未注册的情况下直接发了链接，默认注册达人
				String parentdBrokerId = Global.getConfig("default_parent_broker_id");//固定达人ID 
				Broker parentBroker = brokerService.get(parentdBrokerId);
//				broker = new Broker();
				broker.setId(Util.md5(broker.getOpenid()));
				broker.setIsNewRecord(true);
				broker.setParent(parentBroker);
				broker.setHierarchy(parentBroker.getHierarchy()+1);
//				broker.setOpenid(broker.getOpenid());
				broker.setPoints(20);//默认设置
				String nickname = "确幸生活家";
				if(broker.getNickname()!=null&&broker.getNickname().trim().length()>0) {
					nickname = broker.getNickname();
				}
				broker.setNickname(nickname);
				brokerService.save(broker);
				//发送通知到上级达人
				JSONObject json = new JSONObject();
				json.put("title", "静默达人自动注册");
				json.put("name", nickname);
				json.put("openid", Global.getConfig("default_parent_broker_openid"));//固定达人openid
				HttpClientHelper.getInstance().post(
						Global.getConfig("wechat.templateMessenge")+"/notify-parent-broker", 
						json,null);//推送上级达人通知
				return broker;
			}
		}else {
			//出错了。无法继续。直接返回吧
			return new Broker();
		}
		
	}
	
	
	/**
	 * 修改达人：包括修改单个属性如“升级状态”
	 */
	@ResponseBody
	@RequestMapping(value = "rest/{id}", method = RequestMethod.PUT)
	public Map<String, Object> modifyBroker(@PathVariable String id,@RequestBody Broker broker, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = Maps.newHashMap();
		Broker old = brokerService.get(id);
		if(old == null) {
			result.put("status",false);
			result.put("description","Broker does not exist. id:"+id);
		}else {
			broker.setParent(old.getParent());
			broker.setId(old.getId());//使用路径ID替换传递的id参数，避免误操作
			brokerService.save(broker);
			result.put("status",true);
			result.put("description","Broker modified successfully");
		}
		return result;
	}
	
	/**
	 * 获取达人收益汇总数据:总额、已锁定总额(已收款+已锁定)、可提款总额（已收款+已清算）、已提现总额
	 * 结算中金额 = 总额-已锁定总额-可提款总额
	 * 提现金额 = 可提款总额-已提现总额
	 */
	@ResponseBody
	@RequestMapping(value = "rest/money/{id}", method = RequestMethod.GET)
	public Map<String, Object> getMoney(@PathVariable String id, HttpServletRequest request, HttpServletResponse response, Model model) {
		return brokerService.getMoney(id);
	}
}