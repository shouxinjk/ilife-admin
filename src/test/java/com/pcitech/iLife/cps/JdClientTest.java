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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arangodb.entity.BaseDocument;
import com.jd.open.api.sdk.domain.kplunion.CategoryService.response.get.CategoryResp;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.request.query.JFGoodsReq;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.JFGoodsResp;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.JingfenQueryResult;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.PromotionGoodsResp;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowResp;
import com.jd.open.api.sdk.domain.kplunion.promotioncommon.PromotionService.response.get.PromotionCodeResp;
import com.jd.open.api.sdk.internal.util.JsonUtil;
import com.jd.open.api.sdk.response.kplunion.UnionOpenGoodsPromotiongoodsinfoQueryResponse;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.task.JdItemSync;
import com.pcitech.iLife.task.JdItemsSearcher;
import com.pcitech.iLife.task.JdOrderSync;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.Util;


@RunWith(SpringJUnit4ClassRunner.class) 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class JdClientTest {
	private static Logger logger = LoggerFactory.getLogger(JdClientTest.class);
    ArangoDbClient arangoClient;
    String host = Global.getConfig("arangodb.host");
    String port = Global.getConfig("arangodb.port");
    String username = Global.getConfig("arangodb.username");
    String password = Global.getConfig("arangodb.password");
    String database = Global.getConfig("arangodb.database");
	@Autowired
	JdHelper jdHelper;
	
	@Autowired
	JdItemSync jdItemSync;
	@Autowired
	JdOrderSync jdOrderSync;
	@Autowired
	JdItemsSearcher jdItemsSearcher;
	
	@Test
	public void seach() {
		JFGoodsReq request = new JFGoodsReq();
		request.setEliteId(1);//传递下标
		request.setPageIndex(1);//默认从第一页开始:下标从1开始
		request.setPageSize(20);
		request.setSortName("commissionShare");//排序字段(price：单价, commissionShare：佣金比例, commission：佣金， inOrderCount30DaysSku：sku维度30天引单量，comments：评论数，goodComments：好评数)
		request.setSort("desc");//asc,desc升降序,默认降序
		request.setPid(Global.getConfig("jd.pid"));//联盟id_应用id_推广位id，三段式
		request.setFields("hotWords,documentInfo,skuLabelInfo,promotionLabelInfo");//支持出参数据筛选，逗号','分隔，目前可用：videoInfo(视频信息),hotWords(热词),similar(相似推荐商品),documentInfo(段子信息)，skuLabelInfo（商品标签），promotionLabelInfo（商品促销标签）
		try {
			System.out.println("try to search items.[request]"+JsonUtil.toJson(request));
			JingfenQueryResult resp = jdHelper.search(request);
			JFGoodsResp[] jfgoods = resp.getData();
			for(JFGoodsResp jfgood:jfgoods)
				System.err.println(JsonUtil.toJson(jfgood));
		}catch(Exception ex) {
			
		}
	}
	
	@Test
	public void queryItemDetail() {
		System.out.println("now start query item details ... ");
		try {
			PromotionGoodsResp[] goods = jdHelper.getDetail("100020914362,65860078475,10030352556187");
			for(PromotionGoodsResp good:goods) {
				System.err.println(good.getSkuId()+" -->"+good.getGoodsName());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void queryCpsLink() {
		System.out.println("now start query cps link ... ");
		String[] urls = {
				"https://item.jd.com/10035168338846.html",
				"https://item.jd.com/10022780362421.html",
				"https://item.jd.com/100007754639.html"
		};
		try {
			for(String url:urls) {
				PromotionCodeResp resp = jdHelper.getCpsLink(url);
				System.err.println(url+"-->"+(resp==null?"没有导购链接":resp.getClickURL()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void getCatgory() {
		System.out.println("now start query category list ... ");
		  //准备连接
		arangoClient = new ArangoDbClient(host,port,username,password,database);
		getCategories(0,0);
		//完成后关闭arangoDbClient
		arangoClient.close();
	}
	
	private void getCategories(int parentId,int grade) {
		String source = "jd";
		logger.debug("now start query category list ..[parentId]"+parentId+"[grade]"+grade);
		try {
			CategoryResp[] categories = jdHelper.getCategory(parentId,grade);
			if(categories==null)
				return;

			for(CategoryResp item:categories) {
				String itemKey = Util.md5(source+item.getId());//所有原始category保持 source+CategoryId的形式唯一识别
				BaseDocument doc = new BaseDocument();
				doc.setKey(itemKey);
				doc.getProperties().put("source", source);
				doc.getProperties().put("pid", ""+item.getParentId());//原始父id
				doc.getProperties().put("name", ""+item.getName());//名称
				doc.getProperties().put("id", ""+item.getId());//原始id
				doc.getProperties().put("level", item.getGrade());//层级 
				logger.debug("try to upsert jd category.[itemKey]"+itemKey+"[doc]"+JsonUtil.toJson(item));
				arangoClient.upsert("platform_categories", itemKey, doc);  
				if(item.getGrade()<2)
					getCategories(item.getId(),item.getGrade()+1);//递归获取下层分类
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void getOrder() {
		System.out.println("now start query order list ... ");
		try {
			OrderRowResp[] orders = jdHelper.getOrder();
			if(orders == null) {//还没有订单，要努力哦
				System.err.println("一个订单都没有，还不赶紧去推广");
			}else {//有订单，我们看看挣了多少银子吧
				for(OrderRowResp order:orders) {
					System.err.println(order.getSkuName()+":"+order.getSkuId()+" :: "+order.getOrderTime()+"-->"+order.getFinalRate()+":"+order.getActualFee());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}

	@Test
	public void syncJdOrder() {
		try {
			jdOrderSync.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void syncJdItem() {
		try {
			jdItemSync.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void syncJdSearch() {
		try {
			jdItemsSearcher.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
