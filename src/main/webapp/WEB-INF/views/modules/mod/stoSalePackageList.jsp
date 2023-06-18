<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>SaaS套餐管理</title>
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
		<li class="active"><a href="${ctx}/mod/stoSalePackage/">SaaS套餐列表</a></li>
		<shiro:hasPermission name="mod:stoSalePackage:edit"><li><a href="${ctx}/mod/stoSalePackage/form">SaaS套餐添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="stoSalePackage" action="${ctx}/mod/stoSalePackage/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>套餐名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>套餐名称</th>
				<shiro:hasPermission name="mod:stoSalePackage:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="stoSalePackage">
			<tr>
				<td><a href="${ctx}/mod/stoSalePackage/form?id=${stoSalePackage.id}">
					${stoSalePackage.name}
				</a></td>
				<shiro:hasPermission name="mod:stoSalePackage:edit"><td>
    				<a href="${ctx}/mod/stoSalePackage/form?id=${stoSalePackage.id}">修改</a>
					<a href="${ctx}/mod/stoSalePackage/delete?id=${stoSalePackage.id}" onclick="return confirmx('确认要删除该SaaS套餐吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>