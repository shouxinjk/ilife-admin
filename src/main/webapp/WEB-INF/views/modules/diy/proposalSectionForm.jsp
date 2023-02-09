<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>个性化定制章节管理</title>
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
		<li><a href="${ctx}/diy/proposalSection/">章节列表</a></li>
		<li class="active"><a href="${ctx}/diy/proposalSection/form?id=${proposalSection.id}">章节<shiro:hasPermission name="diy:proposalSection:edit">${not empty proposalSection.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="diy:proposalSection:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="proposalSection" action="${ctx}/diy/proposalSection/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">主题：</label>
			<div class="controls">
				 <sys:treeselect id="scheme" name="scheme.id" value="${proposalSection.scheme.id}" labelName="scheme.name" labelValue="${proposalSection.scheme.name}"
					title="定制主题" url="/diy/proposalScheme/listData"  cssClass="required"/>					
			</div>			
		</div>
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-xxlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">所属类目：</label>
			<div class="controls">
				<sys:treeselect id="category" name="category.id" value="${proposalSection.category.id}" labelName="category.name" labelValue="${proposalSection.category.name}"
					title="所属类目" url="/mod/itemCategory/treeData"  notAllowSelectRoot="true"  cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>			
		</div>		
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="4" maxlength="512" class="input-xxlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">排序：</label>
			<div class="controls">
				<form:input path="priority" htmlEscape="false" maxlength="11" class="input-xxlarge  digits"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="diy:proposalSection:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>