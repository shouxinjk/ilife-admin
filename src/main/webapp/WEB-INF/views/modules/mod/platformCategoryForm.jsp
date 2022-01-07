<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>电商平台类目映射管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")){
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
		<li><a href="${ctx}/mod/platformCategory/?treeId=${treeId}">类目映射列表</a></li>
		<li class="active"><a href="${ctx}/mod/platformCategory/form?id=${platformCategory.id}&parent.id=${platformCategoryparent.id}">类目映射<shiro:hasPermission name="mod:platformCategory:edit">${not empty platformCategory.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:platformCategory:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="platformCategory" action="${ctx}/mod/platformCategory/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">父节点:</label>
			<div class="controls">
				<sys:treeselect id="parent" name="parent.id" value="${platformCategory.parent.id}" labelName="parent.name" labelValue="${platformCategory.parent.name}"
					title="原始父ID" url="/mod/platformCategory/treeData" extId="${platformCategory.id}" cssClass="" allowClear="true"/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">来源平台：</label>
			<div class="controls">
				<form:select path="platform" class="input-medium">
					<form:options items="${fns:getDictList('platform')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>				
			</div>			
		</div>
		<div class="control-group">
			<label class="control-label">标准类目：</label>
			<div class="controls">
				 <sys:treeselect id="category" name="category.id" value="${platformCategory.category.id}" labelName="category.name" labelValue="${platformCategory.category.name}"
					title="商品分类" url="/mod/itemCategory/treeData" notAllowSelectRoot="true" cssClass=""/>
			</div>
		</div>		
		<div class="form-actions">
			<shiro:hasPermission name="mod:platformCategory:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>