<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户标注管理</title>
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
		<li><a href="${ctx}/ope/userPerformance/listPending?treeId=${pid}&treeModule=${pType}">待标注</a></li>
		<li><a href="${ctx}/ope/userPerformance/list?treeId=${pid}&treeModule=${pType}">已标注</a></li>
		<li class="active"><a href="${ctx}/ope/userPerformance/form?id=${userPerformance.id}&pid=${pid}&pType=${pType}">标注<shiro:hasPermission name="ope:userPerformance:edit">${not empty userPerformance.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="ope:userPerformance:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="userPerformance" action="${ctx}/ope/userPerformance/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" id="pid" name="pid" value="${pid}"/>
		<input type="hidden" id="pType" name="pType" value="${pType}"/>	
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">关键属性：</label>
			<div class="controls">
				<sys:treeselect id="measure" name="measure.id" value="${userPerformance.measure.id}" labelName="measure.name" labelValue="${userPerformance.measure.name}"
					title="关键属性" url="/mod/measure/treeData"  cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">原始值：</label>
			<div class="controls">
				<form:input path="originalValue" htmlEscape="false" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">标注值(1-10)：</label>
			<div class="controls">
				<form:input path="markedValue" htmlEscape="false" class="input-xlarge  number"/>
			</div>
		</div>		
		<!--div class="control-group">
			<label class="control-label">归一值：</label>
			<div class="controls">
				<form:input path="normalizedValue" htmlEscape="false" class="input-xlarge  number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">对照值：</label>
			<div class="controls">
				<form:input path="controlValue" htmlEscape="false" class="input-xlarge  number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">等级：</label>
			<div class="controls">
				<form:input path="level" htmlEscape="false" maxlength="11" class="input-xlarge  digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">所属维度：</label>
			<div class="controls">
				<form:input path="dimension" htmlEscape="false" maxlength="500" class="input-xlarge "/>
			</div>
		</div-->
		<div class="form-actions">
			<shiro:hasPermission name="ope:userPerformance:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>