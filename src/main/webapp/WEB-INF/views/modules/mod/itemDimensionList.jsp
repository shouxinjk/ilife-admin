<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>维度管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		<%--$(document).ready(function() {--%>
			<%--var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");--%>
			<%--var data = ${fns:toJson(list)}, ids = [], rootIds = [];--%>
			<%--for (var i=0; i<data.length; i++){--%>
				<%--ids.push(data[i].id);--%>
			<%--}--%>
			<%--ids = ',' + ids.join(',') + ',';--%>
			<%--for (var i=0; i<data.length; i++){--%>
				<%--if (ids.indexOf(','+data[i].parentId+',') == -1){--%>
					<%--if ((','+rootIds.join(',')+',').indexOf(','+data[i].parentId+',') == -1){--%>
						<%--rootIds.push(data[i].parentId);--%>
					<%--}--%>
				<%--}--%>
			<%--}--%>
			<%--for (var i=0; i<rootIds.length; i++){--%>
				<%--addRow("#treeTableList", tpl, data, rootIds[i], true);--%>
			<%--}--%>
			<%--$("#treeTable").treeTable({expandLevel : 5});--%>
		<%--});--%>
		<%--function addRow(list, tpl, data, pid, root){--%>
			<%--for (var i=0; i<data.length; i++){--%>
				<%--var row = data[i];--%>
				<%--if ((${fns:jsGetVal('row.parentId')}) == pid){--%>
					<%--$(list).append(Mustache.render(tpl, {--%>
						<%--dict: {--%>
						<%--blank123:0}, pid: (root?0:pid), row: row--%>
					<%--}));--%>
					<%--addRow(list, tpl, data, row.id);--%>
				<%--}--%>
			<%--}--%>
		<%--}--%>
        $(document).ready(function() {
            $("#treeTable").treeTable({expandLevel : 3});
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
		<li class="active"><a href="${ctx}/mod/itemDimension/">维度列表</a></li>
		<shiro:hasPermission name="mod:itemDimension:edit"><li><a href="${ctx}/mod/itemDimension/form">维度添加</a></li></shiro:hasPermission>
	</ul>
	<%--<form:form id="searchForm" modelAttribute="itemDimension" action="${ctx}/mod/itemDimension/" method="post" class="breadcrumb form-search">--%>
		<%--<ul class="ul-form">--%>
			<%--<li><label>名称：</label>--%>
				<%--<form:input path="name" htmlEscape="false" maxlength="20" class="input-medium"/>--%>
			<%--</li>--%>
			<%--<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>--%>
			<%--<li class="clearfix"></li>--%>
		<%--</ul>--%>
	<%--</form:form>--%>
	<sys:message content="${message}"/>
	<form id="listForm" method="post">
	<table id="treeTable" class="table table-striped table-bordered table-condensed">

			<tr>
				<th>名称</th>
				<th>占比</th>
                <th style="text-align:center;">排序</th>
				<shiro:hasPermission name="mod:itemDimension:edit"><th>操作</th></shiro:hasPermission>
			</tr>


		<c:forEach items="${list}" var="row">
			<tr id="{row.id}" pId="${row.parent.id ne '1'?row.parent.id:'0'}">
				<td><a href="${ctx}/mod/itemDimension/form?id=${row.id}">
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
				<shiro:hasPermission name="mod:itemDimension:edit"><td>
					<a href="${ctx}/mod/itemDimension/form?id=${row.id}">修改</a>
					<a href="${ctx}/mod/itemDimension/delete?id=${row.id}" onclick="return confirmx('确认要删除该维度及所有子维度吗？', this.href)">删除</a>
					<a href="${ctx}/mod/itemDimension/form?parent.id=${row.id}">添加下级维度</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>

	</table>
	<shiro:hasPermission name="mod:itemCategory:edit"><div class="form-actions pagination-left">
		<input id="btnSubmit" class="btn btn-primary" type="button" value="保存排序" onclick="updateSort();"/>
	</div></shiro:hasPermission>
	</form>
	<script type="text/template" id="treeTableTpl">
		<%--<tr id="{{row.id}}" pId="${row.parent.id ne '1'?row.parent.id:'0'}">--%>
			<%--<td><a href="${ctx}/mod/itemDimension/form?id={{row.id}}">--%>
				<%--{{row.name}}--%>
			<%--</a></td>--%>
			<%--<td>--%>
				<%--{{row.weight}}--%>
			<%--</td>--%>
            <%--<td style="text-align:center;">--%>
                <%--<shiro:hasPermission name="mod:itemCategory:edit">--%>
                    <%--<input type="hidden" name="ids" value="${row.id}"/>--%>
                    <%--<input name="sorts" type="text" value="${row.sort}" style="width:50px;margin:0;padding:0;text-align:center;">--%>
                <%--</shiro:hasPermission><shiro:lacksPermission name="mod:itemCategory:edit">--%>
                <%--${row.sort}--%>
            <%--</shiro:lacksPermission>--%>
            <%--</td>--%>
			<%--<shiro:hasPermission name="mod:itemDimension:edit"><td>--%>
   				<%--<a href="${ctx}/mod/itemDimension/form?id={{row.id}}">修改</a>--%>
				<%--<a href="${ctx}/mod/itemDimension/delete?id={{row.id}}" onclick="return confirmx('确认要删除该维度及所有子维度吗？', this.href)">删除</a>--%>
				<%--<a href="${ctx}/mod/itemDimension/form?parent.id={{row.id}}">添加下级维度</a> --%>
			<%--</td></shiro:hasPermission>--%>
		<%--</tr>--%>
	</script>
</body>
</html>