<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>品类需要满足管理</title>
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
		<li class="active"><a href="${ctx}/mod/categoryNeed/?treeId=${pid}&treeModule=${pType}">需要列表</a></li>
		<shiro:hasPermission name="mod:categoryNeed:edit"><li><a href="${ctx}/mod/categoryNeed/form?pid=${pid}&pType=${pType}">需要添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="categoryNeed" action="${ctx}/mod/categoryNeed/?treeId=${pid}&treeModule=${pType}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>需要</th>
				<th>权重</th>
				<th>表达式</th>
				<th>描述</th>
				<th>创建时间</th>
				<th>最后更新</th>
				<shiro:hasPermission name="mod:categoryNeed:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="categoryNeed">
			<tr>
				<td>
					${categoryNeed.need.name}
				</td>
				<td>
					${categoryNeed.weight}
				</td>
				<td>
					${categoryNeed.expression}
				</td>
				<td>
					${categoryNeed.description}
				</td>
				<td><fmt:formatDate value="${categoryNeed.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td><fmt:formatDate value="${categoryNeed.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<shiro:hasPermission name="mod:categoryNeed:edit"><td>
    				<a href="${ctx}/mod/categoryNeed/form?id=${categoryNeed.id}&pid=${pid}&pType=${pType}">修改</a>
					<a href="${ctx}/mod/categoryNeed/delete?id=${categoryNeed.id}&pid=${pid}&pType=${pType}" onclick="return confirmx('确认要删除该需要吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>