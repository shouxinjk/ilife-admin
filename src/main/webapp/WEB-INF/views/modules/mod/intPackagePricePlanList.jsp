<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订阅套餐明细管理</title>
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
		<li class="active"><a href="${ctx}/mod/intPackagePricePlan/">订阅套餐明细列表</a></li>
		<shiro:hasPermission name="mod:intPackagePricePlan:edit"><li><a href="${ctx}/mod/intPackagePricePlan/form">订阅套餐明细添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="intPackagePricePlan" action="${ctx}/mod/intPackagePricePlan/" method="post" class="breadcrumb form-search">
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
				<th>产品套餐</th>
				<th>订阅产品</th>
				<th>订阅计划</th>
				<shiro:hasPermission name="mod:intPackagePricePlan:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="intPackagePricePlan">
			<tr>
				<td><a href="${ctx}/mod/intPackagePricePlan/form?id=${intPackagePricePlan.id}">
					${intPackagePricePlan.salePackageId}
				</a></td>
				<td>
					${intPackagePricePlan.softwareId}
				</td>
				<td>
					${intPackagePricePlan.pricePlanId}
				</td>
				<shiro:hasPermission name="mod:intPackagePricePlan:edit"><td>
    				<a href="${ctx}/mod/intPackagePricePlan/form?id=${intPackagePricePlan.id}">修改</a>
					<a href="${ctx}/mod/intPackagePricePlan/delete?id=${intPackagePricePlan.id}" onclick="return confirmx('确认要删除该订阅套餐明细吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>