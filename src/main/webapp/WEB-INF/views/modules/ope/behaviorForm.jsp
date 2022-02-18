<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户行为管理</title>
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
		<li><a href="${ctx}/ope/behavior/">用户行为列表</a></li>
		<li class="active"><a href="${ctx}/ope/behavior/form?id=${behavior.id}">用户行为<shiro:hasPermission name="ope:behavior:edit">${not empty behavior.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="ope:behavior:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="behavior" action="${ctx}/ope/behavior/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<div class="control-group">
			<label class="control-label">目标：</label>
			<div class="controls">
				<form:select path="category" items="${fns:getDictList('behavior_category')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</div>
		</div>			
		<div class="control-group">
			<label class="control-label">类型：</label>
			<div class="controls">
				<form:select path="type" items="${fns:getDictList('behavior_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:select path="status" items="${fns:getDictList('active_inactive')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</div>
		</div>				
		<div class="control-group">
			<label class="control-label">备注名：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>	
		<!--div class="control-group">
			<label class="control-label">CODE：</label>
			<div class="controls">
				<form:input path="code" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div-->			
					
		<div class="control-group">
			<label class="control-label">用户更新Expr：</label>
			<div class="controls">
				<form:textarea path="exprPerson" htmlEscape="false" rows="5" maxlength="500" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品更新Expr：</label>
			<div class="controls">
				<form:textarea path="exprItem" htmlEscape="false" rows="5" maxlength="500" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">用户需要更新Expr：</label>
			<div class="controls">
				<form:textarea path="exprUserNeed" htmlEscape="false" rows="5" maxlength="500" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品需要更新Expr：</label>
			<div class="controls">
				<form:textarea path="exprItemNeed" htmlEscape="false" rows="5" maxlength="500" class="input-xlarge "/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">说明：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="5" maxlength="1000" class="input-xlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="ope:behavior:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>