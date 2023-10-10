package com.github.binarywang.wx.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wx.util.ReturnModel;
import com.github.binarywang.wx.util.Sha1Util;
import com.github.binarywang.wx.util.XMLUtil;
import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result.DecryptNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.util.SignUtils;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.modules.diy.entity.TenantPoints;
import com.pcitech.iLife.modules.diy.service.TenantPointsService;
import com.pcitech.iLife.modules.mod.entity.Broker;
import com.pcitech.iLife.modules.mod.entity.IntPackagePricePlan;
import com.pcitech.iLife.modules.mod.entity.IntPricePlanPermission;
import com.pcitech.iLife.modules.mod.entity.IntTenantSoftware;
import com.pcitech.iLife.modules.mod.entity.StoPricePlan;
import com.pcitech.iLife.modules.mod.entity.StoSalePackage;
import com.pcitech.iLife.modules.mod.entity.StoSoftware;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.mod.service.IntPackagePricePlanService;
import com.pcitech.iLife.modules.mod.service.IntPricePlanPermissionService;
import com.pcitech.iLife.modules.mod.service.IntTenantSoftwareService;
import com.pcitech.iLife.modules.mod.service.StoPricePlanService;
import com.pcitech.iLife.modules.mod.service.StoSalePackageService;
import com.pcitech.iLife.modules.mod.service.StoSoftwareService;
import com.pcitech.iLife.modules.mod.entity.Subscription;
import com.pcitech.iLife.modules.mod.entity.SysDepart;
import com.pcitech.iLife.modules.mod.entity.SysRole;
import com.pcitech.iLife.modules.mod.entity.SysTenant;
import com.pcitech.iLife.modules.mod.entity.SysUser;
import com.pcitech.iLife.modules.mod.entity.SysUserRole;
import com.pcitech.iLife.modules.mod.entity.SysUserTenant;
import com.pcitech.iLife.modules.mod.service.SubscriptionService;
import com.pcitech.iLife.modules.mod.service.SysDepartService;
import com.pcitech.iLife.modules.mod.service.SysRoleService;
import com.pcitech.iLife.modules.mod.service.SysTenantService;
import com.pcitech.iLife.modules.mod.service.SysUserRoleService;
import com.pcitech.iLife.modules.mod.service.SysUserService;
import com.pcitech.iLife.modules.mod.service.SysUserTenantService;
import com.pcitech.iLife.modules.wx.entity.WxArticle;
import com.pcitech.iLife.modules.wx.entity.WxPaymentPoint;
import com.pcitech.iLife.modules.wx.service.WxArticleService;
import com.pcitech.iLife.modules.wx.service.WxPaymentAdService;
import com.pcitech.iLife.modules.wx.service.WxPaymentPointService;
import com.pcitech.iLife.modules.wx.service.WxPointsService;
import com.pcitech.iLife.util.HttpClientHelper;
import com.pcitech.iLife.util.PasswordUtil;
import com.pcitech.iLife.util.Util;

import scala.Console;

/**
 * 微信支付Controller
 * <p>
 * Created by FirenzesEagle on 2016/6/20 0020.
 * Email:liumingbo2008@gmail.com
 */
@Controller
@RequestMapping(value = "${adminPath}/wxPay")
public class WechatPaymentController extends GenericController {

    @Autowired
    private WxPayConfig payConfig; 
    @Autowired
    private WxPayService payService;
    @Autowired
    private BrokerService brokerService;
    @Autowired
    private WxArticleService wxArticleService;
    @Autowired
    private WxPointsService wxPointsService;
    @Autowired
    private WxPaymentAdService wxPaymentAdService;
    @Autowired
    private WxPaymentPointService wxPaymentPointService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private TenantPointsService tenantPointsService;
    @Autowired
    private IntPackagePricePlanService intPackagePricePlanService;
    @Autowired
    private IntTenantSoftwareService intTenantSoftwareService;
    @Autowired
    private StoSalePackageService stoSalePackageService;
    @Autowired
    private StoPricePlanService stoPricePlanService;
    @Autowired
    private StoSoftwareService stoSoftwareService;
    @Autowired
    private SysTenantService sysTenantService;
    @Autowired
    private SysUserTenantService sysUserTenantService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysDepartService sysDepartService;
    @Autowired
    private IntPricePlanPermissionService intPricePlanPermissionService;
    
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 用于返回预支付的结果 WxMpPrepayIdResult，一般不需要使用此接口
     *
     * @param response
     * @param request
     */
    @RequestMapping(value = "getPrepayIdResult")
    public void getPrepayId(HttpServletResponse response,
                            HttpServletRequest request) throws WxPayException {
        WxPayUnifiedOrderRequest payInfo = WxPayUnifiedOrderRequest.newBuilder()
            .openid(request.getParameter("openid"))
            .outTradeNo(request.getParameter("out_trade_no"))
            .totalFee(Integer.valueOf(request.getParameter("total_fee")))
            .body(request.getParameter("body"))
            .tradeType(request.getParameter("trade_type"))
            .spbillCreateIp(request.getParameter("spbill_create_ip"))
            .notifyUrl("")
            .build();
        this.logger
            .info("PartnerKey is :" + this.payConfig.getMchKey());
        WxPayUnifiedOrderResult result = this.payService.unifiedOrder(payInfo);

        this.logger.info(new Gson().toJson(result));
        renderString(response, result);
    }

