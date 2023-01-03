<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>个性化定制指南条目管理</title>
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
		<li class="active"><a href="${ctx}/diy/guideTerm/">指南规则列表</a></li>
		<shiro:hasPermission name="diy:guideTerm:edit"><li><a href="${ctx}/diy/guideTerm/form">指南规则添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="guideTerm" action="${ctx}/diy/guideTerm/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>指南：</label>
				<form:input path="book.id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>章节：</label>
				<form:input path="section.id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>指南</th>
				<th>章节</th>
				<th>名称</th>
				<th>描述</th>
				<th>注意事项</th>
				<th>标签</th>				
				<th>适用条件描述</th>
				<th>优先级</th>
				<th>更新时间</th>
				<shiro:hasPermission name="diy:guideTerm:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="guideTerm">
			<tr>
				<td><a href="${ctx}/diy/guideTerm/form?id=${guideTerm.id}">
					${guideTerm.book.name}
				</a></td>
				<td>
					${guideTerm.section.name}
				</td>
				<td>
					${guideTerm.name}
				</td>
				<td>
					${guideTerm.description}
				</td>
				<td>
					${guideTerm.tips}
				</td>
				<td>
					${guideTerm.tags}
				</td>								
				<td>
					${guideTerm.criteriaDesc}
				</td>
				<td>
					${guideTerm.priority}
				</td>
				<td>
					<fmt:formatDate value="${guideTerm.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="diy:guideTerm:edit"><td>
    				<a href="${ctx}/diy/guideTerm/form?id=${guideTerm.id}">修改</a>
					<a href="${ctx}/diy/guideTerm/delete?id=${guideTerm.id}" onclick="return confirmx('确认要删除该个性化定制指南条目吗？', this.href)">删除</a>
					<a href="${ctx}/diy/guideTermItem/?term.id=${guideTerm.id}">查看关联条目</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>