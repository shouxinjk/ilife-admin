<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>外部诱因管理</title>
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
		<li><a href="${ctx}/mod/occasion/?treeId=${treeId}">外部诱因列表</a></li>
		<li class="active"><a href="${ctx}/mod/occasion/form?id=${occasion.id}&occasionCategory.id=${treeId}">外部诱因<shiro:hasPermission name="mod:occasion:edit">${not empty occasion.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:occasion:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="occasion" action="${ctx}/mod/occasion/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" id="pid" name="pid" value="${pid}"/>
		<input type="hidden" id="pType" name="pType" value="${pType}"/>	
		<sys:message content="${message}"/>		

		<table width="100%">
		<tr>
		<td width="300px" valign="top" style="padding-left:20px">	
			<div>类别：</div>
			<sys:treeselect id="occasionCategory" name="occasionCategory.id" value="${occasion.occasionCategory.id}" labelName="occasionCategory.name" labelValue="${occasion.occasionCategory.name}"
							title="外部诱因分类" url="/mod/occasionCategory/treeData" cssClass="required"/>
			<span class="help-inline"><font color="red">*</font> </span><br/><br/>
			<div>名称：</div>
			<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/><br/><br/>
			<div>效果描述：</div>
			<form:input path="exprTrigger" htmlEscape="false" maxlength="100" class="input-xlarge "/><br/><br/>
			<!--div>适用用户：</div>
			<form:input path="exprUser" htmlEscape="false" maxlength="100" class="input-xlarge "/><br/><br/>			
			<div>适用品类：</div>
			<form:input path="exprItem" htmlEscape="false" maxlength="100" class="input-xlarge "/><br/><br/-->				
			<div>触发条件（开始时间）：</div>
			<form:input path="expression" htmlEscape="false" maxlength="100" class="input-xlarge "/><br/><br/>
			<div>持续时间（截止时间）：</div>
			<form:input path="exprTrigger" htmlEscape="false" maxlength="100" class="input-xlarge "/><br/><br/>
			来源类别：<form:radiobuttons path="triggerDirection" items="${fns:getDictList('insideOrOutside')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/><br/><br/>
			触发类别：<form:radiobuttons path="triggerType" items="${fns:getDictList('activeOrPassive')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
		</td>
		<td valign="top">
			<div>事件表达式（参见模板管理中【运营事件通知】）：</div>
			<form:textarea path="triggerActions" htmlEscape="false" rows="20" style="width:95%"/>	
		</td>
		</tr>
		<tr>
		<td colspan="2">
			<div class="form-actions">
				<shiro:hasPermission name="mod:occasion:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>	
		</td>
		</tr>
		</table>				
	</form:form>
</body>
</html>