<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户标签管理</title>
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
		<li><a href="${ctx}/mod/userTag/">标签列表</a></li>
		<li class="active"><a href="${ctx}/mod/userTag/form?id=${userTag.id}">标签<shiro:hasPermission name="mod:userTag:edit">${not empty userTag.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:userTag:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="userTag" action="${ctx}/mod/userTag/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<div class="control-group">
			<label class="control-label">分类：</label>
			<div class="controls">
				 <sys:treeselect id="userTagCategory" name="userTagCategory.id" value="${userTag.userTagCategory.id}" labelName="userTagCategory.name" labelValue="${userTag.userTagCategory.name}"
					title="分类" url="/mod/userTagCategory/treeData" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">关联属性：</label>
			<div class="controls">
				 <sys:treeselect id="userMeasure" name="userMeasure.id" value="${userTag.userMeasure.id}" labelName="userMeasure.name" labelValue="${userTag.userMeasure.name}"
					title="用户属性" url="/mod/userMeasure/treeData"/>
				<!--span class="help-inline"><font color="red">*</font> </span-->
			</div>
		</div>			
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">类型：</label>
			<div class="controls">
				<form:select path="type" items="${fns:getDictList('tagType')}" itemLabel="label" itemValue="value" htmlEscape="false" class="input-xlarge "/>
				<!--form:input path="type" htmlEscape="false" maxlength="10" class="input-xlarge "/-->
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">表达式：</label>
			<div class="controls">
				<form:textarea path="expression" htmlEscape="false" rows="5" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">判定规则：</label>
			<div class="controls">
				<form:textarea path="ruleOfJudgment" htmlEscape="false" rows="5" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Key：</label>
			<div class="controls">
				<form:textarea path="tagKey" htmlEscape="false" rows="5" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">排序：</label>
			<div class="controls">
				<form:input path="sort" htmlEscape="false" rows="5" maxlength="200" class="input-xlarge"/>
			</div>
		</div>		
		<div class="form-actions">
			<shiro:hasPermission name="mod:userTag:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>