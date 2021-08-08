<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品分类管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#treeTable").treeTable({expandLevel : 3});
		});
	
		
		function updateSort() {
			loading('正在提交，请稍等...');
	    	$("#listForm").attr("action", "${ctx}/mod/itemCategory/updateSort");
	    	$("#listForm").submit();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mod/itemCategory/">商品分类列表</a></li>
		<shiro:hasPermission name="mod:itemCategory:edit"><li><a href="${ctx}/mod/itemCategory/form">商品分类添加</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
	<form id="listForm" method="post">
		<table id="treeTable" class="table table-striped table-bordered table-condensed">
			<tr>
				<th>名称</th>
				<th>标签</th>
				<th>满足动机</th>
				<th>满足诱因</th>
				<th>适用条件</th>
				<th>适用条件量化</th>
				<th>摘要模版</th>
				<th>场景标签</th>
				<th style="text-align:center;">排序</th>
				<th>商品数量</th>
				<th>操作</th>
			</tr>
			<c:forEach items="${list}" var="tpl">
				<tr id="${tpl.id}" pId="${tpl.parent.id ne '1'?tpl.parent.id:'0'}">
					<td><a href="${ctx}/mod/itemCategory/form?id=${tpl.id}">
						${tpl.name}
					</a></td>
					<td>
						${tpl.tags}
					</td>					
					<td>
						${tpl.motivationNames}
					</td>
					<td>
						${tpl.occasionNames}
					</td>
					<td>
						${tpl.expressionDesc}
					</td>
					<td>
						${tpl.expression}
					</td>
					<td>
						${tpl.outlineTemplate}
					</td>
					<td>
						${tpl.scenarioId}
					</td>
					<td style="text-align:center;">
							<shiro:hasPermission name="mod:itemCategory:edit">
								<input type="hidden" name="ids" value="${tpl.id}"/>
								<input name="sorts" type="text" value="${tpl.sort}" style="width:50px;margin:0;padding:0;text-align:center;">
							</shiro:hasPermission><shiro:lacksPermission name="mod:itemCategory:edit">
								${tpl.sort}
							</shiro:lacksPermission>
					</td>
					<td>0</td>
					<td>
						<shiro:hasPermission name="mod:itemCategory:edit">
							<a href="${ctx}/mod/itemCategory/form?id=${tpl.id}">修改</a>
							<a href="${ctx}/mod/itemCategory/delete?id=${tpl.id}" onclick="return confirmx('要删除该分类及所有子分类项吗？', this.href)">删除</a>
							<a href="${ctx}/mod/itemCategory/form?parent.id=${tpl.id}">添加下级分类</a>
						</shiro:hasPermission>
					</td>
				</tr>
			</c:forEach>
		</table>
		<shiro:hasPermission name="mod:itemCategory:edit"><div class="form-actions pagination-left">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="保存排序" onclick="updateSort();"/>
		</div></shiro:hasPermission>
	</form>
<!-- 	<table id="contentTable" class="table table-striped table-bordered table-condensed"> -->
<!-- 		<thead> -->
<!-- 			<tr> -->
<!-- 				<th>名称</th> -->
<!-- 				<th>满足动机</th> -->
<!-- 				<th>满足诱因</th> -->
<!-- 				<th>适用条件</th> -->
<!-- 				<th>适用条件量化</th> -->
<!-- 				<th>摘要模版</th> -->
<!-- 				<th>场景标签</th> -->
<!-- 				<th>更新时间</th> -->
<%-- 				<shiro:hasPermission name="mod:itemCategory:edit"><th>操作</th></shiro:hasPermission> --%>
<!-- 			</tr> -->
<!-- 		</thead> -->
<!-- 		<tbody> -->
<%-- 		<c:forEach items="${page.list}" var="itemCategory"> --%>
<!-- 			<tr> -->
<%-- 				<td><a href="${ctx}/mod/itemCategory/form?id=${itemCategory.id}"> --%>
<%-- 					${itemCategory.name} --%>
<!-- 				</a></td> -->
<!-- 				<td> -->
<%-- 					${itemCategory.motivationId} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					${itemCategory.occasionId} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					${itemCategory.expressionDesc} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					${itemCategory.expression} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					${itemCategory.outlineTemplate} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					${itemCategory.scenarioId} --%>
<!-- 				</td> -->
<!-- 				<td> -->
<%-- 					<fmt:formatDate value="${itemCategory.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/> --%>
<!-- 				</td> -->
<%-- 				<shiro:hasPermission name="mod:itemCategory:edit"><td> --%>
<%--     				<a href="${ctx}/mod/itemCategory/form?id=${itemCategory.id}">修改</a> --%>
<%-- 					<a href="${ctx}/mod/itemCategory/delete?id=${itemCategory.id}" onclick="return confirmx('确认要删除该商品分类吗？', this.href)">删除</a> --%>
<%-- 				</td></shiro:hasPermission> --%>
<!-- 			</tr> -->
<%-- 		</c:forEach> --%>
<!-- 		</tbody> -->
<!-- 	</table> -->
<%-- 	<div class="pagination">${page}</div> --%>
</body>
</html>