    /**
     * 发起微信支付，返回前端H5调用JS支付所需要的参数，公众号支付调用此接口
     */
	@ResponseBody
	@RequestMapping(value = "rest/payinfo", method = RequestMethod.POST)
    public JSONObject getJSSDKPayInfo(@RequestBody JSONObject json) {
		JSONObject result = new JSONObject();
        WxPayUnifiedOrderRequest prepayInfo = WxPayUnifiedOrderRequest.newBuilder()
            .openid(json.getString("openid"))
            .outTradeNo(json.getString("out_trade_no"))
            .totalFee(Integer.valueOf(json.getString("total_fee")))
            .body(json.getString("body"))
            .tradeType(json.getString("trade_type"))
            .spbillCreateIp(Global.getConfig("sx.server.ip"))//spbill_create_ip：用户地址需要传入，服务器端IP地址可配置
            .notifyUrl(Global.getConfig("sx.callback.prefix")+"/wxPay/rest/callback")//微信支付完成后将调用回传数据
            .build();
        
        prepayInfo.setSignType("MD5");

        try {
            Map<String, String> payInfo = this.payService.getPayInfo(prepayInfo);
            payInfo.put("appId", payService.getConfig().getAppId());
            result.put("success", true);
            result.put("data", payInfo);
        } catch (WxPayException e) {
        	logger.error(e.getErrCodeDes());
        	result.put("success", false);
        	result.put("errcode", e.getErrCode());
        	result.put("errmsg", e.getErrCodeDes());
        }
        return result;
    }
	

    /**
     * 发起微信支付，返回二维码链接
     * PC端web站点微信支付调用此接口。前端根据qrcodeUrl生成二维码完成
     */
	@ResponseBody
	@RequestMapping(value = "rest/qrcode-url", method = RequestMethod.POST)
    public JSONObject getNativePayInfo(@RequestBody JSONObject json) {
		logger.error("try generate wechatpay qrcode url."+ JSONObject.toJSONString(json));
		JSONObject result = new JSONObject();
		WxPayUnifiedOrderV3Request unifiedOrderRequest = new WxPayUnifiedOrderV3Request();
		//unifiedOrderRequest.setAppid(json.getString("appId"));//由前端传入，可以支持多个web站点
		unifiedOrderRequest.setAppid(payService.getConfig().getAppId());//采用公众号APPId
		unifiedOrderRequest.setOutTradeNo(json.getString("out_trade_no"));
		unifiedOrderRequest.setDescription(json.getString("decription"));
		unifiedOrderRequest.setNotifyUrl(Global.getConfig("sx.callback.prefix")+"/wxPay/rest/native-callback");//微信支付完成后将调用回传数据
		
		//设置金额
		WxPayUnifiedOrderV3Request.Amount amount = new WxPayUnifiedOrderV3Request.Amount();
		amount.setTotal(Integer.valueOf(json.getString("total_fee")));
		amount.setCurrency("CNY");
		unifiedOrderRequest.setAmount(amount);
		logger.error("build unified order request." + JSONObject.toJSONString(unifiedOrderRequest));

        try {
        	//ClassCast 失败，直接采用JSONObject
    		//WxPayUnifiedOrderV3Result orderV3Result = payService.createOrderV3(TradeTypeEnum.NATIVE, unifiedOrderRequest);
        	String codeUrl = payService.createOrderV3(TradeTypeEnum.NATIVE, unifiedOrderRequest);
            result.put("success", true);
            result.put("data", codeUrl); //直接返回URL
            logger.error("generate wechatpay qrcode url done.", codeUrl);
        } catch (WxPayException e) {
        	logger.error(e.getErrCodeDes());
        	result.put("success", false);
        	result.put("error", e);
        	result.put("errcode", e.getErrCode());
        	result.put("errmsg", e.getErrCodeDes());
        }
        return result;
    }
	
	 /**
     * 根据out_trade_no查询支付结果。
     * 应用场景：前端扫码支付后，通过查询支付结果判断后续跳转
     */
	@ResponseBody
	@RequestMapping(value = "rest/pay-result/{outTradeNo}", method = RequestMethod.POST)
    public JSONObject checkNativePayResult(@PathVariable String outTradeNo) {
		logger.debug("try get wechatpay result."+ outTradeNo);
		JSONObject result = new JSONObject();
		result.put("success", false);
		
        try {
        	WxPayOrderQueryV3Result orderResult = payService.queryOrderV3(null, outTradeNo);
            result.put("success", true);
            result.put("data", orderResult); //直接返回URL
            logger.debug("generate wechatpay qrcode url done."+JSONObject.toJSONString(orderResult));
            
            if("SUCCESS".equalsIgnoreCase(orderResult.getTradeState())) {
	            //更新检查结果：仅补充transaction信息缺失记录
		    	String transactionId = orderResult.getTransactionId();
		    	String resultCode = orderResult.getTradeState();
		    	Integer totalFee = orderResult.getAmount().getTotal();
		    	String openid = orderResult.getPayer().getOpenid();//获取支付openid：注意是绑定的APPId下的openid
		    	
		    	Instant instant = Instant.parse(orderResult.getSuccessTime()); //时间戳为rfc3339字符串 2018-06-08T10:34:56+08:00
		    	Date paymentTime = instant.toDate();
		    	
	            updateNatviePayResult(outTradeNo, transactionId, resultCode, totalFee, openid, paymentTime);
	            
            }
            
        } catch (WxPayException e) {
        	logger.error(e.getErrCodeDes());
        	result.put("success", false);
        	result.put("error", e);
        	result.put("errcode", e.getErrCode());
        	result.put("errmsg", e.getErrCodeDes());
        }
        
        return result;
    }
	
