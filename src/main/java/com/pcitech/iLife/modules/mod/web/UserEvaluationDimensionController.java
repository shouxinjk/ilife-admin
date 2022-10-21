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
import com.pcitech.iLife.modules.mod.entity.UserEvaluation;
import com.pcitech.iLife.modules.mod.entity.UserEvaluationDimension;
import com.pcitech.iLife.modules.mod.entity.UserDimension;
import com.pcitech.iLife.modules.mod.entity.UserEvaluation;
import com.pcitech.iLife.modules.mod.entity.UserEvaluationDimension;
import com.pcitech.iLife.modules.mod.entity.UserEvaluationDimension;
import com.pcitech.iLife.modules.mod.service.UserDimensionService;
import com.pcitech.iLife.modules.mod.service.UserEvaluationDimensionService;
import com.pcitech.iLife.modules.mod.service.UserEvaluationService;
import com.pcitech.iLife.util.Util;

/**
 * 用户主观评价-维度Controller
 * @author qchzhu
 * @version 2019-03-13
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/userEvaluationDimension")
public class UserEvaluationDimensionController extends BaseController {

	@Autowired
	private UserEvaluationDimensionService userEvaluationDimensionService;
	
	@Autowired
	private UserEvaluationService userEvaluationService;
	
	@Autowired
	private UserDimensionService userDimensionService;
	
	@ModelAttribute
	public UserEvaluationDimension get(@RequestParam(required=false) String id) {
		UserEvaluationDimension entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userEvaluationDimensionService.get(id);
		}
		if (entity == null){
			entity = new UserEvaluationDimension();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:userEvaluationDimension:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserEvaluationDimension userEvaluationDimension,String evaluationId, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(userEvaluationDimension.getEvaluation() == null) {
			userEvaluationDimension.setEvaluation(userEvaluationService.get(evaluationId));
		}
		Page<UserEvaluationDimension> page = userEvaluationDimensionService.findPage(new Page<UserEvaluationDimension>(request, response), userEvaluationDimension); 
		model.addAttribute("page", page);
		model.addAttribute("evaluationId", userEvaluationDimension.getEvaluation().getId());
		return "modules/mod/userEvaluationDimensionList";
	}

	@RequiresPermissions("mod:userEvaluationDimension:view")
	@RequestMapping(value = {"list2"})
	public String listPendingDimemsions(UserEvaluationDimension userEvaluationDimension,String evaluationId, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<UserDimension> pendingDimensions = Lists.newArrayList();
		
		//查询已经关联的客观评价维度
		UserEvaluation userEvaluation = userEvaluationService.get(evaluationId);//重新加载得到全部信息
		UserEvaluationDimension query = new UserEvaluationDimension();
		query.setEvaluation(userEvaluation);
		List<UserEvaluationDimension> existUserEvaluationDimension = userEvaluationDimensionService.findList(query);
		List<String> ids = Lists.newArrayList();//记录已经添加的itemDimensionMeasure ID列表
		for(UserEvaluationDimension item:existUserEvaluationDimension) {
			ids.add(item.getDimension().getId());
		}
		
		//获取所有客观评价节点
		UserDimension q = new UserDimension();
		List<UserDimension> dimensions =userDimensionService.findList(q);
		for (UserDimension item:dimensions){
			if(ids.indexOf(item.getId())<0)
				pendingDimensions.add(item);
		}
		model.addAttribute("evaluationId", evaluationId);		
		model.addAttribute("dimensions", pendingDimensions);
		return "modules/mod/userEvaluationDimensionList2";
	}
	

	/**
	 * 批量保存dimension到evaluation上
	 * @param  {evaluationId:xxx, dimensions:[xx,xx]}
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("mod:userEvaluationDimension:edit")
	@RequestMapping(value = "rest/batch", method = RequestMethod.POST)
	public JSONObject bacthAddDimensions(@RequestBody JSONObject json, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		String evaluationId = json.getString("evaluationId");
		JSONArray dimensionIds = json.getJSONArray("dimensionIds");
		logger.debug("got params.[evaluationId]"+evaluationId+" [dimensionIds]"+dimensionIds);
		for(int i=0;i<dimensionIds.size();i++) {
			String dimensionId = dimensionIds.getString(i);
			UserEvaluationDimension evaluationDimension = new UserEvaluationDimension();
			UserEvaluation evaluation = userEvaluationService.get(evaluationId);
			UserDimension dimension = userDimensionService.get(dimensionId);
			evaluationDimension.setName(evaluation.getName()+"-"+dimension.getName());
			evaluationDimension.setEvaluation(evaluation);
			evaluationDimension.setDimension(dimension);
			evaluationDimension.setCategory(dimension.getCategory());
			evaluationDimension.setWeight(0);//默认占比为0，需要到评价首页调整
			evaluationDimension.setCreateDate(new Date());
			evaluationDimension.setUpdateDate(new Date());
			evaluationDimension.setDescription("");
			//evaluation和dimension唯一
			evaluationDimension.setId(Util.md5(evaluationId+dimension.getId()));
			evaluationDimension.setIsNewRecord(true);
			try {
				userEvaluationDimensionService.save(evaluationDimension);
			}catch(Exception ex) {
				//just ignore. do nothing
				logger.error("add need failed.[evaluationId]"+evaluationId+" [dimensionIds]"+dimensionIds);
			}
		}
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("evaluationId", evaluationId);
		return result;
	}
	
	
	@RequiresPermissions("mod:userEvaluationDimension:view")
	@RequestMapping(value = "form")
	public String form(UserEvaluationDimension userEvaluationDimension, Model model) {
		if(userEvaluationDimension.getId() == null) {//对于新添加记录需要根据ID补充dimension
			userEvaluationDimension.setEvaluation(userEvaluationService.get(userEvaluationDimension.getEvaluation().getId()));
			userEvaluationDimension.setName(userEvaluationDimension.getEvaluation().getName());
		}
		model.addAttribute("userEvaluationDimension", userEvaluationDimension);
		return "modules/mod/userEvaluationDimensionForm";
	}

	@RequiresPermissions("mod:userEvaluationDimension:edit")
	@RequestMapping(value = "save")
	public String save(UserEvaluationDimension userEvaluationDimension, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userEvaluationDimension)){
			return form(userEvaluationDimension, model);
		}
		userEvaluationDimensionService.save(userEvaluationDimension);
		addMessage(redirectAttributes, "保存用户主观评价-维度成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userEvaluation/?evaluation.id="+userEvaluationDimension.getEvaluation().getId()+"&repage";
	}
	
	@RequiresPermissions("mod:userEvaluationDimension:edit")
	@RequestMapping(value = "delete")
	public String delete(UserEvaluationDimension userEvaluationDimension, RedirectAttributes redirectAttributes) {
		userEvaluationDimensionService.delete(userEvaluationDimension);
		addMessage(redirectAttributes, "删除用户主观评价-维度成功");
		return "redirect:"+Global.getAdminPath()+"/mod/userEvaluation/?evaluation.id="+userEvaluationDimension.getEvaluation().getId()+"&repage";
	}

}