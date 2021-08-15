<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>模型展示模板管理</title>
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
		<li><a href="${ctx}/mod/viewTemplate/">模型展示模板列表</a></li>
		<li class="active"><a href="${ctx}/mod/viewTemplate/form?id=${viewTemplate.id}">模型展示模板<shiro:hasPermission name="mod:viewTemplate:edit">${not empty viewTemplate.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:viewTemplate:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="viewTemplate" action="${ctx}/mod/viewTemplate/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<table width="100%">
		<tr>
		<td width="300px" valign="top" style="padding-left:20px">	
			<div>类型：</div>
			<form:select path="type" items="${fns:getDictList('viewTemplateType')}" itemLabel="label" itemValue="value" htmlEscape="false" style="width:270px"/>
			<span class="help-inline"><font color="red">*</font> </span><br/><br/>
			<div>格式：</div>
			<form:select path="subType" items="${fns:getDictList('viewTemplateFormat')}" itemLabel="label" itemValue="value" htmlEscape="false" style="width:270px"/>
			<span class="help-inline"><font color="red">*</font> </span><br/><br/>
			<div>名称：</div>
			<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/><br/><br/>
			<div>优先级：</div>
			<form:input path="priority" htmlEscape="false" maxlength="8" class="input-xlarge "/><br/><br/>
			<div>描述：</div>
			<form:textarea path="description" htmlEscape="false" rows="6" maxlength="1024" class="input-xlarge "/>	<br/><br/>
		</td>
		<td valign="top">
			<div>模板表达式：</div>
			<form:textarea path="expression" htmlEscape="false" rows="20" style="width:95%"/>	
		</td>
		</tr>
		<tr>
		<td colspan="2">
			<div class="form-actions">
				<shiro:hasPermission name="mod:viewTemplate:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>		
		</td>
		</tr>
		</table>		
	</form:form>
</body>
</html>