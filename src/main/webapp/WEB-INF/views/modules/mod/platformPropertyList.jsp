<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>电商平台属性映射管理</title>
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
		<li class="active"><a href="${ctx}/mod/platformProperty/">属性映射列表</a></li>
		<shiro:hasPermission name="mod:platformProperty:edit"><li><a href="${ctx}/mod/platformProperty/form?platform=${treeId}">属性映射添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="platformProperty" action="${ctx}/mod/platformProperty/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>			
			<li><label>原始类目：</label>
				<sys:treeselect id="platformCategory" name="platformCategory.id" value="${platformProperty.platformCategory.id}" labelName="platformCategory.name" labelValue="${platformProperty.platformCategory.name}"
					title="原始类目" url="/mod/platformCategory/treeData" notAllowSelectRoot="false"/>	
			</li>
			<!--li><label>来源平台：</label>
				<form:select path="platform" class="input-medium">
					<form:option value="" label="不限"/>
					<form:options items="${fns:getDictList('platform')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>	
			</li-->
			<!--li><label>标准类目：</label>
				<sys:treeselect id="category" name="category.id" value="${platformProperty.category.id}" labelName="category.name" labelValue="${platformProperty.category.name}"
					title="商品分类" url="/mod/itemCategory/treeData" notAllowSelectRoot="true" cssClass="required"/>				
			</li-->
			<li><label>标准属性：</label>
				<sys:treeselect id="measure" name="measure.id" value="${platformProperty.measure.id}" labelName="measure.name" labelValue="${platformProperty.measure.name}"
					title="关键属性" url="/mod/measure/treeData"/>
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
				<th>来源平台</th>
				<th>原始类目</th>
				<!--th>标准类目</th-->
				<th>标准属性</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:platformProperty:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="platformProperty">
			<tr>
				<td><a href="${ctx}/mod/platformProperty/form?id=${platformProperty.id}">
					${platformProperty.name}
				</a></td>
				<td>${fns:getDictLabel(platformProperty.platform, 'platform', '-')}</td>
				<td>${platformProperty.platformCategory.name}</td>
				<!--td>${platformProperty.category.name}</td-->
				<td>${platformProperty.measure.name}</td>
				<td>
					<fmt:formatDate value="${platformProperty.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:platformProperty:edit"><td>
    				<a href="${ctx}/mod/platformProperty/form?id=${platformProperty.id}">修改</a>
					<a href="${ctx}/mod/platformProperty/delete?id=${platformProperty.id}" onclick="return confirmx('确认要删除该电商平台属性映射吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>