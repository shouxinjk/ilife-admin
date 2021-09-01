<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>链接模板管理</title>
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
		<li class="active"><a href="${ctx}/mod/linkTemplate/">链接模板列表</a></li>
		<shiro:hasPermission name="mod:linkTemplate:edit"><li><a href="${ctx}/mod/linkTemplate/form">链接模板添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="linkTemplate" action="${ctx}/mod/linkTemplate/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>电商平台：</label>
				<form:select path="platform" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('platform')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>链接类型：</label>
				<form:select path="type" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('link_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>模板名称：</label>
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
				<th>电商平台</th>
				<th>链接类型</th>
				<th>优先级</th>
				<th>规则定义</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:linkTemplate:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="linkTemplate">
			<tr>
				<td><a href="${ctx}/mod/linkTemplate/form?id=${linkTemplate.id}">
					${linkTemplate.name}
				</a></td>
				<td>
					${fns:getDictLabel(linkTemplate.platform, 'platform', '-')}
				</td>				
				<td>
					${fns:getDictLabel(linkTemplate.type, 'link_type', '-')}
				</td>	
				<td>
					${linkTemplate.priority}
				</td>				
				<td>
					适用条件：<br/>
					${linkTemplate.condition}<br/>
					处理规则：<br/>
					${linkTemplate.expression}<br/>
				</td>							
				<td>
					<fmt:formatDate value="${linkTemplate.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:linkTemplate:edit"><td>
    				<a href="${ctx}/mod/linkTemplate/form?id=${linkTemplate.id}">修改</a>
					<a href="${ctx}/mod/linkTemplate/delete?id=${linkTemplate.id}" onclick="return confirmx('确认要删除该链接模板吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>