	private void updateNatviePayResult(String outTradeNo, String transactionId, String resultCode, Integer totalFee,String openid, Date paymentTime) {
		boolean notify = false; //默认认为无需发送通知，仅在新建记录时通知

	    logger.debug("got parsed pay result notify."+
	    		"\noutTradeNo="+outTradeNo+
	    		"\ntransactionId="+transactionId+
	    		"\nresultCode="+resultCode+
	    		"\ntotalFee="+totalFee+
	    		"\nopenid="+openid+
	    		"\npaymentTime="+paymentTime
	    		);
	    
    	//根据回传数据更改已经购买的商品状态：更改对应的记录状态。根据out_trade_no修改所有购买记录的状态，增加transaction_id
    	Broker broker = brokerService.getByOpenid(openid);
    	
	    // 根据结果更新购买记录，同时更新租户虚拟豆数量
	    if("SUCCESS".equalsIgnoreCase(resultCode)){
	
	    	//由于仅根据返回的prepayInfo更新，同时处理adPay及pointPay。其中adPay更新状态，pointPay则执行点数增加（仅执行一次）
	    	//out_trade_no通过payAd、payPoint开头进行区分
	    	Map<String,Object> params = Maps.newHashMap();
	    	params.put("out_trade_no", outTradeNo);
	    	params.put("transaction_id", transactionId);
	    	params.put("result_code", resultCode);
	    	params.put("payment_time", paymentTime);
	    	params.put("payment_amount", totalFee);
	    	String purchaseType = "微信购买";
	    	if(outTradeNo !=null && outTradeNo.startsWith("ppt")) {//表示购买阅豆：同时支持公众号、网页端发起。SaaS端根据云豆金额充值
	    		purchaseType = "虚拟豆充值";
	    		//建立租户购买记录：支持前端查询构建，先根据购买out_trade_no查询是否已经存在购买记录
	    		WxPaymentPoint wxPaymentPoint = new WxPaymentPoint();
	    		wxPaymentPoint.setTradeNo(outTradeNo);
	    		List<WxPaymentPoint> wxPaymentPoints = wxPaymentPointService.findList(wxPaymentPoint);
	    		if(wxPaymentPoints!=null && wxPaymentPoints.size()>0) {
	    			wxPaymentPoint = wxPaymentPoints.get(0);
	    			//判断是否已经更新transactionId，如果有则表示已经更新过，不需重复处理
	    			if(wxPaymentPoint.getTransactionId()==null || wxPaymentPoint.getTransactionId().trim().length()<10) {
	    				//通知信息
	    				notify = true;
	    				purchaseType = wxPaymentPoint.getPoints().getName();
	    				//更新交易记录
			    		wxPaymentPointService.updateWxTransactionInfoByTradeNo(params);
		        		//更新租户虚拟豆
		    			TenantPoints tenantPoints = new TenantPoints();
		    			tenantPoints.setId(""+wxPaymentPoint.getTenantId());//ID与tenantId完全一致
		    			tenantPoints.setTenantId(wxPaymentPoint.getTenantId());
						tenantPoints.setPoints(wxPaymentPoint.getPoints().getPoints()); //只需要设置新购买的增量
						tenantPointsService.updatePoints(tenantPoints);
	    			}else {
	    				//已经处理过了就不做任何处理
	    			}
					
	    		}else { //如果不存交易记录则直接新建：注意数据有缺失，无法获取Product信息
    				//通知信息
    				notify = true;
    				
	    			logger.debug("failed find wxPaymentPoint record.");
	    			wxPaymentPoint.setBroker(broker);
	    	        BigDecimal totalFeeDecimal = new BigDecimal(""+totalFee);
	    	        BigDecimal ratio = new BigDecimal("0.01");
        			wxPaymentPoint.setAmount(totalFeeDecimal.multiply(ratio));
        			wxPaymentPoint.setPoints(null);//无法获取支付points
        			wxPaymentPoint.setTradeNo(outTradeNo);
        			wxPaymentPoint.setTradeState(resultCode);
        			wxPaymentPoint.setTransactionId(transactionId);//默认为空，待微信支付后更新
        			wxPaymentPoint.setCreateDate(new Date());
        			wxPaymentPoint.setUpdateDate(new Date());
        			wxPaymentPoint.setPaymentDate(new Date());
        			wxPaymentPointService.save(wxPaymentPoint);//保存记录
	    		}
	    		
	    	}else if(outTradeNo !=null && outTradeNo.startsWith("sub")) {//表示订阅或续费，同时支持公众号、网页端发起。SaaS端根据套餐类型直接支付
	    		//需要建立订阅记录，包括初次订阅及续费。默认付款人为租户管理员。
	    		purchaseType = "订阅/续费";
	    		//建立租户购买记录：支持前端查询构建，先根据购买out_trade_no查询是否已经存在购买记录
	    		Subscription subscription = new Subscription();
	    		subscription.setTradeNo(outTradeNo);
	    		List<Subscription> subscriptions = subscriptionService.findList(subscription);
	    		if(subscriptions!=null && subscriptions.size()>0) {
	    			subscription = subscriptions.get(0);
	    			//判断是否已经更新transactionId，如果有则表示已经更新过，不需重复处理
	    			if(subscription.getTransactionCode()==null || subscription.getTransactionCode().trim().length()<10) {
	    				//通知信息
	    				notify = true;
	    				
	    				//仅在套餐订阅时才需要检查是否创建租户
	    				if(subscription.getSalePackage()!=null && subscription.getSalePackage().getId()!=null ) {
	    					purchaseType = "More+定制 "+subscription.getSalePackage().getName();
	    				}else if( subscription.getSoftware()!=null && subscription.getPricePlan() != null && subscription.getSoftware().getId()!=null) {
	    					purchaseType = "More+定制 "+subscription.getSoftware().getName()+" "+subscription.getPricePlan().getName();
	    				}else if( subscription.getPricePlan() != null && subscription.getPricePlan().getId() != null) {
	    					purchaseType = "More+定制 "+subscription.getSoftware().getName();
	    				}else {
	    					logger.error("failed get subscription type");
	    				}
	    				
	    				//更新交易记录
	    				logger.debug("try update subscription record. " + params);
	    				subscriptionService.updateWxTransactionInfoByTradeNo(params);
	    				
//	    				subscription.setTransactionCode(transactionId);
//	    				subscription.setTransactionCode(transactionId);
//	    				subscription.setPaymentAmount(totalFee);
//	    				subscription.setTradeState(resultCode);
//	    				subscription.setPayerOpenid(openid);
//	    				subscription.setPaymentTime(paymentTime);
//	    				subscription.setUpdateDate(new Date());
//	    				
//	    				logger.debug("try update subscription record. " + subscription);
//	    				subscriptionService.save(subscription);
	    				
	    				//建立租户下订阅记录：tenantSoftware列表：已经存在则更新到期时间，如果不存在则建立记录
	    				IntPackagePricePlan pkgPricePlan = new IntPackagePricePlan();
	    				pkgPricePlan.setSalePackage(subscription.getSalePackage());
	    				List<IntPackagePricePlan> intPkgPricePlans = intPackagePricePlanService.findList(pkgPricePlan);
	    				for(IntPackagePricePlan intPkgPricePlan:intPkgPricePlans) {
	    					//得到pricePlan记录，获取ext配置表单及配置数据。支持优先获取pricePlan扩展信息，其次获取software扩展信息
	    					StoPricePlan stoPricePlan = stoPricePlanService.get(intPkgPricePlan.getPricePlan());
	    					StoSoftware stoSoftware = stoSoftwareService.get(intPkgPricePlan.getSoftware());
	    					StoSalePackage stoSalePackage = stoSalePackageService.get(intPkgPricePlan.getSalePackage());
	    					//建立或更新记录到期时间
	    					//根据tenantId softwareId pricePlanId查询记录，如果存在则更新，否则新建
	    					IntTenantSoftware intTenantSoftware = new IntTenantSoftware();
	    					intTenantSoftware.setSalePackageId(subscription.getSalePackage().getId());
	    					try {
	    						intTenantSoftware.setTenantId(Integer.parseInt(subscription.getTenant().getId()));
	    					}catch(Exception ex) {}
	    					intTenantSoftware.setPricePlanId(intPkgPricePlan.getPricePlan().getId());
	    					intTenantSoftware.setPricePlanId(intPkgPricePlan.getSoftware().getId());
	    					List<IntTenantSoftware> intTenantSoftwares = intTenantSoftwareService.findList(intTenantSoftware);
	    					if(intTenantSoftwares!=null&&intTenantSoftwares.size()>0) {
	    						intTenantSoftware = intTenantSoftwares.get(0);//存在则取第一条，更新到期时间
	    						intTenantSoftware.setUpdateDate(new Date());
	    						intTenantSoftware.setUpdateTime(new Date());
	    					}else {
	    						intTenantSoftware.setIsNewRecord(true);//新建记录
	    						String id = Util.md5(subscription.getSalePackage().getId()+
	    								intPkgPricePlan.getSoftware().getId()+
	    								intPkgPricePlan.getPricePlan().getId()+
	    								subscription.getTenant().getId());
	    						intTenantSoftware.setId(id);
	    						//设置ext扩展信息，优先pricePlan上的ext配置，其次software上的ext配置
	    						if(stoPricePlan.getExtForm() !=null && stoPricePlan.getExtForm().trim().length()>0) {
	    							intTenantSoftware.setExtForm(stoPricePlan.getExtForm());
	    							intTenantSoftware.setExtInfo(stoPricePlan.getExtInfo());
	    						}else if(stoSoftware.getExtForm() !=null && stoSoftware.getExtForm().trim().length()>0) {
	    							intTenantSoftware.setExtForm(stoSoftware.getExtForm());
	    							intTenantSoftware.setExtInfo(stoSoftware.getExtInfo());
	    							//注意：software扩展信息包含auditInfo及extInfo两部分，仅需要处理extInfo部分
	    							try {
	    								JSONObject extInfo = JSONObject.parseObject(stoSoftware.getExtInfo());
	    								if(extInfo.getJSONObject("extInfo")!=null)
	    									intTenantSoftware.setExtInfo( JSONObject.toJSONString(extInfo.getJSONObject("extInfo")) );
	    							}catch(Exception ex) {
	    								logger.error("failed parse extInfo.",ex);
	    							}
	    						}
	    						intTenantSoftware.setCreateDate(new Date());
	    						intTenantSoftware.setCreateTime(new Date());
	    					}
	    					//修改到期时间：对于到期前续费，在原有到期日期之上增加一年，否则直接从今天开始增加一年
	    					Date expireDate = intTenantSoftware.getExpireOn();
	    					if( expireDate == null)
	    						expireDate = new Date();
	    					Calendar cal = Calendar.getInstance();
	    					cal.setTime(expireDate);//从到期日期或者当前时间开始计算
	    					if(stoSalePackage !=null) { //在订阅套餐时，直接根据salePackage设置
	    						if("year".equalsIgnoreCase( stoSalePackage.getDurationType() )){
	    							cal.add(Calendar.YEAR, 1);//增加一年
	    						}else if("half-year".equalsIgnoreCase( stoSalePackage.getDurationType() )){
	    							cal.add(Calendar.MONTH, 6);//增加半年
	    						}else if("quarter".equalsIgnoreCase( stoSalePackage.getDurationType() )){
	    							cal.add(Calendar.MONTH, 3);//增加一个季度
	    						}else if("month".equalsIgnoreCase( stoSalePackage.getDurationType() )){
	    							cal.add(Calendar.MONTH, 1);//增加一个月
	    						}
	    					}else if(stoPricePlan !=null) { //在单独订阅时，根据pricePlan设置
	    						if("year".equalsIgnoreCase( stoPricePlan.getDurationType() )){
	    							cal.add(Calendar.YEAR, 1);//增加一年
	    						}else if("half-year".equalsIgnoreCase( stoPricePlan.getDurationType() )){
	    							cal.add(Calendar.MONTH, 6);//增加半年
	    						}else if("quarter".equalsIgnoreCase( stoPricePlan.getDurationType() )){
	    							cal.add(Calendar.MONTH, 3);//增加一个季度
	    						}else if("month".equalsIgnoreCase( stoPricePlan.getDurationType() )){
	    							cal.add(Calendar.MONTH, 1);//增加一个月
	    						}
	    					}else {//这个应该是出错了，默认设置为7天，是试用期
	    						cal.add(Calendar.DATE, 7);//设置试用
	    					}
	    					intTenantSoftware.setExpireOn(cal.getTime());
	    					intTenantSoftwareService.save(intTenantSoftware);
	    				}
	    				
	    				//对于初次订阅，需要建立租户、默认部门、默认管理员用户，并且根据授权模板建立租户专有角色（不同bizType下授权不同）
	    				StoSalePackage salePackage = stoSalePackageService.get(subscription.getSalePackage());
	    				if(salePackage!=null) {
	    					//不发送付款成功通知：避免重复受到通知消息
	    					notify = false; 
	    					JSONObject accountInfo = register(subscription,salePackage, openid);
	    					//到期时间设置为1年后
	    					Calendar cal = Calendar.getInstance();
	    					cal.setTime(new Date());//从到期日期或者当前时间开始计算
	    					cal.add(Calendar.YEAR, 1);//增加一年
	    					//发送管理员通知：包含登录信息：用户名、密码
	    					Map<String,String> header = new HashMap<String,String>();
	    				    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
	    			    	JSONObject msg = new JSONObject();
	    					msg.put("openid", openid);
	    					msg.put("title", "恭喜，套餐订阅成功！");
	    					msg.put("company", subscription.getUserName());
	    					msg.put("package", "More+定制 "+salePackage.getName());
	    					msg.put("username", accountInfo.get("username"));
	    					msg.put("password", accountInfo.get("password"));
	    					msg.put("expireOn", fmt.format(cal.getTime()));
	    					msg.put("remark", "请登录后修改密码并妥善保管");
	    					
	    					HttpClientHelper.getInstance().post(
	    							Global.getConfig("wechat.templateMessenge")+"/notify-mp-tenant-ready", 
	    							msg,header);
	    				}else {
	    					logger.error("sale package not found.[data]"+subscription.getSalePackage());
	    				}
	    				
	    			}else {
	    				//已经处理过了就不做任何处理
	    			}
					
	    		}else { //如果不存交易记录则直接新建：仅做一笔记录，不能与业务数据应对
    				//通知信息
    				notify = true;
    				
	    			logger.debug("failed find subscription record.");
	    			//subscription.setBroker(broker);
        			subscription.setPaymentAmount(totalFee);
        			subscription.setTradeNo(outTradeNo);
        			subscription.setTradeState(resultCode);
        			subscription.setTransactionCode(transactionId);//默认为空，待微信支付后更新
        			subscription.setCreateDate(new Date());
        			subscription.setUpdateDate(new Date());
        			subscription.setPaymentTime(new Date());
        			subscriptionService.save(subscription);//保存记录
	    		}
	    	}else {
	    		//糟糕了，不做任何处理
	    		logger.warn("wrong out_trade_no.[out_trade_no]"+outTradeNo);
	    		purchaseType = "未知支付类型";
	    	}
	    	
	    	//发送通知到微信：由于多次通知，仅第一次接收回调时发送通知，否则不发送
	    	if(notify) {
		    	String amountStr = totalFee * 0.01 + "元";
			    Map<String,String> header = new HashMap<String,String>();
			    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
			    JSONObject result = null;
		    	JSONObject msg = new JSONObject();
				msg.put("product", purchaseType);
				msg.put("amount", amountStr);
				msg.put("status", resultCode);
				msg.put("time", fmt.format(new Date()));
				msg.put("ext", "订单号："+outTradeNo);
				msg.put("remark", "流水号："+transactionId);
				result = HttpClientHelper.getInstance().post(
						Global.getConfig("wechat.templateMessenge")+"/payment-success-notify", 
						msg,header);
	    	}

	        logger.debug("out_trade_no: " + outTradeNo + " pay SUCCESS!");
	    }else {
	    	logger.debug("out_trade_no: " + outTradeNo + ". pay result: "+resultCode);
	    }

	}
	
