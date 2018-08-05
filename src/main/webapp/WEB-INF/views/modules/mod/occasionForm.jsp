<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>外部诱因管理</title>
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
		<li><a href="${ctx}/mod/occasion/?treeId=${treeId}">外部诱因列表</a></li>
		<li class="active"><a href="${ctx}/mod/occasion/form?id=${occasion.id}&occasionCategory.id=${treeId}">外部诱因<shiro:hasPermission name="mod:occasion:edit">${not empty occasion.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:occasion:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="occasion" action="${ctx}/mod/occasion/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
		<label class="control-label">分类：</label>
		<div class="controls">
			<sys:treeselect id="occasionCategory" name="occasionCategory.id" value="${occasion.occasionCategory.id}" labelName="occasionCategory.name" labelValue="${occasion.occasionCategory.name}"
							title="外部诱因分类" url="/mod/occasionCategory/treeData" cssClass="required"/>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
		<div class="control-group">
			<label class="control-label">匹配用户：</label>
			<div class="controls">
				<form:input path="exprUser" htmlEscape="false" maxlength="500" class="input-xlarge "/>
			</div>
		</div>
<!-- 		<div class="control-group"> -->
<!-- 			<label class="control-label">匹配商品类别：</label> -->
<!-- 			<div class="controls"> -->
<%-- 				<form:input path="exprItem" htmlEscape="false" maxlength="500" class="input-xlarge "/> --%>
<!-- 			</div> -->
<!-- 		</div> -->
		<div class="control-group">
			<label class="control-label">触发条件：</label>
			<div class="controls">
				<form:input path="exprTrigger" htmlEscape="false" maxlength="500" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">触发条件量化：</label>
			<div class="controls">
				<form:textarea path="expression" htmlEscape="false" rows="5" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">持续时间：</label>
			<div class="controls">
				<form:input path="exprDuration" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">内在/外在：</label>
			<div class="controls">
				<form:radiobuttons path="triggerDirection" items="${fns:getDictList('insideOrOutside')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">主动/被动：</label>
			<div class="controls">
				<form:radiobuttons path="triggerType" items="${fns:getDictList('activeOrPassive')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:occasion:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>