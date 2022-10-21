/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.ItemDimension;
import com.pcitech.iLife.modules.mod.entity.ItemDimensionMeasure;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.UserDimension;
import com.pcitech.iLife.modules.mod.entity.UserDimensionMeasure;
import com.pcitech.iLife.modules.mod.entity.UserMeasure;
import com.pcitech.iLife.modules.mod.service.UserDimensionMeasureService;
import com.pcitech.iLife.modules.mod.service.UserDimensionService;
import com.pcitech.iLife.modules.mod.service.UserMeasureService;

/**
 * 用户客观评价-属性Controller
 * @author qchzhu
 * @version 2019-03-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/userDimensionMeasure")
public class UserDimensionMeasureController extends BaseController {

	@Autowired
	private UserDimensionMeasureService userDimensionMeasureService;
	
	@Autowired
	private UserDimensionService userDimensionService;
	
	@Autowired
	private UserMeasureService userMeasureService;
	
	@ModelAttribute
	public UserDimensionMeasure get(@RequestParam(required=false) String id) {
		UserDimensionMeasure entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userDimensionMeasureService.get(id);
		}
		if (entity == null){
			entity = new UserDimensionMeasure();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:userDimensionMeasure:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserDimensionMeasure userDimensionMeasure,String dimensionId, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(userDimensionMeasure.getDimension() == null) {
			userDimensionMeasure.setDimension(userDimensionService.get(dimensionId));
		}
		Page<UserDimensionMeasure> page = userDimensionMeasureService.findPage(new Page<UserDimensionMeasure>(request, response), userDimensionMeasure); 
		model.addAttribute("page", page);
		model.addAttribute("dimensionId", userDimensionMeasure.getDimension().getId());
		return "modules/mod/userDimensionMeasureList";
	}
	
	@RequiresPermissions("mod:userDimensionMeasure:view")
	@RequestMapping(value = {"list2"})
	public String listPendingMeasures(UserDimensionMeasure userDimensionMeasure,String dimensionId, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<UserMeasure> pendingMeasures = Lists.newArrayList();
		//查询已经关联的属性，根据dimension查询获取
		UserDimension itemDimension = userDimensionService.get(dimensionId);//重新加载得到全部信息
		UserDimensionMeasure query = new UserDimensionMeasure();
		query.setDimension(itemDimension);
		List<UserDimensionMeasure> existUserDimensionMeasures = userDimensionMeasureService.findList(query);
		List<String> ids = Lists.newArrayList();//记录已经添加的userDimensionMeasure ID列表
		for(UserDimensionMeasure item:existUserDimensionMeasures) {
			ids.add(item.getMeasure().getId());
		}
		
		//获取所有用户属性
		List<UserMeasure> measures =userMeasureService.findList(new UserMeasure());//查询所有
		for (UserMeasure item:measures){
			if(ids.indexOf(item.getId())<0)
				pendingMeasures.add(item);
		}
				
		model.addAttribute("measures", pendingMeasures);
		model.addAttribute("dimensionId", dimensionId);
		model.addAttribute("treeId", dimensionId);
		return "modules/mod/userDimensionMeasureList2";
	}

	/**
	 * 批量保存Measure到Dimension上
	 * @param  {dimensionId:xxx, measures:[xx,xx]}
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("mod:userDimensionMeasure:edit")
	@RequestMapping(value = "rest/batch", method = RequestMethod.POST)
	public JSONObject bacthAddMeasures(@RequestBody JSONObject json, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		String dimensionId = json.getString("dimensionId");
		JSONArray measureIds = json.getJSONArray("measureIds");
		logger.debug("got params.[dimensionId]"+dimensionId+" [measureIds]"+measureIds);
		for(int i=0;i<measureIds.size();i++) {
			String measureId = measureIds.getString(i);
			UserDimensionMeasure dimensionMeasure = new UserDimensionMeasure();
			UserDimension dimension = userDimensionService.get(dimensionId);
			UserMeasure measure = userMeasureService.get(measureId);
			dimensionMeasure.setName(dimension.getName()+"-"+measure.getName());
			dimensionMeasure.setDimension(dimension);
			dimensionMeasure.setMeasure(measure);
			dimensionMeasure.setCategory(dimension.getCategory());
			dimensionMeasure.setWeight(0);//默认占比为0，需要到评价首页调整
			dimensionMeasure.setCreateDate(new Date());
			dimensionMeasure.setUpdateDate(new Date());
			dimensionMeasure.setDescription("");
			try {
				userDimensionMeasureService.save(dimensionMeasure);
			}catch(Exception ex) {
				//just ignore. do nothing
				logger.error("add need failed.[dimensionId]"+dimensionId+" [measureId]"+measureId);
			}
		}
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("dimensionId", dimensionId);
		return result;
	}
	
	@RequiresPermissions("mod:userDimensionMeasure:view")
	@RequestMapping(value = "form")
	public String form(UserDimensionMeasure userDimensionMeasure, Model model) {
		if(userDimensionMeasure.getId() == null) {//对于新添加记录默认放到根节点下
			if(userDimensionMeasure.getDimension()!=null && userDimensionMeasure.getDimension().getId()!=null){//从标签页开始添加不带有dimension信息，只有从维度操作列添加带有该信息
				userDimensionMeasure.setDimension(userDimensionService.get(userDimensionMeasure.getDimension().getId()));
				userDimensionMeasure.setName(userDimensionMeasure.getDimension().getName());
			}
		}		
		model.addAttribute("userDimensionMeasure", userDimensionMeasure);
		return "modules/mod/userDimensionMeasureForm";
	}

	@RequiresPermissions("mod:userDimensionMeasure:edit")
	@RequestMapping(value = "save")
	public String save(UserDimensionMeasure userDimensionMeasure, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userDimensionMeasure)){
			return form(userDimensionMeasure, model);
		}
		userDimensionMeasureService.save(userDimensionMeasure);
		addMessage(redirectAttributes, "保存用户客观评价-属性成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userDimension/?dimension.id="+userDimensionMeasure.getDimension().getId()+"&repage";
	}
	
	@RequiresPermissions("mod:userDimensionMeasure:edit")
	@RequestMapping(value = "delete")
	public String delete(UserDimensionMeasure userDimensionMeasure, RedirectAttributes redirectAttributes) {
		userDimensionMeasureService.delete(userDimensionMeasure);
		addMessage(redirectAttributes, "删除用户客观评价-属性成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userDimension/?dimension.id="+userDimensionMeasure.getDimension().getId()+"&repage";
	}

}