<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>个性化定制指南条目管理</title>
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
			//加载用户属性
			loadUserProps();
			
		    //显示tabs
		    $( "#tabs" ).tabs();
		    
		    //注册事件：转换为脚本
		    $("#generateScriptBtn").click(function(){
		    	var script = $("#criteriaDesc").val();
		    	//直接遍历prop，将属性名称替换为属性property
		    	props.forEach(function(prop){
		    		const re = new RegExp(prop.name, 'g');
		    		script = script.replace(re, prop.property);
		    	});
		    	$("#criteria").val(script);
		    });
		    /**
		    //初始化tinyMCE编辑器
		    tinymce.init({
		        selector: '#criteria',
		        branding: false,
		        menubar: false,
		        toolbar: [
		            'styleselect | fontselect | fontsizeselect | bold italic underline strikethrough | link h2 h3 h4 blockquote | forecolor backcolor | link image | alignleft aligncenter alignright'
		          ],
		        plugins: 'autoresize',
		        autoresize_bottom_margin: 50, 
		        autoresize_max_height: 800, // 编辑区域的最大高
		        autoresize_min_height: 400, //编辑区域的最小高度
		        autoresize_on_init: true,
		        autoresize_overflow_padding: 50
		      });	
		    //**/
		});
		
		//加载用户属性并显示到界面
		var props = [];
		function loadUserProps(){
		    //获取所有用户属性
		    $.ajax({
		        url:"${ctx}/mod/userMeasure/rest/measures",
		        type:"get",
		        data:{},
		        success:function(res){
		            console.log("\n===got user measures ===\n",res);
		            //遍历模板
		            props = res;
		            props.forEach(function(prop){
		            	//找到属性分类div，如果没有则新建
		            	if($("#propcat"+prop.category.id).length==0){
		            		$("#props").append("<div id='propcat"+prop.category.id+"' style='display:flex;flex-direction:row;flex-wrap:wrap;margin-top:5px;'></div>");
		            		$("#propcat"+prop.category.id).append("<div style='width:100%;border-bottom:1px solid silver;'>"+prop.category.name+"</div>");//显示分类标签
		            	}
		            	//然后在属性分类div下增加属性
		            	$("#propcat"+prop.category.id).append("<div id='prop"+prop.id+"' data-property='"+prop.property+"' data-id='"+prop.id+"' data-name='"+prop.name+"' style='border:1px solid silver;border-radius:5px;margin:5px 2px;padding:2px 5px;line-height:18px;'>"+prop.name+"</div>");
		            	//注册事件：点击后自动填写到textarea
		            	$("#prop"+prop.id).click(function(){
		            		var property = $(this).data("property");
		            		var name = $(this).data("name");
		            		//填充到脚本区域
		            		$("#criteriaDesc").val($("#criteriaDesc").val()+" "+name);
		            	});
		            	
		            });
		        }
		    }); 			
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/diy/guideTerm/">个性化定制指南条目列表</a></li>
		<li class="active"><a href="${ctx}/diy/guideTerm/form?id=${guideTerm.id}">个性化定制指南条目<shiro:hasPermission name="diy:guideTerm:edit">${not empty guideTerm.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="diy:guideTerm:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
			<form:form id="inputForm" modelAttribute="guideTerm" action="${ctx}/diy/guideTerm/save" method="post" class="form-horizontal">
				<form:hidden path="id"/>
				<sys:message content="${message}"/>		
	<div style="display:flex;flex-direction:row;flex-wrap:nowrap">
		<div style="width:40%">		
				<div class="control-group">
					<label class="control-label">指南：</label>
					<div class="controls">
						 <sys:treeselect id="book" name="book.id" value="${guideTerm.book.id}" labelName="book.name" labelValue="${guideTerm.book.name}"
							title="定制指南列表" url="/diy/guideBook/listData"  cssClass="required"/>					
					</div>			
				</div>
				<div class="control-group">
					<label class="control-label">Section：</label>
					<div class="controls">
						 <sys:treeselect id="section" name="section.id" value="${guideTerm.section.id}" labelName="section.name" labelValue="${guideTerm.section.name}"
							title="定制指南列表" url="/diy/proposalSection/listData"  cssClass="required"/>					
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
					<label class="control-label">描述：</label>
					<div class="controls">
						<form:textarea path="description" htmlEscape="false" rows="4" maxlength="512" class="input-xlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">注意事项：</label>
					<div class="controls">
						<form:textarea path="tips" htmlEscape="false" rows="4" maxlength="512" class="input-xlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">标签：</label>
					<div class="controls">
						<form:textarea path="tags" htmlEscape="false" rows="2" maxlength="256" class="input-xlarge "/>
					</div>
				</div>	

				<!-- 
				<div class="control-group">
					<label class="control-label">脚本规则：</label>
					<div class="controls">
						<form:textarea path="script" htmlEscape="false" rows="4" class="input-xxlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">脚本描述：</label>
					<div class="controls">
						<form:textarea path="scriptDesc" htmlEscape="false" rows="4" class="input-xxlarge "/>
					</div>
				</div>
				-->
				<div class="control-group">
					<label class="control-label">优先级：</label>
					<div class="controls">
						<form:input path="priority" htmlEscape="false" maxlength="11" class="input-xlarge  digits"/>
					</div>
				</div>
				<div class="form-actions">
					<shiro:hasPermission name="diy:guideTerm:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
		</div>
		<!-- tabs：海报及图文内容 -->
	    <div id="tabs" style="width:60%">
	        <ul>
	          <li><a href="#scriptInfo">规则编辑</a></li>
	          <li><a href="#scriptTest">规则校验</a></li>
	        </ul>
	        <!-- 公式编辑 -->
	        <div id="scriptInfo">
		        <div style="display:flex;flex-direction:row;flex-wrap:nowrap">
		        	<div style="width:60%">
						<div>适用条件：选择属性编辑适用条件，结果为true或false，完成后 <span id="generateScriptBtn" style="color:blue;">生成脚本</span></div>
						<div>
							<form:textarea path="criteriaDesc" id="criteriaDesc" htmlEscape="false" rows="8" class="input-xxlarge "/>
						</div>		
						<div style="margin-top:10px;">规则脚本：支持数学运算及逻辑运算，点击右侧属性列表可自动获取属性名称</div>
						<div>
							<form:textarea path="criteria" id="criteria" htmlEscape="false" rows="8" class="input-xxlarge "/>
						</div>   
					</div>  
		        	<div style="width:40%;border-left:1px solid silver;padding-left:5px;min-height:400px;">
						<div>用户属性列表：</div>
						<div id="props"></div>		 
					</div> 					
				</div>   
	        </div>	
	        <!-- 公式检查 -->
	        <div id="scriptTest">
	        </div>	 
	    </div>  	
	</div>	
			</form:form>	               		
</body>
</html>