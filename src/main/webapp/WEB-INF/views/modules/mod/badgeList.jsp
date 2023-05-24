<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>勋章管理</title>
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
		<li class="active"><a href="${ctx}/mod/badge/">勋章列表</a></li>
		<shiro:hasPermission name="mod:badge:edit"><li><a href="${ctx}/mod/badge/form">勋章添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="badge" action="${ctx}/mod/badge/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>CODE：</label>
				<form:select path="code" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('badge_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>名称</th>
				<th>描述</th>
				<th>图标</th>
				<th>CODE</th>
				<th>等级</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:badge:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="badge">
			<tr>
				<td><a href="${ctx}/mod/badge/form?id=${badge.id}">
					${badge.name}
				</a></td>
				<td>
					${badge.description}
				</td>
				<td>
					${badge.icon}
				</td>
				<td>
					${fns:getDictLabel(badge.code, 'badge_type', '')}
				</td>
				<td>
					${badge.level}
				</td>
				<td>
					<fmt:formatDate value="${badge.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:badge:edit"><td>
    				<a href="${ctx}/mod/badge/form?id=${badge.id}">修改</a>
					<a href="${ctx}/mod/badge/delete?id=${badge.id}" onclick="return confirmx('确认要删除该勋章吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>