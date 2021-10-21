package com.pcitech.iLife.task;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.arangodb.entity.BaseDocument;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.task.PddItemSync;
import com.pcitech.iLife.task.PddItemsSearcher;
import com.pcitech.iLife.task.PddOrderSync;
import com.pcitech.iLife.task.TaobaoItemSync;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.Util;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsDetailResponse.GoodsDetailResponse;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkGoodsSearchRequest;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse.GoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsSearchResponse.GoodsSearchResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsSearchResponse.GoodsSearchResponseGoodsListItem;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsZsUnitUrlGenResponse.GoodsZsUnitGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkOrderListRangeGetResponse.OrderListGetResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkOrderListRangeGetResponse.OrderListGetResponseOrderListItem;
import com.pdd.pop.sdk.http.api.pop.response.PddGoodsCatTemplateGetResponse.OpenApiResponsePropertiesItem;
import com.pdd.pop.sdk.http.api.pop.response.PddGoodsCatTemplateGetResponse.OpenApiResponsePropertiesItemValuesItem;
import com.pdd.pop.sdk.http.api.pop.response.PddGoodsCatsGetResponse.GoodsCatsGetResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddGoodsCatsGetResponse.GoodsCatsGetResponseGoodsCatsListItem;
import com.taobao.api.ApiException;

@RunWith(SpringJUnit4ClassRunner.class) 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class ClearOrderTest {
	private static Logger logger = LoggerFactory.getLogger(ClearOrderTest.class);
  
	@Autowired
	ClearOrder clearOrderTask;

	@Test
	public void clear() {
		try {
			clearOrderTask.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
