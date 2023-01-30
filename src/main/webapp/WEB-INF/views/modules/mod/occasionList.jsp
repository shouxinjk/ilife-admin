<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>外部诱因管理</title>
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
		<li class="active"><a href="${ctx}/mod/occasion/?treeId=${treeId}">外部诱因列表</a></li>
		<shiro:hasPermission name="mod:occasion:edit"><li><a href="${ctx}/mod/occasion/form?occasionCategory.id=${treeId}">外部诱因添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="occasion" action="${ctx}/mod/occasion/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>分类</th>
				<th>名称</th>
				<th>内在/外在</th>
				<th>主动/被动</th>
				<th>商品类目</th>
				<!--th>匹配用户</th>
				<th>匹配商品类别</th-->
				<th>期望效果</th>
				<th>开始时间</th>
				<th>结束时间</th>
				<shiro:hasPermission name="mod:occasion:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="occasion">
			<tr>
				<td>
					${occasion.occasionCategory.name}
				</td>			
				<td>
					${occasion.name}
				</td>
				<td>
					${fns:getDictLabel(occasion.triggerDirection, 'insideOrOutside', '-')}
				</td>
				<td>
					${fns:getDictLabel(occasion.triggerType, 'activeOrPassive', '-')}
				</td>
				<!--td>
					${occasion.exprUser}
				</td>
				<td>
					${occasion.exprItem}
				</td-->
				<td>
					${occasion.category.name}
				</td>				
				<td>
					${occasion.exprTrigger}
				</td>
				<td>
					${occasion.expression}
				</td>
				<td>
					${occasion.exprDuration}
				</td>
				<shiro:hasPermission name="mod:occasion:edit"><td>
    				<a href="${ctx}/mod/occasion/form?id=${occasion.id}">修改</a>
					<a href="${ctx}/mod/occasion/delete?id=${occasion.id}&treeId=${treeId}" onclick="return confirmx('确认要删除该外部诱因吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>