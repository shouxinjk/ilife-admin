<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户管理</title>
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
		<li><a href="${ctx}/ope/person/">用户列表</a></li>
		<li class="active"><a href="${ctx}/ope/person/form?id=${person.id}">用户<shiro:hasPermission name="ope:person:edit">${not empty person.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="ope:person:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="person" action="${ctx}/ope/person/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<!-- 
		<div class="control-group">
			<label class="control-label">分数：</label>
			<div class="controls">
				<form:input path="score" htmlEscape="false" class="input-xlarge  number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">等级：</label>
			<div class="controls">
				<form:input path="level" htmlEscape="false" maxlength="8" class="input-xlarge  digits"/>
			</div>
		</div>
		-->
		<div class="control-group">
			<label class="control-label">昵称：</label>
			<div class="controls">
				<form:input path="nickname" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">头像：</label>
			<div class="controls">
				<form:input path="logo" htmlEscape="false" maxlength="1000" class="input-xlarge "/>
			</div>
		</div>		
		<!-- 
		<div class="control-group">
			<label class="control-label">economy：</label>
			<div class="controls">
				<form:input path="economy" htmlEscape="false" maxlength="500" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">culture：</label>
			<div class="controls">
				<form:input path="culture" htmlEscape="false" maxlength="500" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">society：</label>
			<div class="controls">
				<form:input path="society" htmlEscape="false" maxlength="500" class="input-xlarge "/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">functionality：</label>
			<div class="controls">
				<form:input path="functionality" htmlEscape="false" maxlength="500" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">security：</label>
			<div class="controls">
				<form:input path="security" htmlEscape="false" maxlength="500" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">love：</label>
			<div class="controls">
				<form:input path="love" htmlEscape="false" maxlength="500" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">respect：</label>
			<div class="controls">
				<form:input path="respect" htmlEscape="false" maxlength="500" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">realization：</label>
			<div class="controls">
				<form:input path="realization" htmlEscape="false" maxlength="500" class="input-xlarge "/>
			</div>
		</div>		
		 -->
		<div class="control-group">
			<label class="control-label">偏好模型：</label>
			<div class="controls">
				<form:input path="lambda" htmlEscape="false" maxlength="1000" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">信用评价：</label>
			<div class="controls">
				<form:input path="credit" htmlEscape="false" class="input-xlarge  number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">阶段偏移量：</label>
			<div class="controls">
				<form:input path="offset" htmlEscape="false" class="input-xlarge  number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">信息健全度：</label>
			<div class="controls">
				<form:input path="integrity" htmlEscape="false" maxlength="8" class="input-xlarge  digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">所属阶段：</label>
			<div class="controls">
				 <sys:treeselect id="phase" name="phase.id" value="${person.phase.id}" labelName="phase.name" labelValue="${person.phase.name}"
					title="人生阶段" url="/mod/phase/treeData" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">所属阶层：</label>
			<div class="controls">
				<sys:treeselect id="hierarchy" name="hierarchy.id" value="${person.hierarchy.id}" labelName="hierarchy.name" labelValue="${person.hierarchy.name}"
								title="社会分层" url="/mod/hierarchy/listData" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">用户分群：</label>
			<div class="controls">
				<sys:treeselect id="persona" name="persona.id" value="${person.persona.id}" labelName="persona.name" labelValue="${person.persona.name}"
					title="用户分群" url="/mod/persona/treeData"  cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">生活方式：</label>
			<div class="controls">
				<sys:treeselect id="lifeStyle" name="lifeStyle.id" value="${person.lifeStyle.id}" labelName="lifeStyle.name" labelValue="${person.lifeStyle.name}"
					title="用户分群" url="/mod/lifeStyle/treeData"  cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="ope:person:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>