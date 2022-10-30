<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>个性定制方案条目管理</title>
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
		<li class="active"><a href="${ctx}/diy/solutionItem/">个性定制方案条目列表</a></li>
		<shiro:hasPermission name="diy:solutionItem:edit"><li><a href="${ctx}/diy/solutionItem/form">个性定制方案条目添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="solutionItem" action="${ctx}/diy/solutionItem/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>方案：</label>
				<form:input path="solution.id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>类型：</label>
				<form:input path="type" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>方案</th>
				<th>名称</th>
				<th>类型</th>
				<th>标签</th>
				<th>描述</th>
				<th>排序</th>
				<th>更新时间</th>
				<shiro:hasPermission name="diy:solutionItem:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="solutionItem">
			<tr>
				<td><a href="${ctx}/diy/solutionItem/form?id=${solutionItem.id}">
					${solutionItem.solution.name}
				</a></td>
				<td>
					${solutionItem.name}
				</td>
				<td>
					${solutionItem.type}
				</td>
				<td>
					${solutionItem.tags}
				</td>
				<td>
					${solutionItem.description}
				</td>
				<td>
					${solutionItem.priority}
				</td>
				<td>
					<fmt:formatDate value="${solutionItem.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="diy:solutionItem:edit"><td>
    				<a href="${ctx}/diy/solutionItem/form?id=${solutionItem.id}">修改</a>
					<a href="${ctx}/diy/solutionItem/delete?id=${solutionItem.id}" onclick="return confirmx('确认要删除该个性定制方案条目吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>