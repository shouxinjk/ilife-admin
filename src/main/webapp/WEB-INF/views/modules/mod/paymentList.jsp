<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>支付管理管理</title>
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
		<li class="active"><a href="${ctx}/mod/payment/">支付管理列表</a></li>
		<shiro:hasPermission name="mod:payment:edit"><li><a href="${ctx}/mod/payment/form">支付管理添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="payment" action="${ctx}/mod/payment/" method="post" class="breadcrumb form-search">
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
				<th>推广达人</th>
				<th>账户类型</th>
				<th>收款账户</th>
				<th>申请金额</th>
				<th>实付金额</th>
				<th>付款渠道</th>
				<th>付款状态</th>
				<th>付款凭证</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:payment:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="payment">
			<tr>
				<td>${payment.broker.name}</td>
				<td>${fns:getDictLabel(payment.type, 'paymentType', '-')}</td>
				<td>${payment.account}</td>
				<td>${payment.amountRequest}</td>
				<td>${payment.amountPayment}</td>
				<td>${fns:getDictLabel(payment.channel, 'paymentChannelType', '-')}</td>
				<td>${payment.status}</td>
				<td>${payment.voucher}</td>
				<td><fmt:formatDate value="${payment.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<shiro:hasPermission name="mod:payment:edit"><td>
    				<a href="${ctx}/mod/payment/form?id=${payment.id}">修改</a>
					<a href="${ctx}/mod/payment/delete?id=${payment.id}" onclick="return confirmx('确认要删除该支付管理吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>