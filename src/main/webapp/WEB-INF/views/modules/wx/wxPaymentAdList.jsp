<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>广告购买记录</title>
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
		<li class="active"><a href="${ctx}/wx/wxPaymentAd/">广告购买记录列表</a></li>
		<shiro:hasPermission name="wx:wxPaymentAd:edit"><li><a href="${ctx}/wx/wxPaymentAd/form">广告购买记录添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wxPaymentAd" action="${ctx}/wx/wxPaymentAd/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>付款达人：</label>
				<form:input path="broker.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>广告位：</label>
				<form:input path="advertise.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>展示日期：</label>
				<input name="advertiseDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${wxPaymentAd.advertiseDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</li>
			<li><label>付款时间：</label>
				<input name="paymentDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${wxPaymentAd.paymentDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>展示类型</th>
				<th>展示内容</th>
				<th>展示日期</th>
				<th>展示时段</th>
				<th>广告位</th>
				<th>广告标价(元)</th>
				<th>付款金额(元)</th>
				<th>购买达人</th>
				<th>付款时间</th>
				<th>商户订单号</th>
				<th>交易状态</th>
				<th>支付流水号</th>
				<th>创建时间</th>
				<th>最后更新</th>
				<shiro:hasPermission name="wx:wxPaymentAd:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wxPaymentAd">
			<tr>
				<td>
					${fns:getDictLabel(wxPaymentAd.advertise.type, 'wx_advertise_type', '')}
				</td>
				<td>
					<c:if test="${wxPaymentAd.advertise.type eq 'article'}">
						<a href="${wxPaymentAd.article.url}">${wxPaymentAd.article.title}</a>
					</c:if>
					<c:if test="${wxPaymentAd.advertise.type eq 'account'}">
						${wxPaymentAd.account.name}
					</c:if>					
				</td>								
				<td>
					<fmt:formatDate value="${wxPaymentAd.advertiseDate}" pattern="yyyy-MM-dd"/>
				</td>
				<td>
					<fmt:formatDate value="${wxPaymentAd.advertise.timeSlotFrom}" pattern="HH:mm"/>-<fmt:formatDate value="${wxPaymentAd.advertise.timeSlotTo}" pattern="HH:mm"/>
				</td>				
				<td>
					${wxPaymentAd.advertise.name}
				</td>			
				<td>
					${wxPaymentAd.advertise.price*0.01}
				</td>									
				<td>
					${wxPaymentAd.amount*0.01}
				</td>
				<td>
					${wxPaymentAd.broker.nickname}
				</td>				
				<td>
					<fmt:formatDate value="${wxPaymentAd.paymentDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>				
				<td>
					${wxPaymentAd.tradeNo}
				</td>
				<td>
					${fns:getDictLabel(wxPaymentAd.tradeState, 'wx_trade_state', wxPaymentAd.tradeState)}
				</td>
				<td>
					${wxPaymentAd.transactionId}
				</td>
				<td>
					<fmt:formatDate value="${wxPaymentAd.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>				
				<td>
					<fmt:formatDate value="${wxPaymentAd.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="wx:wxPaymentAd:edit"><td>
    				<a href="${ctx}/wx/wxPaymentAd/form?id=${wxPaymentAd.id}">修改</a>
					<a href="${ctx}/wx/wxPaymentAd/delete?id=${wxPaymentAd.id}" onclick="return confirmx('确认要删除该广告购买记录吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>