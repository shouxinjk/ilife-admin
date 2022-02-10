<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户客观评价管理</title>
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
		<li class="active"><a href="${ctx}/mod/userDimension/">用户客观评价列表</a></li>
		<shiro:hasPermission name="mod:userDimension:edit"><li><a href="${ctx}/mod/userDimension/form">用户客观评价添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="userDimension" action="${ctx}/mod/userDimension/" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			<li><label>name：</label>
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="treeTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>名称</th>
				<th>Key</th>
				<th>类型</th>
				<th>特征</th>
				<th>占比</th>
				<th>脚本</th>
				<th>分类</th>
				<!--th>排序</th-->
				<th>更新时间</th>
				<shiro:hasPermission name="mod:userDimension:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody id="treeTableList"></tbody>
	</table>
	<script type="text/template" id="treeTableTpl">
		<tr id="{{row.id}}" pId="{{pid}}">
			<td><a href="${ctx}/mod/userDimensionMeasure/list?dimensionId={{row.id}}&dimension.id={{row.id}}">
				{{row.name}}
			</a></td>
			<td>
				{{row.propKey}}
			</td>
			<td>
				{{row.type}}
			</td>
			<td>
				{{row.featured}}
			</td>
			<td>
				{{row.weight}}
			</td>
			<td>
				{{row.script}}
			</td>
			<td>
				{{row.category}}
			</td>
			<!--td>
				{{row.sort}}
			</td-->
			<td>
				{{row.updateDate}}
			</td>
			<shiro:hasPermission name="mod:userDimension:edit"><td>
   				<a href="${ctx}/mod/userDimension/form?id={{row.id}}">修改</a>
				<a href="${ctx}/mod/userDimension/delete?id={{row.id}}" onclick="return confirmx('确认要删除该用户客观评价及所有子用户客观评价吗？', this.href)">删除</a>
				<a href="${ctx}/mod/userDimension/form?parent.id={{row.id}}">添加下级</a> 
			</td></shiro:hasPermission>
		</tr>
	</script>
</body>
</html>