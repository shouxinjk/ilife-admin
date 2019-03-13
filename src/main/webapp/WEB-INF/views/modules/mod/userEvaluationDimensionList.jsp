<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户主观评价-维度管理</title>
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
		<li class="active"><a href="${ctx}/mod/userEvaluationDimension/">用户主观评价-维度列表</a></li>
		<shiro:hasPermission name="mod:userEvaluationDimension:edit"><li><a href="${ctx}/mod/userEvaluationDimension/form">用户主观评价-维度添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="userEvaluationDimension" action="${ctx}/mod/userEvaluationDimension/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>name：</label>
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>name</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:userEvaluationDimension:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userEvaluationDimension">
			<tr>
				<td><a href="${ctx}/mod/userEvaluationDimension/form?id=${userEvaluationDimension.id}">
					${userEvaluationDimension.name}
				</a></td>
				<td>
					<fmt:formatDate value="${userEvaluationDimension.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:userEvaluationDimension:edit"><td>
    				<a href="${ctx}/mod/userEvaluationDimension/form?id=${userEvaluationDimension.id}">修改</a>
					<a href="${ctx}/mod/userEvaluationDimension/delete?id=${userEvaluationDimension.id}" onclick="return confirmx('确认要删除该用户主观评价-维度吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>