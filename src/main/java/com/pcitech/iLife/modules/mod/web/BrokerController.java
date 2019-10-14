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
import com.pcitech.iLife.modules.mod.entity.ItemDimension;
import com.pcitech.iLife.modules.mod.service.BrokerService;

/**
 * 达人管理Controller
 * @author qchzhu
 * @version 2019-10-14
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/broker")
public class BrokerController extends BaseController {

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

}