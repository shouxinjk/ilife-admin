<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>个性定制方案管理</title>
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
		<li class="active"><a href="${ctx}/diy/solution/">个性定制方案列表</a></li>
		<shiro:hasPermission name="diy:solution:edit"><li><a href="${ctx}/diy/solution/form">个性定制方案添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="solution" action="${ctx}/diy/solution/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>所属主题：</label>
				<form:input path="scheme.id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>引用方案：</label>
				<form:input path="refer.id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>目标用户：</label>
				<form:input path="forOpenid" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>定制用户：</label>
				<form:input path="byOpenid" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>状态：</label>
				<form:select path="status" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>名称</th>
				<th>主题</th>
				<th>引用方案</th>
				<th>目标用户</th>
				<th>定制用户</th>
				<th>状态</th>
				<th>描述</th>
				<th>更新时间</th>
				<shiro:hasPermission name="diy:solution:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="solution">
			<tr>
				<td><a href="${ctx}/diy/solutionItem/list?solution.id=${solution.id}">
					${solution.name}
				</a></td>			
				<td>
					${solution.scheme.name}
				</td>
				<td>
					${solution.refer.name}
				</td>
				<td>
					${solution.forNickname}
				</td>
				<td>
					${solution.byNickname}
				</td>
				<td>
					${fns:getDictLabel(solution.status, 'yes_no', '')}
				</td>
				<td>
					${solution.description}
				</td>
				<td>
					<fmt:formatDate value="${solution.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="diy:solution:edit"><td>
    				<a href="${ctx}/diy/solution/form?id=${solution.id}">修改</a>
					<a href="${ctx}/diy/solution/delete?id=${solution.id}" onclick="return confirmx('确认要删除该个性定制方案吗？', this.href)">删除</a>
					<a href="${ctx}/diy/solutionItem/list?solution.id=${solution.id}">查看方案条目</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>