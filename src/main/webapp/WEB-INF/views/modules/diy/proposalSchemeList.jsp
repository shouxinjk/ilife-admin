<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>个性化定制模板管理</title>
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
		<li class="active"><a href="${ctx}/diy/proposalScheme/">主题列表</a></li>
		<shiro:hasPermission name="diy:proposalScheme:edit"><li><a href="${ctx}/diy/proposalScheme/form">添加主题</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="proposalScheme" action="${ctx}/diy/proposalScheme/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>类型：</label>
				<form:select path="type" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('proposal_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>类别：</label>
				<form:input path="category" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>达人：</label>
				<form:input path="broker.id" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>激活：</label>
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
				<th>描述</th>
				<th>类型</th>
				<th>表单</th>
				<th>类别</th>
				<th>达人</th>
				<th>激活</th>
				<th>优先级</th>
				<th>更新时间</th>
				<shiro:hasPermission name="diy:proposalScheme:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="proposalScheme">
			<tr>
				<td>
					<a href="${ctx}/diy/proposalSection/?scheme.id=${proposalScheme.id}">${proposalScheme.name}</a>
				</td>
				<td>
					${proposalScheme.description}
				</td>
				<td>
					${fns:getDictLabel(proposalScheme.type, 'proposal_type', '')}
				</td>
				<td>
					${proposalScheme.form.name}
				</td>
				<td>
					${proposalScheme.category}
				</td>
				<td>
					${proposalScheme.broker.nickname}
				</td>
				<td>
					${fns:getDictLabel(proposalScheme.status, 'yes_no', '')}
				</td>
				<td>
					${proposalScheme.priority}
				</td>
				<td>
					<fmt:formatDate value="${proposalScheme.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="diy:proposalScheme:edit"><td>
    				<a href="${ctx}/diy/proposalScheme/form?id=${proposalScheme.id}">修改</a>
					<a href="${ctx}/diy/proposalScheme/delete?id=${proposalScheme.id}" onclick="return confirmx('确认要删除该个性化定制模板吗？', this.href)">删除</a>
					<a href="${ctx}/diy/proposalSection/?scheme.id=${proposalScheme.id}">查看section</a>
					<a href="${ctx}/diy/guideBookProposal/?proposal.id=${proposalScheme.id}">查看指南</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>