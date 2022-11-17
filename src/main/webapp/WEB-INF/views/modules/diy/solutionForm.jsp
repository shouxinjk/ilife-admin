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

			//显示tabs
		    $( "#tabs" ).tabs();
		    //加载微信群
		    loadWxGroups();
			
		});
		
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
		
		var solution = {
				id: "${solution.id}",
				name: "${solution.name}",
				description: "${solution.description}",
				byOpenid: "${solution.byOpenid}",
				byNickname: "${solution.byNickname}",
		};//当前方案
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
	          <!--li><a href="#posterInfo">分享海报</a></li>
	          <li><a href="#articleInfo">图文内容</a></li-->
	          <li><a href="#wxGroupInfo">云推送</a></li>
	        </ul>
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