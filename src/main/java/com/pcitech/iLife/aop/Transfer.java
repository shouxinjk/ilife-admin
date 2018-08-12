package com.pcitech.iLife.aop;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.pcitech.iLife.common.utils.KafkaUtils;
import com.pcitech.iLife.modules.mod.entity.Occasion;

@Component
@Aspect
public class Transfer {
	private Logger kafka = LoggerFactory.getLogger("kafkaLogger");//kafka output
//	private Logger logger = LoggerFactory.getLogger(Transfer.class); //file output
	//TODO 对于新建内容无法获取id，需要使用Around或After进行切片
    @Before("execution(* com.pcitech.iLife.modules.mod.service.OccasionService.save(..)) && args(occasion)")
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
        kafka.info(gson.toJson(map));
//        logger.debug(gson.toJson(map));
        
    }
}
