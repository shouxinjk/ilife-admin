<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>动态表单管理</title>
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
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/diy/jsonForm/">动态表单列表</a></li>
		<li class="active"><a href="${ctx}/diy/jsonForm/form?id=${jsonForm.id}">动态表单<shiro:hasPermission name="diy:jsonForm:edit">${not empty jsonForm.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="diy:jsonForm:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jsonForm" action="${ctx}/diy/jsonForm/save" method="post" class="form-horizontal">
	<div style="display:flex;flex-direction:row;flex-wrap:nowrap">
		<div style="width:35%">		
			
				<form:hidden path="id"/>
				<sys:message content="${message}"/>		
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
						<form:textarea path="description" htmlEscape="false" rows="19" maxlength="512" class="input-xlarge "/>
					</div>
				</div>

			
	
		</div>
		<!-- JSON表单定义 -->
	    <div id="tabs" style="width:65%">
				<!-- 表单定义 -->  
				<div class="control-group" style="border-left:1px solid silver;padding-left:10px;border-bottom:0;">
					<div class="controls" style="width:100%;margin:10px 0px;">表单JSON：</div>				
					<div class="controls" style="width:100%;margin:10px 0px;">
						<form:textarea path="json" htmlEscape="false" rows="20" class="input-xxlarge " style="width:90%;"/>
					</div>
				</div>               
	    </div>  	
	</div>	
	
	<div class="form-actions">
		<shiro:hasPermission name="diy:jsonForm:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>	
	</form:form>	
</body>
</html>