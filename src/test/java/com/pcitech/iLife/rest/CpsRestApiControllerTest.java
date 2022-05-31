package com.pcitech.iLife.rest;

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

import com.alibaba.fastjson.JSONObject;
import com.pcitech.iLife.modules.mod.web.CpsRestApiController;
import com.pcitech.iLife.task.CpsLinkSync;
import net.minidev.json.JSONUtil;


@RunWith(SpringJUnit4ClassRunner.class) 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class CpsRestApiControllerTest {
	
	@Autowired
	CpsRestApiController cpsRestApiController;
	
	@Test
	public void pdd() {
		String openid="o8HmJ1JeYicv-JFGPyHvicLO6QQ8";
		//有佣金
//		String url = "https://mobile.yangkeduo.com/goods.html?goods_id=168100600611&page_from=0&pxq_secret_key=QXUHBYEDQJCXVCNLDX4OMNNQNGV7RISQADX6QAI54DUKSSP4TOCA&share_uin=CGBDTV2LTOI773KSGBKZGODYHY_GEXDA&refer_share_id=65f1fb622c2a4d018da4c2d9c48871cd&refer_share_uid=8195428613276&refer_share_uin=CGBDTV2LTOI773KSGBKZGODYHY_GEXDA&refer_share_channel=copy_link&refer_share_form=text";
		//无佣金
		String url = "https://mobile.yangkeduo.com/goods2.html?goods_id=202437618555&page_from=23&pxq_secret_key=QXUHBYEDQJCXVCNLDX4OMNNQNHCDRCU3ZDZXYHB6YUVC5GRQK5TA&share_uin=CGBDTV2LTOI773KSGBKZGODYHY_GEXDA&refer_share_id=a4b55537e6fd4967b7028252f828b584&refer_share_uid=8195428613276&refer_share_uin=CGBDTV2LTOI773KSGBKZGODYHY_GEXDA&refer_share_channel=copy_link&refer_share_form=text";
		JSONObject data = new JSONObject();
		data.put("url", url);
		data.put("openid", openid);
		JSONObject result = cpsRestApiController.getPddItem(data);
		System.err.println(result);
	}
}
