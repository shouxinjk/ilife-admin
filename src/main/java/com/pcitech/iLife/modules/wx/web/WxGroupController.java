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
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.sys.utils.UserUtils;
import com.pcitech.iLife.modules.wx.entity.WxGroup;
import com.pcitech.iLife.modules.wx.entity.WxGroupTask;
import com.pcitech.iLife.modules.wx.service.WxGroupService;
import com.pcitech.iLife.modules.wx.service.WxGroupTaskService;
import com.pcitech.iLife.util.Util;

/**
 * 微信群Controller
 * @author ilife
 * @version 2022-06-07
 */
@Controller
@RequestMapping(value = "${adminPath}/wx/wxGroup")
public class WxGroupController extends BaseController {

	@Autowired
	private WxGroupService wxGroupService;
	@Autowired
	private WxGroupTaskService wxGroupTaskService;
	@Autowired
	private BrokerService brokerService;
	
	@ModelAttribute
	public WxGroup get(@RequestParam(required=false) String id) {
		WxGroup entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wxGroupService.get(id);
		}
		if (entity == null){
			entity = new WxGroup();
		}
		return entity;
	}
	
	@RequiresPermissions("wx:wxGroup:view")
	@RequestMapping(value = {"list", ""})
	public String list(WxGroup wxGroup, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<WxGroup> page = wxGroupService.findPage(new Page<WxGroup>(request, response), wxGroup); 
		model.addAttribute("page", page);
		return "modules/wx/wxGroupList";
	}

	@RequiresPermissions("wx:wxGroup:view")
	@RequestMapping(value = "form")
	public String form(WxGroup wxGroup, Model model) {
		model.addAttribute("wxGroup", wxGroup);
		return "modules/wx/wxGroupForm";
	}

	@RequiresPermissions("wx:wxGroup:edit")
	@RequestMapping(value = "save")
	public String save(WxGroup wxGroup, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, wxGroup)){
			return form(wxGroup, model);
		}
		wxGroupService.save(wxGroup);
		addMessage(redirectAttributes, "保存微信群成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxGroup/?repage";
	}
	
	@RequiresPermissions("wx:wxGroup:edit")
	@RequestMapping(value = "delete")
	public String delete(WxGroup wxGroup, RedirectAttributes redirectAttributes) {
		wxGroupService.delete(wxGroup);
		addMessage(redirectAttributes, "删除微信群成功");
		return "redirect:"+Global.getAdminPath()+"/wx/wxGroup/?repage";
	}
	
	/**
	 * 同步微信群。
	 * 如果已经存在，则不做处理。
	 * 如果不存在则建立群，并且建立默认群任务
	 * 参数：
	 * gname: 群名称
	 * gid: 群ID
	 * token: 激活码
	 */
	@ResponseBody
	@RequestMapping(value = "rest/sync", method = RequestMethod.POST)
	public Map<String,Object> updateGroupTask(@RequestBody JSONObject data) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		WxGroup wxGroup = new WxGroup();
		wxGroup.setGid(data.getString("gid"));
		if(wxGroupService.findList(wxGroup).size()>0) {
			result.put("msg", "group exists. skipped.");
			result.put("success", true);
			return result;
		}
		//新建群
		Broker broker = brokerService.get(data.getString("brokerId"));
		String id = Util.md5(data.getString("gid"));
		wxGroup.setId(id);
		wxGroup.setIsNewRecord(true);
		wxGroup.setBroker(broker);
		wxGroup.setName(data.getString("gname"));
		wxGroup.setMembers(data.getInteger("members"));
		wxGroup.setToken(data.getString("token"));
		wxGroup.setStatus("active");
		wxGroup.setCreateDate(new Date());
		wxGroup.setUpdateDate(new Date());
		wxGroupService.save(wxGroup);
		
		//新建群任务：自动推送
		long sendItemMinutes = 30+System.currentTimeMillis()%10; //在30-40分钟一条
		WxGroupTask wxGroupTask = new WxGroupTask();
		wxGroupTask.setBroker(broker);
		wxGroupTask.setWxgroup(wxGroup);
		wxGroupTask.setType("sendItem");
		wxGroupTask.setTags("*");
		wxGroupTask.setStatus("active");
		wxGroupTask.setCron((System.currentTimeMillis()%60)+" "+sendItemMinutes+" 7-22 * * ?");//随机取开始秒数
		wxGroupTask.setName("自动推送商品");
		wxGroupTask.setCreateDate(new Date());
		wxGroupTask.setUpdateDate(new Date());
		wxGroupTaskService.save(wxGroupTask);
		
		//新建群任务：手动推送
		long sendFeatureMinutes = 20+System.currentTimeMillis()%10; //在20-30分钟一条
		wxGroupTask = new WxGroupTask();
		wxGroupTask.setBroker(broker);
		wxGroupTask.setWxgroup(wxGroup);
		wxGroupTask.setType("sendFeature");
		wxGroupTask.setTags("*");
		wxGroupTask.setStatus("active");
		wxGroupTask.setCron((System.currentTimeMillis()%60)+" */"+sendFeatureMinutes+" 7-23 * * ?");//随机取开始秒数
		wxGroupTask.setName("手动推送选品");
		wxGroupTask.setCreateDate(new Date());
		wxGroupTask.setUpdateDate(new Date());
		wxGroupTaskService.save(wxGroupTask);
		
		//获取新建任务，返回前端用于即时启动
		WxGroupTask query = new WxGroupTask();
		query.setWxgroup(wxGroup);
		query.setStatus("active");
		List<WxGroupTask> tasks = wxGroupTaskService.findList(query);
		
		result.put("task", tasks);
		result.put("msg", "group and group task created.");
		result.put("success", true);
		return result;
	}

	/**
	 * 修改微信群信息：根据gid修改topic及群员数量
	 * 参数：
	 * owner:string 群主微信ID（注意不是openid） 当前备用
	 * gid: string 微信群ID
	 * name：string 微信群名称
	 * members：int 成员人数
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "rest/syncByGid", method = RequestMethod.POST)
	public Map<String,Object> changeGroupInfo(@RequestBody JSONObject data) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		WxGroup wxGroup = wxGroupService.findGroupByGid(data.getString("gid"));
		if(wxGroup == null) { //如果不存在则认为是新群，自动建立，但状态为pending
			//新建群
			wxGroup = new WxGroup();
			wxGroup.setIsNewRecord(true);
			//Broker broker = brokerService.get(Global.getConfig("default_parent_broker_id")); //默认为固定达人
			Broker broker = null;
			if(data.getString("brokerId") != null && data.getString("gid").trim().length()>0) {
				broker = brokerService.get(data.getString("brokerId"));
			}
			String id = Util.md5(data.getString("gid"));
			wxGroup.setId(id);
			wxGroup.setBroker(broker);
			wxGroup.setName(data.getString("name"));
			wxGroup.setOwner(data.getString("owner"));
			wxGroup.setMembers(data.getInteger("members"));
			wxGroup.setType("pending");//合作类型为pending，等待确认
			wxGroup.setStatus("pending");//状态为pending，等待确认
			wxGroup.setCreateDate(new Date());
			wxGroup.setUpdateDate(new Date());
			wxGroupService.save(wxGroup);
			
			//新建群任务：自动推送
			long sendItemMinutes = 20+System.currentTimeMillis()%10; //在20-30分钟一条
			WxGroupTask wxGroupTask = new WxGroupTask();
			wxGroupTask.setBroker(broker);
			wxGroupTask.setWxgroup(wxGroup);
			wxGroupTask.setType("sendItem");
			wxGroupTask.setTags("*");
			wxGroupTask.setStatus("pending");//状态为pending
			wxGroupTask.setCron((System.currentTimeMillis()%60)+" */"+sendItemMinutes+" 7-22 * * ?");//随机取开始秒数
			wxGroupTask.setName("自动推送商品");
			wxGroupTask.setCreateDate(new Date());
			wxGroupTask.setUpdateDate(new Date());
			wxGroupTaskService.save(wxGroupTask);
			
			//新建群任务：手动推送
			long sendFeatureMinutes = 20+System.currentTimeMillis()%10; //在20-30分钟一条
			wxGroupTask = new WxGroupTask();
			wxGroupTask.setBroker(broker);
			wxGroupTask.setWxgroup(wxGroup);
			wxGroupTask.setType("sendFeature");
			wxGroupTask.setTags("*");
			wxGroupTask.setStatus("pending");//状态为pending
			wxGroupTask.setCron((System.currentTimeMillis()%60)+" */"+sendFeatureMinutes+" 7-23 * * ?");//随机取开始秒数
			wxGroupTask.setName("手动推送选品");
			wxGroupTask.setCreateDate(new Date());
			wxGroupTask.setUpdateDate(new Date());
			wxGroupTaskService.save(wxGroupTask);
			
			result.put("msg", "group doesnot exist. create new one and default tasks.");
			result.put("data", data);
			result.put("success", true);
		}else {
			wxGroup.setName(data.getString("name"));
			wxGroup.setOwner(data.getString("owner"));
			wxGroup.setMembers(data.getInteger("members"));
			wxGroupService.save(wxGroup);
			result.put("msg", "group info updated.");
			result.put("success", true);
		}
		return result;
	}

	/**
	 * 根据brokerId加载微信群
	 * 如果broker为空，则加载所有活跃状态微信群
	 */
	@ResponseBody
	@RequestMapping(value = "rest/listByBrokerId", method = RequestMethod.GET)
	public List<WxGroup> listGroupByBrokerId(@RequestParam String brokerId) {
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		WxGroup wxGroup = new WxGroup();
		wxGroup.setStatus("active");//仅支持活跃微信群
		if(brokerId!=null&&brokerId.trim().length()>0) {
			Broker broker = brokerService.get(brokerId);
			wxGroup.setBroker(broker);
		}
		//return wxGroupService.findList(wxGroup);
		return wxGroupService.findFeaturedGroup(wxGroup);
	}
	
	/**
	 * 根据brokerId加载微信群
	 * 如果broker为空，则加载所有活跃状态微信群
	 */
	@ResponseBody
	@RequestMapping(value = "rest/listByCurrentUser", method = RequestMethod.GET)
	public List<WxGroup> listByCurrentUser() {
		Map<String,Object> result = Maps.newHashMap();
		result.put("success", false);
		Map<String,String> param = Maps.newHashMap();
		param.put("status", "active");//仅支持活跃微信群
		if(UserUtils.getUser().getCompany().getName().equalsIgnoreCase("ilife") || 
				UserUtils.getUser().getCompany().getName().equalsIgnoreCase("确幸生活")){ //如果是系统运营用户则直接显示所有微信群
			param.put("userId", "");//运营人员查询所有微信群
		}else {
			param.put("userId", UserUtils.getUser().getId());//查询当前用户关联的所有微信群
		}
		return wxGroupService.findFeaturedGroupByUserId(param);
	}
}