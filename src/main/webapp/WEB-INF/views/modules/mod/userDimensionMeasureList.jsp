<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户客观评价-属性管理</title>
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
		<li><a href="${ctx}/mod/userDimension/list">客观评价列表</a></li>
		<shiro:hasPermission name="mod:userDimension:edit"><li><a href="${ctx}/mod/userDimension/form">客观评价添加</a></li></shiro:hasPermission>
		<li class="active"><a href="${ctx}/mod/userDimensionMeasure/list?dimensionId=${dimensionId}">评价明细列表</a></li>
		<shiro:hasPermission name="mod:userDimensionMeasure:edit"><li><a href="${ctx}/mod/userDimensionMeasure/form?dimension.id=${dimensionId}">评价明细添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="userDimensionMeasure" action="${ctx}/mod/userDimensionMeasure/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>name：</label>
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
				<th>客观评价</th>
				<th>关联属性</th>
				<th>占比</th>
				<th>描述</th>
				<th>排序</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:userDimensionMeasure:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userDimensionMeasure">
			<tr>
				<td><a href="${ctx}/mod/userDimensionMeasure/form?id=${userDimensionMeasure.id}">
					${userDimensionMeasure.name}
				</a></td>	
				<td>
					${userDimensionMeasure.category}
				</td>				
				<td>
					${userDimensionMeasure.dimension.name}
				</td>		
				<td>
					${userDimensionMeasure.measure.name}
				</td>	
				<td>
					${userDimensionMeasure.weight}
				</td>				
				<td>
					${userDimensionMeasure.description}
				</td>		
				<td>
					${userDimensionMeasure.sort}
				</td>	
				<td>
					<fmt:formatDate value="${userDimensionMeasure.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:userDimensionMeasure:edit"><td>
    				<a href="${ctx}/mod/userDimensionMeasure/form?id=${userDimensionMeasure.id}">修改</a>
					<a href="${ctx}/mod/userDimensionMeasure/delete?id=${userDimensionMeasure.id}" onclick="return confirmx('确认要删除该用户客观评价-属性吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>