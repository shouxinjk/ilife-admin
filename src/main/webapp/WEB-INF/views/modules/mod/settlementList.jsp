<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>结算管理</title>
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
		<li class="active"><a href="${ctx}/mod/settlement/">结算记录列表</a></li>
		<shiro:hasPermission name="mod:settlement:edit"><li><a href="${ctx}/mod/settlement/form">结算记录添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="settlement" action="${ctx}/mod/settlement/" method="post" class="breadcrumb form-search">
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
				<th>分润类型</th>
				<th>结算金额</th>
				<th>结算凭证号</th>
				<th>支付凭证号</th>
				<th>状态</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:settlement:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="settlement">
			<tr>
				<td>${settlement.broker.name}</td>	
				<td>${fns:getDictLabel(settlement.type, 'shareProfitType', '-')}</td>
				<td>${settlement.amount}</td>
				<td>${settlement.voucher}</td>
				<td>${settlement.paymentId}</td>		
				<td>${settlement.status}</td>		
				<td><fmt:formatDate value="${settlement.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<shiro:hasPermission name="mod:settlement:edit"><td>
    				<a href="${ctx}/mod/settlement/form?id=${settlement.id}">修改</a>
					<a href="${ctx}/mod/settlement/delete?id=${settlement.id}" onclick="return confirmx('确认要删除该结算管理吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>