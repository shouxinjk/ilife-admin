<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>个性化定制模板管理</title>
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
		<li><a href="${ctx}/diy/proposalScheme/">主题列表</a></li>
		<li class="active"><a href="${ctx}/diy/proposalScheme/form?id=${proposalScheme.id}"><shiro:hasPermission name="diy:proposalScheme:edit">${not empty proposalScheme.id?'修改':'添加'}</shiro:hasPermission>主题<shiro:lacksPermission name="diy:proposalScheme:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="proposalScheme" action="${ctx}/diy/proposalScheme/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">上级：</label>
			<div class="controls">
				 <sys:treeselect id="parent" name="parent.id" value="${proposalScheme.parent.id}" labelName="parent.name" labelValue="${proposalScheme.parent.name}"
					title="动态表单" url="/diy/proposalScheme/listData"/>					
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="4" maxlength="512" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">类型：</label>
			<div class="controls">
				<form:select path="type" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('proposal_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">LOGO：</label>
			<div class="controls">
				<form:input path="logo" htmlEscape="false" maxlength="1024" class="input-xlarge "/>
			</div>
		</div>		
		<!-- 
		<div class="control-group">
			<label class="control-label">表单：</label>
			<div class="controls">
				 <sys:treeselect id="form" name="form.id" value="${proposalScheme.form.id}" labelName="form.name" labelValue="${proposalScheme.form.name}"
					title="动态表单" url="/diy/jsonForm/listData"/>					
			</div>				
		</div>
		-->
		<div class="control-group">
			<label class="control-label">所属类目：</label>
			<div class="controls">
				<sys:treeselect id="itemCategory" name="itemCategory.id" value="${proposalScheme.itemCategory.id}" labelName="itemCategory.name" labelValue="${proposalScheme.itemCategory.name}"
					title="所属类目" url="/mod/itemCategory/treeData"  notAllowSelectRoot="true"  cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>			
		</div>
		<div class="control-group">
			<label class="control-label">标签：</label>
			<div class="controls">
				<form:input path="category" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">达人：</label>
			<div class="controls">
				 <sys:treeselect id="broker" name="broker.id" value="${proposalScheme.broker.id}" labelName="broker.nickname" labelValue="${proposalScheme.broker.nickname}"
					title="达人" url="/mod/broker/treeData"/>					
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">激活：</label>
			<div class="controls">
				<form:select path="status" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">优先级：</label>
			<div class="controls">
				<form:input path="priority" htmlEscape="false" maxlength="11" class="input-xlarge  digits"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="diy:proposalScheme:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>