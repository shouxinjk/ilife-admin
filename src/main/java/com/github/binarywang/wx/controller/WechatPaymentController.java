package com.github.binarywang.wx.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.util.SignUtils;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.modules.mod.service.BrokerService;
import com.pcitech.iLife.modules.wx.entity.WxArticle;
import com.pcitech.iLife.modules.wx.service.WxArticleService;
import com.pcitech.iLife.modules.wx.service.WxPaymentAdService;
import com.pcitech.iLife.modules.wx.service.WxPaymentPointService;
import com.pcitech.iLife.modules.wx.service.WxPointsService;
import com.pcitech.iLife.util.HttpClientHelper;

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
     * 微信通知支付结果的回调地址，notify_url
     *
     * @param request
     * @param response
     */
    //@RequestMapping(value = "getJSSDKCallbackData")
    @RequestMapping(value = "rest/callback")
    public void getJSSDKCallbackData(HttpServletRequest request,
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
                    	Map<String,String> params = Maps.newHashMap();
                    	params.put("out_trade_no", outTradeNo);
                    	params.put("transaction_id", kvm.get("transaction_id"));
                    	params.put("result_code", kvm.get("result_code"));
                    	String purchaseType = "微信购买";
                    	if(outTradeNo !=null && outTradeNo.startsWith("pad")) {//表示购买广告
                    		wxPaymentAdService.updateWxTransactionInfoByTradeNo(params);
                    		purchaseType = "购买置顶广告";
                    	}else if(outTradeNo !=null && outTradeNo.startsWith("ppt")) {//表示购买阅豆
                    		wxPaymentPointService.updateWxTransactionInfoByTradeNo(params);
                    		purchaseType = "阅豆充值";
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

