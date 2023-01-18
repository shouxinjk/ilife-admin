<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>贡献度管理</title>
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
		<li class="active"><a href="${ctx}/mod/credit/">贡献度列表</a></li>
		<shiro:hasPermission name="mod:credit:edit"><li><a href="${ctx}/mod/credit/form">贡献度添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="credit" action="${ctx}/mod/credit/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>KEY：</label>
				<form:select path="key" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('behavior_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>类型：</label>
				<form:select path="type" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('credit_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>KEY</th>
				<th>类型</th>
				<th>名称</th>
				<th>描述</th>
				<th>排序</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:credit:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="credit">
			<tr>
				<td><a href="${ctx}/mod/credit/form?id=${credit.id}">
					${fns:getDictLabel(credit.key, 'behavior_type', '')}
				</a></td>
				<td>
					${fns:getDictLabel(credit.type, 'credit_type', '')}
				</td>
				<td>
					${credit.name}
				</td>
				<td>
					${credit.description}
				</td>
				<td>
					${credit.priority}
				</td>
				<td>
					<fmt:formatDate value="${credit.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:credit:edit"><td>
    				<a href="${ctx}/mod/credit/form?id=${credit.id}">修改</a>
					<a href="${ctx}/mod/credit/delete?id=${credit.id}" onclick="return confirmx('确认要删除该贡献度吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>