<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品管理</title>
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
		<li class="active"><a href="${ctx}/ope/item/">商品列表</a></li>
		<shiro:hasPermission name="ope:item:edit"><li><a href="${ctx}/ope/item/form">商品添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="item" action="${ctx}/ope/item/" method="post" class="breadcrumb form-search">
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
				<th>商品类别</th>
				<th>来源</th>
				<th>名称</th>
				<th>导购连接</th>
				<th>摘要</th>
				<th>状态</th>
				<th>更新者</th>
				<th>更新时间</th>
				<shiro:hasPermission name="ope:item:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="item">
			<tr>
				<td><a href="${ctx}/ope/item/form?id=${item.id}">
					${item.itemCategory.name}
				</a></td>
				<td>
					${item.source}
				</td>
				<td>
					${item.name}
				</td>
				<td>
					${item.url}
				</td>
				<td>
					${item.summary}
				</td>
				<td>
					${fns:getDictLabel(item.status, 'is_active', '无')}
				</td>
				<td>
					${item.updateBy.id}
				</td>
				<td>
					<fmt:formatDate value="${item.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="ope:item:edit"><td>
    				<a href="${ctx}/ope/item/form?id=${item.id}">修改</a>
					<a href="${ctx}/ope/item/delete?id=${item.id}" onclick="return confirmx('确认要删除该商品吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>