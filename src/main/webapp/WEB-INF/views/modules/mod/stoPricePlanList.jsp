<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品订阅计划管理</title>
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
		<li class="active"><a href="${ctx}/mod/stoPricePlan/">产品订阅计划列表</a></li>
		<shiro:hasPermission name="mod:stoPricePlan:edit"><li><a href="${ctx}/mod/stoPricePlan/form">产品订阅计划添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="stoPricePlan" action="${ctx}/mod/stoPricePlan/" method="post" class="breadcrumb form-search">
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
				<shiro:hasPermission name="mod:stoPricePlan:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="stoPricePlan">
			<tr>
				<td><a href="${ctx}/mod/stoPricePlan/form?id=${stoPricePlan.id}">
					${stoPricePlan.name}
				</a></td>
				<shiro:hasPermission name="mod:stoPricePlan:edit"><td>
    				<a href="${ctx}/mod/stoPricePlan/form?id=${stoPricePlan.id}">修改</a>
					<a href="${ctx}/mod/stoPricePlan/delete?id=${stoPricePlan.id}" onclick="return confirmx('确认要删除该产品订阅计划吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>