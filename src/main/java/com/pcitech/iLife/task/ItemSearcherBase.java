package com.pcitech.iLife.task;

import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.modules.mod.service.PlatformCategoryService;
import com.pcitech.iLife.util.ArangoDbClient;

public class ItemSearcherBase {
	protected static Logger logger = LoggerFactory.getLogger(ItemSearcherBase.class);
	protected static Logger kafkaStuffLogger = LoggerFactory.getLogger("kafkaLoggerStuff");//kafka output:提交到kafka
	protected ArangoDbClient arangoClient;
    protected String host = Global.getConfig("arangodb.host");
    protected String port = Global.getConfig("arangodb.port");
    protected String username = Global.getConfig("arangodb.username");
    protected String password = Global.getConfig("arangodb.password");
    protected String database = Global.getConfig("arangodb.database");
    protected DecimalFormat df = new DecimalFormat("#.00");//double类型直接截断，保留小数点后两位，不四舍五入
    
    @Autowired
    protected CalcProfit calcProfit;
    @Autowired
    protected CalcProfit2Party calcProfit2Party;
    @Autowired
	protected PlatformCategoryService platformCategoryService;
    
}
