<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>vals分类管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")){
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
		<li><a href="${ctx}/mod/lifeStyleCategory/">vals分类列表</a></li>
		<li class="active"><a href="${ctx}/mod/lifeStyleCategory/form?id=${lifeStyleCategory.id}&parent.id=${lifeStyleCategoryparent.id}">vals分类<shiro:hasPermission name="mod:lifeStyleCategory:edit">${not empty lifeStyleCategory.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:lifeStyleCategory:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="lifeStyleCategory" action="${ctx}/mod/lifeStyleCategory/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">上级父级编号:</label>
			<div class="controls">
				<sys:treeselect id="parent" name="parent.id" value="${lifeStyleCategory.parent.id}" labelName="parent.name" labelValue="${lifeStyleCategory.parent.name}"
					title="父级编号" url="/mod/lifeStyleCategory/treeData" extId="${lifeStyleCategory.id}" cssClass="" allowClear="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">标题：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">logo：</label>
			<div class="controls">
				<form:hidden id="logo" path="logo" htmlEscape="false" maxlength="2000" class="input-xlarge"/>
				<sys:ckfinder input="logo" type="files" uploadPath="/mod/lifeStyleCategory" selectMultiple="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="4" class="input-xxlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">排序（升序）：</label>
			<div class="controls">
				<form:input path="sort" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:lifeStyleCategory:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>