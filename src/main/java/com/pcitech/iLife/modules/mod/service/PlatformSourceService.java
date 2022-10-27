/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcitech.iLife.common.persistence.Page;
import com.pcitech.iLife.common.service.CrudService;
import com.pcitech.iLife.modules.mod.entity.PlatformSource;
import com.pcitech.iLife.modules.mod.dao.PlatformSourceDao;

/**
 * 商品数据来源Service
 * @author ilife
 * @version 2022-03-03
 */
@Service
@Transactional(readOnly = true)
public class PlatformSourceService extends CrudService<PlatformSourceDao, PlatformSource> {
	//查询所有可用source
	public List<PlatformSource> findActiveSources(PlatformSource platformSource) {
		return dao.findActiveSources(platformSource);
	}
	
	public PlatformSource get(String id) {
		return super.get(id);
	}
	
	public List<PlatformSource> findList(PlatformSource platformSource) {
		return super.findList(platformSource);
	}
	
	public Page<PlatformSource> findPage(Page<PlatformSource> page, PlatformSource platformSource) {
		return super.findPage(page, platformSource);
	}
	
	@Transactional(readOnly = false)
	public void save(PlatformSource platformSource) {
		super.save(platformSource);
	}
	
	@Transactional(readOnly = false)
	public void delete(PlatformSource platformSource) {
		super.delete(platformSource);
	}
	
	public String getPlatformByUrl(String url) {
		PlatformSource q = new PlatformSource();
		List<PlatformSource> platformSources = dao.findList(q);
		for(PlatformSource platformSource:platformSources) {
			if(platformSource.getMatchExpr()==null || platformSource.getMatchExpr().trim().length()==0)
				continue;
			String[] urlExprs = platformSource.getMatchExpr().split(" ");
			for(String expr: urlExprs) {
				Pattern p=Pattern.compile(expr); 
				Matcher m=p.matcher(url); 
				while(m.find()) { //仅处理第一个即可
					return platformSource.getPlatform();
				}
			}
		}
		return "notsupport";
	}
	
}