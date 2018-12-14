<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客观评价明细</title>
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
		<li><a href="${ctx}/mod/itemDimension/list?treeId=${categoryId}">客观评价列表</a></li>
		<shiro:hasPermission name="mod:itemDimension:edit"><li><a href="${ctx}/mod/itemDimension/form?category.id=${categoryId}">客观评价添加</a></li></shiro:hasPermission>
		<li class="active"><a href="${ctx}/mod/itemDimensionMeasure/list?dimensionId=${dimensionId}">评价明细列表</a></li>
		<shiro:hasPermission name="mod:itemDimensionMeasure:edit"><li><a href="${ctx}/mod/itemDimensionMeasure/form?dimension.id=${dimensionId}&category.id=${categoryId}">评价明细添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="itemDimensionMeasure" action="${ctx}/mod/itemDimensionMeasure/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-medium"/>
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
				<th>所属类别</th>
				<th>评价维度</th>
				<th>关键属性</th>
				<th>占比</th>
				<th>描述</th>
				<th>排序</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:itemDimensionMeasure:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="itemDimensionMeasure">
			<tr>
				<td><a href="${ctx}/mod/itemDimensionMeasure/form?id=${itemDimensionMeasure.id}">
					${itemDimensionMeasure.name}
				</a></td>	
				<td>
					${itemDimensionMeasure.category.name}
				</td>				
				<td>
					${itemDimensionMeasure.dimension.name}
				</td>		
				<td>
					${itemDimensionMeasure.measure.name}
				</td>	
				<td>
					${itemDimensionMeasure.weight}
				</td>				
				<td>
					${itemDimensionMeasure.description}
				</td>		
				<td>
					${itemDimensionMeasure.sort}
				</td>														
				<td>
					<fmt:formatDate value="${itemDimensionMeasure.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:itemDimensionMeasure:edit"><td>
    				<a href="${ctx}/mod/itemDimensionMeasure/form?id=${itemDimensionMeasure.id}">修改</a>
					<a href="${ctx}/mod/itemDimensionMeasure/delete?id=${itemDimensionMeasure.id}" onclick="return confirmx('确认要删除该客观评价明细吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>