package com.pcitech.iLife.cps.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcitech.iLife.cps.crawler.impl.Jd;
import com.pcitech.iLife.cps.crawler.impl.Pdd;
import com.pcitech.iLife.cps.crawler.impl.Vip;
import com.pcitech.iLife.modules.mod.service.PlatformSourceService;

@Service
public class CrawlerUtil {
	private static Logger logger = LoggerFactory.getLogger(CrawlerUtil.class);
	
    @Autowired  PlatformSourceService platformSourceService;
    
	@Autowired Pdd pdd;
	@Autowired Jd jd;
	@Autowired Vip vip;
	
	//提供一个工厂方法，能够根据类型获取爬虫实例
	public Crawler getCrawler(String url) {
		String type = getType(url);
		if("pdd".equalsIgnoreCase(type))
			return pdd;
		else if("jd".equalsIgnoreCase(type))
			return jd;
		else if("vip".equalsIgnoreCase(type))
			return vip;
		else
			return null;
	}
	
	//根据url判定类型
	private String getType(String url) {
		String type = platformSourceService.getPlatformByUrl(url);
		logger.debug("got url type.[type]"+type,url);
		return type;
	}
	
}
