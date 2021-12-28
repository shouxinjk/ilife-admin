<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>海报模板管理</title>
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
		<li><a href="${ctx}/mod/posterTemplate/">海报模板列表</a></li>
		<li class="active"><a href="${ctx}/mod/posterTemplate/form?id=${posterTemplate.id}">海报模板<shiro:hasPermission name="mod:posterTemplate:edit">${not empty posterTemplate.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:posterTemplate:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<div style="display:flex;flex-direction:row;">
	<div style="width:50%;">
	<form:form id="inputForm" modelAttribute="posterTemplate" action="${ctx}/mod/posterTemplate/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="255" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">缩略图：</label>
			<div class="controls">
				<form:textarea path="logo" htmlEscape="false" rows="4" maxlength="512" class="input-xxlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">类型：</label>
			<div class="controls">
				<form:select path="type" class="input-medium">
					<form:options items="${fns:getDictList('poster_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>				
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="4" class="input-xxlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">适用类目：</label>
			<div class="controls">
				<form:input path="category.id" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">海报编号：</label>
			<div class="controls">
				<form:input path="code" htmlEscape="false" maxlength="512" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:select path="status" class="input-medium">
					<form:options items="${fns:getDictList('active_inactive')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">适用条件：</label>
			<div class="controls">
				<form:textarea path="condition" htmlEscape="false" rows="4" class="input-xxlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">参数配置：</label>
			<div class="controls">
				<form:textarea path="options" htmlEscape="false" rows="4" class="input-xxlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">优先级：</label>
			<div class="controls">
				<form:input path="priority" htmlEscape="false" maxlength="4" class="input-xlarge  digits"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:posterTemplate:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	</div>
	<div style="width:40%">
		<label class="control-label">预览</label>
		<div class="controls">
			<img src="${posterTemplate.logo}"  style="width:600px;object-fill:cover;"/>
		</div>
	</div>	
	</div>
</body>
</html>