package com.pcitech.iLife.cps.crawler;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.arangodb.entity.BaseDocument;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.modules.mod.entity.CreditScheme;
import com.pcitech.iLife.modules.mod.entity.ProfitShareItem;
import com.pcitech.iLife.modules.mod.entity.ProfitShareScheme;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.mod.service.CommissionSchemeService;
import com.pcitech.iLife.modules.mod.service.CreditSchemeService;
import com.pcitech.iLife.modules.mod.service.PlatformCategoryService;
import com.pcitech.iLife.modules.mod.service.PlatformSourceService;
import com.pcitech.iLife.modules.mod.service.ProfitShareItemService;
import com.pcitech.iLife.modules.mod.service.ProfitShareSchemeService;
import com.pcitech.iLife.modules.sys.service.DictService;
import com.pcitech.iLife.task.CalcProfit;
import com.pcitech.iLife.task.CalcProfit2Party;
import com.pcitech.iLife.util.ArangoDbClient;
import com.pcitech.iLife.util.NumberUtil;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

@Service
public class CrawlerBase extends Crawler {
	protected static Logger logger = LoggerFactory.getLogger(CrawlerBase.class);
	protected static Logger kafkaStuffLogger = LoggerFactory.getLogger("kafkaLoggerStuff");//kafka output:提交到kafka
	protected ArangoDbClient arangoClient;
    protected String host = Global.getConfig("arangodb.host");
    protected String port = Global.getConfig("arangodb.port");
    protected String username = Global.getConfig("arangodb.username");
    protected String password = Global.getConfig("arangodb.password");
    protected String database = Global.getConfig("arangodb.database");
    protected DecimalFormat df = new DecimalFormat("#.00");//double类型直接截断，保留小数点后两位，不四舍五入
    
    @Autowired
   	protected BrokerService brokerService;
    
    @Autowired
    protected PlatformCategoryService platformCategoryService;
    
    @Autowired 
    protected PlatformSourceService platformSourceService;
    
    @Autowired
    protected CalcProfit calcProfit;
    @Autowired
    protected CalcProfit2Party calcProfit2Party;
    
	@Override
	public JSONObject enhouse(String url, String openid) {
		JSONObject result = new JSONObject();
		result.put("success", false);
		result.put("msg", "please call implementation class instead.");
		return result;
	}

}
