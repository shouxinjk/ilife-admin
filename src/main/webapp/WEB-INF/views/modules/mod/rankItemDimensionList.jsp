<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>排行榜条目管理</title>
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
		<li class="active"><a href="${ctx}/mod/rankItemDimension/">排行榜条目列表</a></li>
		<shiro:hasPermission name="mod:rankItemDimension:edit"><li><a href="${ctx}/mod/rankItemDimension/form">排行榜条目添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="rankItemDimension" action="${ctx}/mod/rankItemDimension/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>排行榜：</label>
				<form:input path="rank.id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>评价维度：</label>
				<form:input path="dimension.id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>排行榜</th>
				<th>评价维度</th>
				<th>优先级</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:rankItemDimension:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="rankItemDimension">
			<tr>
				<td><a href="${ctx}/mod/rankItemDimension/form?id=${rankItemDimension.id}">
					${rankItemDimension.rank.name}
				</a></td>
				<td>
					${rankItemDimension.dimension.name}
				</td>
				<td>
					${rankItemDimension.priority}
				</td>
				<td>
					<fmt:formatDate value="${rankItemDimension.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:rankItemDimension:edit"><td>
    				<a href="${ctx}/mod/rankItemDimension/form?id=${rankItemDimension.id}">修改</a>
					<a href="${ctx}/mod/rankItemDimension/delete?id=${rankItemDimension.id}" onclick="return confirmx('确认要删除该排行榜条目吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>