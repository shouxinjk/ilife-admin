package com.pcitech.iLife.cps;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.TraceCode;
import com.pcitech.iLife.modules.mod.service.TraceCodeService;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.request.TbkDgMaterialOptionalRequest;
import com.taobao.api.request.TbkDgOptimusMaterialRequest;
import com.taobao.api.request.TbkItemInfoGetRequest;
import com.taobao.api.request.TbkTpwdCreateRequest;
import com.taobao.api.request.TimeGetRequest;
import com.taobao.api.response.TbkDgMaterialOptionalResponse;
import com.taobao.api.response.TbkDgOptimusMaterialResponse;
import com.taobao.api.response.TbkItemInfoGetResponse;
import com.taobao.api.response.TbkItemInfoGetResponse.NTbkItem;
import com.taobao.api.response.TbkTpwdCreateResponse;
import com.taobao.api.response.TimeGetResponse;

//通过淘宝客接口完成商品查询获得类目信息
@Service
public class TaobaoHelper {
	DefaultTaobaoClient client = null;
	
	@Autowired TraceCodeService traceCodeService;
	
	private DefaultTaobaoClient getClient() {
		if(client == null){
			client = new DefaultTaobaoClient(Global.getConfig("taobao.api.http"), Global.getConfig("taobao.appKey"), Global.getConfig("taobao.appSecret"));
		}
		return client;
	}
	
	//根据商品id获取详情，id为逗号分隔，最多40个
	public List<NTbkItem> getItemDetail(String itemIds) throws ApiException {
		//DefaultTaobaoClient client = new DefaultTaobaoClient(Global.getConfig("taobao.api.http"), Global.getConfig("taobao.appKey"), Global.getConfig("taobao.appSecret"));
		getClient();
		TbkItemInfoGetRequest req = new TbkItemInfoGetRequest();
		req.setNumIids(itemIds);
		req.setPlatform(1L);//默认为PC链接
		//req.setIp("11.22.33.43");//不关注
		TbkItemInfoGetResponse rsp = client.execute(req);
		System.err.println(rsp.getBody());
		return rsp.getResults();		
	}	
	
	//返回默认数量推广条目：20条
	public List<TbkDgOptimusMaterialResponse.MapData> getOptimusMaterial(long materialId) throws ApiException{
		return getOptimusMaterial(materialId, 20);
	}
	//根据物料ID获取商品列表，包含CPS链接、价格、佣金比例信息
	public List<TbkDgOptimusMaterialResponse.MapData> getOptimusMaterial(long materialId, long size) throws ApiException {
		//DefaultTaobaoClient client = new DefaultTaobaoClient(Global.getConfig("taobao.api.http"), Global.getConfig("taobao.appKey"), Global.getConfig("taobao.appSecret"));
		//获取系统broker推广位
		Broker broker = new Broker();
		broker.setId("system");
		TraceCode q = new TraceCode();
		q.setPlatform("taobao");
		q.setBroker(broker);
		//从配置文件中读取，格式为：mm_40898641_576750191_109087800036
		String traceCode = Global.getConfig("taobao.pid");
		if(traceCode!=null && traceCode.trim().length()>0) { //解析获取最后一段
			String[] arr = traceCode.split("_");
			traceCode = arr[arr.length-1];
		}
		List<TraceCode> traceCodes = traceCodeService.findList(q);
		if(traceCodes != null && traceCodes.size()>0) {
			//使用查询到的结果
			traceCode = traceCodes.get(0).getCode();
		}
		long traceCodeLong = 109087800036L;
		try {
			traceCodeLong = Long.parseLong(traceCode);
		}catch(Exception ex) {
			//just ignore
		}
		
		getClient();
		TbkDgOptimusMaterialRequest req = new TbkDgOptimusMaterialRequest();
		req.setPageSize(size);
		req.setPageNo(1L);
		req.setAdzoneId(traceCodeLong);//默认使用系统广告位：获取system达人的推广位即可
		req.setMaterialId(materialId);
//		req.setDeviceValue("xxx");
//		req.setDeviceEncrypt("MD5");
//		req.setDeviceType("IMEI");
//		req.setContentId(323L);
//		req.setContentSource("xxx");
//		req.setItemId("33243");
//		req.setFavoritesId("123445");
		TbkDgOptimusMaterialResponse rsp = client.execute(req);
		System.out.println(rsp.getBody());
		return rsp.getResultList();		
	}
	
