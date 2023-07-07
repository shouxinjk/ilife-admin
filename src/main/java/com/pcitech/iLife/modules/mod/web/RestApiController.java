/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Board;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.entity.ProfitShareScheme;
import com.pcitech.iLife.modules.mod.entity.ViewTemplate;
import com.pcitech.iLife.modules.mod.service.BoardItemService;
import com.pcitech.iLife.modules.mod.service.BoardService;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.MeasureService;
import com.pcitech.iLife.modules.mod.service.MotivationService;
import com.pcitech.iLife.modules.mod.service.OccasionService;
import com.pcitech.iLife.modules.mod.service.ViewTemplateService;
import com.pcitech.iLife.modules.ope.entity.Performance;
import com.pcitech.iLife.modules.ope.service.PerformanceService;
import com.pcitech.iLife.modules.sys.entity.Dict;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.util.AIHelper;
import com.pcitech.iLife.util.FastDFSUtils;
import com.pcitech.iLife.util.HttpClientHelper;

import freemarker.template.Configuration;
import freemarker.template.Template;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * Restful API Controller
 * @author qchzhu
 * @version 2019-10-14
 */
@Controller
@RequestMapping(value = "${adminPath}/rest/api")
public class RestApiController extends BaseController {

	@Autowired DictService dictService;
	@Autowired
	private ViewTemplateService viewTemplateService;
	@Autowired
	private BoardService boardService;
	@Autowired
	private BoardItemService boardItemService;	
	@Autowired
	private BrokerService brokerService;
	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private MeasureService measureService;
	@Autowired
	private PerformanceService performanceService;
	@Autowired
	private OccasionService occasionService;
	@Autowired
	private MotivationService motivationService;
	//获取所有board
	@ResponseBody
	@RequestMapping(value = "boards")
	public List<Map<String, Object>> listBoards(Board board, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Board> list =boardService.findList(board);
		for (int i=0; i<list.size(); i++){
			Board e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("name", e.getTitle());
			map.put("description", e.getDescription());
			mapList.add(map);
		}
		return mapList;
	}	
	
