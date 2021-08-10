<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>数据标注管理</title>
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
		<li class="active"><a href="${ctx}/ope/humanMarkedValue/">数据标注列表</a></li>
		<shiro:hasPermission name="ope:humanMarkedValue:edit"><li><a href="${ctx}/ope/humanMarkedValue/form">数据标注添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="humanMarkedValue" action="${ctx}/ope/humanMarkedValue/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<!--th>维度</th-->
				<th>关键属性</th>
				<th>原始值</th>
				<th>标注值</th>
				<th>标注达人</th>
				<th>标注时间</th>
				<shiro:hasPermission name="ope:humanMarkedValue:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="humanMarkedValue">
			<tr>
				<!--td><a href="${ctx}/ope/humanMarkedValue/form?id=${humanMarkedValue.id}">
					${humanMarkedValue.dimension}
				</a></td-->
				<td>
					${humanMarkedValue.measure.name}
				</td>				
				<td>
					${humanMarkedValue.originalValue}
				</td>				
				<td>
					${humanMarkedValue.value}
				</td>
				<td>
					${humanMarkedValue.updateBy.name}
				</td>
				<td>
					<fmt:formatDate value="${humanMarkedValue.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="ope:humanMarkedValue:edit"><td>
    				<a href="${ctx}/ope/humanMarkedValue/form?id=${humanMarkedValue.id}">修改</a>
					<a href="${ctx}/ope/humanMarkedValue/delete?id=${humanMarkedValue.id}" onclick="return confirmx('确认要删除该数据标注吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>