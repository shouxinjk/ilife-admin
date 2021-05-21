package com.pcitech.iLife.cps;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.jd.open.api.sdk.internal.JSON.JSON;
import com.jd.open.api.sdk.internal.util.JsonUtil;
import com.pcitech.iLife.cps.kaola.GoodsInfoResponse;
import com.pcitech.iLife.cps.kaola.OrderInfoResponse;
import com.pcitech.iLife.cps.kaola.ShareLinkResponse;
import com.pcitech.iLife.task.KaolaItemSync;

import net.minidev.json.JSONUtil;


@RunWith(SpringJUnit4ClassRunner.class) 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class KaolaClientTest {
	
	@Autowired
	KaolaHelper kaolaHelper;
	@Autowired
	KaolaItemSync kaolaItemSync;
	
	//网易开发人员比较挫，返回的汉字竟然是GB2312，还要转一次
	public String gb2312ToUtf8(String str) {
        String urlEncode = "" ;
        try {
            urlEncode = URLEncoder.encode (str, "UTF-8" );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return urlEncode;
	}

    public String getEncoding(String str) {    
        String encode = "GB2312";    
       try {    
           if (str.equals(new String(str.getBytes(encode), encode))) {    
                String s = encode;    
               return s;    
            }    
        } catch (Exception exception) {    
        }    
        encode = "ISO-8859-1";    
       try {    
           if (str.equals(new String(str.getBytes(encode), encode))) {    
                String s1 = encode;    
               return s1;    
            }    
        } catch (Exception exception1) {    
        }    
        encode = "UTF-8";    
       try {    
           if (str.equals(new String(str.getBytes(encode), encode))) {    
                String s2 = encode;    
               return s2;    
            }    
        } catch (Exception exception2) {    
        }    
        encode = "GBK";    
       try {    
           if (str.equals(new String(str.getBytes(encode), encode))) {    
                String s3 = encode;    
               return s3;    
            }    
        } catch (Exception exception3) {    
        }    
       return "";    
    } 
	
	private List<String> getGoodsSignList(){
		List<String> goodsSignList = new ArrayList<String>();
		goodsSignList.add("Y932ms86eklU8LcVwvfZA33k3lQMIJzP_JJhfhOR1G");
		goodsSignList.add("Y9X2lY1QjM9U8LcVwvfZA-r_Jyi2DPTd_J1AZLvO7B");
		return goodsSignList;
	}
	
	@Test
	public void getItemDetail() {
		System.out.println("now start query item details ... ");
		String brokerId = "default";
		String goodsIds = "8248852,1488637,5680881";
		GoodsInfoResponse goodsInfoResponse = kaolaHelper.getItemDetail(brokerId, goodsIds);
		try {
			System.out.println(JsonUtil.toJson(goodsInfoResponse));
			System.out.println(JsonUtil.toJson(goodsInfoResponse.getData().get(0).getBaseInfo().getImageList()));
			System.err.println(getEncoding(goodsInfoResponse.getData().get(0).getBaseInfo().getBrandCountryName()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void getOrder() {
		System.out.println("now start query orders ... ");
		OrderInfoResponse orderInfoResponse = kaolaHelper.getOrder();
		try {
			System.out.println(JsonUtil.toJson(orderInfoResponse));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void convertLinks() {
		System.out.println("now start convert links ... ");
		List<String> links = new ArrayList<String>();
		links.add("http://lu.kaola.com/6UAZ3vUW");
		links.add("https://m-goods.kaola.com/product/8248852.html");
		links.add("https://goods.kaola.com/product/8248852.html");
		links.add("https://cps.kaola.com/cps/mini/zhuankeLogin?unionId=zhuanke_701412896&tc1=default&showWapBanner=0&rd=259200000&targetUrl=https%3A%2F%2Fm-goods.kaola.com%2Fproduct%2F8248852.html%3F__da_dad3e203_5c4e7c4025b92c00");
		links.add("https://cps.kaola.com/cps/zhuankeLogin?unionId=zhuanke_701412896&tc1=default&showWapBanner=0&targetUrl=https%3A%2F%2Fm-goods.kaola.com%2Fproduct%2F8248852.html%3F__da_dad3e203_5c4e7c4025b92c00");
		links.add("http://lu.kaola.com/1OF4Ga");
		links.add("https://goods.kaola.com/product/6241234.html");
		ShareLinkResponse shareLinkResponse = kaolaHelper.convertLinks("default", links);
		try {
			System.out.println(JsonUtil.toJson(shareLinkResponse));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert true;
	}
	
	@Test
	public void syncData() {
		try {
			kaolaItemSync.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
