package com.pcitech.iLife.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@RunWith(SpringJUnit4ClassRunner.class) 
//@WebAppConfiguration
//@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class AIHelperTest {
	private static Logger logger = LoggerFactory.getLogger(AIHelperTest.class);

	@Test
	public void chatGPT() {
		String result = AIHelper.getInstance().requestChatGPT("毕加索是一个什么样的人");
		System.err.println("got completion. "+result);
		assert result.length()>0;
	}
	
}