	//完成新用户、新租户建立及权限分配
	//处理逻辑：
	//1，根据subscription获取salePackage，并查询得到订阅类型：个人、团队、企业
	//2，对于团队或企业订阅，则完成租户、部门、管理员用户建立。当前支付用户为租户管理员，否则当前用户加入平台租户下
	//3，对用户授权
	private JSONObject register(Subscription subscription, StoSalePackage salePackage, String openid) {
		JSONObject result = new JSONObject();//放置开通的账户信息
		//根据支付用户openid查找或建立系统用户
		SysUser sysUser = sysUserService.get(openid);//根据openid查找
		if( sysUser == null) { //创建新用户
			sysUser = new SysUser();
			
			//构建4位随机数
			char prefix =(char)(int)(Math.random()*26+97);//采用随机字母前缀
			int id = (int)Math.random()*100000;
			String username = ""+prefix+id;
					
			sysUser.setId(openid);
			sysUser.setUsername( username ); //随机命名
			sysUser.setRealname(subscription.getUserName());
			sysUser.setEmail(username+"@ilife.com");
			sysUser.setPhone(subscription.getUserPhone());
			sysUser.setStatus("1");
//			sysUser.setDelFlag("0");
//			sysUser.setWorkNo(openid);
			sysUser.setThirdType("mp");//设置关联的服务号openid
			sysUser.setThirdId(openid);
			
			//设置默认密码
			String salt = Util.get8bitCode();
			String passwd = Util.get8bitCode();
			String password = PasswordUtil.encrypt(username, passwd, salt);
			
			sysUser.setSalt( salt );
			sysUser.setPassword( password );
			
			sysUserService.save(sysUser);
			
			result.put("username", username);
			result.put("password", password);
			
		}
		
		//获取订阅的salePackage
		
		
		//设置平台用户角色
		SysTenant sysTenant = sysTenantService.get("0");//默认获取平台租户
		if("person".equalsIgnoreCase(subscription.getUserType())) {
			//个人用户关联到平台租户
			SysUserTenant userTenant = new SysUserTenant();
			String tid = Util.md5(openid+sysTenant.getId());
			userTenant.setId(tid);
			userTenant.setUserId(openid);
			userTenant.setTenantId(sysTenant.getId());
			sysUserTenantService.save(userTenant);
			
			//个人用户授权为普通用户: code 固定为 platform_outsource
			String sysRoleCode = "platform_outsource";
			SysRole sysRole = new SysRole();
			sysRole.setRoleCode(sysRoleCode);
			List<SysRole> sysRoles = sysRoleService.findList(sysRole);
			if(sysRoles!= null && sysRoles.size()>0) {
				sysRole = sysRoles.get(0);
				SysUserRole userRole = new SysUserRole();
				String rid = Util.md5(openid+sysRole.getId());//固定为指定role
				userRole.setId(rid);
				userRole.setRoleId(sysRole.getId());
				userRole.setUserId(openid);
				sysUserRoleService.save(userRole);
			}else {
				logger.error("fatal error: no default platform_outsource role.");
			}
		}else if("team".equalsIgnoreCase(subscription.getUserType()) || "company".equalsIgnoreCase(subscription.getUserType())) {
			//企业或团队客户则创建租户、部门、管理员
			//TBC：当前认为初次订阅中才带入userType信息，只要带入该信息则直接创建新租户
			//构建ID：日期+分钟+随机值
			Integer now = (int) System.currentTimeMillis() / 1000;
			Integer r = (int) Math.random() * 100000;
			Integer id = now+r;
			
			sysTenant = new SysTenant();
			sysTenant.setId(""+id);
			sysTenant.setName(subscription.getUserName());
			sysTenant.setTrade(subscription.getBusinessType());
			sysTenant.setDepartment("1");
			sysTenant.setApplyStatus(1);
			sysTenantService.save(sysTenant);
			
			//将支付用户关联到新建立的租户下
			SysUserTenant userTenant = new SysUserTenant();
			String tid = Util.md5(openid+sysTenant.getId());
			userTenant.setId(tid);
			userTenant.setUserId(openid);
			userTenant.setTenantId(sysTenant.getId());
			sysUserTenantService.save(userTenant);
			
			//建立租户顶级部门
			SysDepart sysDepart = new SysDepart();
			sysDepart.setId(tid);//和租户ID一致
			sysDepart.setDepartName(subscription.getUserName());
			sysDepart.setDepartOrder("0");
			sysDepart.setOrgCategory("1");
			sysDepart.setTenantId(tid);
			sysDepart.setOrgCode(Util.get6bitCode());//随机设置6位短码
			sysDepartService.save(sysDepart);
			
			//授权系统用户为租户管理员：根据salePackageId查询intPricePlanPermission
			IntPricePlanPermission intPricePlanPermission = new IntPricePlanPermission();
			intPricePlanPermission.setSalePackageId(salePackage.getId());
			List<IntPricePlanPermission> intPricePlanPermissions = intPricePlanPermissionService.findList(intPricePlanPermission);
			if(intPricePlanPermissions.size()>0) {
				String roleId = intPricePlanPermissions.get(0).getRoleId();
				SysUserRole userRole = new SysUserRole();
				String rid = Util.md5(openid+roleId);//设置为salePackage指定的roleId
				userRole.setId(rid);
				userRole.setRoleId(roleId);
				userRole.setUserId(openid);
				sysUserRoleService.save(userRole);
			}else {
				logger.error("fatal error: no permission found on sale package.");
			}
			
		}else {
			//其他则认为是续订，不作任何处理
		}
		return result;
	}
	
