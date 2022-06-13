package com.pcitech.iLife.cps;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.domain.kplunion.CategoryService.request.get.CategoryReq;
import com.jd.open.api.sdk.domain.kplunion.CategoryService.response.get.CategoryResp;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.request.query.JFGoodsReq;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.JingfenQueryResult;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.PromotionGoodsResp;
import com.jd.open.api.sdk.domain.kplunion.OrderService.request.query.OrderRowReq;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowResp;
import com.jd.open.api.sdk.domain.kplunion.promotioncommon.PromotionService.request.get.PromotionCodeReq;
import com.jd.open.api.sdk.domain.kplunion.promotioncommon.PromotionService.response.get.PromotionCodeResp;
import com.jd.open.api.sdk.request.kplunion.UnionOpenCategoryGoodsGetRequest;
import com.jd.open.api.sdk.request.kplunion.UnionOpenGoodsJingfenQueryRequest;
import com.jd.open.api.sdk.request.kplunion.UnionOpenGoodsPromotiongoodsinfoQueryRequest;
import com.jd.open.api.sdk.request.kplunion.UnionOpenOrderRowQueryRequest;
import com.jd.open.api.sdk.request.kplunion.UnionOpenPromotionCommonGetRequest;
import com.jd.open.api.sdk.response.kplunion.UnionOpenCategoryGoodsGetResponse;
import com.jd.open.api.sdk.response.kplunion.UnionOpenGoodsJingfenQueryResponse;
import com.jd.open.api.sdk.response.kplunion.UnionOpenGoodsPromotiongoodsinfoQueryResponse;
import com.jd.open.api.sdk.response.kplunion.UnionOpenOrderRowQueryResponse;
import com.jd.open.api.sdk.response.kplunion.UnionOpenPromotionCommonGetResponse;
import com.pcitech.iLife.common.config.Global;

//通过拼多多接口完成商品查询获得类目信息
@Service
public class JdHelper {
	private static Logger logger = LoggerFactory.getLogger(JdHelper.class);
	JdClient client = null;
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private JdClient getClient() {
		if(client  == null) {
			client = new DefaultJdClient(Global.getConfig("jd.api.http"), 
					Global.getConfig("jd.accessToken"), 
					Global.getConfig("jd.appKey"),
					Global.getConfig("jd.appSecret"));
		}
	    return client;
	}
	
	/**
	 * 获取每日更新的优选商品
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public JingfenQueryResult search(JFGoodsReq req) throws Exception{
		UnionOpenGoodsJingfenQueryRequest request=new UnionOpenGoodsJingfenQueryRequest();
		request.setGoodsReq(req);
		UnionOpenGoodsJingfenQueryResponse response=getClient().execute(request);
		if(!"0".equalsIgnoreCase(response.getCode())) {
			logger.debug(response.getCode()+"::"+response.getMsg());
		}else if(response.getQueryResult().getCode() == 200){//得到商品列表了，赶紧入库找运营推广吧
			return response.getQueryResult();
		}else {//是不是手残干了啥事被平台限制了？
			logger.debug(response.getQueryResult().getCode()+"::"+response.getQueryResult().getMessage());
		}
		return null;
	}
	
	/**
	 * 查询商品详情
	 * @param skuList 逗号分隔的SKUID列表
	 * @return
	 * @throws Exception 
	 */
	public PromotionGoodsResp[] getDetail(String skuList ) throws Exception {
		UnionOpenGoodsPromotiongoodsinfoQueryRequest request=new UnionOpenGoodsPromotiongoodsinfoQueryRequest();
		request.setSkuIds(skuList);
		UnionOpenGoodsPromotiongoodsinfoQueryResponse response=getClient().execute(request);
		if(!"0".equalsIgnoreCase(response.getCode())) {
			logger.debug(response.getCode()+"::"+response.getMsg());
		}else if(response.getQueryResult().getCode() == 200){//得到商品详情了，赶紧嘚瑟吧
			return response.getQueryResult().getData();
		}else {//是不是手残干了啥事被平台限制了？
			logger.debug(response.getQueryResult().getCode()+"::"+response.getQueryResult().getMessage());
		}
		return null;
	}
	/*
	public PromotionCodeResp getCpsLink(String url ) throws Exception {
		return getCpsLink(url,Global.getConfig("jd.pid"));//使用默认推广位
	}
	//**/
	/**
	 * 查询得到CPS链接
	 * @param url 商品链接
	 * @param pid 推广位：当前不需要
	 * @return
	 * @throws Exception
	 */
	public PromotionCodeResp getCpsLink(String url/*,String pid */,String ext1) throws Exception {
		UnionOpenPromotionCommonGetRequest request=new UnionOpenPromotionCommonGetRequest();
		PromotionCodeReq promotionCodeReq=new PromotionCodeReq();
		promotionCodeReq.setMaterialId(url);
		promotionCodeReq.setExt1(ext1);//设置达人推广跟踪码
		//promotionCodeReq.setUnionId(Long.parseLong(Global.getConfig("jd.unionId")));
		promotionCodeReq.setSiteId(Global.getConfig("jd.appId"));
		//promotionCodeReq.setPid(pid);//注意：不能传递PID，PID仅用于推广联盟，需要标记下级推客时使用
		request.setPromotionCodeReq(promotionCodeReq);
		UnionOpenPromotionCommonGetResponse response=getClient().execute(request);
		if(!"0".equalsIgnoreCase(response.getCode())) {
			logger.debug(response.getCode()+"::"+response.getMsg());
		}else if(response.getGetResult().getCode() == 200){//得到CPS链接了
			return response.getGetResult().getData();
		}else {//是不是手残干了啥事被平台限制了？
			logger.debug(response.getGetResult().getCode()+"::"+response.getGetResult().getMessage());
		}
		return null;
	}
	
