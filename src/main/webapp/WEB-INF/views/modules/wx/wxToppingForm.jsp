<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>置顶记录管理</title>
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
		<li><a href="${ctx}/wx/wxTopping/">置顶记录列表</a></li>
		<li class="active"><a href="${ctx}/wx/wxTopping/form?id=${wxTopping.id}">置顶记录<shiro:hasPermission name="wx:wxTopping:edit">${not empty wxTopping.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="wx:wxTopping:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="wxTopping" action="${ctx}/wx/wxTopping/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">达人：</label>
			<div class="controls">
				<form:input path="broker.id" htmlEscape="false" maxlength="32" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">购买类型：</label>
			<div class="controls">
				<form:select path="advertiseType" class="input-xlarge ">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('wx_topping_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">展示日期：</label>
			<div class="controls">
				<input name="advertiseDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${wxTopping.advertiseDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">展示开始时间：</label>
			<div class="controls">
				<input name="advertiseTimeFrom" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${wxTopping.advertiseDate}" pattern="yyyy-MM-dd HH:mm"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',isShowClear:false});"/>
			</div>			
		</div>
		<div class="control-group">
			<label class="control-label">展示结束时间：</label>
			<div class="controls">
				<input name="advertiseTimeTo" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${wxTopping.advertiseDate}" pattern="yyyy-MM-dd HH:mm"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',isShowClear:false});"/>
			</div>			
		</div>
		<div class="control-group">
			<label class="control-label">展示权重：</label>
			<div class="controls">
				<form:input path="advertiseWeight" htmlEscape="false" maxlength="11" class="input-xlarge  digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">价格(分)：</label>
			<div class="controls">
				<form:input path="advertisePrice" htmlEscape="false" maxlength="11" class="input-xlarge  digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">广告位：</label>
			<div class="controls">
				<form:input path="advertise.id" htmlEscape="false" maxlength="32" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">展示内容类型：</label>
			<div class="controls">
				<form:select path="subjectType" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('wx_advertise_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">展示内容ID：</label>
			<div class="controls">
				<form:input path="subjectId" htmlEscape="false" maxlength="32" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="wx:wxTopping:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>