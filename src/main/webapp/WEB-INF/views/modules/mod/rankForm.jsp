<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>排行榜管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});

			//装载rank
			loadRank("${rank.id}");//加载完成后自动装载商品条目
			
			//显示tabs
		    $( "#tabs" ).tabs();
			
		    //获取图文模板
		    requestArticleScheme();			
			
		    //加载微信群
		    loadWxGroups();
			
		    //注册事件
		    registerSxEvents();		    
		});
		
		

		function registerSxEvents(){
		    //注册图文内容生成事件
		    $("#btnArticle").click(function(){
		        requestArticle();
		    });    
		    //初始化tinyMCE编辑器
		    tinymce.init({
		        selector: '#article',
		        branding: false,
		        menubar: false,
		        toolbar: [
		            'styleselect | fontselect | fontsizeselect | bold italic underline strikethrough | link h2 h3 h4 blockquote | forecolor backcolor | link image | alignleft aligncenter alignright'
		          ],
		        plugins: 'autoresize',
		        autoresize_bottom_margin: 50, 
		        autoresize_max_height: 1000, // 编辑区域的最大高
		        autoresize_min_height: 600, //编辑区域的最小高度
		        autoresize_on_init: true,
		        autoresize_overflow_padding: 50
		      });

		    //注册发布到wordpress事件
		    $("#btnPublish").click(function(){
		        publishArticle();
		    });
		}
		
		//生成图文内容：请求模板列表
		function requestArticleScheme(){
		    //获取模板列表
		    $.ajax({
		        url:"${ctx}/mod/viewTemplate/rest/listByType/rank",
		        type:"get",
		        data:{},
		        success:function(schemes){
		            console.log("\n===got item article schemes ===\n",schemes);
		            //遍历模板
		            var total = 0;
		            for(var i=0;i<schemes.length;i++){
		                //将模板显示到界面，等待选择后生成
		                $("#articleScheme").append("<option value='"+schemes[i].id+"'>"+schemes[i].name+"</option>");
		                total++;
		            }
		            if(total>0){
		                $("#articleScheme option:eq(1)").attr('selected', 'selected');//选中第一个
		                $("#btnArticle").css('display', 'block');//显示生成按钮
		            }
		        }
		    });  
		}

		//根据选中的模板生成当前商品的图文，并等待发布
		function requestArticle(){
		    var templateId = $("#articleScheme").val();
		    var templateName = $("#articleScheme").find("option:selected").text();
		    console.log("\n===try to generate article by template. ===\n",templateId,rank,rankItems,sortedItems);
		    //生成html并显示到界面
		    $.ajax({
		        url:"${ctx}/rest/api/material-html",
		        type:"post",
		        data:JSON.stringify({
		            templateId:templateId,
		            rank: rank,
		            rankItems: rankItems,
		            items: sortedItems //直接使用排序后的条目列表
		        }),
		        headers:{
		            "Content-Type":"application/json"
		        },        
		        success:function(res){
		            console.log("\n===got html article ===\n",res);
		            //直接显示到界面
		            //$("#article").empty();//先清空已有内容
		            //$("#article").append(res.html);
		            tinyMCE.activeEditor.setContent(res.html);
		            //显示标题框及发布按钮
		            $("#btnPublish").css("display","block");
		            $("#postTitle").css("display","block");
		            //$("#postTitle").val(rank.name+" - "+templateName);//默认采用商品标题 - 模板标题
		            $("#postTitle").val(rank.name);//默认采用商品标题
		        }
		    });      
		}

		//发布文章到wordpress
		function publishArticle(){
		    var templateId = $("#articleScheme").val();//获取当前文章对应的ID
		    var postTitle = $("#postTitle").val();//获取发布内容标题
		    //var postContent = $("#article").html();//使用html作为内容
		    var postContent = tinyMCE.activeEditor.getContent();
		    console.log(" got content from editor.",postContent);
		    //判断是否已经生成
		    var isExists = rank.article && rank.article[templateId];
		    if(isExists){//如果已经生成则直接更新，注意存储的是文章ID
		        console.log("\n===try to update exists article. ===\n",rank.article[templateId]);
		        $.ajax({
		            url:"https://mp.biglistoflittlethings.com/wp-json/wp/v2/posts/"+rank.article[templateId],
		            type:"post",
		            data:JSON.stringify({
		                title:postTitle,
		                content: postContent,
		                status: "publish"
		            }),
		            headers:{
		                "Content-Type":"application/json",
		                "Authorization":sxConfig.options.mp_auth
		            },        
		            success:function(res){
		                console.log("\n=== published ===\n",res);
		                sendItemArticleToWebhook(rank.article[templateId]);//发送到企业微信群便于分享
		                //提交文章到 索引
		                var doc = createArticleDoc(res.id);
		                indexArticleDoc(doc);  
		                //显示预览链接
		                $("#btnPreview").attr("href",app.config.mp_api+"/archives/"+res.id);
		                $("#btnPreview").css("display","block");		                
		                //显示提示
		                $("#messageBox").text("成功更新图文。");
		            }
		        }); 
		    }else{//否则生成新的文章，并且更新rank.article
		        console.log("\n===try to publish new article. ===\n");
		        $.ajax({
		            url:"https://mp.biglistoflittlethings.com/wp-json/wp/v2/posts",
		            type:"post",
		            data:JSON.stringify({
		                title:postTitle,
		                content: postContent,
		                status: "publish"
		            }),
		            headers:{
		                "Content-Type":"application/json",
		                "Authorization":sxConfig.options.mp_auth
		            },        
		            success:function(res){
		                console.log("\n=== published ===\n",res);    
		                $("#messageBox").text("图文已发布。");
		                rank.article[templateId]=res.id;
		                sendItemArticleToWebhook(res.id);//发送到企业微信群便于分享
		                //提交文章到 索引
		                var doc = createArticleDoc(res.id);
		                indexArticleDoc(doc);  		                
		                //显示预览链接
		                $("#btnPreview").attr("href",app.config.mp_api+"/archives/"+res.id);
		                $("#btnPreview").css("display","block");
		                updateRank();
		            }
		        }); 
		    }
		}


		//建立Article索引doc
		function createArticleDoc(articleId){

		    console.log("try to build article doc.",articleId);
		    var articleUrl = app.config.mp_api+"/archives/"+articleId;

		    var templateId = $("#articleScheme").val();//获取当前文章对应的ID
		    var postTitle = $("#postTitle").val();//获取发布内容标题
		    //var postContent = $("#article").html();//使用html作为内容
		    var postContent = tinyMCE.activeEditor.getContent();
		    console.log(" got content from editor.",postContent);

		    //合并tags及tagging
		    var tags  = [];

		    //根据条目构建价格等信息
		    var profitMin = 999999999;//集合内最低佣金
		    var profitMax = 0;//集合内最高佣金
		    var priceMin = 999999999;//最低价格
		    var priceMax = 0;//最低价格
		    var distributors = [];
		    var sources = [];
		    items.forEach(function(rank_item){//这个是rankitem
		    	if(rank_item.stuff){
		    		rank_item.stuff.forEach(function(stuff){ //这个才是循环所有stuff条目
		    	    	console.log("try to insert item.",stuff);
				    	if(stuff.price.sale>priceMax)priceMax = stuff.price.sale;
				    	if(stuff.price.sale<priceMin)priceMin = stuff.price.sale;
				    	if(stuff.profit && stuff.profit.order>profitMax)profitMax = stuff.profit.order;
				    	if(stuff.profit && stuff.profit.order<profitMin)profitMin = stuff.profit.order;
				    	if(distributors.indexOf(stuff.distributor.name)<0)distributors.push(stuff.distributor.name);
				    	if(sources.indexOf(stuff.source)<0)sources.push(stuff.source);	    			
		    		});
		    	}
		    });
			//防止数据错误	
		    if(priceMin>999999){
		    	priceMin = priceMax;
		    }
		    if(profitMin>999999){
		    	profitMin = profitMax;
		    }
		    
		    //装配索引文档
		    var doc = {
		        source: sources,
		        type: "rank" ,
		        itemkey: rank.id,   //单品直接用itemKey，列表用boardId，方案rankId
		        template: templateId ,                               
		        url: articleUrl,
		        title: postTitle,
		        summary: rank.description + " "+ postContent, //一股脑扔进去就可以
		        tags: tags,
		        price: {
		            currency: "",
		            bid: priceMax,
		            sale: priceMin,
		            profit: profitMin,
		            profit2: profitMax
		        },                
		        logo: rank.logo,
		        distributor: {
		            country: "",
		            language: "",
		            name: distributors
		        },
		        timestamp: new Date()
		    }

		    return doc;
		}

		//提交索引。将整个文档提交ES建立所以，便于检索物料
		function indexArticleDoc(doc){
		    console.log("try to index article doc.",doc);
		    var data = {
		        records:[{
		            value:doc
		        }]
		    };
		    $.ajax({
		        url:"http://kafka-rest.shouxinjk.net/topics/article",
		        type:"post",
		        data:JSON.stringify(data),//注意：不能使用JSON对象
		        headers:{
		            "Content-Type":"application/vnd.kafka.json.v2+json",
		            "Accept":"application/vnd.kafka.v2+json"
		        },
		        success:function(result){
		            siiimpleToast.message('图文索引已提交',{
		                  position: 'bottom|center'
		                });
		        }
		    }) 
		}



		//发送信息到运营群：运营团队收到新内容提示
		function sendItemArticleToWebhook(articleId){
		    //推动图文内容到企业微信群，便于转发
		    var msg = {
		            "msgtype": "news",
		            "news": {
		               "articles" : [
		                   {
		                       "title" : "排行榜图文上新",
		                       "description" : rank.name,
		                       "url" : "https://www.biglistoflittlethings.com/ilife-web-wx/content.html?id="+articleId,//将跳转到content.html附加浏览用户的formUser、fromBroker信息
		                       "picurl" : rank.logo
		                   }
		                ]
		            }
		        };

		    //推送到企业微信
		    console.log("\n===try to sent webhook msg. ===\n");
		    $.ajax({
		        url:app.config.wechat_cp_api+"/wework/ilife/notify-cp-company-broker",
		        type:"post",
		        data:JSON.stringify(msg),
		        headers:{
		            "Content-Type":"application/json"
		        },        
		        success:function(res){
		            console.log("\n=== webhook message sent. ===\n",res);
		        }
		    });     
		}

		//加载当前用户关联的微信群
		function loadWxGroups(){
		    console.log("try to load wx groups by current user.");
		    $.ajax({
		        url:"${ctx}/wx/wxGroup/rest/listByCurrentUser",
		        type:"get",        
		        success:function(ret){
		            console.log("===got wx groups===\n",ret);
		            //加载到界面供选择
		            ret.forEach(function(wxgroup){
		                console.log("===got wx groups===\n",wxgroup);
		                $("#wxGroup").append("<div style='line-height:20px;'><input id='wxg"+wxgroup.id+"' name='wxgroups' type='checkbox' data-name='"+wxgroup.name+"' value='"+wxgroup.id+"' style='vertical-align:middle;' checked/><label for='wxg"+wxgroup.id+"' style='margin-top:5px;margin-left:2px;'>"+wxgroup.name+"</label></div>");
		            });
		            //没有微信群则提示
		            if(ret.length==0){
		                $("#wxGroup").append("<div style='line-height:20px;font-size:12px;color:red;'>请先建立微信群，并设置手动推送任务</div>");
		                $("#sendWxGroup").css("display","none");
		            }else{
		            	$("#btnCheckAll").css("display","block");
		            	$("#btnUncheckAll").css("display","block");
		            }
		        }
		    }); 
		    //注册点击事件
		    $("#sendWxGroup").click(function(){
		        var selectedWxGroups = [];
		        $("input[name='wxgroups']:checked").each(function(){
		            selectedWxGroups.push($(this).val());
		            saveFeaturedItem(getUUID(), 'sysUser', "wechat", $(this).val(), $(this).attr("data-name"), "rank", rank.id, JSON.stringify(rank), "pending");
		        });   
		        if(selectedWxGroups.length>0){
		            console.log("selected wxgroups.",selectedWxGroups);
		            siiimpleToast.message('哦耶，推送已安排',{
		              position: 'bottom|center'
		            });             
		        }else{
		            console.log("no group selected.");
		            siiimpleToast.message('请选择微信群先~~',{
		              position: 'bottom|center'
		            });             
		        }
		               
		    });
		    //选中全部
		    $("#btnCheckAll").click(function(){
		        $("input[name='wxgroups']").prop("checked","true"); 
		    });
		    //取消选中
		    $("#btnUncheckAll").click(function(){
		        $("input[name='wxgroups']").removeAttr("checked"); 
		    });     
		}
		//存储featured item到ck
		function saveFeaturedItem(eventId, brokerId, groupType, groupId, groupName,itemType, itemKey, jsonStr, status){
		  var q = "insert into ilife.features values ('"+eventId+"','"+brokerId+"','"+groupType+"','"+groupId+"','"+groupName+"','"+itemType+"','"+itemKey+"','"+jsonStr.replace(/'/g, "’")+"','"+status+"',now())";
		  console.log("try to save featured item.",q);
		  jQuery.ajax({
		    url:app.config.analyze_api+"?query=",//+encodeURIComponent(q),
		    type:"post",
		    data:q,
		    headers:{
		    	"content-type": "text/plain; charset=utf-8", // 直接提交raw数据
		      	"Authorization":"Basic ZGVmYXVsdDohQG1AbjA1"
		    },         
		    success:function(json){
		      console.log("===featured item saved.===\n",json);
		    }
		  });    
		}		
		
		//修改rank：在生成文章后需要同步提交
		function updateRank(){   
			var data_str = JSON.stringify(rank);//重要：避免影响rank数据，通过转换后建立新的对象
		    var data = JSON.parse(data_str);
		    data.article = JSON.stringify(rank.article);
		    
		    $.ajax({
		        url:"${ctx}/mod/rank/rest/rank",
		        type:"post",
		        data:JSON.stringify(data),
		        headers:{
	                "Content-Type":"application/json"
	            }, 
		        success:function(res){
		        	if(res.status){
			            console.log("rank updated.", res);
			        }
		        }
		    });
		}		

		//根据rankId查询清单：直接加载一次
		var rank = {};
		var rankItems = [];
		var weightSum=0;//由于参与排行的维度数量会变化，需要重新计算
		function loadRank(rankId){
		    $.ajax({
		        url:"${ctx}/mod/rank/rest/rank/"+rankId,
		        type:"get",
		        data:{},
		        success:function(res){
			        if(res.success){
			            rank = res.rank;
			            loadStuffItems(rank);//触发加载商品条目
			            //处理logo：设置中logo为本地需要转换为URL
			            if(rank.category && rank.category.logo && rank.category.logo.indexOf("http")>-1){
			                rank.logo = rank.category.logo;
			              }else if(rank.category && rank.category.logo && rank.category.logo.trim().length>0){
			                rank.logo = "https://www.biglistoflittlethings.com/ilife-web-wx/images/category/"+rank.category.logo;
			              }else{
			                rank.logo = "http://www.shouxinjk.net/static/logo/distributor/ilife.png";
			              }
			            //处理rankItem：需要将颜色添加到每一个排行条目下，并重新计算weight
			            if(res.items && res.items.length>0)
			            	rankItems = res.items;
			            var  colors = ['#8b0000', '#dc143c', '#ff4500', '#ff6347', '#1e90ff','#40e0d0','#0dbf8c','#9acd32','#32cd32','#228b22','#067633'];
			
			            rankItems.forEach(function(rankItem){
			                weightSum += rankItem.dimension.weight;
			            });
			            if(weightSum==0)weightSum=1;
			            var i=0;
			            rankItems.forEach(function(rankItem){
			                rankItem.weight = Number((rankItem.dimension.weight/weightSum*100).toFixed(0));//动态权重
			                rankItem.color = colors[i];
			                rankItem.sort = i+1;//排序
			                rankItems.splice(i,1,rankItem);//替换原有条目
			                i++;
			            });
			            
			            //解析article
					    try{
					    	var json = JSON.parse(rank.article);//注意是JSON string，需要解析
					    	rank.article = json;
					    }catch(err){
					    	console.log("failed parse rank article");
					    	rank.article = {};
					    }
			        }else{
			        	console.log("failed load rank.[rankId]"+rankId);
			        } 
		        }
		    }) 
		}
		
		//根据rank信息搜索得到排行榜条目，仅显示前10条
		//直接获取前10条数据：取出后在前端完成排序
		var sortedItems = [];//记录已经排序的条目列表
		function loadStuffItems(rank) {
		    //设置query
		    var esQuery = {//搜索控制
		      from: 0,
		      size: 10,
		      "query": {
		        "bool": {
		          "must": [{
		            "nested": {
		              "path": "meta",
		              "query": {
		                "term": {
		                  "meta.category": rank.category.id
		                }
		              }
		            }
		          }]
		        }
		      },
		      "sort": [
		          { "@timestamp": { "order": "desc" } },
		          { "_score": { "order": "desc" } }
		      ] 
		    };
		
		    //如果有关键字，则根据关键字过滤
		    if(rank.keywords && rank.keywords.trim().length>0){
		      console.log("add query text to search.",rank.keywords);
		      esQuery.query.bool.must.push({
		                      "match" : {"full_text": rank.keywords}
		                });
		    }    
		
		    //设置请求头
		    var esHeader = {
		      "Content-Type": "application/json",
		      "Authorization": "Basic ZWxhc3RpYzpjaGFuZ2VtZQ=="
		    };
		
		    console.log("try to search stuff.",JSON.stringify(esQuery), esQuery);
		
		    $.ajax({
		        url:app.config.search_api+"/stuff/_search", 
		        type:"post",
		        data:JSON.stringify(esQuery),//注意：nginx启用CORS配置后不能直接通过JSON对象传值
		        headers:{
		            "Content-Type":"application/json",
		            "Authorization":"Basic ZWxhc3RpYzpjaGFuZ2VtZQ=="
		        },
		        crossDomain: true,
		        timeout:3000,//设置超时
		        success:function(data){
		            console.log("Feed::loadData success.",data);
		            if(data.hits.total == 0 || data.hits.hits.length==0){//如果没有内容，则显示提示文字
		                //do nothing
		            }else{
		                //装载具体条目
		                var hits = data.hits.hits;
		                for(var i = 0 ; i < hits.length ; i++){
		                    var hit = hits[i]._source;
		                    hit["_score"] = Math.random()*100; //先随机给一个值
		                    if(!hit.logo) //设置logo
		                    	hit.logo = hit.images[0].replace(/\.avif/g,"");
		                    items.push(hit);
		                    //加载评价数据并计算得分
		                    loadMeasureScores(hit);//在计算得分后开始显示
		                }
		            }
		        },
		        complete: function (XMLHttpRequest, textStatus) {//调用执行后调用的函数
		            if(textStatus == 'timeout'){//如果是超时，则显示更多按钮
		              console.log("ajax超时",textStatus);
		            }
		        },
		        error: function () {//调用出错执行的函数
		            //请求出错处理：超时则直接显示搜索更多按钮
		        	console.log("ajax错误");
		          }
		    });
		  }
		//加载指定item的客观评分
		function loadMeasureScores(stuff){
		    var itemScore = {};
		    //根据itemKey获取评价结果
		    //feature = 1；dimensionType：0客观评价，1主观评价
		    //注意：由于clickhouse非严格唯一，需要取最后更新值
		    $.ajax({
		        url:app.config.analyze_api+"?query=select dimensionId,score from ilife.info where feature=1 and dimensionType=0 and itemKey='"+stuff._key+"' order by ts format JSON",
		        type:"get",
		        //async:false,//同步调用
		        //data:{},
		        headers:{
		            "Authorization":"Basic ZGVmYXVsdDohQG1AbjA1"
		        },         
		        success:function(json){
		            console.log("===got item score===\n",json);
		            for(var i=0;i<json.rows;i++){
		                itemScore[json.data[i].dimensionId] = json.data[i].score;
		            }
		            console.log("===calculate item score===\n",itemScore);
		            //计算得分：_score*sum(priority<sup>-0.75*weight/sum(weight)*score)/n
		            var score = 0;
		            var sort = 1;
		            rankItems.forEach(function(rankItem){
		              score += Math.pow(sort, -0.75) * rankItem.weight/weightSum * itemScore[rankItem.dimension.id]?itemScore[rankItem.dimension.id]:Math.random();
		              sort ++ ;
		            });
		            score = Math.sqrt(score/rankItems.length*100)*10; //此处做了额外处理，避免得分较低
		            //$("#score"+stuff._key).append(Number(score.toFixed(1)));
		            //将计算结果写入stuff内，并替换列表中的数据
		            stuff["_score"] = score;
		            /**
		            var idx = items.find(item => item._key == stuff._key);
		            items.splice(idx, 1, stuff);
		            //**/
		            sortedItems.push(stuff);

		            //排序完成后开始显示
		            if(sortedItems.length == items.length){ //全部加载完成后就可以显示了
		                sortedItems.sort(sortByScore);//重新排序
		                console.log("排序已完成");
		            }

		        }
		    });   
		}		
		//按照得分多少排序
		function sortByScore(a, b){
		    return b._score - a._score;
		}


		//生成短连接及二维码
		function generateQrcode(){
		    console.log("start generate qrcode......");
		    var longUrl = "https://www.biglistoflittlethings.com/ilife-web-wx/rank.html?fromBroker=system&id="+rank.id;//获取分享目标链接：包含id
    			
		    //生成短码并保存
		    var shortCode = generateShortCode(longUrl);
		    console.log("got short code",shortCode);
		    saveShortCode(hex_md5(longUrl),"rank_"+rank.id,'system','system',"mp",encodeURIComponent(longUrl),shortCode);    
		    var shortUrl = "https://www.biglistoflittlethings.com/ilife-web-wx/s.html?s="+shortCode;//必须是全路径
		    //var logoUrl = imgPrefix+app.globalData.userInfo.avatarUrl;//需要中转，否则会有跨域问题
		    var logoUrl = "http://www.shouxinjk.net/static/logo/distributor-square/default.jpg";//注意由于跨域问题，必须使用当前域名下的图片

		    //生成二维码
		    var qrcode = new QRCode(document.getElementById("app-qrcode-box"), {
		        text: shortUrl,
		        width: 96,
		        height: 96,    
		        drawer: 'png',
		        logo: logoUrl,
		        logoWidth: 24,
		        logoHeight: 24,
		        logoBackgroundColor: '#ffffff',
		        logoBackgroundTransparent: false
		    });  
		    setTimeout(generateImage,1200);
		}

		//转换二维码svg为图片
		function generateImage() {
		    console.log("try generate image.");
		    var canvas = $('#app-qrcode-box canvas');
		    console.log(canvas);
		    var img = canvas.get(0).toDataURL("image/png");

		    //将二维码图片上传
		    uploadPngFile(img, "qrcode"+rank.id+".png");//文件名称以itemKey唯一识别

		    //隐藏canvas
		    jQuery("#app-qrcode-box canvas").css("display","none");
		}

		//上传二维码到poster服务器，便于生成使用
		function uploadPngFile(dataurl, filename){
		    //dataurl = $("#app-qrcode-box img").attr("src");
		    //filename = "broker-qrcode-system.png";
		    console.log("try to upload qrcode.",dataurl,filename);
		    var formData = new FormData();
		    formData.append("file", dataURLtoFile(dataurl, filename));//注意，使用files作为字段名
		    $.ajax({
		         type:'POST',
		         url:"https://poster.biglistoflittlethings.com/api/upload",
		         data:formData,
		         contentType:false,
		         processData:false,//必须设置为false，不然不行
		         dataType:"json",
		         mimeType:"multipart/form-data",
		         success:function(data){//把返回的数据更新到item
		            console.log("qrcode file uploaded. try to update item info.",data);
		            if(data.code ==0 && data.url.length>0 ){//仅在成功返回后才操作
		                brokerQrcode = data.url;
		                console.log("qrcode image.[url] https://poster.biglistoflittlethings.com/"+data.url);
		                //生成海报
		                requestPoster();//全部加载完成后显示海报
		            }
		         }
		     }); 
		}

		//转换base64为png文件
		function dataURLtoFile(dataurl, filename) {
		  // 获取到base64编码
		  const arr = dataurl.split(',')
		  // 将base64编码转为字符串
		  const bstr = window.atob(arr[1])
		  let n = bstr.length
		  const u8arr = new Uint8Array(n) // 创建初始化为0的，包含length个元素的无符号整型数组
		  while (n--) {
		    u8arr[n] = bstr.charCodeAt(n)
		  }
		  return new File([u8arr], filename, {
		    type: 'image/png',//固定为png格式
		  })
		}
		
		var items = [];//个性方案条目列表，内部带有stuff数组，装载引用的stuff
		var brokerQrcode = null;//存放系统达人分享二维码url

		//当前排行榜		
		/**
		var rank = {
				id: "${rank.id}",
				name: "${rank.name}",
				keywords: "${rank.keywords}",
				description: "${rank.description}",
				openid: "${rank.openid}",
				nickname: "${rank.nickname}",
				category:{
					id: "${rank.category.id}",
					logo: "${rank.category.logo}",
					name: "${rank.category.name}",
				}
		};//当前方案
		//**/
		//解析article
	    try{
	    	var json = JSON.parse(${rank.article});//注意是JSON string，需要解析
	    	rank.article = json;
	    }catch(err){
	    	console.log("failed parse rank article");
	    	rank.article = {};
	    }	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mod/rank/">排行榜列表</a></li>
		<li class="active"><a href="${ctx}/mod/rank/form?id=${rank.id}">排行榜<shiro:hasPermission name="mod:rank:edit">${not empty rank.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:rank:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<div style="display:flex;flex-direction:row;flex-wrap:nowrap">
		<div style="width:45%">	
	<form:form id="inputForm" modelAttribute="rank" action="${ctx}/mod/rank/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">类目：</label>
			<div class="controls">
				<form:input path="category.id" htmlEscape="false" maxlength="32" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">关键字：</label>
			<div class="controls">
				<form:input path="keywords" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>		
		<!--div class="control-group">
			<label class="control-label">LOGO，预留：</label>
			<div class="controls">
				<form:input path="logo" htmlEscape="false" maxlength="512" class="input-xlarge "/>
			</div>
		</div-->
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="4" maxlength="512" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">创建者openid：</label>
			<div class="controls">
				<form:input path="openid" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">创建者昵称：</label>
			<div class="controls">
				<form:input path="nickname" htmlEscape="false" maxlength="256" class="input-xlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:rank:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
		</div>
		<!-- tabs：海报及图文内容 -->
	    <div id="tabs" style="width:55%">
	        <ul>
	          <!--li><a href="#posterInfo">分享海报</a></li-->
	          <li><a href="#articleInfo">图文内容</a></li>
	          <li><a href="#wxGroupInfo">云推送</a></li>
	        </ul>
			<!-- 图文列表 -->              
	        <div id="articleInfo"> 
	            <!-- Articles: 图文列表，能够直接发布图文内容 -->
	            <div class="tagging" style="display:flex;flex-direction: row;flex-wrap: nowrap;vertical-align: middle;">
	              <!--label for="articleScheme">模板</label-->
	              <select id="articleScheme" class="sxSelect" style="width:200px"></select>
	              <button id="btnArticle" class="taggingBtn" style="display:none;">生成图文</button>
	              <button id="btnPublish" class="taggingBtn" style="display:none;">发布内容</button>
	              <a id="btnPreview" style="display:none;fond-size:14px;color:blue;line-height:28px;margin-left:10px;" href="#" target="_new">预览</a>
	            </div>
	            <div id="articleTitle" class="prop-key" style="width:90%;display:none;margin-bottom:5px;">以下图文将发送到流量主:</div> 
	            <div class="tagging">
	                <!--label for="postTitle">标题</label-->
	                <input id="postTitle"  placeholder="请输入标题" style="width:95%;margin-left:0;height:30px;border-color:silver;line-height: 30px;"/>  
	                <!--label for="article">内容</label-->
	                <textarea id="article" rows="50" style="min-height:600px;"></textarea> 
	            </div>                  
	        </div> 	        
			<!-- 推送到微信群 -->  
            <div id="wxGroupInfo">
                <div id="wxGroupTitle" class="prop-key" style="width:90%;margin:10px 0px;">
                  <span style="display: inline-block;">请选择微信群:</span>
                  <span style="display: inline-block;"><a id='btnCheckAll' href='#' style='font-size:12px;color:blue;display:none;'>选中全部</a></span>
                  <span style="display: inline-block;"><a id='btnUncheckAll' href='#' style='font-size:12px;color:blue;display:none;'>取消选择</a></span>
                </div> 
                <div id="wxGroup" style="width:100%;">
                </div>     
                <div style="width:90%;margin:10px 0px;"><a id='sendWxGroup' href='#' style='font-size:12px;color:blue;'>推送到已选择微信群</a></div>                    
            </div>  
	                        
	    </div>  	
	</div>	 	
</body>
</html>