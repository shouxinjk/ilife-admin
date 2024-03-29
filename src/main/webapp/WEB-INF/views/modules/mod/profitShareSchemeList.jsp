<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分润规则管理</title>
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
		<li class="active"><a href="${ctx}/mod/profitShareScheme/">分润规则列表</a></li>
		<shiro:hasPermission name="mod:profitShareScheme:edit"><li><a href="${ctx}/mod/profitShareScheme/form">分润规则添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="profitShareScheme" action="${ctx}/mod/profitShareScheme/" method="post" class="breadcrumb form-search">
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
				<th>名称</th>
				<th>类型</th>
				<th>来源</th>
				<th>类别</th>
				<th>激活</th>
				<th>优先级</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:profitShareScheme:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="profitShareScheme">
			<tr>
				<td>${profitShareScheme.name}</td>
				<td>${fns:getDictLabel(profitShareScheme.type, 'shareProfitType', '-')}</td>
				<td>${fns:getDictLabel(profitShareScheme.platform, 'platform', '-')}</td>
				<td>${profitShareScheme.category}</td>
				<td>${fns:getDictLabel(profitShareScheme.status, 'yes_no', '-')}</td>
				<td>${profitShareScheme.priority}</td>
				<td><a href="${ctx}/mod/profitShareScheme/form?id=${profitShareScheme.id}">
					<fmt:formatDate value="${profitShareScheme.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</a></td>
				<shiro:hasPermission name="mod:profitShareScheme:edit"><td>
    				<a href="${ctx}/mod/profitShareScheme/form?id=${profitShareScheme.id}">修改</a>
					<a href="${ctx}/mod/profitShareScheme/delete?id=${profitShareScheme.id}" onclick="return confirmx('确认要删除该分润规则吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>