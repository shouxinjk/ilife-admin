<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>佣金规则设置管理</title>
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
		<li><a href="${ctx}/mod/commissionScheme/">佣金规则设置列表</a></li>
		<li class="active"><a href="${ctx}/mod/commissionScheme/form?id=${commissionScheme.id}">佣金规则设置<shiro:hasPermission name="mod:commissionScheme:edit">${not empty commissionScheme.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:commissionScheme:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="commissionScheme" action="${ctx}/mod/commissionScheme/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">电商平台：</label>
			<div class="controls">
				<form:select path="platform" items="${fns:getDictList('platform')}" itemLabel="label" itemValue="value" htmlEscape="false" style="width:270px"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品类别：</label>
			<div class="controls">
				<form:input path="category" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">佣金类型：</label>
			<div class="controls">
				<!--form:input path="type" htmlEscape="false" maxlength="10" class="input-xlarge "/-->
				<form:select path="type" items="${fns:getDictList('commissionType')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">佣金额：</label>
			<div class="controls">
				<form:input path="amount" htmlEscape="false" class="input-xlarge "/> 元或%
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">生效时间：</label>
			<div class="controls">
				<input name="effectiveFrom" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${commissionScheme.effectiveFrom}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<!--form:input path="status" htmlEscape="false" maxlength="1" class="input-xlarge required"/-->
				<form:radiobuttons path="status" items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:commissionScheme:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>