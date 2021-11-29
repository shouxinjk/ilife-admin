<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>推广达人管理</title>
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
		<li><a href="${ctx}/mod/broker/">推广达人列表</a></li>
		<li class="active"><a href="${ctx}/mod/broker/form?id=${broker.id}">推广达人<shiro:hasPermission name="mod:broker:edit">${not empty broker.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:broker:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="broker" action="${ctx}/mod/broker/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">所属机构:</label>
			<div class="controls">
                <sys:treeselect id="orgnization" name="orgnization.id" value="${broker.orgnization.id}" labelName="orgnization.name" labelValue="${broker.orgnization.name}"
					title="机构" url="/sys/office/treeData?type=1" cssClass="required"/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">上级达人：</label>
			<div class="controls">
				<sys:treeselect id="parent" name="parent.id" value="${broker.parent.id}" labelName="parent.name" labelValue="${broker.parent.name}"
					title="上级达人" url="/mod/broker/treeData" extId="${broker.id}" cssClass="" allowClear="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">OpenId：</label>
			<div class="controls">
				<form:input path="openid" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">真实姓名：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">电话号码：</label>
			<div class="controls">
				<form:input path="phone" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">二维码URL：</label>
			<div class="controls">
				<form:input path="qrcodeUrl" htmlEscape="false" maxlength="512" class="input-xlarge "/>
			</div>
		</div>			
		<div class="control-group">
			<label class="control-label">邮件：</label>
			<div class="controls">
				<form:input path="email" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">层级：</label>
			<div class="controls">
				<form:input path="hierarchy" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">身份证号码：</label>
			<div class="controls">
				<form:input path="securityNo" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">等级：</label>
			<div class="controls">
				<form:input path="level" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">支付宝账号：</label>
			<div class="controls">
				<form:input path="alipayAccount" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">支付宝账户名：</label>
			<div class="controls">
				<form:input path="alipayAccountName" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">账户状态：</label>
			<div class="controls">
				<form:input path="status" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">升级状态：</label>
			<div class="controls">
				<form:input path="upgrade" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:broker:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>