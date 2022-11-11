package com.pcitech.iLife.cps;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.alibaba.fastjson.JSONObject;
import com.pcitech.iLife.cps.crawler.Crawler;
import com.pcitech.iLife.cps.crawler.CrawlerUtil;
import com.pcitech.test.SpringDataProviderRunner;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

//@RunWith(SpringJUnit4ClassRunner.class) 
@RunWith(SpringDataProviderRunner.class)
//@RunWith(DataProviderRunner.class) //cannot work. it will disable @Autowired because lack of spring context. use SpringDataProviderRunner instead.
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class CrawlerTest {
	private static Logger logger = LoggerFactory.getLogger(CrawlerTest.class);
	@Autowired CrawlerUtil crawlerFactory;
	
    @DataProvider
    public static Object[][] urls() {
        return new Object[][] {
//            {"https://item.jd.com/35519901050.html","o8HmJ1EdIUR8iZRwaq1T7D_nPIYc",true },
            {"https://detail.vip.com/detail-1710613779-6919844953155537491.html","o8HmJ1EdIUR8iZRwaq1T7D_nPIYc",true },
            {"https://detail.vip.com/detail-1710617498-6919247833528892314.html","o8HmJ1EdIUR8iZRwaq1T7D_nPIYc",true },
//            {"https://mobile.yangkeduo.com/duo_coupon_landing.html?goods_id=247148672711&pid=20434335_206807608&goods_sign=E932m-F7iFNU8LcRwfDaiZopXsF9TOKL_JQENcpyaEj&cpsSign=CC_220531_20434335_206807608_167757c35a33e72414d1bf494a2ac57a&_x_ddjb_act=%7B%22st%22%3A%221%22%7D&duoduo_type=2","o8HmJ1EdIUR8iZRwaq1T7D_nPIYc",false },
//            {"https://mobile.yangkeduo.com/duo_coupon_landing.html?goods_id=244713040093&pid=20434335_206807608&goods_sign=Y9X2m5w0QUlU8LcRwfDZiTXpvAkzF4kx_J6zypCKUl&customParameters=%7B%22uid%22%3A%2220434335%22%2C%22brokerId%22%3A%22default%22%7D&cpsSign=CC_210518_20434335_206807608_42f6b8466c9b619e8171d8f0e054339c&duoduo_type=2","o8HmJ1EdIUR8iZRwaq1T7D_nPIYc",true}
        };
    }
    

	@Test
	@UseDataProvider("urls")
	public void crawl(String url, String openid, boolean expect) {
		Crawler crawler = crawlerFactory.getCrawler(url);
		JSONObject obj = crawler.enhouse(url, openid);
		System.err.println("got result"+obj);
//		assert obj.getBooleanValue("success") ==  expect;
		assertEquals(obj.getBooleanValue("success"),  expect);
	}
	
}
