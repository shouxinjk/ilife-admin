/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.ope.web;

import java.io.UnsupportedEncodingException;
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
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.ope.entity.Item;
import com.pcitech.iLife.modules.ope.entity.Person;
import com.pcitech.iLife.modules.ope.service.ItemService;
import com.pcitech.iLife.modules.sys.entity.User;
import com.pcitech.iLife.modules.sys.utils.UserUtils;

/**
 * 商品Controller
 * @author chenci
 * @version 2017-09-28
 */
@Controller
@RequestMapping(value = "${adminPath}/ope/item")
public class ItemController extends BaseController {
	@Autowired
	private BrokerService brokerService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	
	@ModelAttribute
	public Item get(@RequestParam(required=false) String id) {
		Item entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = itemService.get(id);
		}
		if (entity == null){
			entity = new Item();
		}
		return entity;
	}
	
	@RequiresPermissions("ope:item:view")
	@RequestMapping(value = {"opeItems"})
	public String listOpeItems(Item item, String treeId,HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Item> page = itemService.findPage(new Page<Item>(request, response), item); 
		model.addAttribute("page", page);
		return "modules/ope/itemList";
	}
	
	@RequiresPermissions("ope:item:view")
	@RequestMapping(value = {"list", ""})
	public String list(Item item, String treeId,HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Item> page = itemService.findPage(new Page<Item>(request, response), item); 
		model.addAttribute("page", page);
//		return "modules/ope/itemList";
		ItemCategory itemCategory = itemCategoryService.get(treeId);
		String categoryName = "";
		if(itemCategory != null) {
			try {
				categoryName = "&classifyName="+java.net.URLEncoder.encode(itemCategory.getName(),"utf-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		//获取当前用户账户，并根据当前用户账户获取对应的达人
		String brokerId = "";
		User currentUser = UserUtils.getUser();
		Broker broker = brokerService.getBySysUserid(currentUser.getId());
		if(broker!=null) {
			brokerId = "&brokerId="+broker.getId();
		}
		return "redirect:http://www.shouxinjk.net/list-admin/index.html?classify="+treeId+categoryName+brokerId;//直接跳转到商品标注界面，需要保留header显示，提供搜索及排序功能
	}
	
	@RequiresPermissions("ope:item:view")
	@RequestMapping(value = {"list2"})
	public String listPendingItemsForGuideTerm(String termId,HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Item> items = itemService.findPendingListForGuideTerm(termId);
		model.addAttribute("items", items);
		model.addAttribute("termId", termId);
		return "modules/ope/itemList2";
	}

	@RequiresPermissions("ope:item:view")
	@RequestMapping(value = "form")
	public String form(Item item, Model model) {
		model.addAttribute("item", item);
		return "modules/ope/itemForm";
	}

	@RequiresPermissions("ope:item:edit")
	@RequestMapping(value = "save")
	public String save(Item item, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, item)){
			return form(item, model);
		}
		itemService.save(item);
		addMessage(redirectAttributes, "保存商品成功");
		return "redirect:"+Global.getAdminPath()+"/ope/item/opeItems?repage";
	}
	
	@RequiresPermissions("ope:item:edit")
	@RequestMapping(value = "delete")
	public String delete(Item item, RedirectAttributes redirectAttributes) {
		itemService.delete(item);
		addMessage(redirectAttributes, "删除商品成功");
		return "redirect:"+Global.getAdminPath()+"/ope/item/opeItems?repage";
	}

	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Map<String, Object>> listData(Item item, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Item> list =itemService.findList(item);
		for (int i=0; i<list.size(); i++){
			Item e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "0");
			map.put("pIds", "0");
			map.put("name", e.getName());
//			if (type != null && "3".equals(type)){
//				map.put("isParent", true);
//			}
			mapList.add(map);
			
		}
		return mapList;
	}
	
	@RequiresPermissions("ope:item:view")
	@RequestMapping(value = "index")
	public String index(Model model) {
		//获取当前用户账户，并根据当前用户账户获取对应的达人
		User currentUser = UserUtils.getUser();
		Broker broker = brokerService.getBySysUserid(currentUser.getId());
		if(broker!=null)
			model.addAttribute("brokerId",broker.getId());
		model.addAttribute("url","ope/item");
		model.addAttribute("title","商品");
		return "treeData/index";
	}
	
	@RequiresPermissions("ope:item:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("url","ope/item");
		model.addAttribute("title","商品类型");
		List<ItemCategory> itemCategoryTree = itemCategoryService.findTree();
		model.addAttribute("list", itemCategoryTree);
		return "treeData/tree";
	}
	
	@RequiresPermissions("ope:item:view")
	@RequestMapping(value = "none")
	public String none(Model model) {
		model.addAttribute("message","请在左侧选择一个类型。");
		//return "treeData/none";
		//获取当前用户账户，并根据当前用户账户获取对应的达人
		String brokerId = "";
		User currentUser = UserUtils.getUser();
		if(currentUser.getCompany().getName().equalsIgnoreCase("ilife") || currentUser.getCompany().getName().equalsIgnoreCase("确幸生活")){ //如果是系统运营用户则直接显示所有微信群
			brokerId = "";
		}else {
			Broker broker = brokerService.getBySysUserid(currentUser.getId());
			if(broker!=null) {
				brokerId = "&brokerId="+broker.getId();
			}
		}
		return "redirect:http://www.shouxinjk.net/list-admin/index.html?classify=all&classifyName=-&showAllItems=true"+brokerId;//默认显示首页
	}
}