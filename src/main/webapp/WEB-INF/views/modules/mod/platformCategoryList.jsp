<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>电商平台类目映射管理</title>
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
		<li class="active"><a href="${ctx}/mod/platformCategory/">类目映射列表</a></li>
		<shiro:hasPermission name="mod:platformCategory:edit"><li><a href="${ctx}/mod/platformCategory/form?platform=${treeId}">类目映射添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="platformCategory" action="${ctx}/mod/platformCategory/" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>原始ID：</label>
				<form:input path="id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>			
			<li><label>来源平台：</label>
				<form:select path="platform" class="input-medium">
					<form:option value="" label="不限"/>
					<form:options items="${fns:getDictList('platform')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>				
			</li>
			<li><label>标准类目：</label>
				<sys:treeselect id="category" name="category.id" value="${platformCategory.category.id}" labelName="category.name" labelValue="${platformCategory.category.name}"
					title="商品分类" url="/mod/itemCategory/treeData" notAllowSelectRoot="true" cssClass="required"/>		
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
				<th>所属平台</th>
				<th>标准类目</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:platformCategory:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody id="treeTableList"></tbody>
	</table>
	<script type="text/template" id="treeTableTpl">
		<tr id="{{row.id}}" pId="{{pid}}">
			<td><a href="${ctx}/mod/platformCategory/form?id={{row.id}}">
				{{row.name}}
			</a></td>
			<td>
				{{row.platform}}
			</td>
			<td>
				{{row.category.name}}
			</td>
			<td>
				{{row.updateDate}}
			</td>
			<shiro:hasPermission name="mod:platformCategory:edit"><td>
   				<a href="${ctx}/mod/platformCategory/form?treeId=${treeId}&id={{row.id}}">修改</a>
				<a href="${ctx}/mod/platformCategory/delete?treeId=${treeId}&id={{row.id}}" onclick="return confirmx('确认要删除该电商平台类目映射及所有子电商平台类目映射吗？', this.href)">删除</a>
				<a href="${ctx}/mod/platformCategory/form?treeId=${treeId}&parent.id={{row.id}}">添加下级节点</a> 
			</td></shiro:hasPermission>
		</tr>
	</script>
</body>
</html>