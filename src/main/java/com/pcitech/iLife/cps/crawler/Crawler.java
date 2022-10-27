package com.pcitech.iLife.cps.crawler;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.mod.service.PlatformCategoryService;
import com.pcitech.iLife.util.ArangoDbClient;

/**
 * 爬虫助手抽象类。通过API获取CPS商品信息、导购链接及佣金
 *
 */
public abstract class Crawler {
	//根据URL执行入库方法。包括详细信息、CPS链接、佣金。输入为URL或者口令
	public abstract JSONObject enhouse(String url, String openid);
	
	//私有方法
    protected double parseNumber(double d) {
    	NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);	// 保留两位小数
		nf.setRoundingMode(RoundingMode.DOWN);// 如果不需要四舍五入，可以使用RoundingMode.DOWN
		String numStr = nf.format(d);
		try {
			return Double.parseDouble(numStr);
		}catch(Exception ex) {
			return d;
		}
    }
}
