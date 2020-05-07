<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>标注管理</title>
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
		<li class="active"><a href="${ctx}/ope/performance/">标注列表</a></li>
		<shiro:hasPermission name="ope:performance:edit"><li><a href="${ctx}/ope/performance/form?pid=${pid}&pType=${pType}">标注添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="performance" action="${ctx}/ope/performance/?treeId=${pid}&treeModule=${pType}" method="post" class="breadcrumb form-search">
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
				<shiro:hasPermission name="ope:performance:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="performance">
			<tr>
				<td><a href="${ctx}/ope/performance/form?id=${performance.id}">
					${performance.measure.name}
				</a></td>
				<td>
					${performance.markedValue}
				</td>
				<td>
					${performance.originalValue}
				</td>
				<td>
					${performance.normalizedValue}
				</td>
				<td>
					${performance.controlValue}
				</td>
				<td>
					${performance.level}
				</td>
				<td>
					${performance.dimension}
				</td>
				<td>
					${performance.updateBy.name}
				</td>
				<td>
					<fmt:formatDate value="${performance.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="ope:performance:edit"><td>
    				<a href="${ctx}/ope/performance/form?id=${performance.id}&pid=${pid}&pType=${pType}">修改</a>
					<a href="${ctx}/ope/performance/delete?id=${performance.id}&pid=${pid}&pType=${pType}" onclick="return confirmx('确认要删除该标注吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>