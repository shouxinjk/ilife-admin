<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户行为管理</title>
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
		<li class="active"><a href="${ctx}/ope/behavior/">用户行为列表</a></li>
		<shiro:hasPermission name="ope:behavior:edit"><li><a href="${ctx}/ope/behavior/form">用户行为添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="behavior" action="${ctx}/ope/behavior/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="type" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户更新Expr</th>
				<th>商品更新Expr</th>
				<th>说明</th>
				<th>用户可信度Expr</th>
				<th>名称</th>
				<th>更新者</th>
				<th>更新时间</th>
				<shiro:hasPermission name="ope:behavior:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="behavior">
			<tr>
				<td><a href="${ctx}/ope/behavior/form?id=${behavior.id}">
					${behavior.exprPerson}
				</a></td>
				<td>
					${behavior.exprItem}
				</td>
				<td>
					${behavior.description}
				</td>
				<td>
					${behavior.exprCredit}
				</td>
				<td>
					${behavior.type}
				</td>
				<td>
					${behavior.updateBy.name}
				</td>
				<td>
					<fmt:formatDate value="${behavior.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="ope:behavior:edit"><td>
    				<a href="${ctx}/ope/behavior/form?id=${behavior.id}">修改</a>
					<a href="${ctx}/ope/behavior/delete?id=${behavior.id}" onclick="return confirmx('确认要删除该用户行为吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>