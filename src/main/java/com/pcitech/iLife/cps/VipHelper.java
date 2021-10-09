package com.pcitech.iLife.cps;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.util.Util;
import com.vip.adp.api.open.service.Category;
import com.vip.adp.api.open.service.CategoryRequest;
import com.vip.adp.api.open.service.CategoryResponse;
import com.vip.adp.api.open.service.CpsUnionPidGenResponse;
import com.vip.adp.api.open.service.GoodsInfo;
import com.vip.adp.api.open.service.GoodsInfoRequest;
import com.vip.adp.api.open.service.GoodsInfoResponse;
import com.vip.adp.api.open.service.OrderQueryModel;
import com.vip.adp.api.open.service.OrderResponse;
import com.vip.adp.api.open.service.PidGenRequest;
import com.vip.adp.api.open.service.UnionGoodsServiceHelper;
import com.vip.adp.api.open.service.UnionOrderServiceHelper;
import com.vip.adp.api.open.service.UnionPidServiceHelper;
import com.vip.adp.api.open.service.UnionUrlService;
import com.vip.adp.api.open.service.UrlGenRequest;
import com.vip.adp.api.open.service.UrlGenResponse;
import com.vip.adp.api.open.service.UrlInfo;
import com.vip.osp.sdk.context.ClientInvocationContext;

import  com.vip.adp.api.open.service.UnionUrlServiceHelper;

import vipapis.address.AddressServiceHelper.AddressServiceClient;



//通过API接口完成商品查询获得类目信息
@Service
public class VipHelper {
	private static Logger logger = LoggerFactory.getLogger(VipHelper.class);
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	ClientInvocationContext context = null;
	
	//生成一个UUID，作为requestId
	private String getRequestId() {
		return UUID.randomUUID().toString();
	}
	
	private ClientInvocationContext getContext() {
		if(context  == null) {
			context = new ClientInvocationContext();
			context.setAppKey(Global.getConfig("vip.appKey"));
			context.setAppSecret(Global.getConfig("vip.appSecret"));
			//context.setAccessToken("accessToken");//替换为你的accessToken，通过Oauth认证时必填
			context.setAppURL(Global.getConfig("vip.api.https"));//包括正式环境与沙箱环境
			//context.setReadTimeOut(30000);//读写超时时间，可选，默认30秒
			//context.setConnectTimeOut(5000);//连接超时时间，可选，默认5秒
		}
	    return context;
	}
	
	/**
	 * 根据VIP url生成导购链接。
	 * https://vop.vip.com/home#/api/method/detail/com.vip.adp.api.open.service.UnionUrlService-1.0.0
	 * @param sxChannelType 渠道标志。自定义，可用于区分微信、微博、QQ、WEB等。当前预留。API接口不需要
	 * @param brokerId 推广达人Id
	 * @param urls 唯品会URL列表。单次不超过50条
	 * @return
	 * @throws Exception
	 */
	public List<UrlInfo> generateCpsLinkByUrl(String  brokerId,List<String> urls,String sxChannelType) throws Exception {
		UrlGenRequest req = new UrlGenRequest();
		//1、获取服务客户端
		UnionUrlServiceHelper.UnionUrlServiceClient  client = new UnionUrlServiceHelper.UnionUrlServiceClient();
		//2、设置client的调用上下文
		client.setClientInvocationContext(getContext());
		//4、调用API及返回
		UrlGenResponse response = client.genByVIPUrl(urls, brokerId, getRequestId(), sxChannelType, req);
		return response.getUrlInfoList();
	}
	//根据单个链接生成cpslink
	public List<UrlInfo> generateCpsLinkByUrl(String  brokerId,String url,String sxChannelType) throws Exception {
		List<String> urls = Lists.newArrayList();
		urls.add(url);
		return generateCpsLinkByUrl(brokerId,urls,sxChannelType);
	}
	
	/**
	 * 生成推广位。可以不生成 ，直接在链接里传递即可
	 * @param brokerIds 需要生成的推广位名称列表 注： 1、一次支持批量最大100个 2、每个推广位的名称最长50个字符
	 * @return
	 * @throws Exception
	 */
	public CpsUnionPidGenResponse generatePid(List<String> brokerIds) throws Exception {
		PidGenRequest req = new PidGenRequest();
		req.setPidNameList(brokerIds);
		req.setRequestId(getRequestId());
		//获取服务客户端
		UnionPidServiceHelper.UnionPidServiceClient client = new UnionPidServiceHelper.UnionPidServiceClient();
		//设置client调用上下文
		client.setClientInvocationContext(getContext());
		//发起请求
		CpsUnionPidGenResponse resp = client.genPid(req);
		return resp;
	}
	//传递单个达人ID生成推广位
	public CpsUnionPidGenResponse generatePid(String brokerId) throws Exception {
		List<String> brokerIds = Lists.newArrayList();
		brokerIds.add(brokerId);
		return generatePid(brokerIds);
	}
	
