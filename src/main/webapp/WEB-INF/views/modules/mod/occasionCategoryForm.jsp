<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>外部诱因类别管理</title>
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
		<li><a href="${ctx}/mod/occasionCategory/">外部诱因类别列表</a></li>
		<li class="active"><a href="${ctx}/mod/occasionCategory/form?id=${occasionCategory.id}">外部诱因类别<shiro:hasPermission name="mod:occasionCategory:edit">${not empty occasionCategory.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:occasionCategory:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="occasionCategory" action="${ctx}/mod/occasionCategory/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">上级分类：</label>
			<div class="controls">
				 <sys:treeselect id="occasionCategory" name="parent.id" value="${occasionCategory.parent.id}" labelName="parent.name" labelValue="${occasionCategory.parent.name}"
					title="外部诱因" url="/mod/occasionCategory/treeData" extId="${occasionCategory.id}" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">排序:</label>
			<div class="controls">
				<form:input path="sort" htmlEscape="false" maxlength="11" class="required digits"/>
				<span class="help-inline">分类的排列次序</span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:occasionCategory:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>