<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Board管理</title>
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
			
			//加载清单及清单明细
			loadBoard("${board.id}");
			loadBoardItems("${board.id}");
			
		    //显示tabs
		    $( "#tabs" ).tabs();
		    
		    //获取海报模板
		    requestPosterScheme();
		    //获取图文模板
		    requestArticleScheme();
		    //注册事件
		    registerSxEvents();
		});
		

		function registerSxEvents(){
			//注册海报生成事件
		    $("#btnPoster").click(function(){
		        //requestPosterScheme();//点击后重新生成海报
		        //获取当前选中的海报
		        var posterSchemeId = $("#posterScheme").val();
		        console.log("got poster scheme.",posterSchemeId);
		        //var scheme = JSON.parse($("#posterScheme").val());
		        currentPosterScheme = posterSchemes[posterSchemeId];
		        //requestPoster(scheme,broker,stuff,app.globalData.userInfo);//根据当前选择重新生成海报
		        //requestPoster(scheme);//根据当前选择重新生成海报
		        //生成分享二维码后生成海报
		        generateQrcode();
		    });
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
		
		//海报模板
		var posterSchemes = {};
		//生成图文内容：请求模板列表
		function requestArticleScheme(){
		    //获取模板列表
		    $.ajax({
		        url:$("#sxContextPath").val()+"/mod/viewTemplate/rest/listByType/board",
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
		        url:$("#sxContextPath").val()+"/rest/api/material-html",
		        type:"post",
		        data:JSON.stringify({
		            templateId:templateId,
		            board: board,
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
		            $("#postTitle").val(board.title+" - "+templateName);//默认采用商品标题 - 模板标题
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
		    var isExists = board.article && board.article[templateId];
		    if(isExists){//如果已经生成则直接更新，注意存储的是文章ID
		        console.log("\n===try to update exists article. ===\n",board.article[templateId]);
		        $.ajax({
		            url:"https://mp.biglistoflittlethings.com/wp-json/wp/v2/posts/"+board.article[templateId],
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
		                sendItemArticleToWebhook(board.article[templateId]);//发送到企业微信群便于分享
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
		    }else{//否则生成新的文章，并且更新board.article
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
		                board.article[templateId]=res.id;
		                sendItemArticleToWebhook(res.id);//发送到企业微信群便于分享
		                //提交文章到 索引
		                var doc = createArticleDoc(res.id);
		                indexArticleDoc(doc);  		                
		                //显示预览链接
		                $("#btnPreview").attr("href",app.config.mp_api+"/archives/"+res.id);
		                $("#btnPreview").css("display","block");
		                updateBoard();
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
		    if(board.tags && board.tags.trim().length>0){
		        board.tags.split(" ").forEach(function(item){
		            if(tags.indexOf(item)<0)tags.push(item);
		        });
		    }
		    if(board.keywords && board.keywords.trim().length>0){
		        board.keywords.split(" ").forEach(function(item){
		            if(tags.indexOf(item)<0)tags.push(item);
		        });
		    }

		    //根据条目构建价格等信息
		    var profitMin = 999999999;//集合内最低佣金
		    var profitMax = 0;//集合内最高佣金
		    var priceMin = 999999999;//最低价格
		    var priceMax = 0;//最低价格
		    var distributors = [];
		    var sources = [];
		    items.forEach(function(item){
		    	if(item.price.sale>priceMax)priceMax = item.price.sale;
		    	if(item.price.sale<priceMin)priceMin = item.price.sale;
		    	if(item.profit.order>profitMax)profitMax = item.profit.order;
		    	if(item.profit.order<profitMin)profitMin = item.profit.order;
		    	if(distributors.indexOf(item.distributor.name)<0)distributors.push(item.distributor.name);
		    	if(sources.indexOf(item.source)<0)source.push(item.source);
		    });

		    //装配索引文档
		    var doc = {
		        source: sources,
		        type: "board" ,
		        itemkey: board.id,   //单品直接用itemKey，列表用boardId
		        template: templateId ,                               
		        url: articleUrl,
		        title: postTitle,
		        summary: board.description + " "+ postContent, //一股脑扔进去就可以
		        tags: tags,
		        price: {
		            currency: "",
		            bid: priceMax,
		            sale: priceMin,
		            profit: profitMin,
		            profit2: profitMax
		        },                
		        logo: board.logo?board.logo:items[0].images[0],
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
		                       "title" : "Board列表图文上新",
		                       "description" : board.title,
		                       "url" : "https://www.biglistoflittlethings.com/ilife-web-wx/content.html?id="+articleId,//将跳转到content.html附加浏览用户的formUser、fromBroker信息
		                       "picurl" : board.logo
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
		
		//生成商品海报：先获得海报列表
		function requestPosterScheme(){
		    $.ajax({
		        url:$("#sxContextPath").val()+"/mod/posterTemplate/rest/board-templates",
		        type:"get",
		        data:{},
		        success:function(schemes){
		            console.log("\n===got item poster scheme ===\n",schemes);
		            //遍历海报并生成
		            for(var i=0;i<schemes.length;i++){
		                //传递broker/stuff/userInfo作为海报生成参数
		                //requestPoster(schemes[i],broker,stuff,app.globalData.userInfo);
		                //将海报列表填写到选择器，可以根据需要选择生成
		               //遍历模板
		                var total = 0;
		                for(var i=0;i<schemes.length;i++){
		                    //将模板显示到界面，等待选择后生成：注意将scheme json作为value
		                    //$("#posterScheme").append("<option value='"+JSON.stringify(schemes[i])+"'>"+schemes[i].name+"</option>");
		                    posterSchemes[schemes[i].id] = schemes[i];//记录poster定义
		                    $("#posterScheme").append("<option value='"+schemes[i].id+"'>"+schemes[i].name+"</option>");
		                    total++;
		                }
		                if(total>0){
		                    $("#posterScheme option:eq(1)").attr('selected', 'selected');//选中第一个
		                    $("#btnPoster").css('display', 'block');//显示生成按钮
		                }
		            }
		        }
		    });  
		}

		//生成海报，返回海报图片URL
		//注意：海报模板中适用条件及参数仅能引用这三个参数
		function requestPoster(scheme,xBroker,xItem,xUser){
			if(!scheme)
				scheme = currentPosterScheme;
		    //判断海报模板是否匹配当前条目
		    var isOk = true;
		    if(scheme.condition && scheme.condition.length>0){//如果设置了适用条件则进行判断
		        try{
		            isOk = eval(scheme.condition);
		        }catch(err){
		            console.log("\n=== eval poster condition error===\n",err);
		        }
		    }
		    if(!isOk){//如果不满足则直接跳过
		        console.log("condition not satisifed. ignore.");
		        return;       
		    }

		    //准备海报参数
		    try{
		        eval(scheme.options);//注意：脚本中必须使用 var xParam = {}形式赋值
		    }catch(err){
		        console.log("\n=== eval poster options error===\n",err);
		        return;//这里出错了就别玩了
		    }
		    console.log("\n===eval poster options===\n",xParam);
		    var poster_options = {
		    		accessKey: "ApfrIzxCoK1DwNZO",
	                secretKey: "EJCwlrnv6QZ0PCdvrWGi"
		    }
		    var options = {//merge参数配置
		                  ...poster_options,//静态参数：accessKey、accessSecret信息
		                  ...xParam //动态参数：配置时定义
		                }
		    console.log("\n===start request poster with options===\n",options);
		    //请求生成海报
		    $.ajax({
		        url:"https://poster.biglistoflittlethings.com/api/link",
		        type:"post",
		        data:JSON.stringify(options),
		        success:function(res){
		            console.log("\n===got item poster info ===\n",res);
		            //将海报信息更新到stuff
		            if(res.code==0 && res.url && res.url.length>0){
		                //显示到界面
	                    $("#poster").empty();//清空
	                    $("#poster").append("<div id='poster"+scheme.id+"' class='prop-row'><img style='object-fill:cover;width:100%' src='"+res.url+"'/></div>");
	                    $("#posterTitle").css("display","block"); 
		                //更新到board
		                board.poster[scheme.id] = res.url;
		                sendItemPosterToWebhook(res.url);//推送到企业微信群
		                updateBoard();
		            }
		        }
		    });     
		}		
		
		//发送信息到运营群：运营团队收到新内容提示
		//发送卡片：其链接为图片地址
		function sendItemPosterToWebhook(posterImgUrl){
		    //推动图文内容到企业微信群，便于转发
		    var msg = {
		            "msgtype": "news",
		            "news": {
		               "articles" : [
		                   {
		                       "title" : "新增Board海报",
		                       "description" : board.title,
		                       "url" : posterImgUrl,
		                       "picurl" : board.logo
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
		
		//根据boardId查询清单：这里有重复调用，直接读取board内容，未通过服务器端返回
		function loadBoard(boardId){
		    $.ajax({
		        url:"${ctx}/mod/board/rest/board/"+boardId,
		        type:"get",
		        data:{},
		        success:function(res){
			        if(res.status){
			            board = res.data;
			            //解析article
					    try{
					    	var json = JSON.parse(board.article);//注意是JSON string，需要解析
					    	board.article = json;
					    }catch(err){
					    	console.log("failed parse board article");
					    	board.article = {};
					    }
					  	//解析poster
					    try{
					    	var json = JSON.parse(board.poster);//注意是JSON string，需要解析
					    	board.poster = json;
					    }catch(err){
					    	console.log("failed parse board poster");
					    	board.poster = {};
					    }
			        }else{
			        	console.log("failed load board.[boardId]"+boardId);
			        } 
		        }
		    }) 
		}
		//修改board：注意同时修改board信息以及board列表描述内容
		function updateBoard(){   
			var data_str = JSON.stringify(board);//重要：避免影响board数据，通过转换后建立新的对象
		    var data = JSON.parse(data_str);
		    data.poster = JSON.stringify(board.poster);
		    data.article = JSON.stringify(board.article);
		    
		    $.ajax({
		        url:"${ctx}/mod/board/rest/board/"+board.id,
		        type:"put",
		        data:JSON.stringify(data),
		        headers:{
	                "Content-Type":"application/json"
	            }, 
		        success:function(res){
		        	if(res.status){
			            console.log("board updated.", res);
			        }
		        }
		    });
		}		
		
		//根据boardId查询所有item列表
		function loadBoardItems(boardId){
		    $.ajax({
		        url:"${ctx}/mod/boardItem/rest/board-items/"+boardId,
		        type:"get",
		        data:{},
		        success:function(items){
			        for(var i = 0 ; i < items.length ; i++){
			            loadBoardItem(items[i]);//查询具体的item条目
			        }  
		        }
		    }) 
		}
		//查询单个item明细
		function loadBoardItem(boardItem){//获取内容列表
		    $.ajax({
		        url:"https://data.shouxinjk.net/_db/sea/my/stuff/"+boardItem.item,
		        type:"get",
		        data:{},
		        success:function(data){
		        	boardItem.stuff = data;//装载stuff到boarditem   
		            items.push(boardItem); //装载到列表   
		        }
		    })            
		}


		//生成短连接及二维码：海报生成带有系统达人分享信息
		/**
		function generateQrcode(){
		    console.log("start generate qrcode......");
		    var longUrl = "https://www.biglistoflittlethings.com/ilife-web-wx/board2-waterfall.html?fromBroker=system&posterId="
		    			+currentPosterScheme.id+"&id="+board.id;//获取分享目标链接：包含boardId及posterId
		    
		    $.ajax({
		         type:'POST',
		         url:"https://data.shouxinjk.net/ilife-wechat/wechat/ilife/short-url",
		         data:JSON.stringify({ longUrl: longUrl }),
				 headers:{
				        "Content-Type":"application/json"
				    },
		         success:function (res) {
				        console.log("generate short url.",longUrl,res);
				        var shortUrl = longUrl;
				        if (res.status) {//获取短连接
				            shortUrl = res.data.url;
				        }
				        //bug修复：qrcode在生成二维码时，如果链接长度是192-217之间会导致无法生成，需要手动补齐
				        if(shortUrl.length>=192 && shortUrl.length <=217){
				            shortUrl += "&placehold=fix-qrcode-bug-url-between-192-217";
				        }
				        console.log("generate qrcode by short url.[length]"+shortUrl.length,shortUrl);
				        var qrcode = new QRCode("app-qrcode-box");
				        qrcode.makeCode(shortUrl);
				        setTimeout(uploadPngFile,300);//需要图片装载完成后才能获取 
				    }
		     }); 		    			
   
		}
//**/

		//生成短连接及二维码
		function generateQrcode(){
		    console.log("start generate qrcode......");
		    var longUrl = "https://www.biglistoflittlethings.com/ilife-web-wx/board2-waterfall.html?fromBroker=system&posterId="
    			+currentPosterScheme.id+"&id="+board.id;//获取分享目标链接：包含boardId及posterId
    			
		    //生成短码并保存
		    var shortCode = generateShortCode(longUrl);
		    console.log("got short code",shortCode);
		    saveShortCode(hex_md5(longUrl),"board_"+board.id,'system','system',"mp",encodeURIComponent(longUrl),shortCode);    
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
		    uploadPngFile(img, "qrcode"+board.id+currentPosterScheme.id+(new Date().getTime())+".png");//文件名称以itemKey+posterId+时间戳唯一识别

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
		
		var items = [];//清单条目列表
		var board = {};//当前清单
		var brokerQrcode = null;//存放系统达人分享二维码url
		var currentPosterScheme = null;//存放当前选中的海报模板
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mod/board/">Board列表</a></li>
		<li class="active"><a href="${ctx}/mod/board/form?id=${board.id}">Board<shiro:hasPermission name="mod:board:edit">${not empty board.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:board:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<div style="display:flex;flex-direction:row;flex-wrap:nowrap">
		<div style="width:45%">
			<form:form id="inputForm" modelAttribute="board" action="${ctx}/mod/board/save" method="post" class="form-horizontal">
				<form:hidden path="id"/>
				<input id="sxContextPath" name="sxContextPath" type="hidden" value="${ctx}"/>
				<sys:message content="${message}"/>	
				<div class="control-group">
					<label class="control-label">达人：</label>
					<div class="controls">
						<sys:treeselect id="broker" name="broker.id" value="${board.broker.id}" labelName="broker.name" labelValue="${board.broker.name}"
							title="选择达人" url="/mod/broker/treeData" extId="${broker.id}" cssClass="" allowClear="true"/>
					</div>
				</div>			
				<div class="control-group">
					<label class="control-label">标题：</label>
					<div class="controls">
						<form:input path="title" htmlEscape="false" maxlength="255" class="input-xlarge"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">Logo：</label>
					<div class="controls">
						<form:textarea path="logo" htmlEscape="false" maxlength="512" class="input-xlarge" rows="5"/>
					</div>
				</div>		
				<div class="control-group">
					<label class="control-label"></label>
					<div class="controls">
						<img src="${board.logo}" width="200"/>
					</div>
				</div>			
				<div class="control-group">
					<label class="control-label">描述：</label>
					<div class="controls">
						<form:textarea path="description" htmlEscape="false" class="input-xlarge " rows="5"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">类型：</label>
					<div class="controls">
						<form:select path="type" items="${fns:getDictList('boardType')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">标签：</label>
					<div class="controls">
						<form:textarea path="tags" htmlEscape="false" maxlength="255" class="input-xlarge " rows="5"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">关键词：</label>
					<div class="controls">
						<form:textarea path="keywords" htmlEscape="false" maxlength="512" class="input-xlarge " rows="5"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">状态：</label>
					<div class="controls">
						<form:select path="status" items="${fns:getDictList('active_inactive')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</div>
				</div>
				<div class="form-actions">
					<shiro:hasPermission name="mod:board:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
			</form:form>
		</div>
		<!-- tabs：海报及图文内容 -->
	    <div id="tabs" style="width:55%">
	        <ul>
	          <li><a href="#posterInfo">分享海报</a></li>
	          <li><a href="#articleInfo">图文内容</a></li>
	        </ul>
	        <!-- 海报 -->
	        <div id="posterInfo">
	            <!-- Posters: 海报列表，便于下载分享 -->
	            <div class="tagging" style="display:flex;flex-direction: row;flex-wrap: nowrap;vertical-align: middle;">
	              <select id="posterScheme" class="sxSelect" style="width:200px"></select>
	              <button id="btnPoster" class="taggingBtn" class="btn" style="margin:0 10px;">生成海报</button>
	            </div>
	            <div id="posterTitle" class="prop-key" style="width:90%;display:none;margin:10px 0px;">请确定后下载海报图片发送到运营群:</div> 
	            <div id="poster" style="width:100%;">
	            </div>   
	            <div id="app-qrcode-box" style="display:none"></div>                      
	        </div>
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
	    </div>  	
	</div>
</body>
</html>