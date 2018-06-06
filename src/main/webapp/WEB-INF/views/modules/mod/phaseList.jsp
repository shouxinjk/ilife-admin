<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>人生阶段管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#treeTable").treeTable({expandLevel : 3});
		});
	
		
		function updateSort() {
			loading('正在提交，请稍等...');
	    	$("#listForm").attr("action", "${ctx}/mod/phase/updateSort");
	    	$("#listForm").submit();
    	}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mod/phase/">人生阶段列表</a></li>
		<shiro:hasPermission name="mod:phase:edit"><li><a href="${ctx}/mod/phase/form">人生阶段添加</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
	<form id="listForm" method="post">
		<table id="treeTable" class="table table-striped table-bordered table-condensed">
			<tr><th>名称</th><th>判定条件</th><th>生理</th><th>安全</th><th>社交</th><th>尊重</th><th>价值</th><th style="text-align:center;">排序</th><th>匹配用户</th><th>操作</th></tr>
			<c:forEach items="${list}" var="tpl">
				<tr id="${tpl.id}" pId="${tpl.parent.id ne '1'?tpl.parent.id:'0'}">
					<td><a href="${ctx}/mod/phase/form?id=${tpl.id}">${tpl.name}</a></td>
				<td>
 					${tpl.expression}
 				</td>
				<td>
					${tpl.alpha}
				</td>
				<td>
					${tpl.beta}
				</td>
				<td>
					${tpl.gamma}
				</td>
				<td>
					${tpl.delte}
				</td>
				<td>
					${tpl.epsilon}
				</td>
				<td style="text-align:center;">
					<shiro:hasPermission name="mod:phase:edit">
						<input type="hidden" name="ids" value="${tpl.id}"/>
						<input name="sorts" type="text" value="${tpl.sort}" style="width:50px;margin:0;padding:0;text-align:center;">
					</shiro:hasPermission><shiro:lacksPermission name="mod:phase:edit">
						${tpl.sort}
					</shiro:lacksPermission>
				</td>
				<td>0</td>
				<td>
					<shiro:hasPermission name="mod:phase:edit">
						<a href="${ctx}/mod/phase/form?id=${tpl.id}">修改</a>
						<a href="${ctx}/mod/phase/delete?id=${tpl.id}" onclick="return confirmx('要删除该阶段及所有子阶段吗？', this.href)">删除</a>
						<a href="${ctx}/mod/phase/form?parent.id=${tpl.id}">添加下级栏目</a>
					</shiro:hasPermission>
				</td>
			</tr>
			</c:forEach>
		</table>
		<shiro:hasPermission name="mod:phase:edit"><div class="form-actions pagination-left">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="保存排序" onclick="updateSort();"/>
		</div></shiro:hasPermission>
	</form>
<!-- 	<table id="contentTable" class="table table-striped table-bordered table-condensed"> -->
<!-- 		<thead> -->
<!-- 			<tr> -->
<!-- 				<th>name</th> -->
<!-- 				<th>expression</th> -->
<!-- 				<th>alpha</th> -->
<!-- 				<th>beta</th> -->
<!-- 				<th>gamma</th> -->
<!-- 				<th>delte</th> -->
<!-- 				<th>epsilon</th> -->
<!-- 				<th>更新者</th> -->
<!-- 				<th>更新时间</th> -->
<%-- 				<shiro:hasPermission name="mod:phase:edit"><th>操作</th></shiro:hasPermission> --%>
<!-- 			</tr> -->
<!-- 		</thead> -->
<!-- 		<tbody> -->
<%-- 		<c:forEach items="${page.list}" var="phase"> --%>
<!-- 			<tr> -->
<%-- 				<td><a href="${ctx}/mod/phase/form?id=${phase.id}"> --%>
<%-- 					${phase.name} --%>
<!-- 				</a></td> -->
<!-- 				<td> -->
<%-- 					${phase.expression} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					${phase.alpha} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					${phase.beta} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					${phase.gamma} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					${phase.delte} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					${phase.epsilon} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					${phase.updateBy.id} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					<fmt:formatDate value="${phase.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/> --%>
<!-- 				</td> -->
<%-- 				<shiro:hasPermission name="mod:phase:edit"><td> --%>
<%--     				<a href="${ctx}/mod/phase/form?id=${phase.id}">修改</a> --%>
<%-- 					<a href="${ctx}/mod/phase/delete?id=${phase.id}" onclick="return confirmx('确认要删除该人生阶段吗？', this.href)">删除</a> --%>
<%-- 				</td></shiro:hasPermission> --%>
<!-- 			</tr> -->
<%-- 		</c:forEach> --%>
<!-- 		</tbody> -->
<!-- 	</table> -->
<%-- 	<div class="pagination">${page}</div> --%>
</body>
</html>