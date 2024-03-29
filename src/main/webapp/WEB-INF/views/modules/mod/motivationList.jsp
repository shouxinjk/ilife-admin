<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>内部动机管理</title>
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
		<li class="active"><a href="${ctx}/mod/motivation/?treeId=${pid}&treeModule=${pType}&topId${topId }">内部动机列表</a></li>
		<shiro:hasPermission name="mod:motivation:edit"><li><a href="${ctx}/mod/motivation/form?pid=${pid}&pType=${pType}&topId=${topId }">内部动机添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="motivation" action="${ctx}/mod/motivation/?treeId=${pid}&treeModule=${pType}&topId${topId }" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>别名：</label>
				<form:input path="displayName" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>			
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>类别</th>
				<th>类型</th>
				<th>名称</th>
				<th>别名</th>				
				<!--th>阶段</th>
				<th>比重</th-->
				<th>触发条件</th>
				<th>触发条件量化</th>
				<!--th>持续时间</th-->
				<!--th>商品类目</th-->
				<shiro:hasPermission name="mod:motivation:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="motivation">
			<tr>		
				<td width="120">
					${motivation.motivationCategory.name}
				</td>			
				<td width="80">
			      <c:choose>
			         <c:when test = "${motivation.type == 'alpha'}">
			            <span style="background-color:#A49EE2;padding:2px 5px;color:#fff;">生存需要</span>
			         </c:when>			
			         <c:when test = "${motivation.type == 'beta'}">
			            <span style="background-color:#40B4E7;padding:2px 5px;color:#fff;">安全需要</span>
			         </c:when>	
			         <c:when test = "${motivation.type == 'gamma'}">
			            <span style="background-color:#8BCE2D;padding:2px 5px;color:#fff;">爱与归属</span>
			         </c:when>	
			         <c:when test = "${motivation.type == 'delte'}">
			            <span style="background-color:#F6B100;padding:2px 5px;color:#fff;">尊重需要</span>
			         </c:when>	
			         <c:when test = "${motivation.type == 'epsilon'}">
			            <span style="background-color:#E85552;padding:2px 5px;color:#fff;">自我实现</span>
			         </c:when>	
			         			         			         			         			         
			         <c:otherwise>
			            <span style="background-color:#000;padding:2px 5px;color:#fff;">未知</span>
			         </c:otherwise>
			      </c:choose>					
				</td>	
				<td>
					${motivation.name}
				</td>
				<td>
					${motivation.displayName}
				</td>					
				<!--td>
					${motivation.phase.name}
				</td>
				<td>
					${motivation.percentage}
				</td-->
				<td>
					${motivation.exprTrigger}
				</td>
				<td>
					${motivation.expression}
				</td>
				<!--td>
					${motivation.exprDuration}
				</td-->
				<!--td>
					${motivation.exprItem}
				</td-->
				<shiro:hasPermission name="mod:motivation:edit"><td>
    				<a href="${ctx}/mod/motivation/form?id=${motivation.id}&pid=${pid}&pType=${pType}&topId=${topId }">修改</a>
					<a href="${ctx}/mod/motivation/delete?id=${motivation.id}&pid=${pid}&pType=${pType}&topId=${topId }" onclick="return confirmx('确认要删除该内部动机吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>