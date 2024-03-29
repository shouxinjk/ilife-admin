<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>清算管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mod/clearing/">清算列表</a></li>
		<li class="active"><a href="${ctx}/mod/clearing/form?id=${clearing.id}">清算<shiro:hasPermission name="mod:clearing:edit">${not empty clearing.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:clearing:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="clearing" action="${ctx}/mod/clearing/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">电商平台：</label>
			<div class="controls">
				<form:select path="platform" items="${fns:getDictList('platform')}" itemLabel="label" itemValue="value" htmlEscape="false" style="width:270px"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">关联订单：</label>
			<div class="controls">
				<form:input path="order.id" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品类别：</label>
			<div class="controls">
				<form:input path="category" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">订单金额：</label>
			<div class="controls">
				<form:input path="amountOrder" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">佣金总额：</label>
			<div class="controls">
				<form:input path="amountCommission" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分润金额：</label>
			<div class="controls">
				<form:input path="amountProfit" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分润规则：</label>
			<div class="controls">
				<form:input path="scheme.id" htmlEscape="false" maxlength="64" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分润明细：</label>
			<div class="controls">
				<form:input path="schemeItem.id" htmlEscape="false" maxlength="64" class="input-xlarge"/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">受益方类型：</label>
			<div class="controls">
				<form:select path="beneficiaryType" items="${fns:getDictList('beneficiaryCategory')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">受益方：</label>
			<div class="controls">
				<form:input path="beneficiary.id" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">所占份额：</label>
			<div class="controls">
				<form:input path="share" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品名称：</label>
			<div class="controls">
				<form:input path="item" htmlEscape="false" maxlength="255" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">订单完成时间：</label>
			<div class="controls">
				<input name="orderTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${clearing.orderTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">清算状态：</label>
			<div class="controls">
				<form:input path="statusClear" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">结算状态：</label>
			<div class="controls">
				<form:input path="statusSettle" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">收款状态：</label>
			<div class="controls">
				<form:input path="statusCash" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>				
		<div class="control-group">
			<label class="control-label">收款凭证号：</label>
			<div class="controls">
				<form:input path="voucherCollection" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">结算凭证号：</label>
			<div class="controls">
				<form:input path="voucherSettlement" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:clearing:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>