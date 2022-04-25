package com.pcitech.iLife.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class WxHelper {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static WxHelper helper = null;
	
	public static WxHelper getInstance() {
		if(helper == null) {
			helper = new WxHelper();
		}
		return helper;
	}
	  /**
	   * 根据URL地址爬取微信文章 标题、logo、作者、时间、
	   * @param url
	   * @return JSONObject
	   * 	author:作者
	   * 	title：标题
	   * 	coverImg：封面图片
	   * 	publishOn：发布时间
	 * @throws IOException 
	   */
	  public JSONObject getWxArticleInfo(String url) throws IOException {
		  logger.debug("start request article. [url]"+url);
		  JSONObject data = new JSONObject();
		  
		  //请求页面
		  Document doc = Jsoup.connect(url).timeout(3000).get();
		  
		  //获取标题
			Elements titles = doc.getElementsByClass("rich_media_title");
			String title = titles.text();
			if(title != null && title.trim().length() ==0)//标题有时无法获取，前端会采用默认设置
				data.put("title", title);
			logger.debug("got title. [title]"+title);
		  //获取封面图片
			String picUrl = null;
			int flag;
			String htmlString=doc.toString();
			flag=htmlString.indexOf("cdn_url_1_1");//获取1：1图片 //("msg_cdn_url");
			while(htmlString.charAt(flag)!='\"'){
				flag++;
			}
			int beginIndex=++flag;
			while(htmlString.charAt(flag)!='\"')
				flag++;
			int endIndex=--flag;
			picUrl=htmlString.substring(beginIndex,endIndex);
			logger.debug("got coverImg. [coverImg]"+picUrl);
			data.put("coverImg", picUrl);

		  //获取作者
			Element authors = doc.getElementById("js_name");
			String author = authors.text();
			data.put("author", author);
			logger.debug("got author. [author]"+author);
		  //获取发布时间
			/**
			String time=null;
			Elements scripts = doc.select("script");
	        for (Element script : scripts) {
	            String html = script.html();
	            if (html.contains("document.getElementById(\"publish_time\")")) {
	                int fromIndex = html.indexOf("s=\"");
	                time=html.substring(fromIndex+3,fromIndex+13);
	                break;
	            }
	        }
	        data.put("publishTime", time);
	        logger.debug("got publishTime. [publishTime]"+time);
	        //**/
		  return data;
	  }
}
