<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>社会分层管理</title>
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
		<li class="active"><a href="${ctx}/mod/hierarchy/">社会分层列表</a></li>
		<shiro:hasPermission name="mod:hierarchy:edit"><li><a href="${ctx}/mod/hierarchy/form">社会分层添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="hierarchy" action="${ctx}/mod/hierarchy/" method="post" class="breadcrumb form-search">
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
				<th>经济资本</th>
				<th>文化资本</th>
				<th>社会资本</th>
				<th>识别规则</th>
				<th>matrix曲线</th>
				<shiro:hasPermission name="mod:hierarchy:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="hierarchy">
			<tr>
				<td><a href="${ctx}/mod/hierarchy/form?id=${hierarchy.id}" title="${hierarchy.description}">
					${hierarchy.name}
				</a></td>
				<td>
					${hierarchy.economyLevel}(${hierarchy.economyScoreMin } - ${hierarchy.economyScoreMax })
				</td>
				<td>
					${hierarchy.cultureLevel}(${hierarchy.cultureScoreMin } - ${hierarchy.cultureScoreMax })
				</td>
				<td>
					${hierarchy.societyLevel}(${hierarchy.societyScoreMin } - ${hierarchy.societyScoreMax })
				</td>
				<td>
					${hierarchy.expression}
				</td>
				<td>
					${hierarchy.matrix}
				</td>
				<shiro:hasPermission name="mod:hierarchy:edit"><td>
    				<a href="${ctx}/mod/hierarchy/form?id=${hierarchy.id}">修改</a>
					<a href="${ctx}/mod/hierarchy/delete?id=${hierarchy.id}" onclick="return confirmx('确认要删除该社会分层吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>