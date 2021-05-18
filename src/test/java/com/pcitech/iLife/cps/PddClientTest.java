package com.pcitech.iLife.cps;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.pcitech.iLife.task.PddItemSync;
import com.pcitech.iLife.task.TaobaoItemSync;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsDetailResponse.GoodsDetailResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse.GoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsZsUnitUrlGenResponse.GoodsZsUnitGenerateResponse;
import com.taobao.api.ApiException;

@RunWith(SpringJUnit4ClassRunner.class) 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class PddClientTest {
	
	@Autowired
	PddHelper pddHelper;
	
	@Autowired
	PddItemSync pddSyncTask;
	
	private List<String> getGoodsSignList(){
		List<String> goodsSignList = new ArrayList<String>();
		goodsSignList.add("Y932ms86eklU8LcVwvfZA33k3lQMIJzP_JJhfhOR1G");
		goodsSignList.add("Y9X2lY1QjM9U8LcVwvfZA-r_Jyi2DPTd_J1AZLvO7B");
		return goodsSignList;
	}
	
	@Test
	public void getItemDetail() {
		System.out.println("now start query item details ... ");
		String brokerId = "alexchew";
		try {
			GoodsDetailResponse resp = pddHelper.getItemDetail(brokerId, "Y9X2mrzObNZU8LcVwvfZAj3OWHofN1ZJ_JmVkDoMpC");
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
			GoodsZsUnitGenerateResponse resp = pddHelper.generateCpsLinksByGoodsUrl(brokerId,"https://jinbao.pinduoduo.com/goods-detail?s=Y9z2s_dyW-dU8LcVwvfZCxeQHhqL5_0E_J976NZBG2");
			System.err.println("Mobile URL::"+resp.getMobileUrl());
			System.err.println("Web URL::"+resp.getUrl());
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
			PddDdkGoodsPromotionUrlGenerateResponse response = pddHelper.generateCpsLinksByGoodsSign(brokerId,getGoodsSignList());
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

}
