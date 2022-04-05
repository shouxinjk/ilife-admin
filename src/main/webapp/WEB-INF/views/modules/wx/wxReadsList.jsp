<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>阅读记录管理</title>
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
		<li class="active"><a href="${ctx}/wx/wxReads/">阅读记录列表</a></li>
		<shiro:hasPermission name="wx:wxReads:edit"><li><a href="${ctx}/wx/wxReads/form">阅读记录添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wxReads" action="${ctx}/wx/wxReads/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>文章：</label>
				<form:input path="article.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>读者broker：</label>
				<form:input path="broker.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>读者openid：</label>
				<form:input path="openid" htmlEscape="false" maxlength="128" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>文章</th>
				<th>读者broker</th>
				<th>读者openid</th>
				<th>阅读报数</th>
				<th>更新时间</th>
				<shiro:hasPermission name="wx:wxReads:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wxReads">
			<tr>
				<td><a href="${wxReads.url}">
					${wxReads.article.name}
				</a></td>
				<td>
					${wxReads.broker.nickname}
				</td>
				<td>
					${wxReads.openid}
				</td>
				<td>
					${wxReads.readCount}
				</td>
				<td>
					<fmt:formatDate value="${wxReads.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="wx:wxReads:edit"><td>
    				<a href="${ctx}/wx/wxReads/form?id=${wxReads.id}">修改</a>
					<a href="${ctx}/wx/wxReads/delete?id=${wxReads.id}" onclick="return confirmx('确认要删除该阅读记录吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>