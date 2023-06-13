<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>SaaS订阅管理</title>
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
		<li class="active"><a href="${ctx}/mod/subscription/">SaaS订阅列表</a></li>
		<shiro:hasPermission name="mod:subscription:edit"><li><a href="${ctx}/mod/subscription/form">SaaS订阅添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="subscription" action="${ctx}/mod/subscription/" method="post" class="breadcrumb form-search">
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
				<th>创建日期</th>
				<th>租户</th>
				<th>订阅内容</th>
				<th>套餐类型</th>
				<th>订阅类型</th>
				<th>生效时间</th>
				<th>失效时间</th>
				<th>支付金额</th>
				<th>支付时间</th>
				<th>商户订单号</th>
				<th>交易状态</th>
				<th>支付流水号</th>
				<shiro:hasPermission name="mod:subscription:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="subscription">
			<tr>
				<td><a href="${ctx}/mod/subscription/form?id=${subscription.id}">
					<fmt:formatDate value="${subscription.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</a></td>
				<td>
					${subscription.tenantId}
				</td>
				<td>
					${subscription.appId}
				</td>
				<td>
					${subscription.planId}
				</td>
				<td>
					${subscription.subscribeType}
				</td>
				<td>
					<fmt:formatDate value="${subscription.effectiveOn}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${subscription.expireOn}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${subscription.paymentAmount}
				</td>
				<td>
					<fmt:formatDate value="${subscription.paymentTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${subscription.tradeNo}
				</td>
				<td>
					${subscription.tradeState}
				</td>
				<td>
					${subscription.transactionCode}
				</td>
				<shiro:hasPermission name="mod:subscription:edit"><td>
    				<a href="${ctx}/mod/subscription/form?id=${subscription.id}">修改</a>
					<a href="${ctx}/mod/subscription/delete?id=${subscription.id}" onclick="return confirmx('确认要删除该SaaS订阅吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>