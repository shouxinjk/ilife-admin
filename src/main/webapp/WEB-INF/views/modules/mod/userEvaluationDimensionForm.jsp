<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户主观评价-维度管理</title>
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
		<li><a href="${ctx}/mod/userEvaluation/list">主观评价列表</a></li>
		<shiro:hasPermission name="mod:userEvaluation:edit"><li><a href="${ctx}/mod/userEvaluation/form">主观评价添加</a></li></shiro:hasPermission>
		<li><a href="${ctx}/mod/userEvaluationDimension/list?evaluationId=${userEvaluationDimension.evaluation.id}">评价明细列表</a></li>
		<li class="active"><a href="${ctx}/mod/userEvaluationDimension/form?id=${userEvaluationDimension.id}&evaluation.id=${userEvaluationDimension.evaluation.id}">评价明细<shiro:hasPermission name="mod:userEvaluationDimension:edit">${not empty userEvaluationDimension.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:userEvaluationDimension:edit">查看</shiro:lacksPermission></a></li>		
	</ul><br/>
	<form:form id="inputForm" modelAttribute="userEvaluationDimension" action="${ctx}/mod/userEvaluationDimension/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">主观评价：</label>
			<div class="controls">
				<sys:treeselect id="evaluation" name="evaluation.id" value="${userEvaluationDimension.evaluation.id}" labelName="evaluation.name" labelValue="${userEvaluationDimension.evaluation.name}"
								title="主观评价" url="/mod/userEvaluation/treeData" cssClass="required" disabled="disabled"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客观评价：</label>
			<div class="controls">
				<sys:treeselect id="dimension" name="dimension.id" value="${userEvaluationDimension.dimension.id}" labelName="dimension.name" labelValue="${userEvaluationDimension.dimension.name}"
								title="客观评价" url="/mod/userDimension/treeData" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
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
			<label class="control-label">类别：</label>
			<div class="controls">
				<form:input path="category" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:input path="description" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">排序：</label>
			<div class="controls">
				<form:input path="sort" value="10" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div>		
		<div class="form-actions">
			<shiro:hasPermission name="mod:userEvaluationDimension:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>