<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>个性化定制章节管理</title>
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
		<li class="active"><a href="${ctx}/diy/proposalSection/">章节列表</a></li>
		<shiro:hasPermission name="diy:proposalSection:edit"><li><a href="${ctx}/diy/proposalSection/form">章节添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="proposalSection" action="${ctx}/diy/proposalSection/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>主题：</label>
				<form:input path="scheme.id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>类目：</label>
				<form:input path="category.name" htmlEscape="false" maxlength="50" class="input-medium"/>
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
				<th>主题</th>
				<th>所属类目</th>
				<th>名称</th>
				<th>描述</th>
				<th>排序</th>
				<th>更新时间</th>
				<shiro:hasPermission name="diy:proposalSection:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="proposalSection">
			<tr>
				<td><a href="${ctx}/diy/proposalSection/form?id=${proposalSection.id}">
					${proposalSection.scheme.name}
				</a></td>
				<td>
					${proposalSection.category.name}
				</td>				
				<td>
					${proposalSection.name}
				</td>
				<td>
					${proposalSection.description}
				</td>
				<td>
					${proposalSection.priority}
				</td>
				<td>
					<fmt:formatDate value="${proposalSection.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="diy:proposalSection:edit"><td>
    				<a href="${ctx}/diy/proposalSection/form?id=${proposalSection.id}">修改</a>
					<a href="${ctx}/diy/proposalSection/delete?id=${proposalSection.id}" onclick="return confirmx('确认要删除该个性化定制章节吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>