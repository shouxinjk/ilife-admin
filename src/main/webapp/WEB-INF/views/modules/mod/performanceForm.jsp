<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>推广效果管理</title>
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
		<li><a href="${ctx}/mod/performance/">推广效果列表</a></li>
		<li class="active"><a href="${ctx}/mod/performance/form?id=${performance.id}">推广效果<shiro:hasPermission name="mod:performance:edit">${not empty performance.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:performance:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="performance" action="${ctx}/mod/performance/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">所属达人：</label>
			<div class="controls">
				<sys:treeselect id="broker" name="broker.id" value="${performance.broker.id}" labelName="broker.name" labelValue="${performance.broker.name}"
					title="推广达人" url="/mod/broker/treeData" extId="${performance.broker.id}" cssClass="" allowClear="true"/>
			</div>			
		</div>
		<div class="control-group">
			<label class="control-label">任务类型：</label>
			<div class="controls">
				<form:input path="taskType" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分享量：</label>
			<div class="controls">
				<form:input path="countShare" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">浏览量：</label>
			<div class="controls">
				<form:input path="countView" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">意向数：</label>
			<div class="controls">
				<form:input path="countBuy" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">订单数：</label>
			<div class="controls">
				<form:input path="countOrder" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">团队人数：</label>
			<div class="controls">
				<form:input path="countTeam" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">订单佣金：</label>
			<div class="controls">
				<form:input path="amountOrder" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">团队佣金：</label>
			<div class="controls">
				<form:input path="amountTeam" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">意向激励：</label>
			<div class="controls">
				<form:input path="amountBuy" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">积分激励：</label>
			<div class="controls">
				<form:input path="amountCredit" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">计算状态：</label>
			<div class="controls">
				<form:input path="statusCalc" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">通知状态：</label>
			<div class="controls">
				<form:input path="statusNotify" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">计算完成时间：</label>
			<div class="controls">
				<input name="dateCalc" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${performance.dateCalc}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">通知完成时间：</label>
			<div class="controls">
				<input name="dateNotify" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${performance.dateNotify}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:performance:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>