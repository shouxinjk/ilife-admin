/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.quartz.JobExecutionException;
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
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;
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
	private DictService dictService;
	
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
	 * 不会自动创建
	 * 
	 * 当前仅在scan及subscribe事件中应用
	 */
	@ResponseBody
	@RequestMapping(value = "rest/checkBrokerByOpenid/{openid}", method = RequestMethod.GET)
	public Map<String, Object> checkBrokerByOpenid(@PathVariable String openid) {
		Map<String, Object> result = Maps.newHashMap();
		Broker broker = brokerService.getByOpenid(openid);//根据openid获取达人
		if(broker == null) {//如果未找到对应的达人直接返回空
			result.put("status", false);
			result.put("msg", "no broker found by openid:"+openid);
		}else {
			result.put("status", true);
			result.put("data", broker);
		}
		return result;
	}
	
	/**
	 * 接受邀请后给上级达人增加邀请阅豆
	 * 仅增加阅豆，适用于在达人分享自己的二维码之后，扫码进入的场景
	 * @param openid
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/reward/invite/{brokerId}", method = RequestMethod.POST)
	public Map<String, Object> addInvitePoints(@PathVariable String brokerId) {
		Map<String, Object> result = Maps.newHashMap();
		Broker broker = brokerService.get(brokerId);//根据openid获取达人
		if(broker == null) {//如果未找到对应的达人直接返回空
			result.put("status", false);
			result.put("msg", "no broker found by id:"+brokerId);
		}else {
			if("disabled".equalsIgnoreCase(broker.getStatus())) {
				result.put("status", false);
				result.put("msg", "账号异常。请与我们联系。");//给不怀好意的人的善意提示
			}else if("offline".equalsIgnoreCase(broker.getStatus())) {//如果已经取关，也就不管了
				result.put("status", false);
				result.put("msg", "账号异常。请与我们联系。");//给不怀好意的人的善意提示
			}else {//正常更新
				//查询增加虚拟豆
				Dict dict = new Dict();
				dict.setType("publisher_point_cost");//查找流量主虚拟豆字典设置
				List<Dict> points = dictService.findList(dict);
				int pointsReward = 50;//默认为50个
				for(Dict point:points) {
					if("invite-person".equalsIgnoreCase(point.getValue())) {
						try {
							pointsReward = Integer.parseInt(point.getLabel());
						}catch(Exception ex) {
							//do nothing
						}
						break;
					}
				}
				broker.setPoints(broker.getPoints()+pointsReward);
				broker.setUpdateDate(new Date());
				brokerService.save(broker);
				result.put("status", true);
				result.put("points", pointsReward);
			}
		}
		return result;
	}
	
	/**
	 * 确认接受邀请并建立邀请关系。
	 * 适用于通过分享文章或列表页面链接后关注的用户。其逻辑为：
	 * 1，达人分享文章或公众号列表链接，链接带有fromBroker参数
	 * 2，新用户扫码关注，默认作为defaultBroker的下级达人。此时defaultBroker能够收到加入信息
	 * 3，扫码后新用户将立即进入列表页面，会带有isNewBroker及fromBroker参数，发起修改邀请信息请求
	 * 4，进入本方法：将新加入达人的上级达人修改为fromBroker；给fromBroker增加阅豆，并向fromBroker发送奖励通知
	 * @param brokerId 新加入达人ID
	 * @param fromBrokerId 邀请达人ID，即上级达人ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rest/change/invite/{brokerId}/{fromBrokerId}", method = RequestMethod.POST)
	public Map<String, Object> changeInviteInfo(@PathVariable String brokerId,@PathVariable String fromBrokerId) {
		Map<String, Object> result = Maps.newHashMap();
   	    //准备发起HTTP请求：设置data server Authorization
	    Map<String,String> header = new HashMap<String,String>();
	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
		
		//获取新加入达人
		Broker broker = brokerService.get(brokerId);//根据brokerId获取被邀请达人
		if(broker == null) {//如果未找到对应的达人直接返回空
			result.put("status", false);
			result.put("msg", "no broker found by id:"+brokerId);
			return result;
		}
		
		//获取上级达人
		Broker parentBroker = brokerService.get(fromBrokerId);//根据fromBrokerId获取上级达人
		if(parentBroker == null) {//如果未找到对应的达人直接返回空
			result.put("status", false);
			result.put("msg", "no broker found by id:"+fromBrokerId);
		}else {
			if("disabled".equalsIgnoreCase(parentBroker.getStatus())) {
				result.put("status", false);
				result.put("msg", "账号异常。请与我们联系。");//给不怀好意的人的善意提示
			}else if("offline".equalsIgnoreCase(parentBroker.getStatus())) {//如果已经取关，也就不管了
				result.put("status", false);
				result.put("msg", "账号异常。请与我们联系。");//给不怀好意的人的善意提示
			}else if(broker.getParent()!=null && broker.getParent().getId().equalsIgnoreCase(fromBrokerId)) {//如果已经是当前邀请达人了，则不重复奖励
				result.put("status", true);
				result.put("msg", "done with no points reward.");
			}else {//正常更新
				//查询增加虚拟豆
				Dict dict = new Dict();
				dict.setType("publisher_point_cost");//查找流量主虚拟豆字典设置
				List<Dict> points = dictService.findList(dict);
				int pointsReward = 50;//默认为50个
				for(Dict point:points) {
					if("invite-person".equalsIgnoreCase(point.getValue())) {
						try {
							pointsReward = Integer.parseInt(point.getLabel());
						}catch(Exception ex) {
							//do nothing
						}
						break;
					}
				}
				
				//修改当前达人的邀请关系
				broker.setParent(parentBroker);
				broker.setUpdateDate(new Date());
				brokerService.save(broker);
				
				//奖励上级达人阅豆
				parentBroker.setPoints(parentBroker.getPoints()+pointsReward);
				parentBroker.setUpdateDate(new Date());
				brokerService.save(parentBroker);
				
				//给上级达人发送通知
				//组装模板消息
				JSONObject json = new JSONObject();
				json.put("openid", parentBroker.getOpenid());
				json.put("title", "亲，有新成员接受邀请哦~~");
				json.put("name", broker.getNickname());
				json.put("url", "http://www.biglistoflittlethings.com/ilife-web-wx/publisher/team.html");//调到流量主团队界面
				String remark = "以下奖励及权益已到账："
						+ "\n\n邀请奖励："+pointsReward+"阅豆"
						+ "\n权益激活：将分享新成员内容带货收益"
						+ "\n\n感谢分享，我们一起把公众号做的更好~~";

				json.put("remark", remark);
				//发送
				HttpClientHelper.getInstance().post(
						Global.getConfig("wechat.templateMessenge")+"/notify-parent-broker", 
						json,header);
				
				result.put("status", true);
				result.put("points", pointsReward);
			}
		}
		
		return result;
	}
	
	/**
	 * 根据openid获取指定达人信息
	 * 如果不存在会自动创建
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
			if("offline".equalsIgnoreCase(broker.getStatus()))//仅对于取关的进行更新，其他的如disabled不做调整
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
	 * 根据id获取下级达人总数
	 */
	@ResponseBody
	@RequestMapping(value = "rest/count/{id}", method = RequestMethod.GET)
	public Map<String,Object> countChildBrokers(@PathVariable String id, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("count", 0);
		result.put("parentId", id);
		int count =brokerService.countChilds(id);
		result.put("count", count);
		return result;
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
	
	//激活沉寂流量主。向所有已关注，但未发生任何操作的流量主发出激活消息
	@ResponseBody
	@RequestMapping(value = "rest/publisher/activate", method = RequestMethod.POST)
	public Map<String, Object> sendPublisherActivateMsg() {
		Map<String, Object> result = Maps.newHashMap();
   	    //准备发起HTTP请求：设置data server Authorization
	    Map<String,String> header = new HashMap<String,String>();
	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
		
		SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		Map<String,Object> maps = Maps.newHashMap();
		maps.put("from", 0);
		maps.put("to", 1000);//单次最多发送1000条
		
		List<Broker> publishers = brokerService.findInactivePublisherIdList(maps);//注意当前未采用参数，直接发送给全部流量主
		
		logger.debug("Try to activate publisher.[total]"+publishers.size());
		int total = 0;
		for(Broker publisher:publishers) {
			//仅测试使用 
			/**
			if(!"o8HmJ1EdIUR8iZRwaq1T7D_nPIYc".equalsIgnoreCase(publisher.getOpenid()) 
					&& !"o8HmJ1ItjXilTlFtJNO25-CAQbbg".equalsIgnoreCase(publisher.getOpenid())
					&& !"o8HmJ1APyNtRkT1dIVXpBD-yN4Kc".equalsIgnoreCase(publisher.getOpenid()))
				continue;
			//**/
			//组装模板消息
			JSONObject json = new JSONObject();
			json.put("openid", publisher.getOpenid());
			json.put("title", "流量主福利");
			json.put("timestamp", fmt2.format(new Date()));
			json.put("points", publisher.getPoints()+" 阅豆");
			String remark = "";
			remark+="号主大大，感谢一直陪伴。我们最近上线了流量主工具，能够展示文章和公众号，助力增粉增阅。";
			remark+="\n\n我们真心希望能帮助节省时间，以集中精力创作更好的内容，把公众号做的更好。"
					+ "\n\n并已赠送"+publisher.getPoints()+"阅豆，可立即发布文章和公众号，请点击进入查看~~";
			json.put("remark", remark);
			//发送
			HttpClientHelper.getInstance().post(
					Global.getConfig("wechat.templateMessenge")+"/notify-mp-publisher", 
					json,header);
			total ++ ;
		}
		result.put("total", total);
		return result;
	}
}