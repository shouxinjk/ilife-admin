package com.pcitech.iLife.cps.taobao;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pcitech.iLife.common.config.Global;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.request.TbkItemInfoGetRequest;
import com.taobao.api.request.TbkTpwdCreateRequest;
import com.taobao.api.request.TimeGetRequest;
import com.taobao.api.response.TbkItemInfoGetResponse;
import com.taobao.api.response.TbkItemInfoGetResponse.NTbkItem;
import com.taobao.api.response.TbkTpwdCreateResponse;
import com.taobao.api.response.TimeGetResponse;

//通过淘宝客接口完成商品查询获得类目信息
@Service
public class TaobaoHelper {
	DefaultTaobaoClient client = null;
	
	private DefaultTaobaoClient getClient() {
		if(client == null){
			client = new DefaultTaobaoClient(Global.getConfig("taobao.api.http"), Global.getConfig("taobao.appKey"), Global.getConfig("taobao.appSecret"));
		}
		return client;
	}
	//获取商品分类信息
	public List<NTbkItem> getItemDetail(String itemIds) throws ApiException {
		//DefaultTaobaoClient client = new DefaultTaobaoClient(Global.getConfig("taobao.api.http"), Global.getConfig("taobao.appKey"), Global.getConfig("taobao.appSecret"));
		getClient();
		TbkItemInfoGetRequest req = new TbkItemInfoGetRequest();
		req.setNumIids(itemIds);
		req.setPlatform(1L);//默认为PC链接
		//req.setIp("11.22.33.43");//不关注
		TbkItemInfoGetResponse rsp = client.execute(req);
		return rsp.getResults();		
	}	

	//获取移动端导购链接
	//当前存在问题：部分链接使用API调用会返回错误：需要配合脚本工具使用
	/**
{"error_response":{"code":15,"msg":"Remote service error","sub_code":"20000","sub_msg":"口令跳转url不支持口令转换","request_id":"nbgpwalolcnq"}}
	 */
	public String getTaobaoToken(String url) throws ApiException {
		//DefaultTaobaoClient client = new DefaultTaobaoClient(Global.getConfig("taobao.api.http"), Global.getConfig("taobao.appKey"), Global.getConfig("taobao.appSecret"));
		getClient();
		TbkTpwdCreateRequest req = new TbkTpwdCreateRequest();
		//req.setUserId("123");
		req.setText("小确幸大生活");
		req.setUrl(url);
		//req.setLogo("https://uland.taobao.com/");
		//req.setExt("{}");
		TbkTpwdCreateResponse rsp = client.execute(req);
		return rsp.getData().getModel();	
	}
	
}
