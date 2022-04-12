<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>阅豆付款管理</title>
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
		<li class="active"><a href="${ctx}/wx/wxPaymentPoint/">阅豆付款列表</a></li>
		<shiro:hasPermission name="wx:wxPaymentPoint:edit"><li><a href="${ctx}/wx/wxPaymentPoint/form">阅豆付款添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wxPaymentPoint" action="${ctx}/wx/wxPaymentPoint/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>付款达人：</label>
				<form:input path="broker.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>阅豆产品：</label>
				<form:input path="points.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>付款时间：</label>
				<input name="paymentDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${wxPaymentPoint.paymentDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label>商户订单号：</label>
				<form:input path="tradeNo" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>交易状态：</label>
				<form:select path="tradeState" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('wx_trade_state')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>支付流水号：</label>
				<form:input path="transactionId" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>付款达人</th>
				<th>阅豆产品</th>
				<th>付款金额(元)</th>
				<th>付款时间</th>
				<th>商户订单号</th>
				<th>交易状态</th>
				<th>支付流水号</th>
				<th>创建时间</th>
				<th>最后更新</th>
				<shiro:hasPermission name="wx:wxPaymentPoint:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wxPaymentPoint">
			<tr>
				<td>
					${wxPaymentPoint.broker.nickname}
				</td>
				<td>
					${wxPaymentPoint.points.name}
				</td>
				<td>
					${wxPaymentPoint.amount*0.01}
				</td>
				<td>
					<fmt:formatDate value="${wxPaymentPoint.paymentDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${wxPaymentPoint.tradeNo}
				</td>
				<td>
					${fns:getDictLabel(wxPaymentPoint.tradeState, 'wx_trade_state', wxPaymentPoint.tradeState)}
				</td>
				<td>
					${wxPaymentPoint.transactionId}
				</td>
				<td>
					<fmt:formatDate value="${wxPaymentPoint.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>				
				<td>
					<fmt:formatDate value="${wxPaymentPoint.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="wx:wxPaymentPoint:edit"><td>
    				<a href="${ctx}/wx/wxPaymentPoint/form?id=${wxPaymentPoint.id}">修改</a>
					<a href="${ctx}/wx/wxPaymentPoint/delete?id=${wxPaymentPoint.id}" onclick="return confirmx('确认要删除该阅豆付款吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>