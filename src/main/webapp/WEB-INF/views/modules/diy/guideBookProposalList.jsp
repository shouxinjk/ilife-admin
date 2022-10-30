<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>定制指南主题关联管理</title>
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
		<li><a href="${ctx}/diy/guideBook/">主题列表</a></li>
		<shiro:hasPermission name="diy:guideBook:edit"><li><a href="${ctx}/diy/guideBook/form">添加主题</a></li></shiro:hasPermission>	
		<li class="active"><a href="${ctx}/diy/guideBookProposal/?scheme.id=${guideBookProposal.proposal.id}">已关联指南</a></li>
		<shiro:hasPermission name="diy:guideBookProposal:edit"><li><a href="${ctx}/diy/guideBook/list2?schemeId=${guideBookProposal.proposal.id}">待关联指南</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="guideBookProposal" action="${ctx}/diy/guideBookProposal/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>指南：</label>
				<form:input path="guide.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>主题：</label>
				<form:input path="proposal.id" htmlEscape="false" maxlength="32" class="input-medium"/>
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
				<th>指南</th>
				<th>描述</th>
				<th>类型</th>
				<th>类别</th>
				<th>激活</th>
				<th>优先级</th>				
				<th>更新时间</th>
				<shiro:hasPermission name="diy:guideBookProposal:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="guideBookProposal">
			<tr>
				<td><a href="${ctx}/diy/proposalScheme/form?id=${guideBookProposal.proposal.id}">
					${guideBookProposal.proposal.name}
				</a></td>			
				<td><a href="${ctx}/diy/guideBook/form?id=${guideBookProposal.guide.id}">
					${guideBookProposal.guide.name}
				</a></td>
				<td>
					${guideBookProposal.proposal.description}
				</td>
				<td>
					${fns:getDictLabel(guideBookProposal.proposal.type, 'proposal_type', '')}
				</td>
				<td>
					${guideBookProposal.proposal.category}
				</td>
				<td>
					${fns:getDictLabel(guideBookProposal.proposal.status, 'yes_no', '')}
				</td>
				<td>
					${guideBookProposal.proposal.priority}
				</td>
				<td>
					<fmt:formatDate value="${guideBookProposal.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="diy:guideBookProposal:edit"><td>
					<!-- 
    				<a href="${ctx}/diy/guideBookProposal/form?id=${guideBookProposal.id}">修改</a>
    				-->
					<a href="${ctx}/diy/guideBookProposal/delete?id=${guideBookProposal.id}" onclick="return confirmx('确认要删除该定制指南主题关联吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>