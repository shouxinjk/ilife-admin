<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>电商平台类目映射管理</title>
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
		<li class="active"><a href="${ctx}/mod/platformCategory/listPending?treeId=${treeId}">待标注</a></li>
		<li><a href="${ctx}/mod/platformCategory/list?treeId=${treeId}">类目映射列表</a></li>
		<shiro:hasPermission name="mod:platformCategory:edit"><li><a href="${ctx}/mod/platformCategory/form?platform=${treeId}">类目映射添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="platformCategory" action="${ctx}/mod/platformCategory/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>		
			<li><label>来源平台：</label>
				<form:select path="platform" class="input-medium">
					<form:option value="" label="不限"/>
					<form:options items="${fns:getDictList('platform')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>	
			</li>				
			<!--li><label>原始类目：</label>
				<sys:treeselect id="platformCategory" name="platformCategory.id" value="${platformCategory.id}" labelName="platformCategory.name" labelValue="${platformCategory.name}"
					title="原始类目" url="/mod/platformCategory/treeData" notAllowSelectRoot="false"/>	
			</li>
			<li><label>标准类目：</label>
				<sys:treeselect id="category" name="category.id" value="${platformCategory.category.id}" labelName="category.name" labelValue="${platformCategory.category.name}"
					title="商品分类" url="/mod/itemCategory/treeData" notAllowSelectRoot="true" cssClass="required"/>				
			</li-->
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>名称</th>
				<th>来源平台</th>
				<th>标准类目</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:platformCategory:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="platformCategory">
			<tr>
				<td><a href="${ctx}/mod/platformCategory/form?id=${platformCategory.id}">
					${platformCategory.name}
				</a></td>
				<td>${fns:getDictLabel(platformCategory.platform, 'platform', '-')}</td>
				<td>${platformCategory.category.name}</td>
				<td>
					<fmt:formatDate value="${platformCategory.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:platformCategory:edit"><td>
    				<a href="${ctx}/mod/platformCategory/form?id=${platformCategory.id}">修改</a>
					<a href="${ctx}/mod/platformCategory/delete?id=${platformCategory.id}" onclick="return confirmx('确认要删除该电商平台类目映射吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>