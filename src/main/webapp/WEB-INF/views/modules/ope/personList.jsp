<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户管理</title>
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
		<li class="active"><a href="${ctx}/ope/person/">用户列表</a></li>
		<shiro:hasPermission name="ope:person:edit"><li><a href="${ctx}/ope/person/form">用户添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="person" action="${ctx}/ope/person/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>昵称：</label>
				<form:input path="nickname" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>昵称</th>
				<th>vals模型</th>
				<th>所属阶段</th>
				<th>所属阶层</th>
				<th>用户分群</th>
				<th>生活方式</th>
				<th>最后访问时间</th>
				<shiro:hasPermission name="ope:person:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="person">
			<tr>
				<td><a href="${ctx}/ope/person/form?id=${person.id}">
					${person.nickname}
				</a></td>
				<td>
					${person.functionality}
				</td>
				
				<td>
					${person.phase.name}
				</td>
				<td>
					${person.hierarchy.name}
				</td>
				<td>
					${person.persona.name}
				</td>
				<td>
					${person.lifeStyle.name}
				</td>
				<td>
					<fmt:formatDate value="${person.lastAccess}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
				<shiro:hasPermission name="ope:person:edit">
    				<a href="${ctx}/ope/person/form?id=${person.id}">修改</a>
					<a href="${ctx}/ope/person/delete?id=${person.id}" onclick="return confirmx('确认要删除该用户吗？', this.href)">删除</a>
				</shiro:hasPermission>
				<shiro:hasPermission name="mod:personNeed:view">
					<a href="${ctx}/mod/personNeed/?person.id=${person.id}">需要构成</a>
				</shiro:hasPermission>	
				<shiro:hasPermission name="mod:personNeed:edit">
					<a href="${ctx}/mod/personNeed/form?person.id=${person.id}">需要添加</a>
				</shiro:hasPermission>												
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>