    /**
     * 接收Native微信支付回调数据
     * 
     * 接收回调后根据out_trade_no建立或更新记录
     *
     * @param request
     * @param response
     */
	@Transactional(readOnly = false)
    @RequestMapping(value = "rest/native-callback")
    public String nativeCallback(@RequestBody String xmlData) {
		logger.debug("got pay result notify from wechat. \n"+xmlData);
		try {
		    final WxPayOrderNotifyV3Result notifyResult = payService.parseOrderNotifyV3Result(xmlData, null);
		    logger.debug("got parsed pay result notify. \n"+JSONObject.toJSONString(notifyResult));
		    
	    	String outTradeNo = notifyResult.getResult().getOutTradeNo();
	    	String transactionId = notifyResult.getResult().getTransactionId();
	    	String resultCode = notifyResult.getResult().getTradeState();
	    	Integer totalFee = notifyResult.getResult().getAmount().getTotal();
	    	String openid = notifyResult.getResult().getPayer().getOpenid();//获取支付openid：注意是绑定的APPId下的openid
	    	Instant instant = Instant.parse(notifyResult.getResult().getSuccessTime()); //时间戳为rfc3339字符串 2018-06-08T10:34:56+08:00
	    	Date paymentTime = instant.toDate();
	    	
		    updateNatviePayResult(outTradeNo, transactionId, resultCode, totalFee, openid, paymentTime);
		    
		}catch(Exception ex) {
			logger.error("error while parse native pay result",ex);
		}
	    return WxPayNotifyResponse.success("成功");
	  }
	

