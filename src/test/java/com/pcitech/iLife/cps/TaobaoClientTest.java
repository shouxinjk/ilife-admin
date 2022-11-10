package com.pcitech.iLife.cps;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.pcitech.iLife.task.TaobaoItemSync;
import com.pcitech.iLife.task.TaobaoItemsSearcher;
import com.pcitech.test.SpringDataProviderRunner;
import com.taobao.api.ApiException;
import com.taobao.api.response.TbkDgOptimusMaterialResponse;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

//@RunWith(SpringJUnit4ClassRunner.class) 
@RunWith(SpringDataProviderRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class TaobaoClientTest {
	
	@Autowired
	TaobaoHelper taobaoHelper;
	
	@Autowired
	TaobaoItemsSearcher taobaoItemsSearcher;
	
	@Autowired
	TaobaoItemSync taobaoSyncTask;
	
    @DataProvider
    public static Object[][] urls() {
        return new Object[][] {
            {"https://s.click.taobao.com/YI3Uopu",true },
            {"https://s.click.taobao.com/BnQEBRu",false },
            {"https://detail.tmall.com/item_o.htm?ali_trackid=2:mm_40898641_151950481_44929750210:1668041893_051_1016785630&spm=a2159r.13376465.0.0&pvid=null&releaseId=318828&union_lens=lensId:TAPI@1668041256@2127f5d2_09c9_1845f01aff7_411b@035ERW7S1aAPCuXmnZhWw7ZE;recoveryid:1668041893_051_1016785630&bxsign=tbkPiwRSKUkgZY5V20qH2Iqukhr3JG_m3DfevUd0vYbqZnggReq-10LfsnfBKx_Ypk2pfX-8Xs18b9XD23IY09DSUSoVbA9lCmJ9qQhZkW8NjBMaX-cK-lFqkphZwNYVyczAI91c2mPohkvFfMDC67woA&un=27f22f092a0a15f72040bd82ddc2e1ea&bucketId=447258&id=682168120476&scm=null&ut_sk=1.utdid_27658042_1668041856890.TaoPassword-Outside.taoketop",true },
            {"https://s.click.taobao.com/BnQEBRu",false },
           };
    }
	
	@Test
	@UseDataProvider("urls")
	public void getTaobaoToken(String url, boolean isNull) throws ApiException {
		String token = taobaoHelper.getTaobaoToken(url);
		if(isNull) {
			assert token == null ;
		}else {
			assert token != null ;
			System.err.println(url+"\n"+token);
		}
	}
	
	@DataProvider
    public static Object[][] materialIds() {
        return new Object[][] {
            {39313L },
            {27913L },
            {3788L },
            {27454L },
           };
    }
	@Test
	@UseDataProvider("materialIds")
	public void getOptimusMaterial(long materialId) throws ApiException {
		List<TbkDgOptimusMaterialResponse.MapData> items = taobaoHelper.getOptimusMaterial(materialId,2);
		for(TbkDgOptimusMaterialResponse.MapData item:items) {
			System.err.println("price.sale="+item.getZkFinalPrice()+"\tprice.bid="+item.getReservePrice());
		}
	}
	
	@Test
	public void queryItemDetail() {
		System.out.println("now start query item details ... ");
		try {
			taobaoHelper.getItemDetail("535615570326,594562185909,535615570326");
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void syncTaobaoData() {
		try {
			taobaoSyncTask.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void searchTask() {
		try {
			taobaoItemsSearcher.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
