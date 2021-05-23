package com.pcitech.iLife.cps;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import com.pdd.pop.sdk.http.api.pop.request.PddDdkGoodsSearchRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkGoodsZsUnitUrlGenRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkMemberAuthorityQueryRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkOrderListRangeGetRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddGoodsCatsGetRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddPopAuthTokenRefreshRequest;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsDetailResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsDetailResponse.GoodsDetailResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsPromotionUrlGenerateResponse.GoodsPromotionUrlGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsSearchResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsSearchResponse.GoodsSearchResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsZsUnitUrlGenResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsZsUnitUrlGenResponse.GoodsZsUnitGenerateResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkMemberAuthorityQueryResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkOrderListRangeGetResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkOrderListRangeGetResponse.OrderListGetResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddGoodsCatsGetResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddGoodsCatsGetResponse.GoodsCatsGetResponse;
import com.pdd.pop.sdk.http.api.pop.response.PddPopAuthTokenRefreshResponse;


//通过API接口完成商品查询获得类目信息
@Service
public class PddHelper {
	private static Logger logger = LoggerFactory.getLogger(PddHelper.class);
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	PopAccessTokenClient tokenClient  = null;
	PopClient client = null;
	
	private PopClient getClient() {
		if(client  == null) {
			client = new PopHttpClient( Global.getConfig("pdd.clientId"), Global.getConfig("pdd.clientSecret"));
		}
	    return client;
	}
	
	//获取商品分类
	public GoodsCatsGetResponse getCategory(Long parentCategoryId) throws Exception {
		PddGoodsCatsGetRequest request = new PddGoodsCatsGetRequest();
		request.setParentCatId(parentCategoryId);
		PddGoodsCatsGetResponse response = getClient().syncInvoke(request);
		logger.debug(JsonUtil.transferToJson(response));
		return response.getGoodsCatsGetResponse();
	}
	
	//批量获取在推商品
	//goods_search_response
	public GoodsSearchResponse searchCpsItems(PddDdkGoodsSearchRequest request) throws Exception  {
		PddDdkGoodsSearchResponse response = getClient().syncInvoke(request);
		logger.debug(JsonUtil.transferToJson(response));
		return response.getGoodsSearchResponse();
	}
	
	//获取商品详情
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
	 * 将其他拼多多推广链接转换为自己的推广链接。
	 * @param url
	 * @return
	 * @throws Exception 
	 */
	public GoodsZsUnitGenerateResponse generateCpsLinksByUrl(String brokerId,String url) throws Exception {
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
		PddDdkGoodsPromotionUrlGenerateResponse response = getClient().syncInvoke(request);
		return response;
	}
	
	/**
	 * 根据时间段查询订单。默认间隔半小时自动发起查询
	 * @return
	 * @throws Exception
	 */
	public OrderListGetResponse getOrders() throws Exception {
		Calendar cal = Calendar.getInstance();
		Date end = cal.getTime();
		cal.add(Calendar.MINUTE, -30);//每半小时查询一次
		Date from = cal.getTime();
		
		PddDdkOrderListRangeGetRequest request = new PddDdkOrderListRangeGetRequest();
        request.setCashGiftOrder(false);//是否为礼金订单，查询礼金订单时，订单类型不填（默认推广订单）。
        request.setEndTime(sdf.format(end));//支付结束时间，格式: "yyyy-MM-dd HH:mm:ss" ，比如 "2020-12-01 00:00:00"
//        request.setLastOrderId("str");//上一次的迭代器id(第一次不填)
        request.setPageSize(300);//每次请求多少条，建议300
        request.setQueryOrderType(0);//订单类型：1-推广订单；2-直播间订单
        request.setStartTime(sdf.format(from));//支付起始时间，格式: "yyyy-MM-dd HH:mm:ss" ，比如 "2020-12-01 00:00:00"
        PddDdkOrderListRangeGetResponse response = getClient().syncInvoke(request);
        System.out.println(JsonUtil.transferToJson(response));
        return response.getOrderListGetResponse();
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
		PddDdkGoodsPromotionUrlGenerateRequest req = new PddDdkGoodsPromotionUrlGenerateRequest();
		req.setGenerateAuthorityUrl(true);
		req.setGenerateShortUrl(true);
		req.setTargetClientId(Global.getConfig("pdd.clientId"));
		req.setPId(Global.getConfig("pdd.pid"));
		req.setCustomParameters(getCustomParams());
		req.setGoodsSignList(goodsSignList);
		PddDdkGoodsPromotionUrlGenerateResponse  response = getClient().syncInvoke(req);
		logger.debug(JsonUtil.transferToJson(response));
		return response.getGoodsPromotionUrlGenerateResponse();
	}

	/**
	 * TODO 根据时间检查并自动更新AccessToken
	 * 当前accessToken有效期1年，到期后需要使用refreshToken更新
	 * @throws Exception
	 */
	public void refreshAccessToken() throws Exception{
		PddPopAuthTokenRefreshRequest request = new PddPopAuthTokenRefreshRequest();
        request.setRefreshToken(Global.getConfig("pdd.refreshToken"));
        PddPopAuthTokenRefreshResponse response = getClient().syncInvoke(request);
        System.out.println(JsonUtil.transferToJson(response));
	}
}
