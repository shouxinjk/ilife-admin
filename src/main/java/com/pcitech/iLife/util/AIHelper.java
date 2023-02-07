package com.pcitech.iLife.util;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.pcitech.iLife.common.config.Global;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;

public class AIHelper {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static AIHelper helper = null;
	
	public static AIHelper getInstance() {
		if(helper == null) {
			helper = new AIHelper();
		}
		return helper;
	}
	
	  public String requestChatGPT(String keyword) {
		  OpenAiService service = new OpenAiService(Global.getConfig("chatgpt.apikey"),120);
			CompletionRequest completionRequest = CompletionRequest.builder()
			        .prompt(keyword)
			        .model("text-davinci-003")
			        .maxTokens(keyword.length()*2+1000)
			        .echo(true)
			        .build();
			List<CompletionChoice> choices = service.createCompletion(completionRequest).getChoices();
			if(choices!=null && choices.size()>0) {
				return choices.get(0).getText().replaceAll("\\n","")
						.replace(keyword+"？","").replace(keyword+"?","")
						.replace(keyword+"：","").replace(keyword+":","")
						.replace(keyword+" ","");
			}
			return "";
	  }
}
