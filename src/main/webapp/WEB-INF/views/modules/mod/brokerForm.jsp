<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>推广达人管理</title>
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
			
			//加载主客观评价
	    	loadMeasureAndScore();//加载客观评价
	        loadMeasureAndScore2();//加载主观评价内容
		});

		//该方法提供给推荐语模板使用，获取特征指标的评价结果，返回JSON：{propKey: score}
		function getItemScore(){
		    var featuredPropScore = {};
		    featuredDimension.forEach( prop =>{
		        if(prop.propKey)featuredPropScore[prop.propKey] = itemScore[prop.id];//未定义propKey则不予考虑
		        featuredPropScore[prop.name] = itemScore[prop.id];
		    });
		    return featuredPropScore;
		}

		var featuredDimension = [];//客观评价维度列表
		var itemScore = {};//当前条目评分列表：手动修改后同时缓存
		var categoryScore = {};//当前条目所在类目评分列表
		var measureScores = [];//显示到grid供修改，在measure基础上增加score
		var hasItemScore = false; //记录是否已获取商品评分
		var hasCategoryScore = false; //记录是否已获取类目评分，即指定类目下商品的平均值
		function loadMeasureAndScore(){
		    //根据category获取客观评价数据
		    var data = {
		        categoryId:"user" //固定为user
		    };
		    $.ajax({
		        url:"${ctx}/mod/userDimension/rest/dim-tree-by-category",
		        type:"get",     
		        data:data,
		        success:function(res){
		        	console.log("======\nload dimension.",data,res);
			        if (res.length>0) {//本地存储评价数据
			            measureScheme = res;
			        }else{//没有则啥也不干
			            //do nothing
			            console.log("failed load dimension tree.",data);
			        }
		        }
		    }); 
		    //获取类目下的特征维度列表
		    $.ajax({
		        url:"${ctx}/mod/userDimension/rest/featured-dimension",
		        type:"get",
		        //async:false,//同步调用
		        data:data,
		        success:function(json){
		            console.log("===got featured dimension===\n",json);
		            featuredDimension = json;
		            //准备默认值
		            for(var i=0;i<json.length;i++){
		            	var entry = json[i];
		            	itemScore[entry.id] = (entry.score&&entry.score>0)?entry.score:0.75;
		            	categoryScore[entry.id] = 0.5;
		            }
		            //查询已有标注值
		            //根据itemKey获取评价结果
		            //feature = 1；dimensionType：0客观评价，1主观评价
		            //注意：由于clickhouse非严格唯一，需要取最后更新值
		            $.ajax({
		                url:app.config.analyze_api+"?query=select dimensionId,score from ilife.info where feature=1 and dimensionType=0 and itemKey='${broker.id}' order by ts format JSON",
		                type:"get",
		                //async:false,//同步调用
		                //data:{},
		                headers:{
		                    "Authorization":"Basic ZGVmYXVsdDohQG1AbjA1"
		                },         
		                success:function(ret){
		                    console.log("===got item score===\n",ret);
		                    for(var i=0;i<ret.rows;i++){
		                        itemScore[ret.data[i].dimensionId] = ret.data[i].score;
		                    }
		                    console.log("===assemble item score===\n",itemScore);
		                    hasItemScore = true;
		                    pupulateMeasureScore();//展示数据到界面
		                }
		            }); 

		        }
		    });   

		    //根据categoryId获取评价结果
		    //feature = 1；dimensionType：0客观评价，1主观评价
		    $.ajax({
		        url:app.config.analyze_api+"?query=select dimensionId,avg(score) as score from ilife.info where feature=1 and dimensionType=0 and categoryId='user' group by dimensionId format JSON",
		        type:"get",
		        //async:false,//同步调用
		        //data:{},
		        headers:{
		            "Authorization":"Basic ZGVmYXVsdDohQG1AbjA1"
		        },         
		        success:function(json){
		            console.log("===got category score===\n",json);
		            for(var i=0;i<json.rows;i++){
		                categoryScore[json.data[i].dimensionId] = json.data[i].score;
		            }
		            console.log("===assemble category score===\n",categoryScore);
		            hasCategoryScore = true;//设置以获取类目评分
		            pupulateMeasureScore();//显示到界面
		        }
		    }); 	
		}

		//在获取维度定义、商品评分、类目评分后组装展示到界面
		function pupulateMeasureScore(){
		    if(hasItemScore && hasCategoryScore){
		        //组装measureScore
		        for(var i=0;i<featuredDimension.length;i++){
		            var measureScore = featuredDimension[i];
		            measureScore.score = itemScore[measureScore.id]?itemScore[measureScore.id]:0.75;
		            measureScores.push(measureScore);
		        }

		        //显示measureScore表格提供标注功能
		        showMeasureScores();        
		    }
		}

		//该方法提供给推荐语模板使用，获取主观特征指标的评价结果，返回JSON：{propKey: score}
		function getItemScore2(){
		    var featuredPropScore = {};
		    featuredDimension2.forEach( prop =>{
		        if(prop.type)featuredPropScore[prop.type] = itemScore2[prop.id];//未定义type则不加载
		        //featuredPropScore[prop.name] = itemScore2[prop.id];
		    });
		    return featuredPropScore;
		}

		//加载主观评价数据：
		//是客观评价的重复代码：捂脸捂脸捂脸
		var featuredDimension2 = [];//客观评价维度列表
		var itemScore2 = {};//当前条目评分列表：手动修改后同时缓存
		var categoryScore2 = {};//当前条目所在类目评分列表
		var measureScores2 = [];//显示到grid供修改，在measure基础上增加score
		var hasItemScore2 = false; //记录是否已获取商品评分
		var hasCategoryScore2 = false; //记录是否已获取类目评分，即指定类目下商品的平均值
		var defaultVals = {
		    a:0.5,b:0.15,c:0.2,d:0.15,e:0.1,x:0.4,y:0.3,z:0.3
		};//默认vals键值对
		function loadMeasureAndScore2(){
		    //根据category获取主观评价scheme
		    var data = {
		        categoryId:"user"
		    };
		    $.ajax({
		        url:"${ctx}/mod/userEvaluation/rest/dim-tree-by-category",
		        type:"get",     
		        data:data,
		        success:function(res){
		        	console.log("======\nload evalution.",data,res);
			        if (res.length>0) {//本地存储评价数据
			            measureScheme = res;
			        }else{//没有则啥也不干
			            //do nothing
			            console.log("failed load evalution tree.",data);
			        }
		        }
		    }); 

		    //获取类目下的特征维度列表
		    $.ajax({
		        url:"${ctx}/mod/userEvaluation/rest/markable-featured-evaluation",
		        type:"get",
		        //async:false,//同步调用
		        data:data,
		        success:function(json){
		            console.log("===got featured evaluation===\n",json);
		            featuredDimension2 = json;
		            //按照字母序排序：根据type进行，排序结果为：abcdexyz
		            featuredDimension2.sort(function (s1, s2) {
		                x1 = s1.type;
		                x2 = s2.type;
		                if (x1 < x2) {
		                    return -1;
		                }
		                if (x1 > x2) {
		                    return 1;
		                }
		                return 0;
		            });
		            //准备默认值
		            for(var i=0;i<json.length;i++){
		                var entry = json[i];
		                itemScore2[entry.id] = (entry.score&&entry.score>0)?entry.score:defaultVals[entry.type];
		                categoryScore2[entry.id] = 0.5;//平均值直接设置为常数
		            }
		            //查询获取评价值
		            //根据itemKey获取评价结果
		            //feature = 1；dimensionType：0客观评价，1主观评价
		            //注意：由于clickhouse非严格唯一，需要取最后更新值
		            $.ajax({
		                url:app.config.analyze_api+"?query=select dimensionId,score from ilife.info where feature=1 and dimensionType=1 and itemKey='${broker.id}' order by ts format JSON",
		                type:"get",
		                //async:false,//同步调用
		                //data:{},
		                headers:{
		                    "Authorization":"Basic ZGVmYXVsdDohQG1AbjA1"
		                },         
		                success:function(ret){
		                    console.log("===got item score===\n",ret);
		                    for(var i=0;i<ret.rows;i++){
		                        itemScore2[ret.data[i].dimensionId] = ret.data[i].score;
		                    }
		                    console.log("===assemble item evaluation score===\n",itemScore2);
		                    hasItemScore2 = true;
		                    pupulateMeasureScore2();//显示到界面
		                }
		            });  
		            
		        }
		    });  


		    //根据categoryId获取评价结果
		    //feature = 1；dimensionType：0客观评价，1主观评价
		    $.ajax({
		        url:app.config.analyze_api+"?query=select dimensionId,avg(score) as score from ilife.info where feature=1 and dimensionType=1 and categoryId='user' group by dimensionId format JSON",
		        type:"get",
		        //async:false,//同步调用
		        //data:{},
		        headers:{
		            "Authorization":"Basic ZGVmYXVsdDohQG1AbjA1"
		        },         
		        success:function(json){
		            console.log("===got category score===\n",json);
		            for(var i=0;i<json.rows;i++){
		                categoryScore2[json.data[i].dimensionId] = json.data[i].score;
		            }
		            console.log("===assemble category score===\n",categoryScore2);
		            hasCategoryScore2 = true;//设置已经获取类目平均值
		            pupulateMeasureScore2();//显示到界面
		        }
		    });     


		}

		//在获取主观维度定义、商品评分、类目评分后组装展示到界面
		function pupulateMeasureScore2(){
		    if(hasItemScore2 && hasCategoryScore2){
		        //组装measureScore
		        for(var i=0;i<featuredDimension2.length;i++){
		            var measureScore = featuredDimension2[i];
		            measureScore.score = itemScore2[measureScore.id]?itemScore2[measureScore.id]:0.75;
		            measureScores2.push(measureScore);
		        }

		        //显示measureScore表格提供标注功能
		        showMeasureScores2();//主观评价      
		    }
		}		

		//显示客观评价得分表格，便于手动修改调整
		var tmpScores = {};
		function showMeasureScores(){
			//准备评分表格：逐行显示
			for(var i=0;i<measureScores.length;i++){
				tmpScores[measureScores[i].id] = measureScores[i];
				var html = "";
				html += "<div style='display:flex;flex-direction:row;flex-wrap:nowrap;margin:10px 0;'>";
				html += "<div style='width:120px;line-height:24px;'>"+measureScores[i].name+"</div>";
				html += "<div style='width:60px;text-align:center;line-height:24px;' id='mscore"+measureScores[i].id+"'>"+measureScores[i].score+"</div>";
				html += "<div style='width:70%' id='score"+measureScores[i].id+"'></div>";
				html += "</div>";
				$("#measuresList").append(html);//装载到界面
				$("#score"+measureScores[i].id).starRating({//显示为starRating
					totalStars: 10,
					starSize:20,
				    useFullStars:false,//能够显示半星
				    initialRating: measureScores[i].score*10,//注意：评分是0-1,直接转换
				    ratedColors:['#8b0000', '#dc143c', '#ff4500', '#ff6347', '#1e90ff','#00ffff','#40e0d0','#9acd32','#32cd32','#228b22'],
				    callback: function(currentRating, el){
				        //获取当前评价指标
				        var measureId = $(el).attr("id").replace(/score/g,'');
				        var old = tmpScores[measureId];
				        console.log("dude, now try update rating.[old]",measureId,old,currentRating);
				        //保存到本地
				        var newScore = currentRating*0.1;//直接转换到0-1区间
				        itemScore[measureId] = newScore;
				        $("#mscore"+measureId).html(newScore.toFixed(2));
				        $("#radarImg").empty();//隐藏原有图片
				        //showRadar();//重新生成雷达图

				        //提交数据并更新
				        var priority = old.parentIds.length - old.parentIds.replace(/\,/g,"").length;
					    $.ajax({
					        url:app.config.analyze_api+"?query=insert into ilife.info values ('${broker.id}','user','"+old.id+"','"+old.propKey+"',0,"+priority+",1,"+old.weight+",'"+old.script+"',"+newScore+",0,now())",
					        type:"post",
					        //data:{},
					        headers:{
					            "Authorization":"Basic ZGVmYXVsdDohQG1AbjA1"
					        },         
					        success:function(json){
					            console.log("===measure score updated===\n",json);
					        }
					    });  
				    }
				});		
			}
			//显示属性列表
			$("#measuresDiv").css("display","block");      
		}

		//显示主观评价得分表格，便于手动修改调整
		//受不了啦：全是复制代码啊啊啊啊啊啊啊啊啊啊~~~~~~~~~~~
		var tmpScores2 = {};
		function showMeasureScores2(){
		    //准备评分表格：逐行显示
		    for(var i=0;i<measureScores2.length;i++){
		        tmpScores2[measureScores2[i].id] = measureScores2[i];
		        var html = "";
		        html += "<div style='display:flex;flex-direction:row;flex-wrap:nowrap;margin:10px 0;'>";
		        html += "<div style='width:120px;line-height:24px;'>"+measureScores2[i].name+"</div>";
		        html += "<div style='width:60px;text-align:center;line-height:24px;' id='mscore2"+measureScores2[i].id+"'>"+measureScores2[i].score+"</div>";
		        html += "<div style='width:70%' id='score2_"+measureScores2[i].id+"'></div>";
		        html += "</div>";
		        $("#measuresList2").append(html);//装载到界面
		        $("#score2_"+measureScores2[i].id).starRating({//显示为starRating
		            totalStars: 10,
		            starSize:20,
		            useFullStars:false,//能够显示半星
		            initialRating: measureScores2[i].score*10,//注意：评分是0-1,直接转换
		            ratedColors:['#8b0000', '#dc143c', '#ff4500', '#ff6347', '#1e90ff','#00ffff','#40e0d0','#9acd32','#32cd32','#228b22'],
		            callback: function(currentRating, el){
		                //获取当前评价指标
		                var measureId = $(el).attr("id").replace(/score2_/g,'');
		                var old = tmpScores2[measureId];
		                console.log("dude, now try update evaluation rating.[old]",measureId,old,currentRating);
		                //保存到本地
		                var newScore = currentRating*0.1;//直接转换到0-1区间
		                itemScore2[measureId] = newScore;
		                $("#mscore2"+measureId).html(newScore.toFixed(2));
		                $("#radarImg2").empty();//隐藏原有图片
		                //showRadar2();//重新生成雷达图

		                //提交数据并更新
		                var priority = old.parentIds.length - old.parentIds.replace(/\,/g,"").length;
		                $.ajax({
		                    url:app.config.analyze_api+"?query=insert into ilife.info values ('${broker.id}','user','"+old.id+"','"+old.propKey+"',1,"+priority+",1,"+old.weight+",'"+old.script+"',"+newScore+",1,now())",
		                    type:"post",
		                    //data:{},
		                    headers:{
		                        "Authorization":"Basic ZGVmYXVsdDohQG1AbjA1"
		                    },         
		                    success:function(json){
		                        console.log("===measure score updated===\n",json);
		                    }
		                });  
		            }
		        });     
		    }
		    //显示属性列表
		    $("#measuresDiv2").css("display","block");      
		}		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mod/broker/">达人列表</a></li>
		<li class="active"><a href="${ctx}/mod/broker/form?id=${broker.id}">推广达人<shiro:hasPermission name="mod:broker:edit">${not empty broker.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:broker:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<div style="display:flex;flex-direction:row;flex-wrap:nowrap">
		<div style="width:40%">	
			<form:form id="inputForm" modelAttribute="broker" action="${ctx}/mod/broker/save" method="post" class="form-horizontal">
				<form:hidden path="id"/>
				<sys:message content="${message}"/>		
				<div class="control-group">
					<label class="control-label">所属机构:</label>
					<div class="controls">
		                <sys:treeselect id="orgnization" name="orgnization.id" value="${broker.orgnization.id}" labelName="orgnization.name" labelValue="${broker.orgnization.name}"
							title="机构" url="/sys/office/treeData?type=1"/>
					</div>
				</div>		
				<div class="control-group">
					<label class="control-label">系统用户:</label>
					<div class="controls">
		                <sys:treeselect id="sysUser" name="sysUser.id" value="${broker.sysUser.id}" labelName="sysUser.name" labelValue="${broker.sysUser.name}"
							title="系统用户" url="/sys/user/treeData" cssClass="" allowClear="true"/>
					</div>
				</div>			
				<div class="control-group">
					<label class="control-label">上级达人：</label>
					<div class="controls">
						<sys:treeselect id="parent" name="parent.id" value="${broker.parent.id}" labelName="parent.name" labelValue="${broker.parent.name}"
							title="上级达人" url="/mod/broker/treeData" extId="${broker.id}" cssClass="" allowClear="true"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">OpenId：</label>
					<div class="controls">
						<form:input path="openid" htmlEscape="false" maxlength="64" class="input-xlarge "/>
					</div>
				</div>	
				<div class="control-group">
					<label class="control-label">微信昵称：</label>
					<div class="controls">
						<form:input path="nickname" htmlEscape="false" maxlength="64" class="input-xlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">虚拟豆：</label>
					<div class="controls">
						<form:input path="points" htmlEscape="false" maxlength="20" class="input-xlarge "/>
					</div>
				</div>	
				<div class="control-group">
					<label class="control-label">金币：</label>
					<div class="controls">
						<form:input path="coins" htmlEscape="false" maxlength="20" class="input-xlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">BOT激活码：</label>
					<div class="controls">
						<form:input path="token" htmlEscape="false" maxlength="20" class="input-xlarge "/>
					</div>
				</div>	
				<div class="control-group">
					<label class="control-label">账户类型：</label>
					<div class="controls">
						<form:input path="accountType" htmlEscape="false" maxlength="20" class="input-xlarge "/>
					</div>
				</div>								
				<div class="control-group">
					<label class="control-label">真实姓名：</label>
					<div class="controls">
						<form:input path="name" htmlEscape="false" maxlength="20" class="input-xlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">身份证号码：</label>
					<div class="controls">
						<form:input path="securityNo" htmlEscape="false" maxlength="50" class="input-xlarge "/>
					</div>
				</div>		
				<div class="control-group">
					<label class="control-label">电话号码：</label>
					<div class="controls">
						<form:input path="phone" htmlEscape="false" maxlength="20" class="input-xlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">微信ID：</label>
					<div class="controls">
						<form:input path="wechatId" htmlEscape="false" maxlength="50" class="input-xlarge "/>
					</div>
				</div>		
				<div class="control-group">
					<label class="control-label">公司全称：</label>
					<div class="controls">
						<form:input path="companyName" htmlEscape="false" maxlength="50" class="input-xlarge "/>
					</div>
				</div>	
				<div class="control-group">
					<label class="control-label">开户行：</label>
					<div class="controls">
						<form:input path="companyBank" htmlEscape="false" maxlength="50" class="input-xlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">公司账户：</label>
					<div class="controls">
						<form:input path="companyAccount" htmlEscape="false" maxlength="50" class="input-xlarge "/>
					</div>
				</div>	
				<div class="control-group">
					<label class="control-label">公司联系人：</label>
					<div class="controls">
						<form:input path="companyContact" htmlEscape="false" maxlength="50" class="input-xlarge "/>
					</div>
				</div>	
				<div class="control-group">
					<label class="control-label">公司联系电话：</label>
					<div class="controls">
						<form:input path="companyTelephone" htmlEscape="false" maxlength="50" class="input-xlarge "/>
					</div>
				</div>											
				<div class="control-group">
					<label class="control-label">二维码URL：</label>
					<div class="controls">
						<form:input path="qrcodeUrl" htmlEscape="false" maxlength="512" class="input-xlarge "/>
					</div>
				</div>			
				<div class="control-group">
					<label class="control-label">邮件：</label>
					<div class="controls">
						<form:input path="email" htmlEscape="false" maxlength="50" class="input-xlarge "/>
					</div>
				</div>
				<!--div class="control-group">
					<label class="control-label">层级：</label>
					<div class="controls">
						<form:input path="hierarchy" htmlEscape="false" maxlength="11" class="input-xlarge "/>
					</div>
				</div-->
				<div class="control-group">
					<label class="control-label">等级：</label>
					<div class="controls">
						<form:input path="level" htmlEscape="false" maxlength="50" class="input-xlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">支付宝账号：</label>
					<div class="controls">
						<form:input path="alipayAccount" htmlEscape="false" maxlength="50" class="input-xlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">支付宝账户名：</label>
					<div class="controls">
						<form:input path="alipayAccountName" htmlEscape="false" maxlength="50" class="input-xlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">账户状态：</label>
					<div class="controls">
						<form:input path="status" htmlEscape="false" maxlength="20" class="input-xlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">升级状态：</label>
					<div class="controls">
						<form:input path="upgrade" htmlEscape="false" maxlength="20" class="input-xlarge "/>
					</div>
				</div>
				<div class="form-actions">
					<shiro:hasPermission name="mod:broker:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
			</form:form>
		</div>
		<!-- 主客观标注 -->
	    <div id="tabs" style="width:55%;border-left:1px solid silver;padding-left:5px;margin-left:5px;">
            <!--客观评价结果：支持标注时手动录入-->
            <div id="measuresDiv" style="display:none">
                <label class="formLabel" style="line-height: 30px;margin:5px 0; padding:5px 0;background-color:#fbe7e2;width:100%;">客观评价结果</label>
                <div id="measuresList"></div>
            </div> 
            
            <!--客观评价结果：支持标注时手动录入-->
            <div id="measuresDiv2" style="display:none">
                <label class="formLabel" style="line-height: 30px;margin:5px 0; padding:5px 0;background-color:#ccf8d1;width:100%;">主观评价结果</label>
                <div id="measuresList2"></div>
            </div>  	    
	   	</div>  	
	</div>	
</body>
</html>