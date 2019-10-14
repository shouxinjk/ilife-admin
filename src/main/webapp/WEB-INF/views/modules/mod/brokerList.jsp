<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>推广达人管理</title>
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
		<li class="active"><a href="${ctx}/mod/broker/">推广达人列表</a></li>
		<shiro:hasPermission name="mod:broker:edit"><li><a href="${ctx}/mod/broker/form">推广达人添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="broker" action="${ctx}/mod/broker/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>真实姓名：</label>
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
				<th>真实姓名</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:broker:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="broker">
			<tr>
				<td><a href="${ctx}/mod/broker/form?id=${broker.id}">
					${broker.name}
				</a></td>
				<td>
					<fmt:formatDate value="${broker.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:broker:edit"><td>
    				<a href="${ctx}/mod/broker/form?id=${broker.id}">修改</a>
					<a href="${ctx}/mod/broker/delete?id=${broker.id}" onclick="return confirmx('确认要删除该推广达人吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>