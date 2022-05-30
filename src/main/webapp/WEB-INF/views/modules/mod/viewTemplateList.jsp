<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>模型展示模板管理</title>
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
		<li class="active"><a href="${ctx}/mod/viewTemplate/">模型展示模板列表</a></li>
		<shiro:hasPermission name="mod:viewTemplate:edit"><li><a href="${ctx}/mod/viewTemplate/form">模型展示模板添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="viewTemplate" action="${ctx}/mod/viewTemplate/" method="post" class="breadcrumb form-search">
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
				<th>LOGO</th>
				<th>类型</th>
				<th>格式</th>			
				<th>优先级</th>
				<th>描述</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:viewTemplate:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="viewTemplate">
			<tr>
				<td><a href="${ctx}/mod/viewTemplate/form?id=${viewTemplate.id}">
					${viewTemplate.name}
				</a></td>
				<td>
					<img src="${viewTemplate.logo}" width="60"/>			
				</td>
				<td>${fns:getDictLabel(viewTemplate.type, 'viewTemplateType', '-')}</td>
				<td>${fns:getDictLabel(viewTemplate.subType, 'viewTemplateFormat', '-')}</td>
				<td>${viewTemplate.priority}</td>
				<td>${viewTemplate.description}</td>
				<td>
					<fmt:formatDate value="${viewTemplate.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:viewTemplate:edit"><td>
    				<a href="${ctx}/mod/viewTemplate/form?id=${viewTemplate.id}">修改</a>
					<a href="${ctx}/mod/viewTemplate/delete?id=${viewTemplate.id}" onclick="return confirmx('确认要删除该模型展示模板吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>