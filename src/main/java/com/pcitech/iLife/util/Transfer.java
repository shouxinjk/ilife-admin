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

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.pcitech.iLife.common.utils.KafkaUtils;
import com.pcitech.iLife.modules.mod.entity.ItemCategory;
import com.pcitech.iLife.modules.mod.entity.Measure;
import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.entity.PlatformCategory;
import com.pcitech.iLife.modules.mod.entity.PlatformProperty;
import com.pcitech.iLife.modules.mod.entity.UserMeasure;
import com.pcitech.iLife.modules.mod.service.ItemCategoryService;
import com.pcitech.iLife.modules.mod.service.MeasureService;
import com.pcitech.iLife.modules.mod.service.PlatformPropertyService;
import com.pcitech.iLife.modules.mod.service.UserMeasureService;
import com.pcitech.iLife.modules.ope.entity.Performance;
import com.pcitech.iLife.modules.ope.entity.UserPerformance;

@Component
@Aspect
public class Transfer {
	private Logger platformCategoryLogger = LoggerFactory.getLogger("kafkaLoggerPlatformCategory");//kafka output
	private Logger platformPropertyLogger = LoggerFactory.getLogger("kafkaLoggerPlatformProperty");//kafka output
	private Logger occasionKafkaLogger = LoggerFactory.getLogger("kafkaLoggerOccasion");//kafka output
	private Logger valueKafkaLogger = LoggerFactory.getLogger("kafkaLoggerMarkedValue");//kafka output
//	private Logger logger = LoggerFactory.getLogger(Transfer.class); //file output
	@Autowired
	MeasureService measureService;
	@Autowired
	UserMeasureService userMeasureService;
	@Autowired
	PlatformPropertyService platformPropertyService;
	@Autowired
	ItemCategoryService itemCategoryService;
	
	/**
	 * 在新增及修改类目映射时更新stuff数据
	 * 推送数据包括：platform、category、categoryId、categoryName
	 * @param 
	 */
    @Before("execution(* com.pcitech.iLife.modules.mod.service.PlatformCategoryService.save(..)) && args(platformCategory)")
    public void savePlatformCategory(PlatformCategory platformCategory) {
        Gson gson = new Gson();
		Map<String,Object> map = Maps.newHashMap();
		//根据设置标准类目category
		ItemCategory category = itemCategoryService.get(platformCategory.getCategory());//需要重新获取得到name信息
		if(category!=null && platformCategory.getName()!=null && platformCategory.getName().trim().length()>0
				 && platformCategory.getPlatform()!=null && platformCategory.getPlatform().trim().length()>0) {//仅在非空时推送
			map.put("categoryId", category.getId());//映射的标准类目ID
			map.put("categoryName", category.getName());//映射的标准类目名称
			map.put("category", platformCategory.getName());
			map.put("platform", platformCategory.getPlatform());
			platformCategoryLogger.info(gson.toJson(map));
		}
    }
	
	/**
	 * 在新增及修改属性映射时推送到分析系统
	 * 推送数据包括：categoryId、property、propertyId、propertyKey、vals分布、标注类型、计算类型。操作类型 save
	 * @param 
	 */
    @Before("execution(* com.pcitech.iLife.modules.mod.service.PlatformPropertyService.save(..)) && args(platformProperty)")
    public void savePlatformProperty(PlatformProperty platformProperty) {
        Gson gson = new Gson();
		Map<String,Object> map = Maps.newHashMap();
		//根据设置分别获取measure及category
		Measure measure = measureService.get(platformProperty.getMeasure());//需要重新获取，得到propertyKey、vals分布、计算定义等信息
		ItemCategory category = itemCategoryService.get(platformProperty.getCategory());//需要重新获取得到name信息
		if(measure!=null && category!=null) {//仅在非空时推送
			map.put("action", "save");//常量，表示更新属性设置
			map.put("categoryId", category.getId());
			map.put("categoryName", category.getName());//冗余发送，实际不会用到。可删除
			map.put("property", platformProperty.getName()==null?"":platformProperty.getName().replace("๏", "").replace("○", ""));
			map.put("platform", platformProperty.getPlatform());
			map.put("propertyId", measure.getId());
			map.put("propertyKey", measure.getProperty()==null?"":measure.getProperty());
			map.put("labelType", measure.getAutoLabelType());
			map.put("labelDict", measure.getAutoLabelDict());
			map.put("labelCategory", measure.getAutoLabelCategory()==null?"":measure.getAutoLabelCategory().getId());
			map.put("labelTagCategory", measure.getAutoLabelTagCategory()==null?"":measure.getAutoLabelTagCategory().getId());
			map.put("normalizeType", measure.getNormalizeType()==null?"min-max":measure.getNormalizeType());
			map.put("multiValueFunc", measure.getMultiValueFunc()==null?"":measure.getMultiValueFunc());
			map.put("alpha", measure.getAlpha());
			map.put("beta", measure.getBeta());
			map.put("gamma", measure.getGamma());
			map.put("delte", measure.getDelte());
			map.put("epsilon", measure.getEpsilon());
			map.put("zeta", measure.getZeta());
			map.put("eta", measure.getEta());
			map.put("theta", measure.getTheta());
			map.put("lambda", measure.getLambda()==null?"":measure.getLambda());
			platformPropertyLogger.info(gson.toJson(map));
		}
    }
	
