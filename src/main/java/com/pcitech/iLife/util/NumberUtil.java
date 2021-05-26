package com.pcitech.iLife.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NumberUtil {
	
	private static NumberUtil instance = null;
	
	public static NumberUtil getInstance() {
		if(instance == null)
			instance = new NumberUtil();
		return instance;
	}
	
	private static Logger logger = LoggerFactory.getLogger(NumberUtil.class);
    DecimalFormat df = new DecimalFormat("#.00");//double类型直接截断，保留小数点后两位，不四舍五入
	NumberFormat nf = null;
    public NumberUtil() {
		nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);	// 保留两位小数
		nf.setRoundingMode(RoundingMode.DOWN);// 如果不需要四舍五入，可以使用RoundingMode.DOWN
	}
    
    public double parseNumber(String str) {
		logger.debug("parse string value.[input]"+str);
		Double d = Double.parseDouble(str);
		String numStr = nf.format(d);
		try {
			return Double.parseDouble(numStr);
		}catch(Exception ex) {
			return 0;
		}
    }
    
    public double parseNumber(double d) {
		logger.debug("format double value.[input]"+d);
		String numStr = nf.format(d);
		try {
			return Double.parseDouble(numStr);
		}catch(Exception ex) {
			return 0;
		}
    }
}
