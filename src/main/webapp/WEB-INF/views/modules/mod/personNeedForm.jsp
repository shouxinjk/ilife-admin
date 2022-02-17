<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户需要构成管理</title>
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
		<li><a href="${ctx}/mod/personNeed/?person.id=${personNeed.person.id}">需要构成</a></li>
		<li class="active"><a href="${ctx}/mod/personNeed/form?id=${personNeed.id}">需要<shiro:hasPermission name="mod:personNeed:edit">${not empty personNeed.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:personNeed:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="personNeed" action="${ctx}/mod/personNeed/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">用户：</label>
			<div class="controls">
				 <sys:treeselect id="person" name="person.id" value="${personNeed.person.id}" labelName="person.nickname" labelValue="${personNeed.person.nickname}"
					title="用户列表" url="/ope/person/listData"  notAllowSelectRoot="false" cssClass="required" disabled="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>				
		</div>
		<div class="control-group">
			<label class="control-label">需要：</label>
			<div class="controls">
				 <sys:treeselect id="need" name="need.id" value="${channelNeed.need.id}" labelName="need.name" labelValue="${channelNeed.need.name}"
					title="需要列表" url="/mod/motivation/treeData"  notAllowSelectRoot="true" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>			
		</div>			
		<div class="control-group">
			<label class="control-label">强度：</label>
			<div class="controls">
				<form:input path="weight" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="4" maxlength="255" class="input-xlarge "/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">表达式：</label>
			<div class="controls">
				<form:textarea path="expression" htmlEscape="false" rows="4" maxlength="1024" class="input-xlarge "/>
			</div>
		</div>
		<!--div class="control-group">
			<label class="control-label">sort：</label>
			<div class="controls">
				<form:input path="sort" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div-->
		<div class="form-actions">
			<shiro:hasPermission name="mod:personNeed:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>