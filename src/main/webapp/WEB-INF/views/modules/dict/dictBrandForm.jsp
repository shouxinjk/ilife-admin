<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>品牌字典管理</title>
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
		<li><a href="${ctx}/dict/dictBrand/?treeId=${pId}&treeModule=${pType}">品牌字典列表</a></li>
		<li class="active"><a href="${ctx}/dict/dictBrand/form?id=${dictBrand.id}&treeId=${treeId}&pId=${pId}&pType=${pType}">品牌字典管理<shiro:hasPermission name="dict:dictBrand:edit">${not empty dictBrand.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="dict:dictBrand:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="dictBrand" action="${ctx}/dict/dictBrand/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" id="treeId" name="treeId" value="${treeId}"/>
		<input type="hidden" id="pId" name="pId" value="${pId}"/>
		<input type="hidden" id="pType" name="pType" value="${pType}"/>			
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">所属类目：</label>
			<div class="controls">
				<sys:treeselect id="category" name="category.id" value="${dictBrand.category.id}" labelName="category.name" labelValue="${dictBrand.category.name}"
					title="所属类目" url="/mod/itemCategory/treeData"  cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>			
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">Logo URL：</label>
			<div class="controls">
				<img src="${dictBrand.logo}" alt="${dictBrand.label}" style="height:60px"/>
			</div>
			<div class="controls">
				<form:input path="logo" htmlEscape="false" maxlength="255" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">国家：</label>
			<div class="controls">
				<form:input path="country" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">等级描述：</label>
			<div class="controls">
				<form:select path="description" class="input-xlarge ">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('brandGrade')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">品牌名称：</label>
			<div class="controls">
				<form:input path="label" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">字典分：</label>
			<div class="controls">
				<form:input path="score" htmlEscape="false" class="input-xlarge  number"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="dict:dictBrand:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>