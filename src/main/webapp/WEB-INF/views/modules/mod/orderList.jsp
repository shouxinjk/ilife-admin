<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单管理管理</title>
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
		<li class="active"><a href="${ctx}/mod/order/">订单管理列表</a></li>
		<shiro:hasPermission name="mod:order:edit"><li><a href="${ctx}/mod/order/form">订单管理添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="order" action="${ctx}/mod/order/" method="post" class="breadcrumb form-search">
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
				<th>订单号</th>
				<th>来源</th>
				<th>跟踪码</th>
				<th>订单金额</th>
				<th>预估佣金</th>
				<th>结算佣金</th>
				<th>结算时间</th>
				<th>订单状态</th>
				<th>推广达人</th>
				<th>通知状态</th>
				<th>商品名称</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:order:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="order">
			<tr>
				<td><a href="${ctx}/mod/order/form?id=${order.id}">${order.orderNo}</a></td>
				<td>${fns:getDictLabel(order.platform, 'platform', '-')}</td>
				<td>${order.traceCode}</td>
				<td>${order.amount}</td>
				<td>${order.commissionEstimate}</td>
				<td>${order.commissionSettlement}</td>
				<td>
					<fmt:formatDate value="${order.orderTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>				
				<td>${order.status}</td>	
				<td>${order.broker.name}</td>
				<td>${fns:getDictLabel(order.notification, 'yes_no', '-')}</td>
				<td>${order.item}</td>
				<td>
					<fmt:formatDate value="${order.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:order:edit"><td>
    				<a href="${ctx}/mod/order/form?id=${order.id}">修改</a>
					<a href="${ctx}/mod/order/delete?id=${order.id}" onclick="return confirmx('确认要删除该订单管理吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>