	//根据关键字搜索获取商品列表，包含CPS链接、价格、佣金比例信息
	public List<TbkDgMaterialOptionalResponse.MapData> getOptimusMaterial(String  keyword) throws ApiException {
		//DefaultTaobaoClient client = new DefaultTaobaoClient(Global.getConfig("taobao.api.http"), Global.getConfig("taobao.appKey"), Global.getConfig("taobao.appSecret"));
		//获取系统broker推广位
		Broker broker = new Broker();
		broker.setId("system");
		TraceCode q = new TraceCode();
		q.setPlatform("taobao");
		q.setBroker(broker);
		//从配置文件中读取，格式为：mm_40898641_576750191_109087800036
		String traceCode = Global.getConfig("taobao.pid");
		if(traceCode!=null && traceCode.trim().length()>0) { //解析获取最后一段
			String[] arr = traceCode.split("_");
			traceCode = arr[arr.length-1];
		}
		List<TraceCode> traceCodes = traceCodeService.findList(q);
		if(traceCodes != null && traceCodes.size()>0) {
			//使用查询到的结果
			traceCode = traceCodes.get(0).getCode();
		}
		long traceCodeLong = 109087800036L;
		try {
			traceCodeLong = Long.parseLong(traceCode);
		}catch(Exception ex) {
			//just ignore
		}
		
		getClient();
		TbkDgMaterialOptionalRequest req = new TbkDgMaterialOptionalRequest();
		req.setStartDsr(10L);
		req.setPageSize(20L);
		req.setPageNo(1L);
		req.setPlatform(1L);//1pc 2移动端
//			req.setEndTkRate(1234L);
//			req.setStartTkRate(1234L);
//			req.setEndPrice(10L);
//			req.setStartPrice(10L);
		req.setIsOverseas(false);
		req.setIsTmall(false);
		req.setSort("tk_rate_des");
//			req.setItemloc("杭州");
//			req.setCat("16,18");
		req.setQ(keyword);
//			req.setMaterialId(2836L);
		req.setHasCoupon(true);
//			req.setIp("13.2.33.4");
		req.setAdzoneId(traceCodeLong);
//			req.setNeedFreeShipment(true);
//			req.setNeedPrepay(true);
//			req.setIncludePayRate30(true);
//			req.setIncludeGoodRate(true);
//			req.setIncludeRfdRate(true);
//			req.setNpxLevel(2L);
//			req.setDeviceEncrypt("MD5");
//			req.setDeviceValue("xxx");
//			req.setDeviceType("IMEI");
//			req.setEndKaTkRate(1234L);
//			req.setStartKaTkRate(1234L);
//			req.setLockRateEndTime(1567440000000L);
//			req.setLockRateStartTime(1567440000000L);
//			req.setLongitude("121.473701");
//			req.setLatitude("31.230370");
//			req.setCityCode("310000");
//			req.setSellerIds("1,2,3,4");
//			req.setSpecialId("2323");
//			req.setRelationId("3243");
//			req.setPageResultKey("abcdef");
//			req.setUcrowdId(1L);
//			List<TbkDgMaterialOptionalRequest.Ucrowdrankitems> list2 = new ArrayList<TbkDgMaterialOptionalRequest.Ucrowdrankitems>();
//			TbkDgMaterialOptionalRequest.Ucrowdrankitems obj3 = new TbkDgMaterialOptionalRequest.Ucrowdrankitems();
//			list2.add(obj3);
//			obj3.setCommirate(1234L);
//			obj3.setPrice("10.12");
//			obj3.setItemId("542808901898");
//			req.setUcrowdRankItems(list2);
//			req.setGetTopnRate(0L);
//			req.setBizSceneId("1");
//			req.setPromotionType("2");
		TbkDgMaterialOptionalResponse rsp = client.execute(req);
		System.out.println(rsp.getBody());
		return rsp.getResultList();		
	}
	
	//按时间段查询订单：当前权限不足，无法使用该接口
	public void getOrders() {
		/**
		getClient();
		TbkOrderDetailsGetRequest req = new TbkOrderDetailsGetRequest();
		req.setEndTime("2020-03-12 11:00:00");
		req.setStartTime("2020-03-12 14:00:00");
		TbkOrderDetailsGetResponse response = client.execute(req);	
		//**/
	}

	//获取移动端导购链接
	//当前存在问题：权限不足，会返回错误提示
	/**
{"error_response":{"code":15,"msg":"Remote service error","sub_code":"20000","sub_msg":"口令跳转url不支持口令转换","request_id":"nbgpwalolcnq"}}
	 */
	public String getTaobaoToken(String url) throws ApiException {
		//DefaultTaobaoClient client = new DefaultTaobaoClient(Global.getConfig("taobao.api.http"), Global.getConfig("taobao.appKey"), Global.getConfig("taobao.appSecret"));
		getClient();
		TbkTpwdCreateRequest req = new TbkTpwdCreateRequest();
		//req.setUserId("123");//should be brokerId
		req.setText("小确幸大生活");
		req.setUrl(url);
		//req.setLogo("https://uland.taobao.com/");
		//req.setExt("{}");
		TbkTpwdCreateResponse rsp = client.execute(req);
		return rsp.getData()==null?null:rsp.getData().getModel();	
	}
	
	/**
	public String convertTaobaoToken(String token) {
//		TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		getClient();
		TbkTpwdConvertRequest req = new TbkTpwdConvertRequest();
		req.setPasswordContent("￥2k12308DjviP￥");
		req.setAdzoneId(12312312L);
		req.setDx("1");
		req.setUcrowdId(1L);
		TbkTpwdConvertResponse rsp = client.execute(req);
		System.out.println(rsp.getBody());
	}
	
	//**/
	
}
