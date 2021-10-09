package com.pcitech.iLife.cps;

import static org.junit.Assert.*;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pcitech.iLife.task.PddItemSync;
import com.pcitech.iLife.task.SuningItemSync;
import com.pcitech.iLife.task.TaobaoItemSync;
import com.pcitech.iLife.task.VipItemSync;
import com.pcitech.iLife.task.VipItemSearcher;
import com.pcitech.iLife.task.VipOrderSync;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsDetailResponse.GoodsDetailResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse.GoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsZsUnitUrlGenResponse.GoodsZsUnitGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkOrderListRangeGetResponse.OrderListGetResponse;
import com.taobao.api.ApiException;
import com.vip.adp.api.open.service.Category;
import com.vip.adp.api.open.service.CpsUnionPidGenResponse;
import com.vip.adp.api.open.service.GoodsInfo;
import com.vip.adp.api.open.service.GoodsInfoResponse;
import com.vip.adp.api.open.service.OrderResponse;
import com.vip.adp.api.open.service.PidInfo;
import com.vip.adp.api.open.service.UrlInfo;

@RunWith(SpringJUnit4ClassRunner.class) 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class VipClientTest {
	
	@Autowired
	VipHelper vipHelper;
	
	@Autowired
	VipItemSync vipSyncTask;
	
	@Autowired
	VipItemSearcher vipSearchTask;
	
	@Autowired
	VipOrderSync vipOrderSyncTask;
	
	@Test
	public void generateCpsLink() {
		String brokerId = "system";
		String channelType = "wechat";
		List<String> urls = new ArrayList<String>();
		urls.add("https://detail.vip.com/detail-1710614365-6918354723321184349.html");
		urls.add("https://detail.vip.com/detail-1711236176-6918710001798316112.html");
		urls.add("https://detail.vip.com/detail-1710615488-6918808670566474496.html");
		try {
			List<UrlInfo> result = vipHelper.generateCpsLinkByUrl( brokerId, urls,channelType);
			for(UrlInfo url:result)
				System.err.println(url.getUrl());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void searchItems() {
		String brokerId = "system";
		String jxCode = "7hfpy0m4";
		int sourceType = 1;
		int channelType = 0;
		int pageSize = 20;
		int pageNo = 0;
		boolean hasMore = true;

		try {
			while(hasMore) {
				GoodsInfoResponse result = vipHelper.searchItems(sourceType, channelType, jxCode, brokerId, pageSize, pageNo);
				System.err.println("total items:"+result.getTotal());
				List<GoodsInfo> goods = result.getGoodsInfoList();
				int totalPages = (result.getTotal()+pageSize-1)/pageSize;
				for(GoodsInfo good:goods)
					System.out.println(good.getGoodsName());
				if(totalPages > pageNo+1)
					pageNo++;
				else
					hasMore = false;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void getItemDetail() {
		String brokerId = "system";
		List<String> ids = new ArrayList<String>();
		ids.add("6918354723321184349");
		ids.add("6918710001798316112");
		ids.add("6918808670566474496");
		try {
			List<GoodsInfo> result = vipHelper.getItemDetail(brokerId, ids);
			for(GoodsInfo goods:result)
				System.err.println(goods.getGoodsName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void getCategory() {
		long parentId = 0;
		int grade = 1;
		try {
			List<Category> result = vipHelper.getCategory(parentId, grade);
			for(Category category:result)
				System.err.println(category.getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void generatePid() {
		String brokerId = "08d9c0f875634670b59ea412465bd951";
		try {
			CpsUnionPidGenResponse result = vipHelper.generatePid(brokerId);
			System.err.println("[total]"+result.getTotal());
			System.err.println("[remain]"+result.getRemainPidCount());
			List<PidInfo> pids = result.getPidInfoList();
			for(PidInfo pid:pids)
				System.out.println(pid.getPidName()+"-->"+pid.getPid());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	
	@Test
	public void getOrders() {
		int pageSize = 100;
		int pageNo = 1;//默认页码从1开始
		int status = 1;//需要分别查询待完结及已完结订单：1：待结算，2：已结算
		
		Calendar cal = Calendar.getInstance();
		long end = cal.getTime().getTime();
		cal.add(Calendar.MINUTE, -30);//每半小时查询一次
		long from = cal.getTime().getTime();
		
		boolean hasMore = true;
		
		try {
			while(hasMore) {
				OrderResponse result = vipHelper.getOrders(status, from, end, pageSize, pageNo);
				System.err.println("[total]"+result.getTotal());
				int totalPages = (result.getTotal()+pageSize-1)/pageSize;
				//TODO process order list
				if(totalPages > pageNo)
					pageNo++;
				else
					hasMore = false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void itemSyncTask() {
		try {
			vipSyncTask.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void itemSearchTask() {
		try {
			vipSearchTask.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void orderSyncTask() {
		try {
			vipOrderSyncTask.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
