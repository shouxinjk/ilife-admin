<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分润规则明细管理</title>
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
		<li><a href="${ctx}/mod/profitShareItem/">分润规则明细列表</a></li>
		<li class="active"><a href="${ctx}/mod/profitShareItem/form?id=${profitShareItem.id}">分润规则明细<shiro:hasPermission name="mod:profitShareItem:edit">${not empty profitShareItem.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:profitShareItem:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="profitShareItem" action="${ctx}/mod/profitShareItem/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">分润规则：</label>
			<div class="controls">
				<!--form:input path="scheme.id" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span-->
				 <sys:treeselect id="scheme" name="scheme.id" value="${profitShareItem.scheme.id}" labelName="scheme.name" labelValue="${profitShareItem.scheme.name}"
					title="分润规则" url="/mod/profitShareScheme/listData"  cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>			
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">受益方类型：</label>
			<div class="controls">
				<!--form:input path="beneficiaryType" htmlEscape="false" maxlength="50" class="input-xlarge "/-->
				<form:select path="beneficiaryType" items="${fns:getDictList('beneficiaryCategory')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">受益方：</label>
			<div class="controls">
				<!--form:input path="beneficiary" htmlEscape="false" maxlength="50" class="input-xlarge "/-->
				<form:select path="beneficiary" items="${fns:getDictList('beneficiaryType')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">占比：</label>
			<div class="controls">
				<form:input path="share" htmlEscape="false" class="input-xlarge "/> &nbsp;%
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:profitShareItem:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>