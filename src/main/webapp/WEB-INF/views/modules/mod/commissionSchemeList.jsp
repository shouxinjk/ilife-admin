<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>佣金规则设置管理</title>
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
		<li class="active"><a href="${ctx}/mod/commissionScheme/">佣金规则设置列表</a></li>
		<shiro:hasPermission name="mod:commissionScheme:edit"><li><a href="${ctx}/mod/commissionScheme/form">佣金规则设置添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="commissionScheme" action="${ctx}/mod/commissionScheme/" method="post" class="breadcrumb form-search">
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
				<th>电商平台</th>
				<th>商品类别</th>
				<th>佣金类型</th>
				<th>佣金额</th>
				<th>生效时间</th>
				<th>状态</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:commissionScheme:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="commissionScheme">
			<tr>
				<td>${commissionScheme.platform}</td>
				<td>${commissionScheme.category}</td>
				<td>${fns:getDictLabel(commissionScheme.type, 'commissionType', '-')}</td>
				<td>${commissionScheme.amount}</td>
				<td>
					<fmt:formatDate value="${commissionScheme.effectiveFrom}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>	
				<td>${fns:getDictLabel(commissionScheme.status, 'yes_no', '-')}</td>						
				<td>
					<fmt:formatDate value="${commissionScheme.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:commissionScheme:edit"><td>
    				<a href="${ctx}/mod/commissionScheme/form?id=${commissionScheme.id}">修改</a>
					<a href="${ctx}/mod/commissionScheme/delete?id=${commissionScheme.id}" onclick="return confirmx('确认要删除该佣金规则设置吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>