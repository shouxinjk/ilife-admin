<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客观评价管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")){
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
		<li><a href="${ctx}/mod/itemDimension/list?treeId=${itemDimension.category.id}">客观评价列表</a></li>
		<li class="active"><a href="${ctx}/mod/itemDimension/form?id=${itemDimension.id}&parent.id=${itemDimension.parent.id}">添加评价节点</a></li>
		<shiro:hasPermission name="mod:itemDimensionMeasure:edit"><li><a href="${ctx}/mod/itemDimensionMeasure/form?category.id=${itemDimension.category.id}">添加属性节点</a></li></shiro:hasPermission>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="itemDimension" action="${ctx}/mod/itemDimension/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="category.id" value="${itemDimension.category.id}"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">上级父级编号:</label>
			<div class="controls">
				<sys:treeselect id="parent" name="parent.id" value="${itemDimension.parent.id}" labelName="parent.name" labelValue="${itemDimension.parent.name}"
					title="父级编号" url="/mod/itemDimension/treeData" extId="${itemDimension.category.id}" cssClass="" allowClear="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">propKey：</label>
			<div class="controls">
				<form:input path="propKey" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>			
		<div class="control-group">
			<label class="control-label">是否特征:</label>
			<div class="controls">
				<form:select path="featured">
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> “是”代表特征评价</span>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">脚本类型:</label>
			<div class="controls">
				<form:select path="scriptType">
					<form:options items="${fns:getDictList('script_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>				
		<div class="control-group">
			<label class="control-label">计算脚本：</label>
			<div class="controls">
				<form:textarea path="script" htmlEscape="false" rows="3" class="input-xxlarge "/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">可读脚本：</label>
			<div class="controls">
				<form:textarea path="scriptMemo" htmlEscape="false" rows="3" class="input-xxlarge "/>
			</div>
		</div>			
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="4" class="input-xxlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">占比：</label>
			<div class="controls">
				<form:input path="weight" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<!--div class="control-group">
			<label class="control-label">sort：</label>
			<div class="controls">
				<form:input path="sort" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div-->
		<div class="form-actions">
			<shiro:hasPermission name="mod:itemDimension:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>