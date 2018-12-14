<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>主观评价-维度管理</title>
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
		<li><a href="${ctx}/mod/itemEvaluation/list?treeId=${itemDimensionMeasure.category.id}">主观评价列表</a></li>
		<shiro:hasPermission name="mod:itemEvaluation:edit"><li><a href="${ctx}/mod/itemEvaluation/form?category.id=${itemEvaluationDimension.category.id}">主观评价添加</a></li></shiro:hasPermission>	
		<li><a href="${ctx}/mod/itemEvaluationDimension/list?evaluationId=${itemEvaluationDimension.evaluation.id}&categoryId=${itemEvaluationDimension.category.id}">评价明细列表</a></li>
		<li class="active"><a href="${ctx}/mod/itemEvaluationDimension/form?id=${itemEvaluationDimension.id}&category.id=${itemEvaluationDimension.category.id}&evaluation.id=${itemEvaluationDimension.evaluation.id}">评价明细<shiro:hasPermission name="mod:itemEvaluationDimension:edit">${not empty itemEvaluationDimension.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:itemEvaluationDimension:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="itemEvaluationDimension" action="${ctx}/mod/itemEvaluationDimension/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">所属分类：</label>
			<div class="controls">
				<sys:treeselect id="category" name="category.id" value="${itemEvaluationDimension.category.id}" labelName="category.name" labelValue="${itemEvaluationDimension.category.name}"
								title="所属分类" url="/mod/itemCategory/treeData" cssClass="required" disabled="disabled"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>			
		<div class="control-group">
			<label class="control-label">主观评价：</label>
			<div class="controls">
				<sys:treeselect id="evaluation" name="evaluation.id" value="${itemEvaluationDimension.evaluation.id}" labelName="evaluation.name" labelValue="${itemEvaluationDimension.evaluation.name}"
								title="主观评价" url="/mod/itemEvaluation/treeData" cssClass="required" disabled="disabled"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客观评价：</label>
			<div class="controls">
				<sys:treeselect id="dimension" name="dimension.id" value="${itemEvaluationDimension.dimension.id}" labelName="dimension.name" labelValue="${itemEvaluationDimension.dimension.name}"
								title="客观评价" url="/mod/itemDimension/treeData" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">占比%：</label>
			<div class="controls">
				<form:input path="weight"  value="0" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">排序：</label>
			<div class="controls">
				<form:input path="sort"  value="10" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:input path="description" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>		
		<div class="form-actions">
			<shiro:hasPermission name="mod:itemEvaluationDimension:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>