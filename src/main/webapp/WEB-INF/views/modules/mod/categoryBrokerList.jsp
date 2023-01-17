<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>类目专家授权管理</title>
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
		<li class="active"><a href="${ctx}/mod/categoryBroker/">类目专家授权列表</a></li>
		<shiro:hasPermission name="mod:categoryBroker:edit"><li><a href="${ctx}/mod/categoryBroker/form">类目专家授权添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="categoryBroker" action="${ctx}/mod/categoryBroker/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>类目：</label>
				<form:input path="category.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>达人：</label>
				<form:input path="broker.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>授权类型：</label>
				<form:input path="badge.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>授权类型</th>
				<th>达人/专家</th>
				<th>关联类目</th>
				<th>姓名</th>
				<th>公司/院校</th>
				<th>身份/职位</th>
				<th>状态</th>							
				<th>更新时间</th>
				<shiro:hasPermission name="mod:categoryBroker:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="categoryBroker">
			<tr>
				<td>
					${categoryBroker.badge.name}
				</td>		
				<td>
					${categoryBroker.broker.nickname}
				</td>					
				<td>
					${categoryBroker.category.name}
				</td>
				<td>
					${categoryBroker.name}
				</td>
				<td>
					${categoryBroker.company}
				</td>
				<td>
					${categoryBroker.job}
				</td>
				<td>
					${categoryBroker.status}
				</td>				
				<td>
					<fmt:formatDate value="${categoryBroker.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:categoryBroker:edit"><td>
    				<a href="${ctx}/mod/categoryBroker/form?id=${categoryBroker.id}">修改</a>
					<a href="${ctx}/mod/categoryBroker/delete?id=${categoryBroker.id}" onclick="return confirmx('确认要删除该类目专家授权吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>