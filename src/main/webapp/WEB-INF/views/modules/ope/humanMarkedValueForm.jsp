<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>数据标注管理</title>
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
		<li><a href="${ctx}/ope/humanMarkedValue/?treeId=${treeId}&pId=${pId}&treeModule=${pType}">数据标注列表</a></li>
		<li class="active"><a href="${ctx}/ope/humanMarkedValue/form?id=${humanMarkedValue.id}&treeId=${treeId}&pId=${pId}&pType=${pType}">数据标注<shiro:hasPermission name="ope:humanMarkedValue:edit">${not empty humanMarkedValue.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="ope:humanMarkedValue:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="humanMarkedValue" action="${ctx}/ope/humanMarkedValue/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" id="treeId" name="treeId" value="${treeId}"/>
		<input type="hidden" id="pId" name="pId" value="${pId}"/>
		<input type="hidden" id="pType" name="pType" value="${pType}"/>			
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">所属类目：</label>
			<div class="controls">
				<sys:treeselect id="category" name="category.id" value="${humanMarkedValue.category.id}" labelName="category.name" labelValue="${humanMarkedValue.category.name}"
					title="所属类目" url="/mod/itemCategory/listData" />
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">关键属性：</label>
			<div class="controls">
				<sys:treeselect id="measure" name="measure.id" value="${humanMarkedValue.measure.id}" labelName="measure.name" labelValue="${humanMarkedValue.measure.name}"
					title="关键属性" url="/mod/measure/listData" />
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">原始值：</label>
			<div class="controls">
				<form:input path="originalValue" htmlEscape="false" class="input-xlarge"/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">标注值：</label>
			<div class="controls">
				<form:input path="score" htmlEscape="false" class="input-xlarge  number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">标注者openid：</label>
			<div class="controls">
				<form:input path="openid" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">标注者昵称：</label>
			<div class="controls">
				<form:input path="nickname" htmlEscape="false" maxlength="512" class="input-xlarge "/>
			</div>
		</div>		
		<div class="form-actions">
			<shiro:hasPermission name="ope:humanMarkedValue:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>