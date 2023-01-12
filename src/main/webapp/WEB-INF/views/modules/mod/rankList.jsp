<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>排行榜管理</title>
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
		<li class="active"><a href="${ctx}/mod/rank/">排行榜列表</a></li>
		<shiro:hasPermission name="mod:rank:edit"><li><a href="${ctx}/mod/rank/form">排行榜添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="rank" action="${ctx}/mod/rank/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>类目：</label>
				<form:input path="category.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>创建者openid：</label>
				<form:input path="openid" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>创建者昵称：</label>
				<form:input path="nickname" htmlEscape="false" maxlength="256" class="input-medium"/>
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
				<th>类目</th>
				<th>主题关键字</th>
				<th>描述</th>
				<th>创建者openid</th>
				<th>创建者昵称</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:rank:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="rank">
			<tr>
				<td><a href="${ctx}/mod/rank/form?id=${rank.id}">
					${rank.name}
				</a></td>
				<td>
					${rank.category.name}
				</td>
				<td>
					${rank.keywords}
				</td>				
				<td>
					${rank.description}
				</td>
				<td>
					${rank.openid}
				</td>
				<td>
					${rank.nickname}
				</td>
				<td>
					<fmt:formatDate value="${rank.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:rank:edit"><td>
    				<a href="${ctx}/mod/rank/form?id=${rank.id}">修改</a>
					<a href="${ctx}/mod/rank/delete?id=${rank.id}" onclick="return confirmx('确认要删除该排行榜吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>