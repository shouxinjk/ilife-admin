/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.arangodb.entity.BaseDocument;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.CategoryNeed;
import com.pcitech.iLife.modules.mod.entity.Channel;
import com.pcitech.iLife.modules.mod.entity.ChannelNeed;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.Motivation;
import com.pcitech.iLife.modules.mod.entity.Phase;
import com.pcitech.iLife.modules.mod.service.CategoryNeedService;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.MeasureService;
import com.pcitech.iLife.modules.mod.service.MotivationService;
import com.pcitech.iLife.modules.mod.service.OccasionService;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.util.ArangoDbClient;

/**
 * 商品分类Controller
 * @author chenci
 * @version 2017-09-22
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/itemCategory")
public class ItemCategoryController extends BaseController {

	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private CategoryNeedService categoryNeedService;
	@Autowired
	private MotivationService motivationService;
	@Autowired
	private OccasionService occasionService;
	@Autowired
	private MeasureService measureService;
	@Autowired
	private DictService dictService;
	
	@ModelAttribute
	public ItemCategory get(@RequestParam(required=false) String id) {
		ItemCategory entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = itemCategoryService.get(id);
		}
		if (entity == null){
			entity = new ItemCategory();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:itemCategory:view")
	@RequestMapping(value = {"list", ""})
	public String list(ItemCategory itemCategory, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<ItemCategory> list = Lists.newArrayList();
		List<ItemCategory> sourcelist = itemCategoryService.findTree();
		ItemCategory.sortList(list, sourcelist, "1",true);
		for(ItemCategory category:list){
			category.setMotivationNames(motivationService.getMotivationNames(category.getMotivationIds()));
			category.setOccasionNames(occasionService.getOccasionNames(category.getOccasionIds()));
		}
        model.addAttribute("list", list);
		return "modules/mod/itemCategoryList";
	}

	@RequiresPermissions("mod:itemCategory:view")
	@RequestMapping(value = "form")
	public String form(ItemCategory itemCategory, Model model) {
		if (itemCategory.getParent()==null||itemCategory.getParent().getId()==null){
			itemCategory.setParent(new ItemCategory("1"));
		}
		ItemCategory parent = itemCategoryService.get(itemCategory.getParent().getId());
		itemCategory.setParent(parent);
		String names=motivationService.getMotivationNames(itemCategory.getMotivationIds());
		itemCategory.setMotivationNames(names);
		itemCategory.setOccasionNames(occasionService.getOccasionNames(itemCategory.getOccasionIds()));
		// 获取排序号，最末节点排序号+30
		if (StringUtils.isBlank(itemCategory.getId())){
			List<ItemCategory> list = Lists.newArrayList();
			List<ItemCategory> sourcelist = itemCategoryService.findTree();
			ItemCategory.sortList(list, sourcelist, itemCategory.getParentId(), false);
			if (list.size() > 0){
				itemCategory.setSort(list.get(list.size()-1).getSort() + 30);
			}
		}
		model.addAttribute("itemCategory", itemCategory);
		return "modules/mod/itemCategoryForm";
	}

	@RequiresPermissions("mod:itemCategory:edit")
	@RequestMapping(value = "save")
	public String save(ItemCategory itemCategory, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, itemCategory)){
			return form(itemCategory, model);
		}
		itemCategoryService.save(itemCategory);
		addMessage(redirectAttributes, "保存商品分类成功");
		return "redirect:"+Global.getAdminPath()+"/mod/itemCategory/?repage";
	}
	
	@RequiresPermissions("mod:itemCategory:edit")
	@RequestMapping(value = "delete")
	public String delete(ItemCategory itemCategory, RedirectAttributes redirectAttributes) {
		itemCategoryService.delete(itemCategory);
		addMessage(redirectAttributes, "删除商品分类成功");
		return "redirect:"+Global.getAdminPath()+"/mod/itemCategory/?repage";
	}
	
	/**
	 * 批量修改排序
	 */
	@RequiresPermissions("mod:itemCategory:edit")
	@RequestMapping(value = "updateSort")
	public String updateSort(String[] ids, Integer[] sorts, RedirectAttributes redirectAttributes) {
    	int len = ids.length;
    	ItemCategory[] entitys = new ItemCategory[len];
    	for (int i = 0; i < len; i++) {
    		entitys[i] = itemCategoryService.get(ids[i]);
    		entitys[i].setSort(sorts[i]);
    		itemCategoryService.save(entitys[i]);
    	}
    	addMessage(redirectAttributes, "保存排序成功!");
		return "redirect:" + adminPath + "/mod/itemCategory/";
	}

	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String module, @RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<ItemCategory> list = itemCategoryService.findTree();
		for (int i=0; i<list.size(); i++){
			ItemCategory e = list.get(i);
			if (extId == null || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParent()!=null?e.getParent().getId():0);
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}
	
	@ResponseBody
	@RequestMapping(value = "categories")
	public List<Map<String, Object>> listCategoryByParentId( @RequestParam(required=false) String parentId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		//List<ItemCategory> list = itemCategoryService.findTree();
		
		if(parentId==null || parentId.trim().length()==0) parentId = "1";//默认查询根目录下的节点
		
		//注意：受限于jstree，本方法返回结果均根据jstree数据要求定制
		List<ItemCategory> list = itemCategoryService.findByParentId(parentId);
		for (int i=0; i<list.size(); i++){
			ItemCategory e = list.get(i);
			//if ( e.getParentId()==parentId){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId()=="1"?"#":e.getId());
				map.put("pId", (e.getParent()==null||e.getParentId()=="1")?"#":e.getParent().getId());
				map.put("text", e.getName());
				map.put("children", true);
				mapList.add(map);
			//}
		}
		return mapList;
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/categories")
	public List<ItemCategory> listCategoryByParentId( @RequestParam(required=false) String parentId) {
		if(parentId==null || parentId.trim().length()==0) parentId = "1";//默认查询根目录下的节点
		return itemCategoryService.findByParentId(parentId);
	}

	/**
	 * 加载系统默认分类。是标准分类。
	 * @param id 父目录ID。为空时返回顶级目录
	 * @param response 返回节点列表，格式如下：
{
	"value": "Bestsellers",
	"id": "Bestsellers",
	"parent": "Thrillers",
	"opened": false,
	"icon": {
		"folder": "fas fa-book",
		"openFolder": "fas fa-book-open",
		"file": "fas fa-file"
	},
	"items": true
}
	 * @return
	 */
	//仅包含分类
	@ResponseBody
	@RequestMapping(value = "standard-categories")
	public List<Map<String, Object>> listStandardCategoriesByParentId( @RequestParam(required=false) String id, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		return listStandardCategoriesAndPropsByParentId(id,false);
	}
	
	//包含分类以及属性
	@ResponseBody
	@RequestMapping(value = "standard-properties")
	public List<Map<String, Object>> listStandardProperetiesByParentId( @RequestParam(required=false) String id, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		return listStandardCategoriesAndPropsByParentId(id,true);
	}
	
	//查询标准类目及属性。
	private List<Map<String, Object>> listStandardCategoriesAndPropsByParentId(String id,boolean withProps) {
		Map<String,String> icon = Maps.newHashMap();
		icon.put("folder","fas fa-book");
		icon.put("openFolder","fas fa-book-open");
		icon.put("file","fas fa-file");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		String categoryId = id;//默认id接受前端传递的id值，但对于根节点，前端传递的是div的id，固定为 tree-source ，需要进行映射
		if(id==null || id.trim().length()==0 || "tree-source".equalsIgnoreCase(id)) 
			categoryId = "0";//默认查询根目录下的节点 

		//加载子分类
		List<ItemCategory> list = itemCategoryService.findByParentId(categoryId);
		for (int i=0; i<list.size(); i++){
			ItemCategory e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
//			if(!"-1".equalsIgnoreCase(id))//根节点不能加
			map.put("parent", id);
			map.put("value", e.getName());
			map.put("id", e.getId());
			map.put("opened", false);
			map.put("items", true);//默认都认为有下级目录
//			map.put("icon", icon);//设置图标
			mapList.add(map);
		}
		
		//添加属性
		if(withProps) {
			List<Measure> list2 =measureService.findByCategory(id);
			for (int i=0; i<list2.size(); i++){
				Measure e = list2.get(i);
				Map<String, Object> map = Maps.newHashMap();
				map.put("parent",  id);
				map.put("value", e.getName());
				map.put("id", "prop-"+e.getId());//属性条目通过 prop- 前缀进行区分
				map.put("opened", true);
				map.put("items", false);
				map.put("icon", icon);//设置图标
				mapList.add(map);
				
			}			
		}
		
		return mapList;
	}
	


	/**
	 * 加载系统默认分类。是标准分类。
	 * @param id 父目录ID。为空时返回顶级目录
	 * @param response 返回节点列表，格式如下：
{
	"value": "Bestsellers",
	"id": "Bestsellers",
	"parent": "Thrillers",
	"opened": false,
	"icon": {
		"folder": "fas fa-book",
		"openFolder": "fas fa-book-open",
		"file": "fas fa-file"
	},
	"items": true
}
	 * @return
	 */
	//仅返回目录
	@ResponseBody
	@RequestMapping(value = "third-party-categories/{source}")
	public List<Map<String, Object>> listPlatformCategoriesByParentId( @PathVariable String source,@RequestParam(required=false) String id, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		return  listPlatformCategoriesAndPropsByParentId(source,id,false);
	}
	
	//返回目录及属性
	@ResponseBody
	@RequestMapping(value = "third-party-properties/{source}")
	public List<Map<String, Object>> listPlatformPropertiesByParentId( @PathVariable String source,@RequestParam(required=false) String id, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		return  listPlatformCategoriesAndPropsByParentId(source,id,true);
	}	
	
	/**
	 * @deprecated 已废弃，当前通过业务系统直接管理
	 * @param source
	 * @param id
	 * @param withProps
	 * @return
	 */
	private List<Map<String, Object>> listPlatformCategoriesAndPropsByParentId( String source,String id,boolean withProps) {
		Map<String,String> icon = Maps.newHashMap();
		icon.put("folder","fas fa-book");
		icon.put("openFolder","fas fa-book-open");
		icon.put("file","fas fa-file");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		String categoryId = id;//默认id接受前端传递的id值，但对于根节点，前端传递的是div的id，固定为 tree-source ，需要进行映射
		if(id==null || id.trim().length()==0 || "tree-target".equalsIgnoreCase(id)) 
			categoryId = "0";//默认查询根目录下的节点:注意，需要将所有根目录节点均设为0
		
		ArangoDbClient arangoClient = new ArangoDbClient();
		
		//查询获得当前目录的名称，用于查询无cid的属性
		String categoryName = "";
		if(withProps) {
			String query = "for doc in platform_categories " + 
					"    filter doc.source==\""+source+"\" " + 
					"    and doc.id== \""+categoryId+"\" " + 
					"    return " + 
					"    {" + 
					"        name:doc.name" + 
					"    }";
	        logger.error("try to query 3rd-party category.[query]"+query);
	        try {
	            List<BaseDocument> items = arangoClient.query(query, null, BaseDocument.class);
	            for (BaseDocument item:items) {
	            		Map<String,Object> props = item.getProperties();
	            		categoryName = props.get("name").toString();
	            		break;
	            }
	        }catch(Exception ex) {
	        	logger.debug("failed query parent category.[id]"+categoryId);
	        }
		}

		String query = "for doc in platform_categories " + 
				"    filter doc.source==\""+source+"\" " + 
				"    and doc.pid== \""+categoryId+"\" " + 
				"    sort doc.id " + 
				"    return " + 
				"    {" + 
				"        itemKey:doc._key,mappingName:doc.mappingName,mappingId:doc.mappingId," + 
				"        parent:doc.pid==\"0\"?\"tree-target\":doc.pid," + 
				"        opened:false," + 
				"        id:doc.id," + 
				"        value:doc.mappingName==null?doc.name:concat(doc.name,\"-->\",doc.mappingName)," + 
				"        items:true" + 
				"    }";
        logger.error("try to query 3rd-party categories.[query]"+query);
        try {
            List<BaseDocument> items = arangoClient.query(query, null, BaseDocument.class);
            for (BaseDocument item:items) {
            		mapList.add(item.getProperties());
            }
            
            //添加属性
            if(withProps) {
		    		String propsQuery = "for doc in platform_properties " + 
		    				"    filter doc.source==\""+source+"\" " + 
		    				"    and (doc.cid== \""+categoryId+"\" or doc.category == \""+categoryName+"\") " + 
		    				"    return " + 
		    				"    {" + 
		    				"        itemKey:doc._key,mappingName:doc.mappingName,mappingId:doc.mappingId," + 
//		    				"        parent:doc.pid==\""+id+"\"," + 
//		    				"        opened:true," + 
//		    				"        items:true," + 
							"        id:doc._key," + 
							"        value:doc.mappingName==null?doc.name:concat(doc.name,\"-->\",doc.mappingName)" + 
		    				"    }";
		            logger.error("try to query 3rd-party properties.[query]"+propsQuery);
		            List<BaseDocument> propItems = arangoClient.query(propsQuery, null, BaseDocument.class);
		            for (BaseDocument propItem:propItems) {
		            		Map<String,Object> prop = propItem.getProperties();
		            		prop.put("parent", id);
		            		prop.put("opened", true);
		            		prop.put("items", false);
		            		prop.put("id", "prop-"+prop.get("id"));//属性条目通过 prop- 前缀进行区分
		            		prop.put("icon", icon);//设置图标
		            		mapList.add(prop);
		            }
            }
        } catch (Exception e) {
            logger.error("Failed to execute query.",e);
        } finally {
	        	//完成后关闭arangoDbClient
	    		arangoClient.close();
        }		
		return mapList;
	}
	
	@ResponseBody
	@RequestMapping(value = "third-party-platforms")
	public List<Map<String, Object>> listPlatforms(HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		//加载电商平台信息，此处手动加载。TODO:后续需要修改为从数据库加载
		Dict dict = new Dict();
		dict.setType("platform");
		List<Dict> platforms = dictService.findList(dict);
		for(Dict platform:platforms) {
			Map<String, Object> item = Maps.newHashMap();
			item.put("id", platform.getValue());
			item.put("name", platform.getLabel());
			item.put("root", "0");//默认根目录都为0
			mapList.add(item);
		}
		/**
		String[] sourceKeyArray = {"pdd","jd","tmall","taobao","kaola","fliggy","ctrip","gome","suning","dangdang","dhc","amazon"};//source list
		String[] sourceNameArray = {"拼多多","京东","天猫","淘宝","考拉","飞猪","携程","国美","苏宁","当当网","DHC","Amazon"};//source list
		String[] sourceRootArray = {"0","0","0","0","0","0","0","0","0","0","0","0"};//root node of different sources
		for(int i=0;i<sourceKeyArray.length;i++) {
			Map<String, Object> item = Maps.newHashMap();
			item.put("id", sourceKeyArray[i]);
			item.put("name", sourceNameArray[i]);
			item.put("root", sourceRootArray[i]);
			mapList.add(item);
		}
		//**/
		return mapList;
	}
	
	/**
	 * 在商品详情界面展示分类，用于商品信息标注
	 * @param parentId
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "categoriesAndMeasures")
	public List<Map<String, Object>> listCategoryAndMeasureByParentId( @RequestParam(required=false) String parentId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		if(parentId==null || parentId.trim().length()==0) parentId = "1";//默认查询根目录下的节点
		
		//注意：受限于jstree，本方法返回结果均根据jstree数据要求定制
		
		//加载子分类
		List<ItemCategory> list = itemCategoryService.findByParentId(parentId);
		for (int i=0; i<list.size(); i++){
			ItemCategory e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("icon", "ext/jstree/images/tree_icon.png");
			map.put("id", e.getId()=="1"?"#":e.getId());
			map.put("pId", (e.getParent()==null||e.getParentId()=="1")?"#":e.getParent().getId());
			map.put("text", e.getName());
			map.put("children", true);
			map.put("type", "category");
			mapList.add(map);
		}
		
		//加载所属关键属性
		List<Measure> list2 =measureService.findByCategory(parentId);
		for (int i=0; i<list2.size(); i++){
			Measure e = list2.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("icon",  "ext/jstree/images/leaf-16.png");
			map.put("id", e.getId());
			map.put("pId", e.getCategory().getId());
			map.put("text", e.getName()+":"+e.getProperty());
			map.put("type", "measure");
			map.put("name", e.getName());
			map.put("property", e.getProperty());
			mapList.add(map);
			
		}		
		return mapList;
	}
	
	
	/**
	 * 一次性加载全部类目信息，用于商品标注。
	 * 类目采用三级结构。返回数据结构为
	 * [
	 * {id:1,label:"1"},
	 * {id:1,label:"2"},
	 * {id:1,label:"3",children:[
	 * 		{id:1,label:"31"},
	 * 		{id:1,label:"32"}
	 * 	]}
	 * ]
	 * @param parentId
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "all-categories")
	public List<Map<String, Object>> listAllCategories( @RequestParam(required=false) String parentId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		if(parentId==null || parentId.trim().length()==0) parentId = "1";//默认查询根目录下的节点
		return listAllCategoriesByParentId(parentId);
	}
	private List<Map<String, Object>> listAllCategoriesByParentId(String parentId) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		//加载子分类
		List<ItemCategory> list = itemCategoryService.findByParentId(parentId);
		for (int i=0; i<list.size(); i++){
			ItemCategory item = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", item.getId());
			map.put("label", item.getName());
			//递归获取下级节点
			List<Map<String, Object>> childs = listAllCategoriesByParentId(item.getId());
			if(childs.size()>0)
				map.put("children", childs);
			mapList.add(map);
		}	
		return mapList;
	}
	
	@ResponseBody
	@RequestMapping(value = "rest/category/{id}")
	public ItemCategory getCategoryById(@PathVariable String id) {
		ItemCategory entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = itemCategoryService.get(id);
		}
		if (entity == null){
			entity = new ItemCategory();
		}
		return entity;
	}
	
	//查询类目下的需要构成列表
	@ResponseBody
	@RequestMapping(value = "rest/needs/{categoryId}", method = RequestMethod.GET)
	public List<CategoryNeed> getNeedsByCategoryId(@PathVariable String categoryId) {
		ItemCategory itemCategory = new ItemCategory();
		itemCategory.setId(categoryId);
		CategoryNeed categoryNeed = new CategoryNeed();
		categoryNeed.setCategory(itemCategory);
		return categoryNeedService.findList(categoryNeed);
	}
	
	//查询所有类目
	@ResponseBody
	@RequestMapping(value = "rest/all-categories", method = RequestMethod.GET)
	public List<ItemCategory> listAllCategories( ) {
		ItemCategory q = new ItemCategory();
		return itemCategoryService.findList(q);//直接返回全部
	}
	
	//查询所有建立有排行榜的
	@ResponseBody
	@RequestMapping(value = "rest/rank-categories", method = RequestMethod.GET)
	public List<ItemCategory> listCategoriesHasRank( ) {
		return itemCategoryService.findListWithRank();//直接返回全部
	}
	
	//查询所有建立有推荐语的
	@ResponseBody
	@RequestMapping(value = "rest/advice-categories", method = RequestMethod.GET)
	public List<ItemCategory> listCategoriesHasAdvice( ) {
		return itemCategoryService.findListWithAdvice();//直接返回全部
	}
}

