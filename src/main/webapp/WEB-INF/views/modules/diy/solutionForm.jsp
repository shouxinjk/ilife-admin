<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>个性定制方案管理</title>
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
		<li><a href="${ctx}/diy/solution/">个性定制方案列表</a></li>
		<li class="active"><a href="${ctx}/diy/solution/form?id=${solution.id}">个性定制方案<shiro:hasPermission name="diy:solution:edit">${not empty solution.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="diy:solution:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="solution" action="${ctx}/diy/solution/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">所属主题：</label>
			<div class="controls">
				 <sys:treeselect id="scheme" name="scheme.id" value="${solution.scheme.id}" labelName="scheme.name" labelValue="${solution.scheme.name}"
					title="定制主题" url="/diy/proposalScheme/listData" />	
				 <span class="help-inline"><font color="red">*</font> </span>				
			</div>			
		</div>
		<div class="control-group">
			<label class="control-label">引用方案：</label>
			<div class="controls">
				 <sys:treeselect id="refer" name="refer.id" value="${solution.refer.id}" labelName="refer.name" labelValue="${solution.refer.name}"
					title="个性化方案" url="/diy/solution/listData" />					
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
			<label class="control-label">目标用户Openid：</label>
			<div class="controls">
				<form:input path="forOpenid" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">定制用户Openid：</label>
			<div class="controls">
				<form:input path="byOpenid" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">目标用户昵称：</label>
			<div class="controls">
				<form:input path="forNickname" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">定制用户昵称：</label>
			<div class="controls">
				<form:input path="byNickname" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:select path="status" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="4" maxlength="512" class="input-xxlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="diy:solution:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>