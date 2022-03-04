<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户标签分类管理</title>
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
		<li><a href="${ctx}/mod/userTagCategory/">标签分类列表</a></li>
		<li class="active"><a href="${ctx}/mod/userTagCategory/form?id=${userTagCategory.id}&parent.id=${userTagCategoryparent.id}">标签分类<shiro:hasPermission name="mod:userTagCategory:edit">${not empty userTagCategory.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:userTagCategory:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="userTagCategory" action="${ctx}/mod/userTagCategory/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">上级分类:</label>
			<div class="controls">
				<sys:treeselect id="parent" name="parent.id" value="${userTagCategory.parent.id}" labelName="parent.name" labelValue="${userTagCategory.parent.name}"
					title="父级编号" url="/mod/userTagCategory/treeData" extId="${userTagCategory.id}" cssClass="" allowClear="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否排斥：</label>
			<div class="controls">
				<form:radiobuttons path="isExclusive" items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
			</div>
		</div>
		<c:if test="${userTagCategory.parent.id eq null}">
			<div class="control-group">
				<label class="control-label">类型：</label>
				<div class="controls">
					<form:input path="type" htmlEscape="false" maxlength="100" class="input-xlarge "/>
				</div>
			</div>
		</c:if>
		<c:if test="${userTagCategory.parent.id ne null}">
			<div class="control-group">
				<label class="control-label">类型：</label>
				<div class="controls">
					<form:input path="type" htmlEscape="false" maxlength="100" class="input-xlarge "  disabled="true"/>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">排序（升序）：</label>
			<div class="controls">
				<form:input path="sort" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:userTagCategory:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>