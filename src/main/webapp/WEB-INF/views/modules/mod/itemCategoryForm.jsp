<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品分类管理</title>
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
		<li><a href="${ctx}/mod/itemCategory/">商品分类列表</a></li>
		<li class="active"><a href="${ctx}/mod/itemCategory/form?id=${itemCategory.id}">商品分类<shiro:hasPermission name="mod:itemCategory:edit">${not empty itemCategory.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:itemCategory:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="itemCategory" action="${ctx}/mod/itemCategory/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">上级分类：</label>
			<div class="controls">
				 <sys:treeselect id="itemCategory" name="parent.id" value="${itemCategory.parent.id}" labelName="itemCategory.name" labelValue="${itemCategory.parent.name}"
					title="商品分类" url="/mod/itemCategory/treeData" extId="${itemCategory.id}" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">别名：</label>
			<div class="controls">
				<form:input path="alias" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">logo：</label>
			<div class="controls">
				<form:input path="logo" htmlEscape="false" maxlength="512" class="input-xlarge "/>
			</div>
		</div>			
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="3" maxlength="1024" class="input-xlarge"/>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">标签：</label>
			<div class="controls">
				<form:textarea path="tags" htmlEscape="false" rows="3" maxlength="1024" class="input-xlarge"/>
			</div>
		</div>		
		<!--div class="control-group">
			<label class="control-label">满足动机：</label>
			<div class="controls">
				<sys:treeselect id="motivationIds" name="motivationIds" value="${itemCategory.motivationIds}" labelName="motivationNames" labelValue="${itemCategory.motivationNames}"
					title="动机" url="/mod/motivation/listData" checked="true"  cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">满足诱因：</label>
			<div class="controls">
				<sys:treeselect id="occasionIds" name="occasionIds" value="${itemCategory.occasionIds}" labelName="occasionNames" labelValue="${itemCategory.occasionNames}"
					title="诱因" url="/mod/occasion/listData" checked="true"  cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">适用条件：</label>
			<div class="controls">
				<form:textarea path="expressionDesc" htmlEscape="false" rows="5" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">适用条件量化：</label>
			<div class="controls">
				<form:textarea path="expression" htmlEscape="false" rows="5" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">摘要模版：</label>
			<div class="controls">
				<form:textarea path="outlineTemplate" htmlEscape="false" rows="5" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">场景标签：</label>
			<div class="controls">
				<form:input path="scenarioId" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div-->
		<div class="form-actions">
			<shiro:hasPermission name="mod:itemCategory:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>