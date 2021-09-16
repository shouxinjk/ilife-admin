<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>清算管理</title>
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
		<li class="active"><a href="${ctx}/mod/clearing/">清算列表</a></li>
		<shiro:hasPermission name="mod:clearing:edit"><li><a href="${ctx}/mod/clearing/form">清算添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="clearing" action="${ctx}/mod/clearing/" method="post" class="breadcrumb form-search">
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
				<th>电商平台</th>
				<th>订单号</th>
				<th>订单金额</th>
				<th>佣金总额</th>
				<th>占比</th>
				<th>分润金额</th>
				<th>清算状态</th>
				<th>结算状态</th>
				<th>收款状态</th>
				<th>受益方</th>
				<th>受益方类型</th>
				<th>分润规则</th>
				<th>订单完成时间</th>
				<th>收款凭证号</th>
				<th>结算凭证号</th>					
				<th>商品类别</th>
				<th>商品名称</th>							
				<th>更新时间</th>
				<shiro:hasPermission name="mod:clearing:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="clearing">
			<tr>
				<td>${fns:getDictLabel(clearing.platform, 'platform', '-')}</td>
				<td>${clearing.order.orderNo}</td>
				<td>${clearing.amountOrder}</td>
				<td>${clearing.amountCommission}</td>
				<td>${clearing.share}</td>
				<td>${clearing.amountProfit}</td>
				<td>${clearing.statusClear}</td>
				<td>${clearing.statusSettle}</td>
				<td>${clearing.statusCash}</td>
				<td>${clearing.beneficiary}</td>
				<td>${fns:getDictLabel(clearing.beneficiaryType, 'beneficiaryCategory', '-')}</td>
				<td>${clearing.scheme.name}-${fns:getDictLabel(clearing.schemeItem.beneficiary, 'beneficiaryType', '-')}</td>	
				<td>
					<fmt:formatDate value="${clearing.orderTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>				
				<td>${clearing.voucherCollection}</td>	
				<td>${clearing.voucherSettlement}</td>			
				<td>${clearing.category}</td>
				<td>${clearing.item}</td>
				<td><fmt:formatDate value="${clearing.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<shiro:hasPermission name="mod:clearing:edit"><td>
    				<a href="${ctx}/mod/clearing/form?id=${clearing.id}">修改</a>
					<a href="${ctx}/mod/clearing/delete?id=${clearing.id}" onclick="return confirmx('确认要删除该清算吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>