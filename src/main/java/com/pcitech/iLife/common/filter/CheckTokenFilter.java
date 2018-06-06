package com.pcitech.iLife.common.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pcitech.iLife.common.utils.jwt.JwtUtils;
import com.pcitech.iLife.common.utils.jwt.TokenState;

import net.minidev.json.JSONObject;

/**
 * toekn校验过滤器
 *
 */
public class CheckTokenFilter  implements Filter {


	@Override
	public void doFilter(ServletRequest argo, ServletResponse arg1,
			FilterChain chain ) throws IOException, ServletException {
		HttpServletRequest request=(HttpServletRequest) argo;
		HttpServletResponse response=(HttpServletResponse) arg1;
//		response.setHeader("Access-Control-Allow-Origin", "*");
		if(request.getRequestURI().endsWith("/xxx/xxx")){
			//url跳过设置
			chain.doFilter(request, response);
			return;
		}
		//其他API接口一律校验token
//		System.out.println("开始校验token");
		//从请求头中获取token
		String token=request.getHeader("token");
		Map<String, Object> resultMap=JwtUtils.validToken(token);
		TokenState state=TokenState.getTokenState((String)resultMap.get("state"));
		switch (state) {
		case VALID:
			//取出payload中数据,放入到request作用域中
			request.setAttribute("data", resultMap.get("data"));
			//用HashMap<String, String> data=(HashMap<String, String>) request.getAttribute("data");取出
			//放行
			chain.doFilter(request, response);
			break;
		case EXPIRED:
		case INVALID:
//			System.out.println("无效token");
			//token过期或者无效，则输出错误信息返回给ajax
			JSONObject outputMSg=new JSONObject();
			outputMSg.put("status", "error");
			outputMSg.put("message", "您的token不合法或者过期了，请重新登陆");
			output(outputMSg.toJSONString(), response);
			break;
		}
		
		
	}
	
	
	public void output(String jsonStr,HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=UTF-8;");
		PrintWriter out = response.getWriter();
//		out.println();
		out.write(jsonStr);
		out.flush();
		out.close();
		
	}
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
//		System.out.println("token过滤器初始化了");
	}

	@Override
	public void destroy() {
		
	}

}
