<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户分群管理</title>
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
		<li><a href="${ctx}/mod/persona/?treeId=${pid}&treeModule=${pType}">用户分群列表</a></li>
		<li class="active"><a href="${ctx}/mod/persona/form?id=${persona.id}&pid=${pid}&pType=${pType}">用户分群<shiro:hasPermission name="mod:persona:edit">${not empty persona.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:persona:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="persona" action="${ctx}/mod/persona/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="parent.id"/>
		<form:hidden path="phase.id"/>
		<input type="hidden" id="pid" name="pid" value="${pid}"/>
		<input type="hidden" id="pType" name="pType" value="${pType}"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">所属分群：</label>
			<div class="controls">
				${persona.parent.name }
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">所属阶段：</label>
			<div class="controls">
				${persona.phase.name }
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">所属分层：</label>
			<div class="controls">
				 <sys:treeselect id="hierarchy" name="hierarchy.id" value="${persona.hierarchy.id}" labelName="hierarchy.name" labelValue="${persona.hierarchy.name}"
					title="社会分层" url="/mod/hierarchy/listData"  cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">画像名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">默认头像：</label>
			<div class="controls">
				<form:hidden id="nameImage" path="logo" htmlEscape="false" maxlength="255" class="input-xlarge"/>
				<sys:ckfinder input="nameImage" type="images" uploadPath="/photo" selectMultiple="false" maxWidth="100" maxHeight="100"/>
			</div>
		</div>
		<c:if test="${not empty persona.id}">
			<div class="control-group">
				<label class="control-label">VALS模型：</label>
				<div class="controls">
					<form:input path="alpha" htmlEscape="false" class="input-small  number"/>
					<form:input path="beta" htmlEscape="false" class="input-small  number"/>
					<form:input path="gamma" htmlEscape="false" class="input-small  number"/>
					<form:input path="delte" htmlEscape="false" class="input-small  number"/>
					<form:input path="epsilon" htmlEscape="false" class="input-small  number"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">能力模型：</label>
				<div class="controls">
					<form:input path="zeta" htmlEscape="false" class="input-small  number"/>
					<form:input path="eta" htmlEscape="false" class="input-small  number"/>
					<form:input path="theta" htmlEscape="false" class="input-small  number"/>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">画像描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="5" maxlength="1000" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">偏好标签：</label>
			<div class="controls">
				<form:textarea path="lambda" htmlEscape="false" rows="5" maxlength="500" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">识别规则：</label>
			<div class="controls">
				<form:textarea path="expression" htmlEscape="false" rows="5" maxlength="500" class="input-xlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:persona:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>