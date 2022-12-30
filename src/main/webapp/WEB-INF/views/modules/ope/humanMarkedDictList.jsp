<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户标注字典管理</title>
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
		<li class="active"><a href="${ctx}/ope/humanMarkedDict/">用户标注字典列表</a></li>
		<shiro:hasPermission name="ope:humanMarkedDict:edit"><li><a href="${ctx}/ope/humanMarkedDict/form">用户标注字典添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="humanMarkedDict" action="${ctx}/ope/humanMarkedDict/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>所属字典：</label>
				<form:input path="dictMeta.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>所属类目：</label>
				<form:input path="category.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>字典值ID：</label>
				<form:input path="dictValue.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>原始值：</label>
				<form:input path="originalValue" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>标注者昵称：</label>
				<form:input path="nickname" htmlEscape="false" maxlength="512" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>所属字典</th>
				<th>所属类目</th>
				<th>字典值</th>
				<th>原始值</th>
				<th>score</th>
				<th>标注者openid</th>
				<th>标注者昵称</th>
				<th>更新时间</th>
				<shiro:hasPermission name="ope:humanMarkedDict:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="humanMarkedDict">
			<tr>
				<td><a href="${ctx}/ope/humanMarkedDict/form?id=${humanMarkedDict.id}">
					${humanMarkedDict.dictMeta.name}
				</a></td>
				<td>
					${humanMarkedDict.category.name}
				</td>
				<td>
					${humanMarkedDict.dictValue.originalValue}
				</td>
				<td>
					${humanMarkedDict.originalValue}
				</td>
				<td>
					${humanMarkedDict.score}
				</td>
				<td>
					${humanMarkedDict.openid}
				</td>
				<td>
					${humanMarkedDict.nickname}
				</td>
				<td>
					<fmt:formatDate value="${humanMarkedDict.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="ope:humanMarkedDict:edit"><td>
    				<a href="${ctx}/ope/humanMarkedDict/form?id=${humanMarkedDict.id}">修改</a>
					<a href="${ctx}/ope/humanMarkedDict/delete?id=${humanMarkedDict.id}" onclick="return confirmx('确认要删除该用户标注字典吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>