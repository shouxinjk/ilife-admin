<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>类目专家授权管理</title>
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
		<li><a href="${ctx}/mod/categoryBroker/">类目专家授权列表</a></li>
		<li class="active"><a href="${ctx}/mod/categoryBroker/form?id=${categoryBroker.id}">类目专家授权<shiro:hasPermission name="mod:categoryBroker:edit">${not empty categoryBroker.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:categoryBroker:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="categoryBroker" action="${ctx}/mod/categoryBroker/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">授权类型：</label>
			<div class="controls">
				 <sys:treeselect id="badge" name="badge.id" value="${categoryBroker.badge.id}" labelName="badge.name" labelValue="${categoryBroker.badge.name}"
					title="授权类型" url="/mod/badge/listData"  cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>			
		</div>		
		<div class="control-group">
			<label class="control-label">关联类目：</label>
			<div class="controls">
				 <sys:treeselect id="category" name="category.id" value="${categoryBroker.category.id}" labelName="category.name" labelValue="${categoryBroker.category.name}"
					title="商品类目" url="/mod/itemCategory/treeData" extId="${category.id}" />
			</div>			
		</div>
		<div class="control-group">
			<label class="control-label">达人：</label>
			<div class="controls">
				<sys:treeselect id="broker" name="broker.id" value="${categoryBroker.broker.id}" labelName="broker.nickname" labelValue="${categoryBroker.broker.nickname}"
					title="达人/定制师/专家" url="/mod/broker/treeData" extId="${broker.id}" cssClass="required" allowClear="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>			
		</div>
		<div class="control-group">
			<label class="control-label">姓名：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">公司：</label>
			<div class="controls">
				<form:input path="company" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>			
		<div class="control-group">
			<label class="control-label">身份/职位：</label>
			<div class="controls">
				<form:input path="job" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">优势简介：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" maxlength="512" class="input-xlarge " rows="4"/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:input path="status" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>			
		<div class="form-actions">
			<shiro:hasPermission name="mod:categoryBroker:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>