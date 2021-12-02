<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Board管理</title>
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
		<li class="active"><a href="${ctx}/mod/board/">Board列表</a></li>
		<shiro:hasPermission name="mod:board:edit"><li><a href="${ctx}/mod/board/form">Board添加</a></li></shiro:hasPermission>	
	</ul>
	<form:form id="searchForm" modelAttribute="board" action="${ctx}/mod/board/" method="post" class="breadcrumb form-search">
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
				<th>标题</th>
				<th>创建者</th>
				<th>标签</th>
				<th>类型</th>
				<th>关键词</th>
				<th>状态</th>
				<th>描述</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:board:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="board">
			<tr>
				<td><a href="${ctx}/mod/boardItem/?boardId=${board.id}">
					${board.title}
				</a></td>
				<td>${board.broker.name}</td>
				<td>${board.tags}</td>
				<td>${fns:getDictLabel(board.type, 'boardType', '-')}</td>
				<td>${board.keywords}</td>
				<td>${board.status}</td>
				<td>${board.description}</td>
				<td>
					<fmt:formatDate value="${board.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:board:edit"><td>
    				<a href="${ctx}/mod/board/form?id=${board.id}">修改</a>
					<a href="${ctx}/mod/board/delete?id=${board.id}" onclick="return confirmx('确认要删除该内容看板管理吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>