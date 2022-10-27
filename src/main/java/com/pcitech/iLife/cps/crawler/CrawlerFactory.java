package com.pcitech.iLife.cps.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcitech.iLife.cps.crawler.impl.Jd;
import com.pcitech.iLife.cps.crawler.impl.Pdd;
import com.pcitech.iLife.modules.mod.service.PlatformSourceService;

@Service
public class CrawlerFactory {
    @Autowired  PlatformSourceService platformSourceService;
    
	@Autowired Pdd pdd;
	@Autowired Jd jd;
	
	//提供一个工厂方法，能够根据类型获取爬虫实例
	public Crawler getCrawler(String url) {
		String type = getType(url);
		if("pdd".equalsIgnoreCase(type))
			return pdd;
		else if("jd".equalsIgnoreCase(type))
			return jd;
		else
			return null;
	}
	
	//根据url判定类型
	private String getType(String url) {
		String type = platformSourceService.getPlatformByUrl(url);
		if("notsupport".equalsIgnoreCase(type))
			return "jd";
		else
			return type;
	}
	
}