    /**
	 * 在忽略属性时推送到分析系统：直接从fact中删除相应条目。并且不根据categoryId进行区分
	 * 推送数据包括：categoryId、property、propertyId、propertyKey、操作类型delete
	 * @param 
	 */
    @Before("execution(* com.pcitech.iLife.modules.mod.service.PlatformPropertyService.delete(..)) && args(platformProperty)")
    public void deletePlatformProperty(PlatformProperty platformProperty) {
        Gson gson = new Gson();
		Map<String,Object> map = Maps.newHashMap();
		//根据设置获取category
		ItemCategory category = itemCategoryService.get(platformProperty.getCategory());//需要重新获取得到name信息
		if(category!=null) {//仅在非空时推送
			map.put("action", "delete");//常量，表示忽略属性设置
			map.put("categoryId", category.getId());
			map.put("categoryName", category.getName());//冗余发送，实际不会用到。可删除
			map.put("property", platformProperty.getName()==null?"":platformProperty.getName().replace("๏", "").replace("○", ""));
			map.put("platform", platformProperty.getPlatform());
			platformPropertyLogger.info(gson.toJson(map));
		}
    }
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
     * @deprecated 待删除。直接通过ope_performance监听数据表
     * 在保存商品标注值时推送到分析库。 
     * @param performance
     */
    //Qingchun:注意，同时存在两个数据同步器，分别是创建和更新。本同步器是创建同步器，在ope_performance初次建立时即同步到分 库。后续在修改标注值时直接通过canal监听更新 。
    @Before("execution(* com.pcitech.iLife.modules.ope.service.PerformanceService.save(..)) && args(performance)")
    public void savePerformance(Performance performance) {
    	Measure m = measureService.get(performance.getMeasure().getId());
        Gson gson = new Gson();
		Map map = new HashMap();
		map.put("id", Util.md5(performance.getCategory().getId()+performance.getMeasure().getId()+m.getProperty()));//根据categoryId+measureId+value做唯一性识别
		map.put("ref_type", "dic");//dic 、 ref，分别表示来源于引用或字典
		map.put("object_type", "item");//item 、 person，分别表示商品标注或用户标注
		map.put("category_id", performance.getCategory().getId());//对于商品标注是ItemCategoryId，对于用户标注是UserCategoryId，对于dic则是具体的表名
		map.put("category", performance.getCategory().getName());//标准类目名称
		map.put("property_id", m.getId());//增加属性ID存储
		map.put("property_key", m.getProperty());
		map.put("property", m.getName());
		map.put("value", performance.getOriginalValue());//原始数值
		map.put("mark_type", "manual");//manual、auto
		map.put("score", performance.getMarkedValue());
		map.put("rank", performance.getLevel());
		map.put("status", "ready");
		map.put("createdOn",performance.getCreateDate());
		map.put("modifiedOn", new Date());
		valueKafkaLogger.info(gson.toJson(map));
    }
    

    /**
     * @deprecated 待删除。直接通过canal监听ope_user_performance表
     * 在用户商品标注值时推送到分析库。 
     * @param performance
     */
    
    @Before("execution(* com.pcitech.iLife.modules.ope.service.UserPerformanceService.save(..)) && args(userPerformance)")
    public void saveUserPerformance(UserPerformance userPerformance) {
    	UserMeasure m = userMeasureService.get(userPerformance.getMeasure().getId());
        Gson gson = new Gson();
		Map map = new HashMap();
		map.put("id", Util.md5(m.getCategory().getId()+m.getProperty()));//根据category和property做唯一性识别
		map.put("ref_type", "dic");//dic 、 ref，分别表示来源于引用或字典
		map.put("object_type", "user");//item 、 person，分别表示商品标注或用户标注
		map.put("category", m.getCategory().getId());//对于商品标注是ItemCategoryId，对于用户标注是UserCategoryId，对于dic则是具体的表名
		map.put("property_key", m.getProperty());
		map.put("property", m.getName());
		map.put("org_value", userPerformance.getOriginalValue());
		map.put("mark_type", "manual");//manual、auto
		map.put("score", userPerformance.getMarkedValue());
		map.put("rank", userPerformance.getLevel());
		map.put("status", "ready");
		map.put("createdOn",userPerformance.getCreateDate());
		map.put("modifiedOn", new Date());
		valueKafkaLogger.info(gson.toJson(map));
    }
}
