package com.pcitech.iLife.cps;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.jd.open.api.sdk.domain.kplunion.promotioncommon.PromotionService.response.get.PromotionCodeResp;
import com.pcitech.iLife.cps.kaola.GoodsInfoResponse;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.CpsLinkScheme;
import com.pcitech.iLife.modules.mod.entity.TraceCode;
import com.pcitech.iLife.modules.mod.service.CpsLinkSchemeService;
import com.pcitech.iLife.modules.mod.service.TraceCodeService;
import com.pcitech.iLife.modules.mod.web.CpsLinkSchemeController;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse;
import com.vip.adp.api.open.service.UrlInfo;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;


//通过手动拼接得到CPS链接
//结合CpsLink配置完成
@Service
public class CpsLinkHelper {
	private static Logger logger = LoggerFactory.getLogger(CpsLinkHelper.class);
	@Autowired
	private CpsLinkSchemeService cpsLinkSchemeService;
	@Autowired
	private TraceCodeService traceCodeService;
	
	@Autowired
	PddHelper pddHelper;
    @Autowired
    JdHelper jdHelper;
    @Autowired
    KaolaHelper kaolaHelper;
    @Autowired
    SuningHelper suningHelper;
    @Autowired
    VipHelper vipHelper;
    
    /**
     * 动态生成CPS链接。包括两种类型：
     * 1，通过CPSLink模板配置的链接规则，采用相同的模板，同时生成 web、wap 链接
     * 2，对于通过调用第三方SDK生成的链接，通过callRemoteApi进行控制，设置为false则不处理
     * @param brokerId
     * @param source
     * @param url
     * @param category
     * @param callRemoteApi
     * @return
     */
	public Map<String, Object> getCpsLink(String brokerId,String source, String url, String category,boolean callRemoteApi) {
		Map<String, Object> map = Maps.newHashMap();
		
		//根据source查询是否存在CPS链接规则
		CpsLinkScheme query = new CpsLinkScheme();
		query.setCategory(category);
		query.setPlatform(source);
		CpsLinkScheme scheme = cpsLinkSchemeService.getByQuery(query);
		if(scheme == null && category!=null && category.trim().length()>0) {//如果不存在指定来源及分类的链接，则使用来源默认规则
			logger.info("no cps shceme match source="+source+" and category="+category+". try to use source default one.");
			query.setCategory(null);//取消category过滤
			scheme = cpsLinkSchemeService.getByQuery(query);
		}
		
		//接下来逐步处理
		if(scheme == null) {//如果不存在链接规则，则直接返回
			map.put("status", false);//默认认为直接采用原始
			map.put("description", "no cps scheme");
			return map;
		}else {//查询broker推广位并根据脚本计算得到链接
			//根据brokerId及source查询推广位
			Broker broker = new Broker();
			broker.setId(brokerId);
			
			TraceCode traceCode = new TraceCode();
			traceCode.setBroker(broker);
			traceCode.setPlatform(source);
			traceCode = traceCodeService.getByBrokerAndPlatform(traceCode);
			if(traceCode==null) {//没有该达人的推广位，则尝试查询system达人的推广位
				traceCode = new TraceCode();
				traceCode.setPlatform(source);
				broker.setId("system");
				traceCode.setBroker(broker);
				traceCode = traceCodeService.getByBrokerAndPlatform(traceCode);
				if(traceCode == null) {//如果连system都没有推广位，表示不需要根据推广位计算链接，traceCode参数不会传递到脚本
					map.put("status", false);
					map.put("description", "no system trace code. [important] do not use traceCode in cpsScheme!!!");
					//注意：对于通过SDK生成CPS的情况，即使没有trace code也可以继续生成，因为不会使用trace code参数。此处不终止处理
					//return map;
				}else {//用system达人的推广位计算链接
					map.put("status", true);
					map.put("broker", "system");
					map.put("traceCode",traceCode.getCode());
					map.put("description", "use system trace code.");
				}
			}else {//有则使用对应达人的推广位计算链接
				map.put("status", true);
				map.put("broker", brokerId);
				map.put("traceCode",traceCode.getCode());
				map.put("description", "use broker specified trace code.");
			}
			
			//获取定义的脚本。其中 remote-api-call为关键字，表示需要调用API完成URL获取，需要优先处理。当前pdd、jd、suning据需要设置
			String  script = org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(scheme.getScript());
			if(callRemoteApi && "remote-api-call".equalsIgnoreCase(script.trim())) {//对于设置为远端调用的情况需要根据source分别处理
				switch(source) {
				case "pdd":
					String goodsSign = url.replace("https://jinbao.pinduoduo.com/goods-detail?s=", "");//取得good_sign
					List<String> goodsSignList = new ArrayList<String>();
					goodsSignList.add(goodsSign);
					try {
						PddDdkGoodsPromotionUrlGenerateResponse ret = pddHelper.generateCpsLinksByGoodsSign(brokerId,null,goodsSignList);//直接用brokerId跟踪
						map.put("link", ret.getGoodsPromotionUrlGenerateResponse().getGoodsPromotionUrlList().get(0).getMobileShortUrl());//返回移动端短连接
						map.put("status", true);
					} catch (Exception e) {
						String msg = "failed generate cps link for borker.[brokerId]"+brokerId+"[url]"+url;
						logger.error(msg,e);
						map.put("status", false);
						map.put("description", msg);
					}
					return map;
				case "jd":
					try {
						PromotionCodeResp ret = jdHelper.getCpsLink(url,map.get("traceCode").toString());//使用traceCode作为跟踪参数
						map.put("link", ret.getClickURL());
						map.put("status", true);
					}catch(Exception ex) {
						String msg = "failed generate cps link for borker.[brokerId]"+brokerId+"[url]"+url;
						logger.error(msg,ex);
						map.put("status", false);
						map.put("description", msg);
					}
					return map;
				case "kaola":
					Pattern p=Pattern.compile("\\d+"); 
					Matcher m=p.matcher(url); 
					while(m.find()) { //仅处理第一个即可
					    String skuId = m.group(); 
					    GoodsInfoResponse goods = kaolaHelper.getItemDetail(brokerId, skuId);//使用达人ID跟踪
					    map.put("link", goods.getData().get(0).getLinkInfo().getShareUrl());
					    map.put("status", true);
					    return map;
					}
					map.put("status", false);
					map.put("description", "failed generate cps link for borker.[brokerId]"+brokerId+"[url]"+url);
					return map;
				case "suning":
					JSONObject ret = suningHelper.generateCpsLink(brokerId, url);
					if(ret != null) {
						map.put("link", URLDecoder.decode(ret.getString("extendUrl"))+"&sub_user="+brokerId);
						map.put("status", true);
					}else { //部分商品可能获取失败：会导致链接不会被更新
						map.put("status", false);
						map.put("description", "failed generate cps link for borker.[brokerId]"+brokerId+"[url]"+url);
					}
					return map;
				case "vip":
					try {
						List<UrlInfo> result = vipHelper.generateCpsLinkByUrl( brokerId, url,"wechat");
						map.put("link", result.get(0).getLongUrl());
						map.put("status", true);
					} catch (Exception ex) {
						logger.error("failed generate vip cps link.",ex);
						map.put("status", false);
						map.put("description", "failed generate cps link for borker.[brokerId]"+brokerId+"[url]"+url);
					}
					return map;
				default:
					map.put("status", false);
					map.put("description", "failed generate cps link for borker.[brokerId]"+brokerId+"[url]"+url);
					return map;
				}
			}else {//否则获取脚本完成URL拼接
				
				//注意：脚本中字符串操作需要unescape，否则如包含双引号等会出现解析错误
				//return url+'?TypeID=2&AllianceID='+traceCode+'&sid=1611278&ouid=&app=0101X00&'
				
				Binding binding = new Binding();
				binding.setVariable("source",source);
				binding.setVariable("category",category);
				binding.setVariable("url",url);
				if(traceCode!=null) {//traceCode仅用于需要通过第三方平台推广位跟踪的情况，在脚本中不需要tracecode的话可以不传递。告诉运营要脑袋瓜子保持清醒哦，别在cpsScheme中用了traceCode，又忘了在traceCode里配置。
					binding.setVariable("traceCode",traceCode.getCode());//是系统在第三方电商平台的推广位。少数平台如淘宝，需要手动给达人创建推广位
				}
				binding.setVariable("brokerId",brokerId);//是达人编码，同时是扩展跟踪码
				try {
			        GroovyShell shell = new GroovyShell(binding);
			        Object value = shell.evaluate(script);//计算得到目标url
			        map.put("link", value.toString());
				}catch(Exception ex) {//如果计算发生错误也使用默认链接
					String msg = "failed generate cps link by script for borker.[brokerId]"+brokerId+"[url]"+url;
					logger.error(msg,ex);
					map.put("status", false);
					map.put("description", msg+"[error]"+ex.getMessage());
				}
		        return map;
			}
		}
	}
	
}
