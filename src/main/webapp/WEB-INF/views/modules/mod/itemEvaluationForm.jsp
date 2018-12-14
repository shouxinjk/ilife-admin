<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>主观评价管理</title>
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
		<li><a href="${ctx}/mod/itemEvaluation/list?treeId=${itemEvaluation.category.id}">主观评价列表</a></li>
		<li class="active"><a href="${ctx}/mod/itemEvaluation/form?id=${itemEvaluation.id}&parent.id=${itemEvaluationparent.id}">主观评价<shiro:hasPermission name="mod:itemEvaluation:edit">${not empty itemEvaluation.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:itemEvaluation:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="itemEvaluation" action="${ctx}/mod/itemEvaluation/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="category.id" value="${itemEvaluation.category.id}"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">上级/父级:</label>
			<div class="controls">
				<sys:treeselect id="parent" name="parent.id" value="${itemEvaluation.parent.id}" labelName="parent.name" labelValue="${itemEvaluation.parent.name}"
					title="父级编号" url="/mod/itemEvaluation/treeData" extId="${itemEvaluation.id}" cssClass="" allowClear="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">占比：</label>
			<div class="controls">
				<form:input path="weight" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">排序：</label>
			<div class="controls">
				<form:input path="sort" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:input path="description" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>		
		<div class="form-actions">
			<shiro:hasPermission name="mod:itemEvaluation:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>