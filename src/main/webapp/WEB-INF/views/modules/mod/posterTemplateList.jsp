<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>海报模板管理</title>
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
		<li class="active"><a href="${ctx}/mod/posterTemplate/">海报模板列表</a></li>
		<shiro:hasPermission name="mod:posterTemplate:edit"><li><a href="${ctx}/mod/posterTemplate/form">海报模板添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="posterTemplate" action="${ctx}/mod/posterTemplate/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称</label>
				<form:input path="name" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>类型：</label>
				<form:select path="type" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('poster_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>			
			<li><label>类目</label>
				<form:input path="category.id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>状态</label>
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
				<th>名称</th>
				<th>缩略图</th>
				<th>类型</th>
				<th>描述</th>
				<th>适用类目</th>
				<th>海报编号</th>
				<th>状态</th>
				<th>适用条件</th>
				<!--th>参数配置</th-->
				<th>优先级</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:posterTemplate:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="posterTemplate">
			<tr>
				<td><a href="${ctx}/mod/posterTemplate/form?id=${posterTemplate.id}">
					${posterTemplate.name}
				</a></td>
				<td>
					<img src="${posterTemplate.logo}" style="width:60px;object-fill:cover;"/>
				</td>
				<td>
					${fns:getDictLabel(posterTemplate.type, 'poster_type', '-')}
				</td>
				<td>
					${posterTemplate.description}
				</td>
				<td>
					${posterTemplate.category.name}
				</td>
				<td>
					${posterTemplate.code}
				</td>
				<td>
					${fns:getDictLabel(posterTemplate.status, 'active_inactive', '-')}
				</td>
				<td>
					${posterTemplate.criteria}
				</td>
				<!--td>
					${posterTemplate.options}
				</td-->
				<td>
					${posterTemplate.priority}
				</td>
				<td>
					<fmt:formatDate value="${posterTemplate.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:posterTemplate:edit"><td>
    				<a href="${ctx}/mod/posterTemplate/form?id=${posterTemplate.id}">修改</a>
					<a href="${ctx}/mod/posterTemplate/delete?id=${posterTemplate.id}" onclick="return confirmx('确认要删除该海报模板吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>