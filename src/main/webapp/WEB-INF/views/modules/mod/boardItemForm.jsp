<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Board条目管理</title>
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
		<shiro:hasPermission name="mod:board:view"><li><a href="${ctx}/mod/board/">Board列表</a></li></shiro:hasPermission>	
		<shiro:hasPermission name="mod:board:edit"><li><a href="${ctx}/mod/board/form?id=${boardId}">Board修改</a></li></shiro:hasPermission>			
		<shiro:hasPermission name="mod:boardItem:view"><li><a href="${ctx}/mod/boardItem/?boardId=${boardId}">商品列表</a></li></shiro:hasPermission>
		<li class="active"><a href="${ctx}/mod/boardItem/form?id=${boardItem.id}">商品<shiro:hasPermission name="mod:boardItem:edit">${not empty boardItem.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:boardItem:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="boardItem" action="${ctx}/mod/boardItem/save?boardId=${boardId}" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">Board：</label>
			<div class="controls">
				 <sys:treeselect id="board" name="board.id" value="${boardItem.board.id}" labelName="board.title" labelValue="${boardItem.board.title}"
					title="Board列表" url="/mod/board/listData"  cssClass="required"/>				
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Item：</label>
			<div class="controls">
				<form:input path="item" htmlEscape="false" maxlength="64" class="input-xlarge"/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">标题：</label>
			<div class="controls">
				<form:input path="title" htmlEscape="false" maxlength="255" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:input path="description" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:boardItem:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>