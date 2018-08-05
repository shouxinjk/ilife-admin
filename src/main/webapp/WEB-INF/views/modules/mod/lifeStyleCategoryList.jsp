<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>vals分类管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
			var data = ${fns:toJson(list)}, ids = [], rootIds = [];
			for (var i=0; i<data.length; i++){
				ids.push(data[i].id);
			}
			ids = ',' + ids.join(',') + ',';
			for (var i=0; i<data.length; i++){
				if (ids.indexOf(','+data[i].parentId+',') == -1){
					if ((','+rootIds.join(',')+',').indexOf(','+data[i].parentId+',') == -1){
						rootIds.push(data[i].parentId);
					}
				}
			}
			for (var i=0; i<rootIds.length; i++){
				addRow("#treeTableList", tpl, data, rootIds[i], true);
			}
			$("#treeTable").treeTable({expandLevel : 5});
		});
		function addRow(list, tpl, data, pid, root){
			for (var i=0; i<data.length; i++){
				var row = data[i];
				if ((${fns:jsGetVal('row.parentId')}) == pid){
					$(list).append(Mustache.render(tpl, {
						dict: {
						blank123:0}, pid: (root?0:pid), row: row
					}));
					addRow(list, tpl, data, row.id);
				}
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mod/lifeStyleCategory/">vals分类列表</a></li>
		<shiro:hasPermission name="mod:lifeStyleCategory:edit"><li><a href="${ctx}/mod/lifeStyleCategory/form">vals分类添加</a></li></shiro:hasPermission>
	</ul>

	<sys:message content="${message}"/>
	<table id="treeTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>标题</th>
				<shiro:hasPermission name="mod:lifeStyleCategory:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody id="treeTableList"></tbody>
	</table>
	<script type="text/template" id="treeTableTpl">
		<tr id="{{row.id}}" pId="{{pid}}">
			<td><a href="${ctx}/mod/lifeStyleCategory/form?id={{row.id}}">
				{{row.name}}
			</a></td>
			<shiro:hasPermission name="mod:lifeStyleCategory:edit"><td>
   				<a href="${ctx}/mod/lifeStyleCategory/form?id={{row.id}}">修改</a>
				<a href="${ctx}/mod/lifeStyleCategory/delete?id={{row.id}}" onclick="return confirmx('确认要删除该vals分类及所有子vals分类吗？', this.href)">删除</a>
				<a href="${ctx}/mod/lifeStyleCategory/form?parent.id={{row.id}}">添加下级vals分类</a> 
			</td></shiro:hasPermission>
		</tr>
	</script>
</body>
</html>