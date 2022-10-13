<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>业务字典定义管理</title>
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
		<li class="active"><a href="${ctx}/mod/dictMeta/">业务字典定义列表</a></li>
		<shiro:hasPermission name="mod:dictMeta:edit"><li><a href="${ctx}/mod/dictMeta/form">业务字典定义添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="dictMeta" action="${ctx}/mod/dictMeta/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>类型：</label>
				<form:select path="type" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('dict_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
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
				<th>类型</th>
				<th>名称</th>
				<th>键值</th>
				<th>描述</th>
				<th>默认数值</th>
				<th>控制数值</th>
				<th>控制值描述</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:dictMeta:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="dictMeta">
			<tr>
				<td><a href="${ctx}/mod/dictMeta/form?id=${dictMeta.id}">
					${fns:getDictLabel(dictMeta.type, 'dict_type', '')}
				</a></td>
				<td>
					${dictMeta.name}
				</td>
				<td>
					${dictMeta.dictKey}
				</td>
				<td>
					${dictMeta.description}
				</td>
				<td>
					${dictMeta.defaultValue}
				</td>
				<td>
					${dictMeta.controlValue}
				</td>
				<td>
					${dictMeta.controlDesc}
				</td>
				<td>
					<fmt:formatDate value="${dictMeta.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:dictMeta:edit"><td>
    				<a href="${ctx}/mod/dictMeta/form?id=${dictMeta.id}">修改</a>
					<a href="${ctx}/mod/dictMeta/delete?id=${dictMeta.id}" onclick="return confirmx('确认要删除该业务字典定义吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>