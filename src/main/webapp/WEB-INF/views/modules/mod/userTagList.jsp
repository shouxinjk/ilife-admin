<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户标签管理</title>
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
		<li class="active"><a href="${ctx}/mod/userTag/">标签列表</a></li>
		<shiro:hasPermission name="mod:userTag:edit"><li><a href="${ctx}/mod/userTag/form?userTagCategory.id=${treeId}">标签添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="userTag" action="${ctx}/mod/userTag/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
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
				<th>所属分类</th>
				<th>关联属性</th>
				<th>表达式</th>
				<th>标签类型</th>
				<th>判定规则</th>
				<shiro:hasPermission name="mod:userTag:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userTag">
			<tr>
				<td><a href="${ctx}/mod/userTag/form?id=${userTag.id}">
					${userTag.name}
				</a></td>
				<td>
					${userTag.userTagCategory.name}
				</td>
				<td>
					${userTag.userMeasure.name}
				</td>
				<td>
					${userTag.expression}
				</td>
				<td>
					${userTag.type}
				</td>
				<td>
					${userTag.ruleOfJudgment}
				</td>
				<shiro:hasPermission name="mod:userTag:edit"><td>
    				<a href="${ctx}/mod/userTag/form?id=${userTag.id}">修改</a>
					<a href="${ctx}/mod/userTag/delete?id=${userTag.id}&treeId=${treeId}" onclick="return confirmx('确认要删除该标签吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>