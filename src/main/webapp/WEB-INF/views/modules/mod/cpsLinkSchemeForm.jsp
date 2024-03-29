<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>CPS链接规则管理</title>
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
		<li><a href="${ctx}/mod/cpsLinkScheme/">CPS链接规则列表</a></li>
		<li class="active"><a href="${ctx}/mod/cpsLinkScheme/form?id=${cpsLinkScheme.id}">CPS链接规则<shiro:hasPermission name="mod:cpsLinkScheme:edit">${not empty cpsLinkScheme.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:cpsLinkScheme:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="cpsLinkScheme" action="${ctx}/mod/cpsLinkScheme/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">所属平台：</label>
			<div class="controls">
				<form:select path="platform" items="${fns:getDictList('platform')}" itemLabel="label" itemValue="value" htmlEscape="false" style="width:270px"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">所属品类：</label>
			<div class="controls">
				<form:input path="category" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">链接规则：</label>
			<div class="controls">
				<form:textarea path="script" htmlEscape="false" rows="10" maxlength="2000" class="input-xlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:cpsLinkScheme:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>