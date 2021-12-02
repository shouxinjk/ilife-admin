<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>看板条目明细管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<shiro:hasPermission name="mod:board:view"><li><a href="${ctx}/mod/board/">Board列表</a></li></shiro:hasPermission>	
		<shiro:hasPermission name="mod:board:edit"><li><a href="${ctx}/mod/board/form?id=${boardId}">Board修改</a></li></shiro:hasPermission>		
		<li class="active"><a href="${ctx}/mod/boardItem/">商品列表</a></li>
		<shiro:hasPermission name="mod:boardItem:edit"><li><a href="${ctx}/mod/boardItem/form?boardId=${boardItem.board.id}">商品添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="boardItem" action="${ctx}/mod/boardItem/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>标题：</label>
				<form:input path="title" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>Board</th>
				<th>商品</th>
				<th>标题</th>
				<th>描述</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:boardItem:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="boardItem">
			<tr>
				<td>${boardItem.board.title}</td>
				<td>${boardItem.item}</td>
				<td>${boardItem.title}</td>
				<td>${boardItem.description}</td>
				<td>
					<fmt:formatDate value="${boardItem.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:boardItem:edit"><td>
    				<a href="${ctx}/mod/boardItem/form?boardId=${boardItem.board.id}&id=${boardItem.id}">修改</a>
					<a href="${ctx}/mod/boardItem/delete?boardId=${boardItem.board.id}&id=${boardItem.id}" onclick="return confirmx('确认要删除该商品吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>