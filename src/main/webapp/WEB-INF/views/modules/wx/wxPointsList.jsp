<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>微信虚拟豆管理</title>
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
		<li class="active"><a href="${ctx}/wx/wxPoints/">微信虚拟豆列表</a></li>
		<shiro:hasPermission name="wx:wxPoints:edit"><li><a href="${ctx}/wx/wxPoints/form">微信虚拟豆添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wxPoints" action="${ctx}/wx/wxPoints/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>产品名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>状态：</label>
				<form:select path="status" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('active_inactive')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>产品名称</th>
				<th>虚拟豆数量</th>
				<th>价格</th>
				<th>优惠策略</th>
				<th>状态</th>
				<th>更新时间</th>
				<shiro:hasPermission name="wx:wxPoints:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wxPoints">
			<tr>
				<td><a href="${ctx}/wx/wxPoints/form?id=${wxPoints.id}">
					${wxPoints.name}
				</a></td>
				<td>
					${wxPoints.points}
				</td>
				<td>
					${wxPoints.price}
				</td>
				<td>
					${wxPoints.discount}
				</td>
				<td>
					${fns:getDictLabel(wxPoints.status, 'active_inactive', '')}
				</td>
				<td>
					<fmt:formatDate value="${wxPoints.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="wx:wxPoints:edit"><td>
    				<a href="${ctx}/wx/wxPoints/form?id=${wxPoints.id}">修改</a>
					<a href="${ctx}/wx/wxPoints/delete?id=${wxPoints.id}" onclick="return confirmx('确认要删除该微信虚拟豆吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>