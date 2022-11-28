<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>个性定制方案管理</title>
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

			//装载solution
			loadSolution("${solution.id}");
			
			//装载solution item
			loadSolutionItems("${solution.id}");
			
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
		        url:"${ctx}/mod/viewTemplate/rest/listByType/solution",
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
		    console.log("\n===try to generate article by template. ===\n",templateId);
		    //生成html并显示到界面
		    $.ajax({
		        url:"${ctx}/rest/api/material-html",
		        type:"post",
		        data:JSON.stringify({
		            templateId:templateId,
		            solution: solution,
		            items: items
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
		            $("#postTitle").val(solution.name+" - "+templateName);//默认采用商品标题 - 模板标题
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
		    var isExists = solution.article && solution.article[templateId];
		    if(isExists){//如果已经生成则直接更新，注意存储的是文章ID
		        console.log("\n===try to update exists article. ===\n",solution.article[templateId]);
		        $.ajax({
		            url:"https://mp.biglistoflittlethings.com/wp-json/wp/v2/posts/"+solution.article[templateId],
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
		                sendItemArticleToWebhook(solution.article[templateId]);//发送到企业微信群便于分享
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
		    }else{//否则生成新的文章，并且更新solution.article
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
		                solution.article[templateId]=res.id;
		                sendItemArticleToWebhook(res.id);//发送到企业微信群便于分享
		                //提交文章到 索引
		                var doc = createArticleDoc(res.id);
		                indexArticleDoc(doc);  		                
		                //显示预览链接
		                $("#btnPreview").attr("href",app.config.mp_api+"/archives/"+res.id);
		                $("#btnPreview").css("display","block");
		                updateSolution();
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
		    items.forEach(function(solution_item){//这个是solutionitem
		    	if(solution_item.stuff){
		    		solution_item.stuff.forEach(function(item){ //这个才是循环所有stuff条目
		    	    	console.log("try to insert item.",item);
				    	if(item.stuff.price.sale>priceMax)priceMax = item.stuff.price.sale;
				    	if(item.stuff.price.sale<priceMin)priceMin = item.stuff.price.sale;
				    	if(item.stuff.profit && item.stuff.profit.order>profitMax)profitMax = item.stuff.profit.order;
				    	if(item.stuff.profit && item.stuff.profit.order<profitMin)profitMin = item.stuff.profit.order;
				    	if(distributors.indexOf(item.stuff.distributor.name)<0)distributors.push(item.stuff.distributor.name);
				    	if(sources.indexOf(item.stuff.source)<0)sources.push(item.stuff.source);	    			
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
		        type: "solution" ,
		        itemkey: solution.id,   //单品直接用itemKey，列表用boardId，方案solutionId
		        template: templateId ,                               
		        url: articleUrl,
		        title: postTitle,
		        summary: solution.description + " "+ postContent, //一股脑扔进去就可以
		        tags: tags,
		        price: {
		            currency: "",
		            bid: priceMax,
		            sale: priceMin,
		            profit: profitMin,
		            profit2: profitMax
		        },                
		        logo: solution.scheme.logo,
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
		                       "title" : "个性定制图文上新",
		                       "description" : solution.name,
		                       "url" : "https://www.biglistoflittlethings.com/ilife-web-wx/content.html?id="+articleId,//将跳转到content.html附加浏览用户的formUser、fromBroker信息
		                       "picurl" : solution.scheme.logo
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
		            saveFeaturedItem(getUUID(), 'sysUser', "wechat", $(this).val(), $(this).attr("data-name"), "solution", solution.id, JSON.stringify(solution), "pending");
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
		
		//修改solution：在生成文章后需要同步提交
		function updateSolution(){   
			var data_str = JSON.stringify(solution);//重要：避免影响solution数据，通过转换后建立新的对象
		    var data = JSON.parse(data_str);
		    data.article = JSON.stringify(solution.article);
		    
		    $.ajax({
		        url:"${ctx}/diy/solution/rest/solution",
		        type:"post",
		        data:JSON.stringify(data),
		        headers:{
	                "Content-Type":"application/json"
	            }, 
		        success:function(res){
		        	if(res.status){
			            console.log("solution updated.", res);
			        }
		        }
		    });
		}		

		//根据solutionId查询清单：直接加载一次
		function loadSolution(solutionId){
		    $.ajax({
		        url:"${ctx}/diy/solution/rest/solution/"+solutionId,
		        type:"get",
		        data:{},
		        success:function(res){
			        if(res.status){
			            solution = res.data;
			            //解析article
					    try{
					    	var json = JSON.parse(solution.article);//注意是JSON string，需要解析
					    	solution.article = json;
					    }catch(err){
					    	console.log("failed parse solution article");
					    	solution.article = {};
					    }
			        }else{
			        	console.log("failed load solution.[solutionId]"+solutionId);
			        } 
		        }
		    }) 
		}
		
		//根据solutionId查询所有item列表
		function loadSolutionItems(solutionId){
		    $.ajax({
		        url:"${ctx}/diy/solutionItem/rest/solution-items/"+solutionId,
		        type:"get",
		        data:{},
		        success:function(items){
			        for(var i = 0 ; i < items.length ; i++){
			            loadStuffItem(items[i]);//查询具体的item条目
			        }  
		        }
		    }) 
		}
		//查询单个solutionItem的stuff条目明细
		function loadstuffItem(solutionItem){//获取内容列表
			//注意，stuff条目有多个，需要逐个获取。这里有性能问题
			var stuff = [];
			solutionItem["stuff"] = stuff;//默认stuff为空白
			items.push(solutionItem);
			
			//装载具体条目
			var itemKeys = solutionItem.items?solutionItem.items.split(","):[];//否则为空
			itemKeys.forEach(function(itemKey){
				if(itemKey && itemKey.trim().length>0){
				    $.ajax({
				        url:"https://data.shouxinjk.net/_db/sea/my/stuff/"+itemKey,
				        type:"get",
				        data:{},
				        success:function(data){
				        	//将条目加入对应的solutionItem的stuff数组内
				        	var idx = items.findIndex((node) => node.id===solutionItem.id);
				        	var solution_item = items[idx];
				        	solution_item.stuff.push(data);//装载到stuff   
				            items.splice(idx,1,solution_item);//注意：这里可能有潜在问题，在多个回调同时返回时，可能导致数据丢失
				        }
				    }) 					
				}				
			});
		}

		//生成短连接及二维码
		function generateQrcode(){
		    console.log("start generate qrcode......");
		    var longUrl = "https://www.biglistoflittlethings.com/ilife-web-wx/solution.html?fromBroker=system&id="+solution.id;//获取分享目标链接：包含id
    			
		    //生成短码并保存
		    var shortCode = generateShortCode(longUrl);
		    console.log("got short code",shortCode);
		    saveShortCode(hex_md5(longUrl),"solution_"+solution.id,'system','system',"mp",encodeURIComponent(longUrl),shortCode);    
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
		    uploadPngFile(img, "qrcode"+solution.id+".png");//文件名称以itemKey唯一识别

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

		//当前个性化方案		
		var solution = {
				id: "${solution.id}",
				name: "${solution.name}",
				description: "${solution.description}",
				byOpenid: "${solution.byOpenid}",
				byNickname: "${solution.byNickname}",
				status: ${solution.status},
				scheme:{
					id: "${solution.scheme.id}",
					logo: "${solution.scheme.logo}",
					name: "${solution.scheme.name}",
				}
		};//当前方案
		//解析article
	    try{
	    	var json = JSON.parse(${solution.article});//注意是JSON string，需要解析
	    	solution.article = json;
	    }catch(err){
	    	console.log("failed parse solution article");
	    	solution.article = {};
	    }	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/diy/solution/">个性定制方案列表</a></li>
		<li class="active"><a href="${ctx}/diy/solution/form?id=${solution.id}">个性定制方案<shiro:hasPermission name="diy:solution:edit">${not empty solution.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="diy:solution:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<div style="display:flex;flex-direction:row;flex-wrap:nowrap">
		<div style="width:45%">	
	<form:form id="inputForm" modelAttribute="solution" action="${ctx}/diy/solution/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">所属主题：</label>
			<div class="controls">
				 <sys:treeselect id="scheme" name="scheme.id" value="${solution.scheme.id}" labelName="scheme.name" labelValue="${solution.scheme.name}"
					title="定制主题" url="/diy/proposalScheme/listData" />	
				 <span class="help-inline"><font color="red">*</font> </span>				
			</div>			
		</div>
		<div class="control-group">
			<label class="control-label">引用方案：</label>
			<div class="controls">
				 <sys:treeselect id="refer" name="refer.id" value="${solution.refer.id}" labelName="refer.name" labelValue="${solution.refer.name}"
					title="个性化方案" url="/diy/solution/listData" />					
			</div>			
		</div>
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">目标用户Openid：</label>
			<div class="controls">
				<form:input path="forOpenid" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">定制用户Openid：</label>
			<div class="controls">
				<form:input path="byOpenid" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">目标用户昵称：</label>
			<div class="controls">
				<form:input path="forNickname" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">定制用户昵称：</label>
			<div class="controls">
				<form:input path="byNickname" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:select path="status" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="4" maxlength="512" class="input-xxlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="diy:solution:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
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