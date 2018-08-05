<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>标签分类管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
// 		$(document).ready(function() {
// 			var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
// 			var data = ${fns:toJson(list)}, ids = [], rootIds = [];
// 			for (var i=0; i<data.length; i++){
// 				ids.push(data[i].id);
// 			}
// 			ids = ',' + ids.join(',') + ',';
// 			for (var i=0; i<data.length; i++){
// 				if (ids.indexOf(','+data[i].parentId+',') == -1){
// 					if ((','+rootIds.join(',')+',').indexOf(','+data[i].parentId+',') == -1){
// 						rootIds.push(data[i].parentId);
// 					}
// 				}
// 			}
// 			for (var i=0; i<rootIds.length; i++){
// 				addRow("#treeTableList", tpl, data, rootIds[i], true);
// 			}
// 			$("#treeTable").treeTable({expandLevel : 5});
// 		});
// 		function addRow(list, tpl, data, pid, root){
// 			for (var i=0; i<data.length; i++){
// 				var row = data[i];
// 				if ((${fns:jsGetVal('row.parentId')}) == pid){
// 					$(list).append(Mustache.render(tpl, {
// 						dict: {
// 						blank123:0}, pid: (root?0:pid), row: row
// 					}));
// 					addRow(list, tpl, data, row.id);
// 				}
// 			}
// 		}
		$(document).ready(function() {
			$("#treeTable").treeTable({expandLevel : 3});
		});
	
		
		function updateSort() {
			loading('正在提交，请稍等...');
	    	$("#listForm").attr("action", "${ctx}/mod/tagCategory/updateSort");
	    	$("#listForm").submit();
    	}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mod/tagCategory/">标签分类列表</a></li>
		<shiro:hasPermission name="mod:tagCategory:edit"><li><a href="${ctx}/mod/tagCategory/form">标签分类添加</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
	<form id="listForm" method="post">
		<table id="treeTable" class="table table-striped table-bordered table-condensed">
				<tr>
					<th>名称</th>
					<th>是否排斥</th>
					<th style="text-align:center;">排序</th>
					<shiro:hasPermission name="mod:tagCategory:edit"><th>操作</th></shiro:hasPermission>
				</tr>
				<c:forEach items="${list}" var="tpl">
					<tr id="${tpl.id}" pId="${tpl.parent.id ne '1'?tpl.parent.id:'0'}">
						<td><a href="${ctx}/mod/tagCategory/form?id=${tpl.id}">
							${tpl.name}
						</a></td>
						<td>
							${fns:getDictLabel(tpl.isexclusive, 'yes_no', '无')}
						</td>
						<td style="text-align:center;">
							<shiro:hasPermission name="mod:tagCategory:edit">
								<input type="hidden" name="ids" value="${tpl.id}"/>
								<input name="sorts" type="text" value="${tpl.sort}" style="width:50px;margin:0;padding:0;text-align:center;">
							</shiro:hasPermission><shiro:lacksPermission name="mod:phase:edit">
								${tpl.sort}
							</shiro:lacksPermission>
						</td>
						<shiro:hasPermission name="mod:tagCategory:edit"><td>
			   				<a href="${ctx}/mod/tagCategory/form?id=${tpl.id}">修改</a>
							<a href="${ctx}/mod/tagCategory/delete?id=${tpl.id}" onclick="return confirmx('确认要删除该标签分类及所有子标签分类吗？', this.href)">删除</a>
							<a href="${ctx}/mod/tagCategory/form?parent.id=${tpl.id}">添加下级标签分类</a> 
						</td></shiro:hasPermission>
					</tr>
				</c:forEach>
		</table>
		<shiro:hasPermission name="mod:tagCategory:edit"><div class="form-actions pagination-left">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="保存排序" onclick="updateSort();"/>
		</div></shiro:hasPermission>
	</form>
	<script type="text/template" id="treeTableTpl">
		<%--<tr id="{{row.id}}" pId="{{pid}}">--%>
			<%--<td><a href="${ctx}/mod/tagCategory/form?id={{row.id}}">--%>
				<%--{{row.name}}--%>
			<%--</a></td>--%>
			<%--<td>--%>
				<%--${fns:getDictLabel('{{row.isexclusive}}', 'yes_no', '无')}--%>
			<%--</td>--%>
			<%--<td>--%>
				<%--{{row.updateBy.name}} --%>
			<%--</td>--%>
			<%--<td>--%>
				<%--{{row.updateDate}}--%>
			<%--</td>--%>
			<%--<shiro:hasPermission name="mod:tagCategory:edit"><td>--%>
   				<%--<a href="${ctx}/mod/tagCategory/form?id={{row.id}}">修改</a>--%>
				<%--<a href="${ctx}/mod/tagCategory/delete?id={{row.id}}" onclick="return confirmx('确认要删除该标签分类及所有子标签分类吗？', this.href)">删除</a>--%>
				<%--<a href="${ctx}/mod/tagCategory/form?parent.id={{row.id}}">添加下级标签分类</a> --%>
			<%--</td></shiro:hasPermission>--%>
		<%--</tr>--%>
	</script>
</body>
</html>