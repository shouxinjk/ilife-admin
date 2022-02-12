<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>阶段需要构成管理</title>
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
		<li><a href="${ctx}/mod/phaseNeed/?treeId=${pid}&treeModule=${pType}">阶段需要构成列表</a></li>
		<li class="active"><a href="${ctx}/mod/phaseNeed/form?id=${phaseNeed.id}">阶段需要构成<shiro:hasPermission name="mod:phaseNeed:edit">${not empty phaseNeed.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:phaseNeed:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="phaseNeed" action="${ctx}/mod/phaseNeed/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">阶段：</label>
			<div class="controls">
				 <sys:treeselect id="phase" name="phase.id" value="${phaseNeed.phase.id}" labelName="phase.name" labelValue="${phaseNeed.phase.name}"
					title="阶段列表" url="/mod/phase/treeData"  notAllowSelectRoot="true" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">需要：</label>
			<div class="controls">
				 <sys:treeselect id="need" name="need.id" value="${phaseNeed.need.id}" labelName="need.name" labelValue="${phaseNeed.need.name}"
					title="需要列表" url="/mod/motivation/treeData"  notAllowSelectRoot="true" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">满足度%：</label>
			<div class="controls">
				<form:input path="weight" htmlEscape="false" class="input-xlarge required number"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">满足度表达式：</label>
			<div class="controls">
				<form:textarea path="expression" htmlEscape="false" rows="4" maxlength="1024" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">需要满足描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="4" maxlength="255" class="input-xlarge "/>
			</div>
		</div>		
		<div class="form-actions">
			<shiro:hasPermission name="mod:phaseNeed:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>