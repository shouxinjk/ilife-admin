<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>积分规则管理</title>
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
		<li class="active"><a href="${ctx}/mod/creditScheme/">积分规则列表</a></li>
		<shiro:hasPermission name="mod:creditScheme:edit"><li><a href="${ctx}/mod/creditScheme/form">积分规则添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="creditScheme" action="${ctx}/mod/creditScheme/" method="post" class="breadcrumb form-search">
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
				<th>规则脚本</th>				
				<th>更新时间</th>
				<shiro:hasPermission name="mod:creditScheme:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="creditScheme">
			<tr>
				<td>${creditScheme.platform}</td>
				<td>${creditScheme.category}</td>
				<td>${creditScheme.script}</td>			
				<td>
					<fmt:formatDate value="${cpsLinkScheme.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:creditScheme:edit"><td>
    				<a href="${ctx}/mod/creditScheme/form?id=${creditScheme.id}">修改</a>
					<a href="${ctx}/mod/creditScheme/delete?id=${creditScheme.id}" onclick="return confirmx('确认要删除该积分规则吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>