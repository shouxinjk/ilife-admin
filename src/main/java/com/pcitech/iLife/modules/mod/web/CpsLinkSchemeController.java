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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.web.BaseController;
import com.pcitech.iLife.common.utils.StringUtils;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.CpsLinkScheme;
import com.pcitech.iLife.modules.mod.entity.TraceCode;
import com.pcitech.iLife.modules.mod.service.CpsLinkSchemeService;
import com.pcitech.iLife.modules.mod.service.TraceCodeService;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * CPS链接规则Controller
 * @author qchzhu
 * @version 2019-10-30
 */
@Controller
@RequestMapping(value = "${adminPath}/mod/cpsLinkScheme")
public class CpsLinkSchemeController extends BaseController {

	@Autowired
	private CpsLinkSchemeService cpsLinkSchemeService;
	@Autowired
	private TraceCodeService traceCodeService;
	
	@ModelAttribute
	public CpsLinkScheme get(@RequestParam(required=false) String id) {
		CpsLinkScheme entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = cpsLinkSchemeService.get(id);
		}
		if (entity == null){
			entity = new CpsLinkScheme();
		}
		return entity;
	}
	
	@RequiresPermissions("mod:cpsLinkScheme:view")
	@RequestMapping(value = {"list", ""})
	public String list(CpsLinkScheme cpsLinkScheme, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CpsLinkScheme> page = cpsLinkSchemeService.findPage(new Page<CpsLinkScheme>(request, response), cpsLinkScheme); 
		model.addAttribute("page", page);
		return "modules/mod/cpsLinkSchemeList";
	}

	@RequiresPermissions("mod:cpsLinkScheme:view")
	@RequestMapping(value = "form")
	public String form(CpsLinkScheme cpsLinkScheme, Model model) {
		model.addAttribute("cpsLinkScheme", cpsLinkScheme);
		return "modules/mod/cpsLinkSchemeForm";
	}

	@RequiresPermissions("mod:cpsLinkScheme:edit")
	@RequestMapping(value = "save")
	public String save(CpsLinkScheme cpsLinkScheme, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, cpsLinkScheme)){
			return form(cpsLinkScheme, model);
		}
		cpsLinkSchemeService.save(cpsLinkScheme);
		addMessage(redirectAttributes, "保存CPS链接规则成功");
		return "redirect:"+Global.getAdminPath()+"/mod/cpsLinkScheme/?repage";
	}
	
	@RequiresPermissions("mod:cpsLinkScheme:edit")
	@RequestMapping(value = "delete")
	public String delete(CpsLinkScheme cpsLinkScheme, RedirectAttributes redirectAttributes) {
		cpsLinkSchemeService.delete(cpsLinkScheme);
		addMessage(redirectAttributes, "删除CPS链接规则成功");
		return "redirect:"+Global.getAdminPath()+"/mod/cpsLinkScheme/?repage";
	}
	
	/**
	 * 根据达人ID获取CPS链接
	 * 
	 * 参数：brokerId，source，category。其中category为预留，当前仅根据platform组织链接
	 * 
	 * 链接生成逻辑：
	 * 1,获取链接规则：根据source查询得到cps链接规则Groovy脚本。如无脚本配置则直接返回原始链接
	 * 2,获取推广位：根据BrokerId、source查询得到广告位。如果无brokerId，则使用默认system进行查询。如无广告位则直接返回原始链接。
	 * 3,将广告位、原始链接传递给脚本，计算并得到相应的link
	 * 
	 * 注意：
	 * 由于淘宝需要通过另外的脚本生成，不能通过此接口完成。此接口内将返回原始ID
	 * 
	 * 
	 * 前端展现逻辑：
	 * 0，获取页面分享brokerId，如果没有则为system
	 * 1，查询 item.link.cps.BROKERID 是否存在，如果有则使用该链接，否则传递source，brokerId，wap尝试生成链接
	 * 2，如果返回结果为false，检查是否有item.link.cps.system,有则使用该值，否则使用item.link.wap显示
	 * 3，如果返回结果为true，则获取返回结果{broker:"xxx",link:"yyy"}，更新item.link.cps.xxx=yyy，并显示该值
	 */
	@ResponseBody
	@RequestMapping(value = "rest/cpslink", method = RequestMethod.GET)
	public Map<String, Object> getCpsLink(@RequestParam String brokerId,@RequestParam String source, @RequestParam String url, @RequestParam String category,HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> map = Maps.newHashMap();
		
		//根据source查询是否存在CPS链接规则
		CpsLinkScheme query = new CpsLinkScheme();
		query.setCategory(category);
		query.setPlatform(source);
		CpsLinkScheme scheme = cpsLinkSchemeService.getByQuery(query);
		if(scheme == null) {//如果不存在链接规则，则直接返回
			map.put("status", false);//默认认为直接采用原始
			map.put("description", "no cps scheme");
			return map;
		}else {//查询broker推广位并根据脚本计算得到链接
			//根据brokerId及source查询推广位
			Broker broker = new Broker();
			broker.setId(brokerId);
			
			TraceCode traceCode = new TraceCode();
			traceCode.setBroker(broker);
			traceCode.setPlatform(source);
			traceCode = traceCodeService.getByBrokerAndPlatform(traceCode);
			if(traceCode==null) {//没有该达人的推广位，则尝试查询system达人的推广位
				traceCode = new TraceCode();
				traceCode.setPlatform(source);
				broker.setId("system");
				traceCode.setBroker(broker);
				traceCode = traceCodeService.getByBrokerAndPlatform(traceCode);
				if(traceCode == null) {//如果连system都没有推广位，那就完蛋了，直接返回false显示原始链接
					map.put("status", false);
					map.put("description", "no system trace code.");
					return map;
				}else {//用system达人的推广位计算链接
					map.put("status", true);
					map.put("broker", "system");
					map.put("description", "use system trace code.");
				}
			}else {//有则使用对应达人的推广位计算链接
				map.put("status", true);
				map.put("broker", brokerId);
				map.put("description", "use broker specified trace code.");
			}
			
			//获取脚本进行计算，样例如下：
			//注意：脚本中字符串操作需要用单引号，双引号表示模板消息，会出现解析错误
			//return url+'?TypeID=2&AllianceID='+traceCode+'&sid=1611278&ouid=&app=0101X00&'
			Binding binding = new Binding();
			binding.setVariable("source",source);
			binding.setVariable("category",category);
			binding.setVariable("url",url);
			binding.setVariable("traceCode",traceCode.getCode());
			try {
		        GroovyShell shell = new GroovyShell(binding);
		        Object value = shell.evaluate(scheme.getScript());//计算得到目标url
		        map.put("link", value.toString());
			}catch(Exception ex) {//如果计算发生错误也使用默认链接
				map.put("status", false);
				map.put("description", ex.getMessage());
			}
	        return map;
		}
	}
}