package com.pcitech.iLife.util;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.thirdparty.DifyService;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;

public class AIHelper {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	DifyService difyService;
	
	private static AIHelper helper = null;
	
	public static AIHelper getInstance() {
		if(helper == null) {
			helper = new AIHelper();
		}
		return helper;
	}
	
	  public String requestChatGPT(String keyword) {
		  int random = (int)Math.floor(Math.random()*100);
		  String[] apiKeys = Global.getConfig("chatgpt.apikey").split(",");
		  String apiKey = apiKeys[random%apiKeys.length];//随机获取一个
		  OpenAiService service = new OpenAiService(apiKey,120);
			CompletionRequest completionRequest = CompletionRequest.builder()
			        .prompt(keyword)
			        .model("text-davinci-003")
			        .maxTokens(keyword.length()*2+2000)
			        .echo(true)
			        .build();
			List<CompletionChoice> choices = service.createCompletion(completionRequest).getChoices();
			if(choices!=null && choices.size()>0) {
				return choices.get(0).getText();
				/**
				return choices.get(0).getText().replaceAll("\\n","")
						.replace(keyword+"？","").replace(keyword+"?","")
						.replace(keyword+"：","").replace(keyword+":","")
						.replace(keyword+" ","");
				//**/
			}
			return "";
	  }
	  
	  public JSONObject requestDify(String user, String keyword) {
		  return difyService.getAnswer(user, keyword);
	  }
}
