

package com.pcitech.iLife.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.util.*;

import java.net.URLDecoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.params.HttpParams;



/**
 * 处理跳转链接，但不能工作
 * @author 
 *
 */
public class HttpUtil {

		 public static void main(String[] args) {
				String urlStr = "http://s.click.taobao.com/t?e=zGU34CA7K%2BPkqB07S4%2FK0CITy7klxxrJ35Nnc0ls" +
				"8UNch6eorWAPrhuAvw2mQW2OJDeSwknfCSiKd622D%2BDnIc9QSYrG4navJ4t7sfX4ek43cWA%3D";
		        HttpClient client;
		        client = new HttpClient(new MultiThreadedHttpConnectionManager());
		        client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
		        client.getParams().setParameter(HttpMethodParams.USER_AGENT, "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0)");  //让服务器认为是IE
		 
		        GetMethod get = new GetMethod(urlStr);
		        
		        get.setFollowRedirects(false); //禁止自动重定向
		 
		 
		        try {
		            int iGetResultCode = client.executeMethod(get); 
		            String _refer = get.getResponseHeader("location").getValue(); //打印地址
		            GetMethod get2 = new GetMethod(URLDecoder.decode(_refer.split("tu=")[1]));
		            get2.addRequestHeader("Referer", _refer);
		            get2.setFollowRedirects(false); //禁止自动重定向
		            int iGetResultCode2 = client.executeMethod(get2); 
		            String realUrl = get2.getResponseHeader("location").getValue(); //打印地址
		            System.out.println("REAL URL: "+realUrl);
		            GetMethod get3 = new GetMethod(realUrl);
		            int iGetResultCode3 = client.executeMethod(get3); 
		            String responseBody = get3.getResponseBodyAsString();
//		            System.out.println("Response Body: "+responseBody);
		            System.out.println(responseBody.substring(responseBody.indexOf("sellerRate=" +
		            		"")+12,responseBody.indexOf("&", responseBody.indexOf("sellerRate="))));
		 
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        } finally {
		            get.releaseConnection();
		        }
		}
	
}
