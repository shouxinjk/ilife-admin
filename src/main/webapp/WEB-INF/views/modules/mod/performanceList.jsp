<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>推广效果管理</title>
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
		<li class="active"><a href="${ctx}/mod/performance/">推广效果列表</a></li>
		<shiro:hasPermission name="mod:performance:edit"><li><a href="${ctx}/mod/performance/form">推广效果添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="performance" action="${ctx}/mod/performance/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>达人</th>
				<th>类型</th>
				<th>分享数</th>
				<th>浏览数</th>
				<th>意向数</th>
				<th>订单数</th>
				<th>团队人数</th>
				<th>订单佣金</th>
				<th>团队佣金</th>
				<th>意向激励</th>
				<th>积分激励</th>
				<th>计算状态</th>		
				<th>通知状态</th>		
				<th>更新时间</th>
				<shiro:hasPermission name="mod:performance:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="performance">
			<tr>
				<td>${performance.broker.name}</td>
				<td>${performance.taskType}</td>
				<td>${performance.countShare}</td>
				<td>${performance.countView}</td>
				<td>${performance.countBuy}</td>
				<td>${performance.countOrder}</td>
				<td>${performance.countTeam}</td>
				<td>${performance.amountOrder}</td>
				<td>${performance.amountTeam}</td>
				<td>${performance.amountBuy}</td>
				<td>${performance.amountCredit}</td>
				<td>${performance.statusCalc}</td>
				<td>${performance.statusNotify}</td>
				<td><a href="${ctx}/mod/performance/form?id=${performance.id}">
					<fmt:formatDate value="${performance.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</a></td>
				<shiro:hasPermission name="mod:performance:edit"><td>
    				<a href="${ctx}/mod/performance/form?id=${performance.id}">修改</a>
					<a href="${ctx}/mod/performance/delete?id=${performance.id}" onclick="return confirmx('确认要删除该推广效果吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>