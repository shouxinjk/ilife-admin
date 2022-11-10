

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

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import java.util.*;

/**
 * 处理跳转链接，但不能工作
 * @author 
 *
 */
public class HttpUtil {

	public static String HttpGetHeader(String url, String referer, String name) {
		 
		 
		 
		String value="";
		 
		CloseableHttpClient httpclient =  createSSLClientDefault();
		 
		try {
		 
		// 创建httpget.
		 
		HttpGet httpget = new HttpGet(url);
		 
		 HttpParams params = new BasicHttpParams();
		 
		           params.setParameter("http.protocol.handle-redirects", false); // 默认不让重定向
		 
		                                                                           // 这样就能拿到Location头了
		 
		           httpget.setParams(params);
		 
		httpget.setHeader("Referer", referer);
		 
		// 执行get请求.
		 
		CloseableHttpResponse response = httpclient.execute(httpget);
		 
		for (Header header : response.getAllHeaders()) {
		 
		if(name.equals(header.getName())){
		 
		value=header.getValue();
		 
		}
		 
		}
		 
		} catch (ClientProtocolException e) {
		 
		e.printStackTrace();
		 
		} catch (ParseException e) {
		 
		e.printStackTrace();
		 
		} catch (IOException e) {
		 
		e.printStackTrace();
		 
		} finally {
		 
		// 关闭连接,释放资源
		 
		try {
		 
		httpclient.close();
		 
		} catch (IOException e) {
		 
		e.printStackTrace();
		 
		}
		 
		}
		 
		 
		 
		return value;
		 
		}
		 
		 
		 
		 public static String unescape(String src) {
		 
		 StringBuffer tmp = new StringBuffer();
		 
		 tmp.ensureCapacity(src.length());
		 
		 int lastPos = 0, pos = 0;
		 
		 char ch;
		 
		 while (lastPos < src.length()) {
		 
		  pos = src.indexOf("%", lastPos);
		 
		  if (pos == lastPos) {
		 
		   if (src.charAt(pos + 1) == 'u') {
		 
		    ch = (char) Integer.parseInt(src
		 
		      .substring(pos + 2, pos + 6), 16);
		 
		    tmp.append(ch);
		 
		    lastPos = pos + 6;
		 
		   } else {
		 
		    ch = (char) Integer.parseInt(src
		 
		      .substring(pos + 1, pos + 3), 16);
		 
		    tmp.append(ch);
		 
		    lastPos = pos + 3;
		 
		   }
		 
		  } else {
		 
		   if (pos == -1) {
		 
		    tmp.append(src.substring(lastPos));
		 
		    lastPos = src.length();
		 
		   } else {
		 
		    tmp.append(src.substring(lastPos, pos));
		 
		    lastPos = pos;
		 
		   }
		 
		  }
		 
		 }
		 
		 return tmp.toString();
		 
		}
		 
		 
		 private static CloseableHttpClient createSSLClientDefault() {
			 try { //调用SSL之前取消重写验证方法，取消检测SSL
					 X509TrustManager trustManager =new X509TrustManager() {
						//检查客户端证书
						 @Override
						public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
						}
						//检查服务器证书
						@Override
						public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
						}
						//返回受信任的X509证书数组
						 @Override
						public X509Certificate[] getAcceptedIssuers() {
						return null;
						}
					 };
					//设置TLS-->数据通信之间保证数据的保密性和完整性
					SSLContext ctx = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
					//初始化
					ctx.init(null, new TrustManager[]{trustManager}, null);
					SSLConnectionSocketFactory socketFactory =new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
					RequestConfig requestConfig = RequestConfig.custom()
													.setCookieSpec(CookieSpecs.STANDARD_STRICT)
													.setExpectContinueEnabled(Boolean.TRUE)
													.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
													.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
					//创建Registry
					Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();
					//创建ConnectionManager,添加Connection配置信息
					PoolingHttpClientConnectionManager connectionManager =new PoolingHttpClientConnectionManager(socketFactoryRegistry);
					CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).build();
					return httpClient;
				} catch (NoSuchAlgorithmException e) {
					new RuntimeException(e);
				} catch (KeyManagementException e) {
					new RuntimeException(e);
				}
				return HttpClients.createDefault();
			}
		 
		 
		public static void main(String[] args) {
		 
		String url = "https://s.click.taobao.com/t?e=m%3D2%26s%3DvOGm3QkUYkUcQipKwQzePOeEDrYVVa64LKpWJ%2Bin0XJRAdhuF14FMahPMeAH3ua1xq3IhSJN6GRQvIOjjqrm2mHsE2aIN1SQFGN2SfoDBWWvLek%2B0zMncU%2FFqDGjYg6Gf1JQd0f50BEK2pKIe%2BSnW4UTCT8olmq1K7NVy%2F1NHQyW5RzxTPSo5MYl7w3%2FA2kb";
		System.err.println("\n===url===\n"+url);
		String tu = HttpGetHeader(url, "","Location");
		System.err.println("\n===tu===\n"+tu);
		String ref = tu.substring(tu.indexOf("tu=")+3,tu.length());
		 
		ref = unescape(ref);
		 
		System.err.println("\n===ref===\n"+ref);
		 
		String taobaoUrl = HttpGetHeader(ref,tu,"Location");
		 
		System.out.println("\n===target url===\n"+taobaoUrl);
		 
		}
		 

		 
	
}
