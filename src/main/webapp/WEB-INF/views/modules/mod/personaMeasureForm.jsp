<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户属性VALS标注管理</title>
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
		<li><a href="${ctx}/mod/personaMeasure/">用户属性VALS标注</a></li>
		<li class="active"><a href="${ctx}/mod/personaMeasure/form?id=${personaMeasure.id}">用户属性VALS标注<shiro:hasPermission name="mod:personaMeasure:edit">${not empty personaMeasure.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:personaMeasure:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="personaMeasure" action="${ctx}/mod/personaMeasure/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" id="pid" name="pid" value="${pid}"/>
		<input type="hidden" id="pType" name="pType" value="${pType}"/>	
		<input type="hidden" id="persona" name="persona.id" value="${personaMeasure.persona.id}"/>			
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">画像：</label>
			<div class="controls">
				${personaMeasure.persona.name }
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">阶段：</label>
			<div class="controls">
				 <sys:treeselect id="phase" name="phase.id" value="${personaMeasure.phase.id}" labelName="phase.name" labelValue="${personaMeasure.phase.name}"
					title="阶段列表" url="/mod/phase/treeData"  notAllowSelectRoot="true" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">属性：</label>
			<div class="controls">
				 <sys:treeselect id="measure" name="measure.id" value="${personaMeasure.measure.id}" labelName="measure.name" labelValue="${personaMeasure.measure.name}"
					title="属性列表" url="/mod/userMeasure/treeData"  notAllowSelectRoot="true" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">概要描述：</label>
			<div class="controls">
				<form:input path="description" htmlEscape="false" maxlength="255" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">占比：</label>
			<div class="controls">
				<form:input path="weight" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">脚本表达式：</label>
			<div class="controls">
				<form:input path="expression" htmlEscape="false" maxlength="1024" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">效益：生理：</label>
			<div class="controls">
				<form:input path="alpha" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">效益：安全：</label>
			<div class="controls">
				<form:input path="beta" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">效益：情感：</label>
			<div class="controls">
				<form:input path="gamma" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">效益：尊重：</label>
			<div class="controls">
				<form:input path="delte" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">效益：价值：</label>
			<div class="controls">
				<form:input path="epsilon" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">成本：经济：</label>
			<div class="controls">
				<form:input path="zeta" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">成本：社会：</label>
			<div class="controls">
				<form:input path="eta" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">成本：文化：</label>
			<div class="controls">
				<form:input path="theta" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">偏好表达式：</label>
			<div class="controls">
				<form:input path="lambda" htmlEscape="false" maxlength="1024" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">sort：</label>
			<div class="controls">
				<form:input path="sort" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:personaMeasure:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>