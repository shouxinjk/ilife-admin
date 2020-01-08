package com.pcitech.iLife.cps.taobao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.pcitech.iLife.task.TaobaoItemSync;
import com.taobao.api.ApiException;

@RunWith(SpringJUnit4ClassRunner.class) 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class TaobaoClientTest {
	
	@Autowired
	TaobaoHelper taobaoHelper;
	
	@Autowired
	TaobaoItemSync taobaoSyncTask;
	
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

}
