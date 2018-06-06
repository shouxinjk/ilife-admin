<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>内部动机类别管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#treeTable").treeTable({expandLevel : 3});
		});
		function updateSort() {
			loading('正在提交，请稍等...');
	    	$("#listForm").attr("action", "${ctx}/mod/motivationCategory/updateSort");
	    	$("#listForm").submit();
    	}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mod/motivationCategory/">内部动机类别列表</a></li>
		<shiro:hasPermission name="mod:motivationCategory:edit"><li><a href="${ctx}/mod/motivationCategory/form">内部动机类别添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="motivationCategory" action="${ctx}/mod/motivationCategory/" method="post" class="breadcrumb form-search">
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
	<form id="listForm" method="post">
		<table id="treeTable" class="table table-striped table-bordered table-condensed">
			<tr><th>名称</th><th style="text-align:center;">排序</th><th>操作</th></tr>
			<c:forEach items="${list}" var="tpl">
				<tr id="${tpl.id}" pId="${tpl.parent.id ne '1'?tpl.parent.id:'0'}">
					<td><a href="${ctx}/mod/occasionCategory/form?id=${tpl.id}">${tpl.name}</a></td>
				
					<td style="text-align:center;">
						<shiro:hasPermission name="mod:motivationCategory:edit">
							<input type="hidden" name="ids" value="${tpl.id}"/>
							<input name="sorts" type="text" value="${tpl.sort}" style="width:50px;margin:0;padding:0;text-align:center;">
						</shiro:hasPermission><shiro:lacksPermission name="mod:motivationCategory:edit">
							${tpl.sort}
						</shiro:lacksPermission>
					</td>
					<td>
						<shiro:hasPermission name="mod:motivationCategory:edit">
							<a href="${ctx}/mod/motivationCategory/form?id=${tpl.id}">修改</a>
							<a href="${ctx}/mod/motivationCategory/delete?id=${tpl.id}" onclick="return confirmx('要删除该分类及所有子分类吗？', this.href)">删除</a>
							<a href="${ctx}/mod/motivationCategory/form?parent.id=${tpl.id}">添加下级分类</a>
						</shiro:hasPermission>
					</td>
				</tr>
			</c:forEach>
		</table>
		<shiro:hasPermission name="mod:motivationCategory:edit"><div class="form-actions pagination-left">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="保存排序" onclick="updateSort();"/>
		</div></shiro:hasPermission>
	</form>
<!-- 	<table id="contentTable" class="table table-striped table-bordered table-condensed"> -->
<!-- 		<thead> -->
<!-- 			<tr> -->
<!-- 				<th>父级编号</th> -->
<!-- 				<th>name</th> -->
<!-- 				<th>更新者</th> -->
<!-- 				<th>更新时间</th> -->
<%-- 				<shiro:hasPermission name="mod:motivationCategory:edit"><th>操作</th></shiro:hasPermission> --%>
<!-- 			</tr> -->
<!-- 		</thead> -->
<!-- 		<tbody> -->
<%-- 		<c:forEach items="${page.list}" var="motivationCategory"> --%>
<!-- 			<tr> -->
<%-- 				<td><a href="${ctx}/mod/motivationCategory/form?id=${motivationCategory.id}"> --%>
<%-- 					${motivationCategory.parent.id} --%>
<!-- 				</a></td> -->
<!-- 				<td> -->
<%-- 					${motivationCategory.name} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					${motivationCategory.updateBy.id} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					<fmt:formatDate value="${motivationCategory.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/> --%>
<!-- 				</td> -->
<%-- 				<shiro:hasPermission name="mod:motivationCategory:edit"><td> --%>
<%--     				<a href="${ctx}/mod/motivationCategory/form?id=${motivationCategory.id}">修改</a> --%>
<%-- 					<a href="${ctx}/mod/motivationCategory/delete?id=${motivationCategory.id}" onclick="return confirmx('确认要删除该内部动机类别吗？', this.href)">删除</a> --%>
<%-- 				</td></shiro:hasPermission> --%>
<!-- 			</tr> -->
<%-- 		</c:forEach> --%>
<!-- 		</tbody> -->
<!-- 	</table> -->
<%-- 	<div class="pagination">${page}</div> --%>
</body>
</html>