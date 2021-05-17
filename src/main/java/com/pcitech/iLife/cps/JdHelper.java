package com.pcitech.iLife.cps;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.domain.kplunion.CategoryService.request.get.CategoryReq;
import com.jd.open.api.sdk.domain.kplunion.CategoryService.response.get.CategoryResp;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.PromotionGoodsResp;
import com.jd.open.api.sdk.domain.kplunion.OrderService.request.query.OrderRowReq;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowResp;
import com.jd.open.api.sdk.domain.kplunion.promotioncommon.PromotionService.request.get.PromotionCodeReq;
import com.jd.open.api.sdk.domain.kplunion.promotioncommon.PromotionService.response.get.PromotionCodeResp;
import com.jd.open.api.sdk.request.kplunion.UnionOpenCategoryGoodsGetRequest;
import com.jd.open.api.sdk.request.kplunion.UnionOpenGoodsPromotiongoodsinfoQueryRequest;
import com.jd.open.api.sdk.request.kplunion.UnionOpenOrderRowQueryRequest;
import com.jd.open.api.sdk.request.kplunion.UnionOpenPromotionCommonGetRequest;
import com.jd.open.api.sdk.response.kplunion.UnionOpenCategoryGoodsGetResponse;
import com.jd.open.api.sdk.response.kplunion.UnionOpenGoodsPromotiongoodsinfoQueryResponse;
import com.jd.open.api.sdk.response.kplunion.UnionOpenOrderRowQueryResponse;
import com.jd.open.api.sdk.response.kplunion.UnionOpenPromotionCommonGetResponse;
import com.pcitech.iLife.common.config.Global;

//通过拼多多接口完成商品查询获得类目信息
@Service
public class JdHelper {
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
	 * 查询商品详情
	 * @param skuList 逗号分隔的SKUID列表
	 * @return
	 * @throws Exception 
	 */
	public PromotionGoodsResp[] getDetail(String skuList ) throws Exception {
		getClient();
		UnionOpenGoodsPromotiongoodsinfoQueryRequest request=new UnionOpenGoodsPromotiongoodsinfoQueryRequest();
		request.setSkuIds(skuList);
		UnionOpenGoodsPromotiongoodsinfoQueryResponse response=client.execute(request);
		if(!"0".equalsIgnoreCase(response.getCode())) {
			System.err.println(response.getCode()+"::"+response.getMsg());
		}else if(response.getQueryResult().getCode() == 200){//得到商品详情了，赶紧嘚瑟吧
			return response.getQueryResult().getData();
		}else {//是不是手残干了啥事被平台限制了？
			System.err.println(response.getQueryResult().getCode()+"::"+response.getQueryResult().getMessage());
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
	public PromotionCodeResp getCpsLink(String url/*,String pid */) throws Exception {
		getClient();
		UnionOpenPromotionCommonGetRequest request=new UnionOpenPromotionCommonGetRequest();
		PromotionCodeReq promotionCodeReq=new PromotionCodeReq();
		promotionCodeReq.setMaterialId(url);
		//promotionCodeReq.setUnionId(Long.parseLong(Global.getConfig("jd.unionId")));
		promotionCodeReq.setSiteId(Global.getConfig("jd.appId"));
		//promotionCodeReq.setPid(pid);//注意：不能传递PID，PID仅用于推广联盟，需要标记下级推客时使用
		request.setPromotionCodeReq(promotionCodeReq);
		UnionOpenPromotionCommonGetResponse response=client.execute(request);
		if(!"0".equalsIgnoreCase(response.getCode())) {
			System.err.println(response.getCode()+"::"+response.getMsg());
		}else if(response.getGetResult().getCode() == 200){//得到CPS链接了
			return response.getGetResult().getData();
		}else {//是不是手残干了啥事被平台限制了？
			System.err.println(response.getGetResult().getCode()+"::"+response.getGetResult().getMessage());
		}
		return null;
	}
	
	public CategoryResp[] getCategory() throws Exception {
		return getCategory(0,0);//获取顶级目录
	}
	
	public CategoryResp[] getCategory(int parentId,int grade ) throws Exception {
		getClient();
		UnionOpenCategoryGoodsGetRequest request=new UnionOpenCategoryGoodsGetRequest();
		CategoryReq req=new CategoryReq();
		req.setParentId(parentId);
		req.setGrade(grade);
		request.setReq(req);
		UnionOpenCategoryGoodsGetResponse response=client.execute(request);
		if(!"0".equalsIgnoreCase(response.getCode())) {
			System.err.println(response.getCode()+"::"+response.getMsg());
		}else if(response.getGetResult().getCode() == 200){//得到分类了
			return response.getGetResult().getData();
		}else {//是不是手残干了啥事被平台限制了？
			System.err.println(response.getGetResult().getCode()+"::"+response.getGetResult().getMessage());
		}
		return null;
	}
	
	public OrderRowResp[] getOrder() throws Exception {
		getClient();
		UnionOpenOrderRowQueryRequest request=new UnionOpenOrderRowQueryRequest();
		Calendar cal = Calendar.getInstance();
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
		UnionOpenOrderRowQueryResponse response=client.execute(request);
		if(!"0".equalsIgnoreCase(response.getCode())) {//搞个串串，调用都不成功，赶紧debug吧
			System.err.println(response.getCode()+"::"+response.getMsg());
		}else if(response.getQueryResult().getCode() == 200){//得到订单了，赶紧嘚瑟吧
			return response.getQueryResult().getData();
		}else {//是不是手残干了啥事被平台降级了？
			System.err.println(response.getQueryResult().getCode()+"::"+response.getQueryResult().getMessage());
		}
		return null;
	}
}
