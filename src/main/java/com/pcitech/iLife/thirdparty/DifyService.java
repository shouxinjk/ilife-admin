package com.pcitech.iLife.thirdparty;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.pcitech.iLife.util.HttpClientHelper;

public class DifyService {
	private static Logger logger = LoggerFactory.getLogger(DifyService.class);
	@Value("#{APP_PROP['dify.endpoint']}")
	private String endpoint;
	@Value("#{APP_PROP['dify.apiKey']}")
	private String apiKey;
	
	//聊天应答
	public JSONObject getAnswer(String user, String query) {
		Map<String, String> headers = Maps.newHashMap();
		headers.put("Authorization", "Bearer "+apiKey);
		JSONObject data = new JSONObject();
		data.put("respond_mode", "streaming");
		data.put("inputs", new JSONObject());//置空
		data.put("user", user);
		data.put("query", query);
		logger.debug("post request.\nheaders:\n"+headers+"\ndata:\n"+data);
	     return HttpClientHelper.getInstance().post(endpoint+"/chat-messages", data, headers);
	}
	
	//聊天应答
	public JSONObject getAnswer(JSONObject data) {
		Map<String, String> headers = Maps.newHashMap();
		headers.put("Authorization", "Bearer "+apiKey);
		logger.debug("post request.\nheaders:\n"+headers+"\ndata:\n"+data);
	     return HttpClientHelper.getInstance().post(endpoint+"/chat-messages", data, headers);
	}
	
	//生成内容：当前不工作，直接用getAnswer
	public JSONObject genContent(JSONObject data) {
		Map<String, String> headers = Maps.newHashMap();
		headers.put("Authorization", "Bearer "+apiKey);
		logger.debug("post request.\nheaders:\n"+headers+"\ndata:\n"+data);
	     return HttpClientHelper.getInstance().post(endpoint+"/completion-messages", data, headers);
	}
	
	//生成内容：当前不工作，直接用getAnswer
	public JSONObject genContent(String user, String query) {
		Map<String, String> headers = Maps.newHashMap();
		headers.put("Authorization", "Bearer "+apiKey);
		JSONObject data = new JSONObject();
		data.put("respond_mode", "streaming");
		data.put("inputs", new JSONObject());//置空
		data.put("user", user);
		data.put("query", query);
		logger.debug("post request.\nheaders:\n"+headers+"\ndata:\n"+data);
	     return HttpClientHelper.getInstance().post(endpoint+"/completion-messages", data, headers);
	}
	
}
