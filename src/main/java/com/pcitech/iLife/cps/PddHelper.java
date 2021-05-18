package com.pcitech.iLife.cps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pcitech.iLife.common.config.Global;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.PopAccessTokenClient;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.token.AccessTokenResponse;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkGoodsDetailRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkGoodsPromotionUrlGenerateRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkGoodsZsUnitUrlGenRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkMemberAuthorityQueryRequest;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsDetailResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsDetailResponse.GoodsDetailResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse.GoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsZsUnitUrlGenResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsZsUnitUrlGenResponse.GoodsZsUnitGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkMemberAuthorityQueryResponse;


//通过API接口完成商品查询获得类目信息
@Service
public class PddHelper {
	private static Logger logger = LoggerFactory.getLogger(PddHelper.class);
	PopAccessTokenClient tokenClient  = null;
	PopClient client = null;
	
	private PopClient getClient() {
		if(client  == null) {
			client = new PopHttpClient( Global.getConfig("pdd.clientId"), Global.getConfig("pdd.clientSecret"));
		}
	    return client;
	}
	
	public GoodsDetailResponse getItemDetail(String brokerId,String goodsSign) throws Exception {
        PddDdkGoodsDetailRequest request = new PddDdkGoodsDetailRequest();
        request.setCustomParameters(getCustomParams(brokerId));
        request.setGoodsSign(goodsSign);
        request.setPid(Global.getConfig("pdd.pid"));
//        request.setSearchId("str");
//        request.setZsDuoId(0L);
        PddDdkGoodsDetailResponse response = getClient().syncInvoke(request);
        logger.debug(JsonUtil.transferToJson(response));
        return response.getGoodsDetailResponse();
	}
	
	/**
	 * 查询生成对应商品的导购链接。当前不能工作。
	 * @deprecated
	 * @param url
	 * @return
	 * @throws Exception 
	 */
	public GoodsZsUnitGenerateResponse generateCpsLinksByGoodsUrl(String brokerId,String url) throws Exception {
		PddDdkGoodsZsUnitUrlGenRequest req = new PddDdkGoodsZsUnitUrlGenRequest();
		req.setTargetClientId(Global.getConfig("pdd.clientId"));
		req.setPid(Global.getConfig("pdd.pid"));
		req.setCustomParameters(getCustomParams(brokerId));
		req.setSourceUrl(url);
		PddDdkGoodsZsUnitUrlGenResponse response = getClient().syncInvoke(req,Global.getConfig("pdd.accessToken"));
		logger.debug(JsonUtil.transferToJson(response));
		return response.getGoodsZsUnitGenerateResponse();
	}
	
	/**
	 * 根据GoodsSign生成导购链接。
	 * @param brokerId 推广达人Id
	 * @param goodsSignList 商品Sign列表。支持多个，但建议一次传递一个
	 * @return
	 * @throws Exception
	 */
	public PddDdkGoodsPromotionUrlGenerateResponse generateCpsLinksByGoodsSign(String  brokerId,List<String> goodsSignList) throws Exception {
		getClient();
		PddDdkGoodsPromotionUrlGenerateRequest request = new PddDdkGoodsPromotionUrlGenerateRequest();
//		request.setCashGiftId(0L);
//		request.setCashGiftName('str');
//		request.setCustomParameters('str');
//		request.setGenerateMallCollectCoupon(false);
//		request.setGenerateQqApp(false);
//		request.setGenerateSchemaUrl(false);
//		request.setGenerateWeApp(false);
		request.setGenerateAuthorityUrl(false);//已经完成授权，此处无需生成授权链接
		request.setGenerateShortUrl(true);
		request.setTargetClientId(Global.getConfig("pdd.clientId"));
		request.setPId(Global.getConfig("pdd.pid"));
		request.setCustomParameters(getCustomParams(brokerId));
		request.setGoodsSignList(goodsSignList);
		request.setMultiGroup(false);//false:无需拼团，true：需要拼团
//		request.setSearchId('str');
//		request.setZsDuoId(0L);
		PddDdkGoodsPromotionUrlGenerateResponse response = client.syncInvoke(request);
		return response;
	}
	
	/**
	 * 默认为平台达人，不带brokerId
	 * @return
	 */
	private String  getCustomParams() {
		return getCustomParams("default");
	}
	
	/**
	 * 将达人Id作为扩展参数，用于跟踪达人推广效果
	 * @param brokerId
	 * @return
	 */
	private String  getCustomParams(String brokerId) {
		Map<String,String> customParams = new HashMap<String,String>();
		customParams.put("uid", Global.getConfig("pdd.uid"));
		customParams.put("brokerId", brokerId);
		logger.debug("[custom_params]"+customParams);
		return JsonUtil.transferToJson(customParams);
	}
	
	public int checkAuthority() throws Exception {
		getClient();
		PddDdkMemberAuthorityQueryRequest request = new PddDdkMemberAuthorityQueryRequest();
        request.setPid(Global.getConfig("pdd.pid"));
        request.setCustomParameters(getCustomParams());
        PddDdkMemberAuthorityQueryResponse response = client.syncInvoke(request);
        logger.debug(JsonUtil.transferToJson(response));
        return response.getAuthorityQueryResponse().getBind();
	}
	
	public GoodsPromotionUrlGenerateResponse generateAuthorityUrl(List<String> goodsSignList) throws Exception {
		getClient();
		PddDdkGoodsPromotionUrlGenerateRequest req = new PddDdkGoodsPromotionUrlGenerateRequest();
		req.setGenerateAuthorityUrl(true);
		req.setGenerateShortUrl(true);
		req.setTargetClientId(Global.getConfig("pdd.clientId"));
		req.setPId(Global.getConfig("pdd.pid"));
		req.setCustomParameters(getCustomParams());
		req.setGoodsSignList(goodsSignList);
		PddDdkGoodsPromotionUrlGenerateResponse  response = client.syncInvoke(req);
		logger.debug(JsonUtil.transferToJson(response));
		return response.getGoodsPromotionUrlGenerateResponse();
	}

	
}
