<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>互助班车管理</title>
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
		<li class="active"><a href="${ctx}/wx/wxGrouping/">互助班车列表</a></li>
		<shiro:hasPermission name="wx:wxGrouping:edit"><li><a href="${ctx}/wx/wxGrouping/form">互助班车添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wxGrouping" action="${ctx}/wx/wxGrouping/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>群名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>班次</th>
				<th>群名称</th>
				<th>开车日期</th>
				<th>发车时间</th>
				<th>截止时间</th>
				<th>内容类型:article,account</th>
				<th>创建时间</th>
				<shiro:hasPermission name="wx:wxGrouping:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wxGrouping">
			<tr>
				<td><a href="${ctx}/wx/wxGrouping/form?id=${wxGrouping.id}">
					${wxGrouping.code}
				</a></td>
				<td>
					${wxGrouping.name}
				</td>
				<td>
					<fmt:formatDate value="${wxGrouping.eventDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${wxGrouping.eventTimeFrom}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${wxGrouping.eventTimeTo}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${fns:getDictLabel(wxGrouping.subjectType, 'wx_advertise_type', '')}
				</td>
				<td>
					<fmt:formatDate value="${wxGrouping.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="wx:wxGrouping:edit"><td>
    				<a href="${ctx}/wx/wxGrouping/form?id=${wxGrouping.id}">修改</a>
					<a href="${ctx}/wx/wxGrouping/delete?id=${wxGrouping.id}" onclick="return confirmx('确认要删除该互助班车吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>