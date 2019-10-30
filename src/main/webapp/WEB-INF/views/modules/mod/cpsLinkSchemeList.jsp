<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>CPS链接规则管理</title>
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
		<li class="active"><a href="${ctx}/mod/cpsLinkScheme/">CPS链接规则列表</a></li>
		<shiro:hasPermission name="mod:cpsLinkScheme:edit"><li><a href="${ctx}/mod/cpsLinkScheme/form">CPS链接规则添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="cpsLinkScheme" action="${ctx}/mod/cpsLinkScheme/" method="post" class="breadcrumb form-search">
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
				<shiro:hasPermission name="mod:cpsLinkScheme:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="cpsLinkScheme">
			<tr>
				<td>${cpsLinkScheme.platform}</td>
				<td>${cpsLinkScheme.category}</td>	
				<td>${cpsLinkScheme.script}</td>		
				<td>
					<fmt:formatDate value="${cpsLinkScheme.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:cpsLinkScheme:edit"><td>
    				<a href="${ctx}/mod/cpsLinkScheme/form?id=${cpsLinkScheme.id}">修改</a>
					<a href="${ctx}/mod/cpsLinkScheme/delete?id=${cpsLinkScheme.id}" onclick="return confirmx('确认要删除该CPS链接规则吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>