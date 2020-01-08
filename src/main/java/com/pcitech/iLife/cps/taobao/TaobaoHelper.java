package com.pcitech.iLife.cps.taobao;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pcitech.iLife.common.config.Global;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.request.TbkItemInfoGetRequest;
import com.taobao.api.request.TimeGetRequest;
import com.taobao.api.response.TbkItemInfoGetResponse;
import com.taobao.api.response.TbkItemInfoGetResponse.NTbkItem;
import com.taobao.api.response.TimeGetResponse;

//通过淘宝客接口完成商品查询获得类目信息
@Service
public class TaobaoHelper {

	public List<NTbkItem> getItemDetail(String itemIds) throws ApiException {
		DefaultTaobaoClient client = new DefaultTaobaoClient(Global.getConfig("taobao.api.http"), Global.getConfig("taobao.appKey"), Global.getConfig("taobao.appSecret"));
		TbkItemInfoGetRequest req = new TbkItemInfoGetRequest();
		req.setNumIids(itemIds);
		req.setPlatform(1L);//默认为PC链接
		//req.setIp("11.22.33.43");//不关注
		TbkItemInfoGetResponse rsp = client.execute(req);
		return rsp.getResults();		
	}	

	
}
