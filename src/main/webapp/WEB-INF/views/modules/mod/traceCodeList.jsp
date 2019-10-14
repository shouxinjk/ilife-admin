<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>推广位管理</title>
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
		<li class="active"><a href="${ctx}/mod/traceCode/">推广位列表</a></li>
		<shiro:hasPermission name="mod:traceCode:edit"><li><a href="${ctx}/mod/traceCode/form">推广位添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="traceCode" action="${ctx}/mod/traceCode/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:traceCode:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="traceCode">
			<tr>
				<td><a href="${ctx}/mod/traceCode/form?id=${traceCode.id}">
					<fmt:formatDate value="${traceCode.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</a></td>
				<shiro:hasPermission name="mod:traceCode:edit"><td>
    				<a href="${ctx}/mod/traceCode/form?id=${traceCode.id}">修改</a>
					<a href="${ctx}/mod/traceCode/delete?id=${traceCode.id}" onclick="return confirmx('确认要删除该推广位吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>