    /**
     * 微信通知支付结果的回调地址，notify_url
     *
     * @param request
     * @param response
     */
    //@RequestMapping(value = "getJSSDKCallbackData")
	@Transactional(readOnly = false)
    @RequestMapping(value = "rest/callback")
    public void jsSDKCallback(HttpServletRequest request,
                                     HttpServletResponse response) {
        try {
            synchronized (this) {
                Map<String, String> kvm = XMLUtil.parseRequestXmlToMap(request);
                if (SignUtils.checkSign(kvm, null, this.payConfig.getMchKey())) {
                    if (kvm.get("result_code").equals("SUCCESS")) {
                    	//由于仅根据返回的prepayInfo更新，同时处理adPay及pointPay。其中adPay更新状态，pointPay则执行点数增加（仅执行一次）
                    	//out_trade_no通过payAd、payPoint开头进行区分
                    	
                    	//根据回传数据更改已经购买的商品状态：更改对应的记录状态。根据out_trade_no修改所有购买记录的状态，增加transaction_id
                    	String outTradeNo = kvm.get("out_trade_no");
                    	Map<String,Object> params = Maps.newHashMap();
                    	params.put("out_trade_no", outTradeNo);
                    	params.put("transaction_id", kvm.get("transaction_id"));
                    	params.put("result_code", kvm.get("result_code"));
                    	String purchaseType = "微信购买";
                    	if(outTradeNo !=null && outTradeNo.startsWith("par")) {//表示购买广告：文章置顶
                    		wxPaymentAdService.updateWxTransactionInfoByTradeNo(params);
                    		purchaseType = "文章置顶广告购买";
                    	}else if(outTradeNo !=null && outTradeNo.startsWith("pac")) {//表示购买广告：公众号置顶
                    		wxPaymentAdService.updateWxTransactionInfoByTradeNo(params);
                    		purchaseType = "公众号置顶广告购买";
                    	}else if(outTradeNo !=null && outTradeNo.startsWith("ppt")) {//表示购买阅豆：同时支持公众号、网页端发起。SaaS端根据云豆金额充值
                    		purchaseType = "云豆充值";
                    		wxPaymentPointService.updateWxTransactionInfoByTradeNo(params);
                    	}else if(outTradeNo !=null && outTradeNo.startsWith("sub")) {//表示订阅或续费，同时支持公众号、网页端发起。SaaS端根据套餐类型直接支付
                    		//需要建立订阅记录，包括
                    		subscriptionService.updateWxTransactionInfoByTradeNo(params);
                    		purchaseType = "订阅/续费";
                    	}else {
                    		//糟糕了，不做任何处理
                    		logger.warn("wrong out_trade_no.[out_trade_no]"+outTradeNo);
                    		purchaseType = "未知支付类型";
                    	}
                    	
                    	//发送通知到微信
                    	String amountStr = kvm.get("total_fee");
                    	try {
                    		int amount = Integer.parseInt(kvm.get("total_fee"));
                    		amountStr = amount*0.01 + "元";
                    	}catch(Exception ex) {
                    		
                    	}
                	    Map<String,String> header = new HashMap<String,String>();
                	    header.put("Authorization","Basic aWxpZmU6aWxpZmU=");
                	    JSONObject result = null;
                    	JSONObject msg = new JSONObject();
            			msg.put("product", purchaseType);
            			msg.put("amount", amountStr);
            			msg.put("status", kvm.get("result_code"));
            			msg.put("time", fmt.format(new Date()));
            			msg.put("ext", "订单号："+kvm.get("out_trade_no"));
            			msg.put("remark", "流水号："+kvm.get("transaction_id"));
            			result = HttpClientHelper.getInstance().post(
            					Global.getConfig("wechat.templateMessenge")+"/payment-success-notify", 
            					msg,header);
                    	//TODO支持分销机制，将订单加入order
                        logger.info("out_trade_no: " + kvm.get("out_trade_no") + " pay SUCCESS!");
                        response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[ok]]></return_msg></xml>");
                        //补全缺失transaction_id信息：每次支付后查询之前的空白记录
                        List<String> itemsWithoutTransactionId = wxPaymentAdService.findItemsWithoutTransactionId();
                        for(String item:itemsWithoutTransactionId) {
                        	queryOrdersByTradeNo(item);
                        }
                        
                    } else {
                        this.logger.error("out_trade_no: "
                            + kvm.get("out_trade_no") + " result_code is FAIL");
                        response.getWriter().write(
                            "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[result_code is FAIL]]></return_msg></xml>");
                    }
                } else {
                    response.getWriter().write(
                        "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[check signature FAIL]]></return_msg></xml>");
                    this.logger.error("out_trade_no: " + kvm.get("out_trade_no")
                        + " check signature FAIL");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //获取transaction_id为空的记录，并根据tradeNo查询补充transaction_id与state
    public void queryOrdersByTradeNo(String outTradeNo) {
    	try {
			WxPayOrderQueryResult result = payService.queryOrder(null, outTradeNo);
			if(result!=null) {
            	Map<String,Object> params = Maps.newHashMap();
            	params.put("out_trade_no", outTradeNo);
            	params.put("transaction_id", result.getTransactionId());
            	params.put("result_code", result.getResultCode());
            	if(outTradeNo !=null && outTradeNo.startsWith("par")) {//表示购买广告：文章置顶
            		wxPaymentAdService.updateWxTransactionInfoByTradeNo(params);
            	}else if(outTradeNo !=null && outTradeNo.startsWith("pac")) {//表示购买广告：公众号置顶
            		wxPaymentAdService.updateWxTransactionInfoByTradeNo(params);
            	}else if(outTradeNo !=null && outTradeNo.startsWith("ppt")) {//表示购买阅豆
            		wxPaymentPointService.updateWxTransactionInfoByTradeNo(params);
            	}else {
            		//糟糕了，不做任何处理
            		logger.warn("wrong out_trade_no.[out_trade_no]"+outTradeNo);
            	}
			}
		} catch (WxPayException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @RequestMapping(value = "entPay")
    public void payToIndividual(HttpServletResponse response,
                                HttpServletRequest request) {
        EntPayRequest wxEntPayRequest = new EntPayRequest();
        wxEntPayRequest.setAppid(payConfig.getAppId());
        wxEntPayRequest.setMchId(payConfig.getMchId());
        wxEntPayRequest.setNonceStr(Sha1Util.getNonceStr());
        wxEntPayRequest.setPartnerTradeNo(request.getParameter("partner_trade_no"));
        wxEntPayRequest.setOpenid(request.getParameter("openid"));
        wxEntPayRequest.setCheckName("NO_CHECK");
        wxEntPayRequest.setAmount(Integer.valueOf(request.getParameter("amount")));
        wxEntPayRequest.setDescription(request.getParameter("desc"));
        wxEntPayRequest.setSpbillCreateIp(request.getParameter("spbill_create_ip"));

        try {
            EntPayResult wxEntPayResult = payService.getEntPayService().entPay(wxEntPayRequest);
            if ("SUCCESS".equals(wxEntPayResult.getResultCode().toUpperCase())
                && "SUCCESS".equals(wxEntPayResult.getReturnCode().toUpperCase())) {
                this.logger.info("企业对个人付款成功！\n付款信息：\n" + wxEntPayResult.toString());
            } else {
                this.logger.error("err_code: " + wxEntPayResult.getErrCode()
                    + "  err_code_des: " + wxEntPayResult.getErrCodeDes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