	/**
	 * 查询推广商品列表
	 * @param sourceType 0：输出高佣及爆款单品；1：根据jxCode输出特定类目商品列表
	 * @param channelType 0：高佣商品；1：销量排行；2：商家补贴佣金商品
	 * @param jxCode：	女装精选	7hfpy0m4
						男装精选	wj7evz2j
						美妆精选	vd0wbfdx
						数码电子	dpot8m5u
						精选-首饰	szkl4kj7
						鞋包精选	byh9331t
						母婴精选	gkf52p8p
						居家精选	cnrzcs22
						运动户外精选	indvf44e
						家用电器	uggxpyh5
	 * @param brokerId 达人ID
	 * @param pageSize 分页大小:默认20，最大100
	 * @param pageNo 页码
	 * @throws Exception
	 */
	public GoodsInfoResponse searchItems(int sourceType,int channelType,String jxCode,String brokerId,int pageSize,int pageNo) throws Exception {
		//组织请求参数
		GoodsInfoRequest req = new GoodsInfoRequest();
		req.setRequestId(getRequestId());//自动生成request id
		req.setSourceType(sourceType);
		req.setChannelType(channelType);
		req.setJxCode(jxCode);
		req.setChanTag(brokerId);
		req.setPage(pageNo);
		req.setPageSize(pageSize);
		//获取服务客户端
		UnionGoodsServiceHelper.UnionGoodsServiceClient client = new UnionGoodsServiceHelper.UnionGoodsServiceClient();
		//设置client调用上下文
		client.setClientInvocationContext(getContext());
		//发起请求
		GoodsInfoResponse resp = client.goodsList(req);
		return resp;
	}
	
	/**
	 * 根据商品ID查询商品明细数据
	 * @param brokerId 达人ID
	 * @param goodIds 商品ID列表，最大10个
	 * @return
	 * @throws Exception
	 */
	public List<GoodsInfo> getItemDetail(String brokerId,List<String> goodIds) throws Exception {
		//获取服务客户端
		UnionGoodsServiceHelper.UnionGoodsServiceClient client = new UnionGoodsServiceHelper.UnionGoodsServiceClient();
		//设置client调用上下文
		client.setClientInvocationContext(getContext());
		//发起请求
		List<GoodsInfo> resp = client.getByGoodsIds(goodIds, getRequestId(), brokerId);
		return resp;
	}
	//根据单个goodId查询商品详情
	public List<GoodsInfo> getItemDetail(String brokerId,String goodId) throws Exception {
		List<String> goodIds = Lists.newArrayList();
		goodIds.add(goodId);
		return getItemDetail(brokerId,goodIds);
	}
	
	/**
	 * 获取商品类目信息
	 * @param parentId 父类目id,一级类目的父类目id为0
	 * @param grade 类目级别（1-一级类目，2-二级类目，3-三级类目）
	 * @return
	 * @throws Exception
	 */
	public List<Category> getCategory(long parentId,int grade) throws Exception {
		CategoryRequest req = new CategoryRequest();
		req.setGrade(grade);
		req.setParentId(parentId);
		req.setRequestId(getRequestId());
		//1、获取服务客户端
		UnionGoodsServiceHelper.UnionGoodsServiceClient client = new UnionGoodsServiceHelper.UnionGoodsServiceClient();
		//2、设置client的调用上下文
		client.setClientInvocationContext(getContext());
		//4、调用API及返回
		CategoryResponse response = client.getCategorys(req);
		return response.getData();
	}
	
	/**
	 * 根据起止时间查询订单
	 * @param status 订单状态:0-不合格，1-待定，2-已完结，该参数不设置默认代表全部状态
	 * @param orderTimeStart 订单时间起始 时间戳 单位毫秒
	 * @param orderTimeEnd 订单时间结束 时间戳 单位毫秒
	 * @param pageSize 页面大小：默认20
	 * @param pageNo 页码：从1开始
	 * @return
	 * @throws Exception
	 */
	public OrderResponse getOrders(int status,long orderTimeStart,long orderTimeEnd,int pageSize, int pageNo) throws Exception {
		OrderQueryModel req = new OrderQueryModel();
		req.setRequestId(getRequestId());
		req.setPage(pageNo);
		req.setPageSize(pageSize);
		req.setOrderTimeStart(orderTimeStart);
		req.setOrderTimeEnd(orderTimeEnd);
		//获取服务客户端
		UnionOrderServiceHelper.UnionOrderServiceClient client = new UnionOrderServiceHelper.UnionOrderServiceClient();
		//设置client调用上下文
		client.setClientInvocationContext(getContext());
		//发起请求
		return client.orderList(req);
	}
	
}
