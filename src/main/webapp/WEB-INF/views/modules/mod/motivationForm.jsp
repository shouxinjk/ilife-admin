<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>内部动机管理</title>
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
		<li><a href="${ctx}/mod/motivation/?treeId=${pid}&treeModule=${pType}&topId=${topId }">内部动机列表</a></li>
		<li class="active"><a href="${ctx}/mod/motivation/form?id=${motivation.id}&pid=${pid}&pType=${pType}&topId=${topId }">内部动机<shiro:hasPermission name="mod:motivation:edit">${not empty motivation.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:motivation:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="motivation" action="${ctx}/mod/motivation/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">别名：</label>
			<div class="controls">
				<form:input path="displayName" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">分类：</label>
			<div class="controls">
				 <sys:treeselect id="motivationCategory" name="motivationCategory.id" value="${motivation.motivationCategory.id}" labelName="motivationCategory.name" labelValue="${motivation.motivationCategory.name}"
					title="内部动机分类" url="/mod/motivationCategory/treeData"  notAllowSelectRoot="true" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--div class="control-group">
			<label class="control-label">阶段：</label>
			<div class="controls">
				 <sys:treeselect id="phase" name="phase.id" value="${motivation.phase.id}" labelName="phase.name" labelValue="${motivation.phase.name}"
					title="内部动机分类" url="/mod/phase/treeData" notAllowSelectRoot="true" notAllowSelectParent="true" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">比重：</label>
			<div class="controls">
				<form:input path="percentage" htmlEscape="false" class="input-xlarge  number"/>
			</div>
		</div-->
		<div class="control-group">
			<label class="control-label">触发条件：</label>
			<div class="controls">
				<form:textarea path="exprTrigger" htmlEscape="false" rows="5" maxlength="500" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">触发条件量化：</label>
			<div class="controls">
				<form:textarea path="expression" htmlEscape="false" rows="5" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<!--div class="control-group">
			<label class="control-label">持续时间：</label>
			<div class="controls">
				<form:input path="exprDuration" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div-->
<!-- 		<div class="control-group"> -->
<!-- 			<label class="control-label">对应商品类别：</label> -->
<!-- 			<div class="controls"> -->
<%-- 				<form:input path="exprItem" htmlEscape="false" maxlength="500" class="input-xlarge " readonly="true"/> --%>
<!-- 			</div> -->
<!-- 		</div> -->
		<div class="form-actions">
			<shiro:hasPermission name="mod:motivation:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>