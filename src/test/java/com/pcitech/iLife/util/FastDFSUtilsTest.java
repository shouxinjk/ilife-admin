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
public class FastDFSUtilsTest {
	private static Logger logger = LoggerFactory.getLogger(FastDFSUtilsTest.class);

	@Test
	public void upload() throws Exception {
		
		FastDFSUtils util = FastDFSUtils.getInstance();
		
        File file = new File("/Users/alexchew/Downloads/5183444_135116531000_2.jpg");
        InputStream inputStream = new FileInputStream(file);
        int length = inputStream.available();
        byte[] bytes = new byte[length];
        inputStream.read(bytes);
        String[] result = util.upload(bytes, "icons.png", length);
        inputStream.close();
        util.close();
        for(String str:result)
        	logger.error(str);
	}
	
	@Test
	public void delete() throws Exception {
		
		FastDFSUtils util = FastDFSUtils.getInstance();
		String group = "group1";
		String[] fileIds = {"M00/00/00/ooYBAGHNSj6AbBHYAAU_rwrAg3E47.jpeg",
				"M00/00/00/oYYBAGHNR96AE4ZGAAU_rwrAg3E89.jpeg",
				"M00/00/00/oYYBAGHNUFiAP68-AAU_rwrAg3E48.jpeg",
				"M00/00/00/o4YBAGHNYs-APRe-AAU_rwrAg3E08.jpeg"};
		for(String fileId:fileIds) {
	        int result = util.delete(group, fileId);
	        logger.error("result {}",result);
		}
        util.close();
        
        
	}
}
