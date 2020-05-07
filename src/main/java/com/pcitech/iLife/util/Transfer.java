package com.pcitech.iLife.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.pcitech.iLife.common.utils.KafkaUtils;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.service.MeasureService;
import com.pcitech.iLife.modules.ope.entity.Performance;

@Component
@Aspect
public class Transfer {
	private Logger occasionKafkaLogger = LoggerFactory.getLogger("kafkaLoggerOccasion");//kafka output
	private Logger valueKafkaLogger = LoggerFactory.getLogger("kafkaLoggerMarkedValue");//kafka output
//	private Logger logger = LoggerFactory.getLogger(Transfer.class); //file output
	@Autowired
	MeasureService measureService;
	
	/**
	 * 在保存Occasion时推送消息
	 * @param occasion
	 */
	//TODO 对于新建内容无法获取id，需要使用Around或After进行切片
    //@Before("execution(* com.pcitech.iLife.modules.mod.service.OccasionService.save(..)) && args(occasion)")
    public void saveOccasion(Occasion occasion) {
        Gson gson = new Gson();
		Map map = new HashMap();
		map.put("_action", "save_occasion");
		map.put("timestamp", new Date());
		map.put("id", occasion.getId());
		map.put("name", occasion.getName());
		map.put("category", occasion.getOccasionCategory().getId());
		map.put("exprUser", occasion.getExprUser());
		map.put("exprItem", occasion.getExprItem());
		map.put("exprTrigger", occasion.getExprTrigger());
		map.put("exprDuration", occasion.getExprDuration());
		map.put("triggerType",occasion.getTriggerType());
		map.put("expression", occasion.getExpression());
//		boolean async = false;
//		KafkaUtils.send(async,gson.toJson(map));
		occasionKafkaLogger.info(gson.toJson(map));
//        logger.debug(gson.toJson(map));
        
    }
    
    /**
     * 在保存商品标注值时推送到分析库。 
     * @param performance
     */
    
    @Before("execution(* com.pcitech.iLife.modules.ope.service.PerformanceService.save(..)) && args(performance)")
    public void savePerformance(Performance performance) {
    		Measure m = measureService.get(performance.getMeasure().getId());
        Gson gson = new Gson();
		Map map = new HashMap();
		map.put("type", "dic");//dic 、 ref，分别表示来源于引用或字典
		map.put("category", m.getCategory().getId());
		map.put("property_key", m.getProperty());
		map.put("property", m.getName());
		map.put("org_value", performance.getOriginalValue());
		map.put("mark_type", "manual");//manual、auto
		map.put("score", performance.getMarkedValue());
		map.put("rank", performance.getLevel());
		map.put("status", "ready");
		map.put("createdOn",performance.getCreateDate());
		map.put("modifiedOn", new Date());
		valueKafkaLogger.info(gson.toJson(map));
    }
}
