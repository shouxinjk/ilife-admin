<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>VALS模型管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
	$(document).ready(function() {
		$("#treeTable").treeTable({expandLevel : 3});
	});

	
	function updateSort() {
		loading('正在提交，请稍等...');
    	$("#listForm").attr("action", "${ctx}/mod/lifeStyle/updateSort");
    	$("#listForm").submit();
	}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mod/lifeStyle/">VALS模型列表</a></li>
		<shiro:hasPermission name="mod:lifeStyle:edit"><li><a href="${ctx}/mod/lifeStyle/form">VALS模型添加</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
	<form id="listForm" method="post">
		<table id="treeTable" class="table table-striped table-bordered table-condensed">
			<tr>
				<th>名称</th>
				<th>阶段</th>
				<th>层级</th>
				<th>画像</th>
				<th style="text-align:center;">排序</th>
				<th>操作</th>
			</tr>
			<c:forEach items="${list}" var="tpl">
				<tr id="${tpl.id}" pId="${tpl.parent.id ne '1'?tpl.parent.id:'0'}">
					<td><a href="${ctx}/mod/lifeStyle/form?id=${tpl.id}">
						${tpl.name}
					</a></td>
					<td>
						${tpl.phase.name}
					</td>
					<td>
						${tpl.hierarchy.name}
					</td>
					<td>
						${tpl.persona.name}
					</td>
					<td style="text-align:center;">
						<shiro:hasPermission name="mod:lifeStyle:edit">
							<input type="hidden" name="ids" value="${tpl.id}"/>
							<input name="sorts" type="text" value="${tpl.sort}" style="width:50px;margin:0;padding:0;text-align:center;">
						</shiro:hasPermission><shiro:lacksPermission name="mod:lifeStyle:edit">
							${tpl.sort}
						</shiro:lacksPermission>
					</td>
					<td>
						<shiro:hasPermission name="mod:lifeStyle:edit">
							<a href="${ctx}/mod/lifeStyle/form?id=${tpl.id}">修改</a>
							<a href="${ctx}/mod/lifeStyle/delete?id=${tpl.id}" onclick="return confirmx('要删除该阶段及所有子模型吗？', this.href)">删除</a>
							<a href="${ctx}/mod/lifeStyle/form?parent.id=${tpl.id}">添加下级模型</a>
						</shiro:hasPermission>
					</td>
				</tr>
			</c:forEach>
		</table>
		<shiro:hasPermission name="mod:lifeStyle:edit"><div class="form-actions pagination-left">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="保存排序" onclick="updateSort();"/>
		</div></shiro:hasPermission>
	</form>
<!-- 	<table id="contentTable" class="table table-striped table-bordered table-condensed"> -->
<!-- 		<thead> -->
<!-- 			<tr> -->
<!-- 				<th>名称</th> -->
<!-- 				<th>阶段</th> -->
<!-- 				<th>层级</th> -->
<!-- 				<th>画像</th> -->
<!-- 				<th>更新时间</th> -->
<%-- 				<shiro:hasPermission name="mod:lifeStyle:edit"><th>操作</th></shiro:hasPermission> --%>
<!-- 			</tr> -->
<!-- 		</thead> -->
<!-- 		<tbody> -->
<%-- 		<c:forEach items="${page.list}" var="lifeStyle"> --%>
<!-- 			<tr> -->
<%-- 				<td><a href="${ctx}/mod/lifeStyle/form?id=${lifeStyle.id}"> --%>
<%-- 					${lifeStyle.name} --%>
<!-- 				</a></td> -->
<!-- 				<td> -->
<%-- 					${lifeStyle.phase.name} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					${lifeStyle.hierarchy.name} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					${lifeStyle.persona.name} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					<fmt:formatDate value="${lifeStyle.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/> --%>
<!-- 				</td> -->
<%-- 				<shiro:hasPermission name="mod:lifeStyle:edit"><td> --%>
<%--     				<a href="${ctx}/mod/lifeStyle/form?id=${lifeStyle.id}">修改</a> --%>
<%-- 					<a href="${ctx}/mod/lifeStyle/delete?id=${lifeStyle.id}" onclick="return confirmx('确认要删除该VALS模型吗？', this.href)">删除</a> --%>
<%-- 				</td></shiro:hasPermission> --%>
<!-- 			</tr> -->
<%-- 		</c:forEach> --%>
<!-- 		</tbody> -->
<!-- 	</table> -->
<%-- 	<div class="pagination">${page}</div> --%>
</body>
</html>