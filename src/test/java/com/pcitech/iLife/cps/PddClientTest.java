package com.pcitech.iLife.cps;

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
import com.pdd.pop.sdk.http.api.pop.response.PddGoodsCatTemplateGetResponse.OpenApiResponsePropertiesItem;
import com.pdd.pop.sdk.http.api.pop.response.PddGoodsCatTemplateGetResponse.OpenApiResponsePropertiesItemValuesItem;
import com.pdd.pop.sdk.http.api.pop.response.PddGoodsCatsGetResponse.GoodsCatsGetResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddGoodsCatsGetResponse.GoodsCatsGetResponseGoodsCatsListItem;
import com.taobao.api.ApiException;

@RunWith(SpringJUnit4ClassRunner.class) 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class PddClientTest {
	private static Logger logger = LoggerFactory.getLogger(PddClientTest.class);
  ArangoDbClient arangoClient;
  String host = Global.getConfig("arangodb.host");
  String port = Global.getConfig("arangodb.port");
  String username = Global.getConfig("arangodb.username");
  String password = Global.getConfig("arangodb.password");
  String database = Global.getConfig("arangodb.database");
	@Autowired
	PddHelper pddHelper;
	
	@Autowired
	PddItemSync pddSyncTask;
	
	@Autowired
	PddItemsSearcher pddItemsSearcher;
	
	private List<String> getGoodsSignList(){
		List<String> goodsSignList = new ArrayList<String>();
		goodsSignList.add("Y932ms86eklU8LcVwvfZA33k3lQMIJzP_JJhfhOR1G");
		goodsSignList.add("Y9X2lY1QjM9U8LcVwvfZA-r_Jyi2DPTd_J1AZLvO7B");
		return goodsSignList;
	}
	
	@Test
	public void getCategory() {
		System.out.println("now start query categories ... ");
		  //准备连接
		arangoClient = new ArangoDbClient(host,port,username,password,database);
		getCategories(0);
		//完成后关闭arangoDbClient
		arangoClient.close();
		assert true;
	}
	
	private void getCategories(long parentId) {
		String source = "pdd";
		logger.debug("now start query category list ..[parentId]"+parentId);
		try {
			GoodsCatsGetResponse resp = pddHelper.getCategory(parentId);
			if(resp==null ||  resp.getGoodsCatsList().size()==0)
				return;

			for(GoodsCatsGetResponseGoodsCatsListItem item:resp.getGoodsCatsList()) {
				logger.debug("目录::[parentId]"+item.getParentCatId()+"\t[id]"+item.getCatId()+"\t[name]"+item.getCatName());
				String itemKey = Util.md5(source + item.getCatId());//所有原始category保持 source+CategoryId的形式唯一识别
				BaseDocument doc = new BaseDocument();
				doc.setKey(itemKey);
				doc.getProperties().put("source", source);
				doc.getProperties().put("pid", ""+item.getParentCatId());//原始父id
				doc.getProperties().put("name", ""+item.getCatName());//名称
				doc.getProperties().put("id", ""+item.getCatId());//原始id
				doc.getProperties().put("level", item.getLevel());//层级 
				logger.debug("try to upsert jd category.[itemKey]"+itemKey+"[doc]"+JsonUtil.transferToJson(item));
				arangoClient.upsert("platform_categories", itemKey, doc);  
//				if(item.getGrade()<2)
				getCategories(item.getCatId());//递归获取下层分类
				//getProperties(item.getCatId());//TODO:无接口权限。获取分类属性列表及属性值列表
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getProperties(long categoryId) {
		String source = "pdd";
		logger.debug("now start query property list ..[categoryId]"+categoryId);
		try {
			List<OpenApiResponsePropertiesItem> resp = pddHelper.getProperty(categoryId);
			if(resp==null ||  resp.size()==0)
				return;
			for(OpenApiResponsePropertiesItem item:resp) {
				String itemKey = Util.md5(source + item.getId());//所有原始property保持 source+propertyId的形式唯一识别
				BaseDocument doc = new BaseDocument();
				doc.setKey(itemKey);
				doc.getProperties().put("source", source);
				doc.getProperties().put("cid", ""+categoryId);//原始分类id
				doc.getProperties().put("pid", ""+item.getParentId());//原始父id
				doc.getProperties().put("name", ""+item.getName());//名称
				doc.getProperties().put("id", ""+item.getId());//原始id
				doc.getProperties().put("isKey", item.getIsKey());//
				doc.getProperties().put("isParent", item.getIsParent());//
				doc.getProperties().put("isSale", item.getIsSale());//
				doc.getProperties().put("isConditionShow", item.getIsConditionShow());//
				logger.debug("try to upsert pdd properties.[itemKey]"+itemKey+"[doc]"+JsonUtil.transferToJson(item));
				arangoClient.upsert("platform_properties", itemKey, doc);  
				getValues( categoryId,item.getId(),item.getValues());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getValues(long categoryId,long propertyId,List<OpenApiResponsePropertiesItemValuesItem> values) {
		String source = "pdd";
		logger.debug("now start insert property values ..[propertyId]"+propertyId);
		try {
			if(values==null ||  values.size()==0)
				return;
			for(OpenApiResponsePropertiesItemValuesItem item:values) {
				String itemKey = Util.md5(source + item.getVid());//所有原始property保持 source+valueId的形式唯一识别
				BaseDocument doc = new BaseDocument();
				doc.setKey(itemKey);
				doc.getProperties().put("source", source);
				doc.getProperties().put("cid", ""+categoryId);//原始分类id
				doc.getProperties().put("pid", ""+propertyId);//所属属性id
				doc.getProperties().put("parentVids", ""+item.getParentVids());//原始父vid，是数组
				doc.getProperties().put("value", ""+item.getValue());//属性值
				doc.getProperties().put("vid", ""+item.getVid());//原始id
				doc.getProperties().put("isParent", item.getIsParent());//
				logger.debug("try to upsert pdd values.[itemKey]"+itemKey+"[doc]"+JsonUtil.transferToJson(item));
				arangoClient.upsert("platform_values", itemKey, doc);  
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void getCpsItems() {
		System.out.println("now start query promotion items ... ");
		PddDdkGoodsSearchRequest request = new PddDdkGoodsSearchRequest();
		try {
			GoodsSearchResponse resp = pddHelper.searchCpsItems(request);
			for(GoodsSearchResponseGoodsListItem item:resp.getGoodsList())
				System.err.println("商品::[goodsSign]"+item.getGoodsSign()+"\t[name]"+item.getGoodsName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void getItemDetail() {
		System.out.println("now start query item details ... ");
		String brokerId = "alexchew";
		try {
			GoodsDetailResponse resp = pddHelper.getItemDetail(brokerId, "Y932ms86eklU8LcVwvfZA33k3lQMIJzP_JJhfhOR1G");
			System.err.println("商品名称::"+resp.getGoodsDetails().get(0).getGoodsName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	
	@Test
	public void generateCpsLinksByUrl() {
		System.out.println("now start generate cps links by url ... ");
		String brokerId = "alexchew";
		try {
			GoodsZsUnitGenerateResponse resp = pddHelper.generateCpsLinksByUrl(brokerId,"https://mobile.yangkeduo.com/duo_coupon_landing.html?goods_id=244713040093&pid=20434335_206807608&goods_sign=Y9H2mpBGVklU8LcTwvfZAiQpGnpkFLvf_JQh6J0oGX4&customParameters=%7B%22uid%22%3A%2220434335%22%2C%22brokerId%22%3A%22default%22%7D&cpsSign=CC_210518_20434335_206807608_42f6b8466c9b619e8171d8f0e054339c&duoduo_type=2");
			System.err.println("Mobile URL::"+resp.getMobileUrl());
			System.err.println("Web URL::"+resp.getUrl());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void getOrders() {
		System.out.println("now start query orders ... ");
		String brokerId = "alexchew";
		try {
			OrderListGetResponse resp = pddHelper.getOrders();
			System.err.println("orders::"+JsonUtil.transferToJson(resp));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void generateCpsLinksByGoodsSign() {
		System.out.println("now start generate cps links by sign ... ");
		String brokerId = "alexchew";
		try {
			PddDdkGoodsPromotionUrlGenerateResponse response = pddHelper.generateCpsLinksByGoodsSign(brokerId,null,getGoodsSignList());
			System.out.println(JsonUtil.transferToJson(response));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	/**
	 * 拼多多要求调用接口前完成签名授权。通过传递PID及CUSTOM_PARAMETER完成。如果未授权则生成授权URL，访问后完成授权即可。
	 * 授权仅执行一次。后续无需调用
	 */
	@Test
	public void checkAndGenerateAuthority() {
		try {
			if(0==pddHelper.checkAuthority()) {
				System.out.println("未绑定授权，需要重新生成 链接。。。");
				GoodsPromotionUrlGenerateResponse response = pddHelper.generateAuthorityUrl(getGoodsSignList());
				System.out.println(JsonUtil.transferToJson(response));
			}else {
				System.err.println("已经绑定授权了，别再瞎鸡巴调用了。。。该干啥干啥就成。。。");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void pddSyncTask() {
		try {
			pddSyncTask.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void pddSearchTask() {
		try {
			pddItemsSearcher.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
