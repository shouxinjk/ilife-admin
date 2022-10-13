<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>业务字典管理</title>
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
		<li><a href="${ctx}/mod/dictValue/">业务字典列表</a></li>
		<li class="active"><a href="${ctx}/mod/dictValue/form?id=${dictValue.id}">业务字典<shiro:hasPermission name="mod:dictValue:edit">${not empty dictValue.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:dictValue:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="dictValue" action="${ctx}/mod/dictValue/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">字典：</label>
			<div class="controls">
				<form:select path="dictMeta.id" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictMetaList('_all')}" itemLabel="name" itemValue="id" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">类目：</label>
			<div class="controls">
				<sys:treeselect id="category" name="category.id" value="${dictValue.category.id}" labelName="category.name" labelValue="${dictValue.category.name}"
					title="应用类目" url="/mod/itemCategory/treeData" notAllowSelectRoot="false"/>	
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">数值：</label>
			<div class="controls">
				<form:input path="originalValue" htmlEscape="false" maxlength="100" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">标注值：</label>
			<div class="controls">
				<form:input path="markedValue" htmlEscape="false" class="input-xlarge  number"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:dictValue:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>