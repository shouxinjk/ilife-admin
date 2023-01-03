<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>个性化定制指南条目管理</title>
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
		<li><a href="${ctx}/diy/guideTerm/">个性化定制指南条目列表</a></li>
		<li class="active"><a href="${ctx}/diy/guideTerm/form?id=${guideTerm.id}">个性化定制指南条目<shiro:hasPermission name="diy:guideTerm:edit">${not empty guideTerm.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="diy:guideTerm:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="guideTerm" action="${ctx}/diy/guideTerm/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">指南：</label>
			<div class="controls">
				 <sys:treeselect id="book" name="book.id" value="${guideTerm.book.id}" labelName="book.name" labelValue="${guideTerm.book.name}"
					title="定制指南列表" url="/diy/guideBook/listData"  cssClass="required"/>					
			</div>			
		</div>
		<div class="control-group">
			<label class="control-label">Section：</label>
			<div class="controls">
				 <sys:treeselect id="section" name="section.id" value="${guideTerm.section.id}" labelName="section.name" labelValue="${guideTerm.section.name}"
					title="定制指南列表" url="/diy/proposalSection/listData"  cssClass="required"/>					
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
			<label class="control-label">注意事项：</label>
			<div class="controls">
				<form:textarea path="tips" htmlEscape="false" rows="4" maxlength="512" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">标签：</label>
			<div class="controls">
				<form:textarea path="tags" htmlEscape="false" rows="2" maxlength="256" class="input-xlarge "/>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">适用条件描述：</label>
			<div class="controls">
				<form:textarea path="criteriaDesc" htmlEscape="false" rows="4" class="input-xlarge "/>
			</div>
		</div>					
		<div class="control-group">
			<label class="control-label">适用条件脚本：</label>
			<div class="controls">
				<form:textarea path="criteria" htmlEscape="false" rows="4" class="input-xxlarge "/>
			</div>
		</div>
		<!-- 
		<div class="control-group">
			<label class="control-label">脚本规则：</label>
			<div class="controls">
				<form:textarea path="script" htmlEscape="false" rows="4" class="input-xxlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">脚本描述：</label>
			<div class="controls">
				<form:textarea path="scriptDesc" htmlEscape="false" rows="4" class="input-xxlarge "/>
			</div>
		</div>
		-->
		<div class="control-group">
			<label class="control-label">优先级：</label>
			<div class="controls">
				<form:input path="priority" htmlEscape="false" maxlength="11" class="input-xlarge  digits"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="diy:guideTerm:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>