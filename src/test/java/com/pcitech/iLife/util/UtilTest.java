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
public class UtilTest {
	private static Logger logger = LoggerFactory.getLogger(UtilTest.class);

	@Test
	public void get8bitCode() throws Exception {
		System.err.println(Util.get8bitCode("顶级达人"));
		System.err.println(Util.get8bitCode("顶级达人"));
		System.err.println(Util.get8bitCode("顶级达人"));
		System.err.println(Util.get8bitCode("顶级达人"));
		System.err.println(Util.get8bitCode(null));
		System.err.println(Util.get8bitCode(null));
		System.err.println(Util.get8bitCode(null));
	}
	
	@Test
	public void get6bitCode() throws Exception {
		System.err.println(Util.get6bitCode("顶级达人"));
		System.err.println(Util.get6bitCode("顶级达人"));
		System.err.println(Util.get6bitCode("顶级达人"));
		System.err.println(Util.get6bitCode("顶级达人"));
		System.err.println(Util.get6bitCode(null));
		System.err.println(Util.get6bitCode(null));
		System.err.println(Util.get6bitCode(null));
	}
	
}
