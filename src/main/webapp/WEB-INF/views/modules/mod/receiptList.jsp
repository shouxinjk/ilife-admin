<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>收款管理</title>
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
		<li class="active"><a href="${ctx}/mod/receipt/">收款记录列表</a></li>
		<shiro:hasPermission name="mod:receipt:edit"><li><a href="${ctx}/mod/receipt/form">收款记录添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="receipt" action="${ctx}/mod/receipt/" method="post" class="breadcrumb form-search">
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
				<th>账户类型</th>			
				<th>收款账户</th>	
				<th>收款渠道</th>
				<th>申请金额</th>
				<th>实收金额</th>
				<th>收款状态</th>
				<th>收款凭证</th>
				<th>明细清单</th>
				<th>备注</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:receipt:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="receipt">
			<tr>
				<td>${receipt.platform}</td>
				<td>${fns:getDictLabel(receipt.type, 'paymentType', '-')}</td>
				<td>${receipt.account}</td>
				<td>${fns:getDictLabel(receipt.channel, 'paymentChannelType', '-')}</td>
				<td>${receipt.amountRequest}</td>
				<td>${receipt.amountReceived}</td>
				<td>${receipt.status}</td>	
				<td>${receipt.voucher}</td>	
				<td>${receipt.filename}</td>	
				<td>${receipt.memo}</td>	
				<td><fmt:formatDate value="${receipt.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<shiro:hasPermission name="mod:receipt:edit"><td>
    				<a href="${ctx}/mod/receipt/form?id=${receipt.id}">修改</a>
					<a href="${ctx}/mod/receipt/delete?id=${receipt.id}" onclick="return confirmx('确认要删除该收款记录吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>