	//从字典中获取accessToken，而不采用配置文件中的值。
	//注意：需要运营支持，每月更新accessToken值
	@ResponseBody
	@RequestMapping(value = "access-token/{type}", method = RequestMethod.GET)
	public JSONObject getAccessToken(@PathVariable String type) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		Dict dict = new Dict();
		dict.setType("access_token");	
		dict.setValue(type);
		List<Dict> dicts = dictService.findList(dict);
		if(dicts.size()>0) {
			result.put("success", true);
			result.put("token", dicts.get(0).getLabel());
		}else if(Global.getConfig(type+".accessToken")!=null && Global.getConfig(type+".accessToken").trim().length()>0) {
			result.put("success", true);
			result.put("token", Global.getConfig(type+".accessToken"));
			result.put("msg", "use access token form config file. please check validation.");
		}else {
			result.put("msg", "no access token found. please check.");
		}
		return result;//直接返回第一个即可
	}
	
	/**
	 * 根据id获取下级达人列表
	 */
	@ResponseBody
	@RequestMapping(value = "brokers/{id}", method = RequestMethod.GET)
	public List<Map<String, Object>> listBrokers(@PathVariable String id, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		Broker parent = new Broker();
		parent.setId(id);
		Broker broker = new Broker();
		broker.setParent(parent);
		List<Broker> list =brokerService.findList(broker);
		for (int i=0; i<list.size(); i++){
			Broker e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("name", e.getName());
			map.put("phone", e.getPhone());
			map.put("level", e.getLevel());
			map.put("upgrade", e.getUpgrade());
			mapList.add(map);
		}
		return mapList;
	}
	
	/**
	 * 给指定ID的达人添加下级达人
	 */
	@ResponseBody
	@RequestMapping(value = "broker/{id}", method = RequestMethod.POST)
	public Map<String, Object> registerBroker(@PathVariable String id,@RequestBody Broker broker, HttpServletRequest request, HttpServletResponse response, Model model) {
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
		}
		
		return result;
	}
	
	/**
	 * 修改达人：包括修改单个属性如“升级状态”
	 */
	@ResponseBody
	@RequestMapping(value = "broker/{id}", method = RequestMethod.PATCH)
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
	 * 新建或更新itemCategory
	 * 输入Map：id,pid,name
	 */
	@ResponseBody
	@RequestMapping(value = "item-category", method = RequestMethod.POST)
	public Map<String, Object> upsaveItemCategory(@RequestBody Map<String,String> params, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = Maps.newHashMap();
		ItemCategory itemCategory = null;
		if(params.get("id")!=null&&params.get("id").trim().length()>0) {//如果带有ID则查询更新
			itemCategory = itemCategoryService.get(params.get("id"));
			if(itemCategory == null) {
				itemCategory = new ItemCategory(params.get("id"));//如果没有对应ID的则新建
				itemCategory.setIsNewRecord(true);
				itemCategory.setDelFlag("0");
			}
		}
		itemCategory.setName(params.get("name"));
		if(params.get("pid")!=null&&params.get("pid").trim().length()>0) {//查询父目录ID
			ItemCategory parentCategory = itemCategoryService.get(params.get("pid"));
			if(parentCategory != null)itemCategory.setParent(parentCategory);
		}
		
		itemCategoryService.save(itemCategory);
		result.put("status",true);
		result.put("description","category created successfully");
		return result;
	}
	
	/**
	 * 新建或更新属性Meausre
	 * 输入Map：id,cid,name
	 */
	@ResponseBody
	@RequestMapping(value = "item-measure", method = RequestMethod.POST)
	public Map<String, Object> upsaveMeasure(@RequestBody Map<String,String> params, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = Maps.newHashMap();
		Measure measure = null;
		String id = params.get("id");
		if(id!=null&&id.trim().length()>0) {//如果带有ID则查询更新
			measure = measureService.get(id);
			if(measure == null) {
				measure = new Measure(id);//如果没有对应ID的则新建
				measure.setIsNewRecord(true);
				measure.setDelFlag("0");
			}
		}
		measure.setName(params.get("name"));
		if(params.get("cid")!=null&&params.get("cid").trim().length()>0) {//查询所属品类
			ItemCategory parentCategory = itemCategoryService.get(params.get("cid"));
			if(parentCategory != null)
				measure.setCategory(parentCategory);
			else
				result.put("warn","no category found.");
		}
		
		measureService.save(measure);
		result.put("status",true);
		result.put("description","measure created successfully");
		return result;
	}
	
	/**
	 * 新建或更新属性值 performance
	 * 输入Map：id,value,pid
	 */
	@ResponseBody
	@RequestMapping(value = "item-performance", method = RequestMethod.POST)
	public Map<String, Object> upsavePerformance(@RequestBody Map<String,String> params, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = Maps.newHashMap();
		Performance performance = null;
		String id = params.get("id");
		if(id!=null&&id.trim().length()>0) {//如果带有ID则查询更新
			performance = performanceService.get(id);
			if(performance == null) {
				performance = new Performance(id);//如果没有对应ID的则新建
				performance.setIsNewRecord(true);
				performance.setDelFlag("0");
			}
		}
		performance.setOriginalValue(params.get("value"));
		if(params.get("pid")!=null&&params.get("pid").trim().length()>0) {//查询所属品类
			Measure measure = measureService.get(params.get("pid"));
			if(measure != null)
				performance.setMeasure(measure);
			else
				result.put("warn","no measure found.");
		}
		
		performanceService.save(performance);
		result.put("status",true);
		result.put("description","performance created successfully");
		return result;
	}
	
	/**
	 * 接收occasionId并且执行相应的通知任务，包括公众号推送、企业朋友圈、企业群推送等
	 * @param occasionId
	 * @param params
	 * @return
	 * @throws WxErrorException
	 * @throws IOException
	 */
	@RequestMapping(value = "/trigger/occasion/{occasionId}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> trigOccasion(@PathVariable String occasionId, @RequestBody JSONObject json) throws WxErrorException, IOException {
		Map<String, Object> result = Maps.newHashMap();
		result.put("status", false);
		logger.debug("try to send occasion notification message.[occasionId]"+occasionId+"[json]"+JSONObject.toJSONString(json));
		//得到occasion定义
		Occasion occasion = occasionService.get(occasionId);
		if(occasion == null) {
			result.put("msg", "cannot find occasion by id.[id]"+occasionId);
			return result;
		}
		
		//根据occasion的事件定义逐个处理：使用JSONObject获取对象
		String actionsStr = occasion.getTriggerActions();
		JSONArray actions = JSONObject.parseArray(org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(actionsStr));//需要unescape
		if(actions == null || actions.size()==0) {
			result.put("msg", "no actions found.[id]"+occasionId);
			return result;
		}
		logger.debug("got occassion actions.[json]"+JSONObject.toJSONString(json));
		//逐个处理
		result.put("totalActions", actions.size());
		Map<String,Object> actionMap = Maps.newHashMap();
		int processedActions = 0;
		for(int i=0;i<actions.size();i++) {
			JSONObject action = actions.getJSONObject(i);
			JSONObject ret = new JSONObject();
			switch(action.getString("type")){
			case "notify-cp-company-broker"://企业微信达人群
			case "notify-cp-broker-customer"://企业微信达人客户群
			case "notify-cp-broker-moment"://企业微信达人朋友圈
				HttpClientHelper.getInstance().post(
						Global.getConfig("wework.templateMessenge")+"/"+action.getString("type"), 
						action.getJSONObject("content"),null);//由企业微信处理。包括查询条目等
				result.put("status", true);
				result.put("msg", "send cp message");
				processedActions++;
				actionMap.put(action.getString("type"),true);
				break;
			case "notify-mp-company"://公众号面向所有
			case "notify-mp-broker"://公众号面向达人
			case "notify-mp-customer"://公众号面非达人用户
				HttpClientHelper.getInstance().post(
						Global.getConfig("wechat.templateMessenge")+"/"+action.getString("type"), 
						action.getJSONObject("content"),null);//由公众号处理，包括查询条目等
				result.put("status", true);
				result.put("msg", "send mp message");
				processedActions++;
				actionMap.put(action.getString("type"),true);
				break;
			case "boost-needs"://调整need权重
				//TODO : 需要匹配用户，并调整其need权重。或者写入分析库，由分析系统完成调整
				result.put("status", true);
				result.put("msg", "boost needs weight.");
				processedActions++;
				actionMap.put(action.getString("type"),true);
				break;
			default:
				//do nothing
				actionMap.put(action.getString("type"),false);
				result.put("status", false);
				result.put("msg", "unkonw action type.");
			}
		}
		result.put("processedActions", processedActions);
		result.put("processedMap", actionMap);
		return result;
	}
	
	/**
	 * 接收文件上传并保存到FastDFS
	 */
	@ResponseBody
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public Map<String, Object> saveFile(@RequestParam( value="files",required=false)MultipartFile file, 
			@RequestParam( value="fileId",required=false)String fileId, 
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = Maps.newHashMap();
		result.put("status",false);
		long size= file.getSize();//这里可以控制大小，当前不做处理
		try {
			FastDFSUtils util = FastDFSUtils.getInstance();
			//先保存
			String[] dfsFile = util.upload(file.getBytes(), file.getOriginalFilename(), file.getSize());
			result.put("group",dfsFile[0]);
			result.put("path",dfsFile[1]);
			result.put("fullpath",dfsFile[0]+"/"+dfsFile[1]);
			result.put("status",true);
			result.put("description","upload and saved successfully");
			//如果有fileId则删除历史文件
			if(fileId!=null && fileId.trim().length()>0) {
				logger.debug("try to delete old file.[fileId]"+fileId);
				//传递进入的类似于：1/M00/00/00/o4YBAGHNFk6AZyMuAC9Tzr0lVvE880.png
				//其中：第一个/前的部分为groupId。需要分解
				int index = fileId.indexOf("/");//使用第一个/分解
				String group = "group"+fileId.substring(0,index);
				String path = fileId.substring(index+1);
				logger.debug("try to remove old file.[group]"+group+"\t[path]"+path+"[first slash index]"+index);
				util.delete(group, path);
				result.put("description","upload/save/delete successfully");
			}
			util.close();
		}catch(Exception ex) {
			result.put("status",false);
			result.put("description","upload/save/delete error.[err]"+ex.getMessage());
			logger.error("save/delete file error.",ex);
		}
		return result;
	}
	

	/**
	 * 生成HTML物料
	 * 传入参数：
	 * board:{
	 * templateId：模板ID
	 * board：board信息
	 * items：boardItem列表。是array
	 * }
	 * 
	 * item:{
	 * templateId：模板ID
	 * item: item信息
	 * }
	 */
	@ResponseBody
	@RequestMapping(value = "material-html", method = RequestMethod.POST)
	public Map<String, Object> genHtmlArticle(@RequestBody JSONObject json, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = Maps.newHashMap();
		logger.debug("start generate html.[json]"+json.toJSONString());

		//获取对应的viewTemplate，并从字符串加载模板
		ViewTemplate viewTemplate = viewTemplateService.get(json.getString("templateId"));
		try {
			Configuration cfg = new Configuration();
			cfg.setDefaultEncoding(Charset.forName("UTF-8").name());
			String expr = org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(viewTemplate.getExpression());
	        Template t = new Template(viewTemplate.getId(), new StringReader(expr),cfg);
	        //生成html并直接返回
	        StringWriter writer = new StringWriter();
	        t.process(json, writer);
	        result.put("html", writer.getBuffer().toString());
			result.put("status",true);
			result.put("description","html generated successfully");
	        writer.close();
		}catch(Exception ex) {
			result.put("status",false);
			result.put("description","failed generate html.[err]"+ex.getMessage());
			logger.error("generate html error.",ex);
		}
		return result;
	}
	
	/**
	 * 生成HTML物料：适配AMIS返回结果。逻辑与material-html完全相同，仅返回结果适配
	 * 返回结果：
	   {
		  "status": 0,
		  "msg": "",
		  "data": {
		    "xxx": "xxxx",
		    "xxx": "xxxx",
		    "xxx": "xxxx",
		  }
		}
	 */
	@ResponseBody
	@RequestMapping(value = "material-html-amis", method = RequestMethod.POST)
	public JSONObject genHtmlArticleAmis(@RequestBody JSONObject json, HttpServletRequest request, HttpServletResponse response, Model model) {
		JSONObject result = new JSONObject();
		logger.debug("start generate html.[json]"+json.toJSONString());

		//获取对应的viewTemplate，并从字符串加载模板
		ViewTemplate viewTemplate = viewTemplateService.get(json.getString("templateId"));
		try {
			Configuration cfg = new Configuration();
			cfg.setDefaultEncoding(Charset.forName("UTF-8").name());
			String expr = org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(viewTemplate.getExpression());
	        Template t = new Template(viewTemplate.getId(), new StringReader(expr),cfg);
	        //生成html并直接返回
	        StringWriter writer = new StringWriter();
	        t.process(json, writer);
	        
	        JSONObject data = new JSONObject();
	        data.put("html", writer.getBuffer().toString());
	        result.put("data",data);
			result.put("success",true);
			result.put("status",0);
			result.put("msg","内容已生成");
	        writer.close();
		}catch(Exception ex) {
			result.put("success",false);
			result.put("status",1);
			result.put("msg","生成内容出错。"+ex.getMessage());
			logger.error("generate html error.",ex);
		}
		return result;
	}
	
	//参数为： {prompt: xxx}
	@ResponseBody
	@RequestMapping(value = "chatgpt", method = RequestMethod.POST)
	public JSONObject requestChatGPT(@RequestBody JSONObject json) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		if(json.getString("prompt")==null || json.getString("prompt").trim().length()==0) {
			result.put("text", "问题不能为空哦~~");
			return result;
		}
		String answer = AIHelper.getInstance().requestChatGPT(json.getString("prompt").trim());
		if(answer == null || answer.trim().length()==0) {
			result.put("text", "ChatGPT都被你问倒了，半天都没说话，换一个试试呢");
		}else {
			result.put("text", answer);
			result.put("success", true);
			//扣除虚拟豆
			Broker broker = brokerService.getByOpenid(json.getString("openid"));
			if(broker!=null) {
				broker.setPoints(broker.getPoints()-1);
				brokerService.save(broker);
			}else {
				result.put("msg", "cannot find broker by openid."+json.getString("openid"));
			}
		}
		return result;
	}
	
}