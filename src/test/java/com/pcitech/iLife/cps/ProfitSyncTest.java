package com.pcitech.iLife.cps;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.pcitech.iLife.task.CalcProfit;
import com.pcitech.iLife.task.CalcProfit2Party;
import com.pcitech.iLife.task.CpsLinkSync;
import net.minidev.json.JSONUtil;


@RunWith(SpringJUnit4ClassRunner.class) 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class ProfitSyncTest {
	
	@Autowired
	CalcProfit2Party calcProfit2PartyTask;
	
	@Autowired
	CalcProfit calcProfit1PartyTask;
	
	@Test
	public void calcProfit2Party() {
		try {
			calcProfit2PartyTask.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void calcProfit() {
		try {
			calcProfit1PartyTask.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
