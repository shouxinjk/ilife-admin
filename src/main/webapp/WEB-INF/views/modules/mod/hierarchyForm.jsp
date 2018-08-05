<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>社会分层管理</title>
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
		<li><a href="${ctx}/mod/hierarchy/">社会分层列表</a></li>
		<li class="active"><a href="${ctx}/mod/hierarchy/form?id=${hierarchy.id}">社会分层<shiro:hasPermission name="mod:hierarchy:edit">${not empty hierarchy.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:hierarchy:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="hierarchy" action="${ctx}/mod/hierarchy/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">经济资本等级：</label>
			<div class="controls">
				<form:input path="economyLevel" htmlEscape="false" maxlength="8" class="input-xlarge  digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">经济资本评分：</label>
			<div class="controls">
				<form:input path="economyScoreMin" htmlEscape="false" class=" number"/> -
				<form:input path="economyScoreMax" htmlEscape="false" class=" number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">社会资本等级：</label>
			<div class="controls">
				<form:input path="societyLevel" htmlEscape="false" maxlength="8" class="input-xlarge  digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">社会资本评分：</label>
			<div class="controls">
				<form:input path="societyScoreMin" htmlEscape="false" class=" number"/> -
				<form:input path="societyScoreMax" htmlEscape="false" class=" number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">文化资本等级：</label>
			<div class="controls">
				<form:input path="cultureLevel" htmlEscape="false" maxlength="8" class="input-xlarge  digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">文化资本评分：</label>
			<div class="controls">
				<form:input path="cultureScoreMin" htmlEscape="false" class=" number"/> -
				<form:input path="cultureScoreMax" htmlEscape="false" class=" number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">识别规则：</label>
			<div class="controls">
				<form:input path="expression" htmlEscape="false" maxlength="1000" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">matrix曲线：</label>
			<div class="controls">
				<form:textarea path="matrix" htmlEscape="false" maxlength="1000" rows="5" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" maxlength="1000" rows="5" class="input-xlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:hierarchy:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>