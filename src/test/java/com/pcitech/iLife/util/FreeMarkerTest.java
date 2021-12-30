package com.pcitech.iLife.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

//@RunWith(SpringJUnit4ClassRunner.class) 
//@WebAppConfiguration
//@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class FreeMarkerTest {
	private static Logger logger = LoggerFactory.getLogger(FreeMarkerTest.class);
  

	@Test
	public void generateHtml() throws IOException, TemplateException {
        /*初始化freemarker模板*/
        Configuration cfg = new Configuration();
        cfg.setDefaultEncoding(Charset.forName("UTF-8").name());
//        cfg.setObjectWrapper(new JSONArrayObjectWrapper());//使用JSONArray封装：否则无法遍历JSONArray对象
        //从字符串加载模板
        String templateStr="<h1>XX列表</h1>"
        		+ "<div>骚货 ${user.name}</div>"
        		+ "<#list partners as partner>"
        		+ "    ${partner.name}--${partner.career}"
        		+ "</#list>";
        Template t = new Template("name", new StringReader(templateStr), cfg);
        
        //需要注入的数据
        JSONObject json = new JSONObject();
        JSONObject user = new JSONObject();
        user.put("name", "白洁");
        user.put("career", "大白奶妹");
        json.put("user", user);
        
        JSONArray partners = new JSONArray();
        String[] names= {"王申","高义","王局长"};
        String[] carrers= {"丈夫","校长","教育局长"};
        int i=0;
        for(String name:names) {
	        JSONObject partner = new JSONObject();
	        partner.put("name", name);
	        partner.put("career", carrers[i]);
	        partners.add(partner);
	        i++;
        }
        json.put("partners", partners);

        //字符串输出
        StringWriter writer = new StringWriter();
        t.process(json, writer);
        
        System.out.println(writer.getBuffer());
	}
}
