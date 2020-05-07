<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户标注管理</title>
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
		<li class="active"><a href="${ctx}/ope/userPerformance/">标注列表</a></li>
		<shiro:hasPermission name="ope:userPerformance:edit"><li><a href="${ctx}/ope/userPerformance/form?pid=${pid}&pType=${pType}">标注添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="userPerformance" action="${ctx}/ope/userPerformance/?treeId=${pid}&treeModule=${pType}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>关键属性</th>
				<th>标注值</th>
				<th>原始值</th>
				<th>归一值</th>
				<th>对照值</th>
				<th>等级</th>
				<th>所属维度</th>
				<th>更新者</th>
				<th>更新时间</th>
				<shiro:hasPermission name="ope:userPerformance:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userPerformance">
			<tr>
				<td><a href="${ctx}/ope/userPerformance/form?id=${userPerformance.id}">
					${userPerformance.measure.name}
				</a></td>
				<td>
					${userPerformance.markedValue}
				</td>
				<td>
					${userPerformance.originalValue}
				</td>
				<td>
					${userPerformance.normalizedValue}
				</td>
				<td>
					${userPerformance.controlValue}
				</td>
				<td>
					${userPerformance.level}
				</td>
				<td>
					${userPerformance.dimension.name}
				</td>
				<td>
					${userPerformance.updateBy.name}
				</td>
				<td>
					<fmt:formatDate value="${userPerformance.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="ope:userPerformance:edit"><td>
    				<a href="${ctx}/ope/userPerformance/form?id=${userPerformance.id}&pid=${pid}&pType=${pType}">修改</a>
					<a href="${ctx}/ope/userPerformance/delete?id=${userPerformance.id}&pid=${pid}&pType=${pType}" onclick="return confirmx('确认要删除该标注吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>