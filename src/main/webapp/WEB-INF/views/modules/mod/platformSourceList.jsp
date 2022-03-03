<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品数据来源管理</title>
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
		<li class="active"><a href="${ctx}/mod/platformSource/">商品数据来源列表</a></li>
		<shiro:hasPermission name="mod:platformSource:edit"><li><a href="${ctx}/mod/platformSource/form">商品数据来源添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="platformSource" action="${ctx}/mod/platformSource/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>平台：</label>
				<form:select path="platform" class="input-medium required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('platform')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>类别：</label>
				<form:select path="type" class="input-medium required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('profit_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>状态：</label>
				<form:select path="status" class="input-medium ">
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
				<th>所属平台</th>
				<th>类别名称</th>
				<th>收益类型</th>
				<th>描述</th>
				<th>入口URL</th>
				<th>UserScript脚本</th>
				<th>排序</th>
				<th>状态</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:platformSource:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="platformSource">
			<tr>
				<td><a href="${ctx}/mod/platformSource/form?id=${platformSource.id}">
					${fns:getDictLabel(platformSource.platform, 'platform', platformSource.platform)}
				</a></td>
				<td>
					${platformSource.category}
				</td>
				<td>
					${fns:getDictLabel(platformSource.type, 'profit_type', platformSource.type)}
				</td>
				<td>
					${platformSource.description}
				</td>
				<td>
					${platformSource.url}
				</td>
				<td>
					${platformSource.userscript}
				</td>
				<td>
					${platformSource.sort}
				</td>
				<td>
					${fns:getDictLabel(platformSource.status, 'active_inactive', platformSource.status)}
				</td>
				<td>
					<fmt:formatDate value="${platformSource.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:platformSource:edit"><td>
    				<a href="${ctx}/mod/platformSource/form?id=${platformSource.id}">修改</a>
					<a href="${ctx}/mod/platformSource/delete?id=${platformSource.id}" onclick="return confirmx('确认要删除该商品数据来源吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>