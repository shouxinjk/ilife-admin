<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客观评价管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
        $(document).ready(function() {
            $("#treeTableDimension").treeTable({expandLevel : 5});
        });

        function updateSort() {
            loading('正在提交，请稍等...');
            $("#listForm").attr("action", "${ctx}/mod/itemDimension/updateSort");
            $("#listForm").submit();
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mod/itemDimension/list?treeId=${treeId}">客观评价列表</a></li>
		<shiro:hasPermission name="mod:itemDimension:edit"><li><a href="${ctx}/mod/itemDimension/form?category.id=${treeId}">客观评价添加</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
	<form id="listForm" method="post">
	<table id="treeTableDimension" class="table table-striped table-bordered table-condensed">

			<tr>
				<th>名称</th>
				<th>占比</th>
                <th style="text-align:center;">排序</th>
				<shiro:hasPermission name="mod:itemDimension:edit"><th>操作</th></shiro:hasPermission>
			</tr>


		<c:forEach items="${list}" var="row">
			<tr id="${row.id}" pId="${row.parent.id ne '1'?row.parent.id:'0'}">
				<td><a href="${ctx}/mod/itemDimensionMeasure/list?dimensionId=${row.id}&categoryId=${treeId}&dimension.id=${row.id}&category.id=${treeId}">
					${row.name}
				</a></td>
				<td>
					${row.weight}
				</td>
				<td style="text-align:center;">
					<shiro:hasPermission name="mod:itemCategory:edit">
						<input type="hidden" name="ids" value="${row.id}"/>
						<input name="sorts" type="text" value="${row.sort}" style="width:50px;margin:0;padding:0;text-align:center;">
					</shiro:hasPermission><shiro:lacksPermission name="mod:itemCategory:edit">
					${row.sort}
				</shiro:lacksPermission>
				</td>
				<td>
					<shiro:hasPermission name="mod:itemDimension:edit">
						<a href="${ctx}/mod/itemDimension/form?id=${row.id}">修改</a>
						<a href="${ctx}/mod/itemDimension/delete?id=${row.id}" onclick="return confirmx('确认要删除该维度及所有子维度吗？', this.href)">删除</a>
						<a href="${ctx}/mod/itemDimension/form?parent.id=${row.id}&category.id=${row.category.id}">添加下级维度</a>
					</shiro:hasPermission>							
				</td>			
			</tr>
		</c:forEach>

	</table>
	<shiro:hasPermission name="mod:itemCategory:edit"><div class="form-actions pagination-left">
		<input id="btnSubmit" class="btn btn-primary" type="button" value="保存排序" onclick="updateSort();"/>
	</div></shiro:hasPermission>
	</form>
</body>
</html>