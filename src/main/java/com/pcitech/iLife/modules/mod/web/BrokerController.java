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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.service.BrokerService;

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
	public Map<String, Object> getBrokerByOpenid(@PathVariable String openid, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = Maps.newHashMap();
		Broker broker = brokerService.getByOpenid(openid);//根据openid获取达人
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
	public List<Broker> listBrokersByOpenid(@PathVariable String openid, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Broker> mapList = Lists.newArrayList();
		Broker parent = brokerService.getByOpenid(openid);//根据openid获取父级达人
		if(parent == null)//如果未找到对应的达人直接返回空
			return mapList;
		Broker broker = new Broker();
		broker.setParent(parent);
		List<Broker> list =brokerService.findList(broker);
		return list;
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
			brokerService.save(broker);
			result.put("status",true);
			result.put("description","Broker created successfully");
			Broker newbroker = brokerService.get(broker);
			result.put("data", newbroker);
		}
		return result;
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
}