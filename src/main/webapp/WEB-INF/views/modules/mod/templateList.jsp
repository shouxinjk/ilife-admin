<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>文案模板管理</title>
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
		<li class="active"><a href="${ctx}/mod/template/">文案模板列表</a></li>
		<shiro:hasPermission name="mod:template:edit"><li><a href="${ctx}/mod/template/form?category.id=${treeId}">文案模板添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="itemTemplate" action="${ctx}/mod/template/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称</label>
				<form:input path="name" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>类型：</label>
				<form:select path="name" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('advice_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
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
				<th>基本信息</th>
				<th>模板规则</th>
				<th>元数据</th>
				<shiro:hasPermission name="mod:template:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="itemTemplate">
			<tr>
				<td>
				名称：<a href="${ctx}/mod/template/form?id=${itemTemplate.id}">${itemTemplate.name}</a><br/>
				类目：${itemTemplate.category.name}<br/>
				类型：${fns:getDictLabel(itemTemplate.type, 'advice_type', '-')}<br/>
				状态：${fns:getDictLabel(itemTemplate.status, 'active_inactive', '-')}<br/>
				优先级：${itemTemplate.priority}<br/>
				描述：${itemTemplate.name}
				</td>
				<td>
				适用条件描述：<br/>${itemTemplate.conditionDesc}<br/>
				适用条件规则：<br/>${itemTemplate.condition}<br/>
				模板描述：<br/>${itemTemplate.description}<br/>				
				模板规则：<br/>${itemTemplate.expression}
				</td>				
				<td>
				创建者：${itemTemplate.createBy.name}<br/>
				更新者：${itemTemplate.updateBy.name}<br/>
				创建时间：<fmt:formatDate value="${itemTemplate.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/><br/>
				更新时间：<fmt:formatDate value="${itemTemplate.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:template:edit"><td>
    				<a href="${ctx}/mod/template/form?id=${itemTemplate.id}">修改</a><br/>
					<a href="${ctx}/mod/template/delete?id=${itemTemplate.id}" onclick="return confirmx('确认要删除该文案模板吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>