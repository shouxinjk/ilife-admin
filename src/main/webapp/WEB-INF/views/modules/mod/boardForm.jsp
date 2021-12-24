<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Board管理</title>
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
		<li><a href="${ctx}/mod/board/">Board列表</a></li>
		<li class="active"><a href="${ctx}/mod/board/form?id=${board.id}">Board列表<shiro:hasPermission name="mod:board:edit">${not empty board.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:board:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="board" action="${ctx}/mod/board/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<div class="control-group">
			<label class="control-label">达人：</label>
			<div class="controls">
				<sys:treeselect id="broker" name="broker.id" value="${board.broker.id}" labelName="broker.name" labelValue="${board.broker.name}"
					title="选择达人" url="/mod/broker/treeData" extId="${broker.id}" cssClass="" allowClear="true"/>
			</div>
		</div>			
		<div class="control-group">
			<label class="control-label">标题：</label>
			<div class="controls">
				<form:input path="title" htmlEscape="false" maxlength="255" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Logo：</label>
			<div class="controls">
				<form:textarea path="logo" htmlEscape="false" maxlength="512" class="input-xlarge" rows="5"/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label"></label>
			<div class="controls">
				<img src="${board.logo}" width="200"/>
			</div>
		</div>			
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" class="input-xlarge " rows="5"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">类型：</label>
			<div class="controls">
				<form:select path="type" items="${fns:getDictList('boardType')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">标签：</label>
			<div class="controls">
				<form:textarea path="tags" htmlEscape="false" maxlength="255" class="input-xlarge " rows="5"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">关键词：</label>
			<div class="controls">
				<form:textarea path="keywords" htmlEscape="false" maxlength="512" class="input-xlarge " rows="5"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:input path="status" htmlEscape="false" maxlength="20" class="input-xlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:board:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>