	public CategoryResp[] getCategory() throws Exception {
		return getCategory(0,0);//获取顶级目录
	}
	
	public CategoryResp[] getCategory(int parentId,int grade ) throws Exception {
		UnionOpenCategoryGoodsGetRequest request=new UnionOpenCategoryGoodsGetRequest();
		CategoryReq req=new CategoryReq();
		req.setParentId(parentId);
		req.setGrade(grade);
		request.setReq(req);
		UnionOpenCategoryGoodsGetResponse response=getClient().execute(request);
		if(!"0".equalsIgnoreCase(response.getCode())) {
			logger.debug(response.getCode()+"::"+response.getMsg());
		}else if(response.getGetResult().getCode() == 200){//得到分类了
			return response.getGetResult().getData();
		}else {//是不是手残干了啥事被平台限制了？
			logger.debug(response.getGetResult().getCode()+"::"+response.getGetResult().getMessage());
		}
		return null;
	}
	
	//获取当前时间端的订单列表
	public OrderRowResp[] getOrder() throws Exception {
		Calendar cal = Calendar.getInstance();
		
		//手动补全缺失订单：注意需要在执行后消除
		if(cal.get(Calendar.YEAR)==2022 &&
			cal.get(Calendar.MONTH)==5 &&
			cal.get(Calendar.DATE)==13 
		) {
			cal.set(Calendar.YEAR, 2022);
			cal.set(Calendar.MONTH, 4);//月份，开始为0
			cal.set(Calendar.DATE, 27);//日期，开始为1
			cal.set(Calendar.HOUR, 8);
			cal.set(Calendar.MINUTE, 0);
		}
		
		return getOrder(cal);
	}
	
	//获取指定时间段的时间列表：指定时间前30分钟的订单
	public OrderRowResp[] getOrder(Calendar cal) throws Exception {
		logger.debug("start query orders by calendar.[calendar]"+cal);
		UnionOpenOrderRowQueryRequest request=new UnionOpenOrderRowQueryRequest();
		Date end = cal.getTime();
		cal.add(Calendar.MINUTE, -30);//每半小时查询一次；API要求 间隔时间不超过1小时
		Date from = cal.getTime();
		OrderRowReq req=new OrderRowReq();
		req.setStartTime(sdf.format(from));//开始时间 格式yyyy-MM-dd HH:mm:ss，与endTime间隔不超过1小时
		req.setEndTime(sdf.format(end));//结束时间 格式yyyy-MM-dd HH:mm:ss，与startTime间隔不超过1小时
		req.setPageSize(500);//每页包含条数，上限为500
		req.setPageIndex(1);//默认 取第一页
		req.setType(1);//订单时间查询类型(1：下单时间，2：完成时间（购买用户确认收货时间），3：更新时间
		request.setOrderReq(req);
		UnionOpenOrderRowQueryResponse response=getClient().execute(request);
		logger.debug(response.getCode()+"::"+response.getMsg());
		if(!"0".equalsIgnoreCase(response.getCode())) {//搞个串串，调用都不成功，赶紧debug吧
			logger.warn(response.getCode()+"::"+response.getMsg());
		}else if(response.getQueryResult().getCode() == 200){//得到订单了，赶紧嘚瑟吧
			return response.getQueryResult().getData();
		}else {//是不是手残干了啥事被平台降级了？
			logger.warn(response.getQueryResult().getCode()+"::"+response.getQueryResult().getMessage());
		}
		return null;
	}
}
