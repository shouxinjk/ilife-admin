<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品属性管理</title>
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
		<li><a href="${ctx}/mod/measure/">商品属性列表</a></li>
		<li class="active"><a href="${ctx}/mod/measure/form?id=${measure.id}">商品属性<shiro:hasPermission name="mod:measure:edit">${not empty measure.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:measure:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="measure" action="${ctx}/mod/measure/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">商品分类：</label>
			<div class="controls">
				 <sys:treeselect id="category" name="category.id" value="${measure.category.id}" labelName="category.name" labelValue="${measure.category.name}"
					title="商品分类" url="/mod/itemCategory/treeData" notAllowSelectRoot="true" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">维度：</label>
			<div class="controls">
				<sys:treeselect id="itemDimension" name="itemDimension.id" value="${measure.itemDimension.id}" labelName="itemDimension.name" labelValue="${measure.itemDimension.name}"
								title="维度" url="/mod/itemDimension/treeData"  notAllowSelectRoot="true" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">属性定义：</label>
			<div class="controls">
				<form:input path="property" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">属性名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">属性占比：</label>
			<div class="controls">
				<form:input path="percentage" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">归一化参照值：</label>
			<div class="controls">
				<form:input path="controlValue" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">默认评分：</label>
			<div class="controls">
				<form:input path="defaultScore" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">默认等级：</label>
			<div class="controls">
				<form:input path="defaultLevel" htmlEscape="false" maxlength="8" class="input-xlarge "/>
			</div>
		</div>
	<div class="control-group">
			<label class="control-label">脚本表达式：</label>
			<div class="controls">
				<form:input path="expression" htmlEscape="false" maxlength="1024" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">效益：生理：</label>
			<div class="controls">
				<form:input path="alpha" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">效益：安全：</label>
			<div class="controls">
				<form:input path="beta" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">效益：情感：</label>
			<div class="controls">
				<form:input path="gamma" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">效益：尊重：</label>
			<div class="controls">
				<form:input path="delte" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">效益：价值：</label>
			<div class="controls">
				<form:input path="epsilon" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">成本：经济：</label>
			<div class="controls">
				<form:input path="zeta" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">成本：社会：</label>
			<div class="controls">
				<form:input path="eta" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">成本：文化：</label>
			<div class="controls">
				<form:input path="theta" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">偏好表达式：</label>
			<div class="controls">
				<form:input path="lambda" htmlEscape="false" maxlength="1024" class="input-xlarge "/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">类型：</label>
			<div class="controls">
				<form:input path="type" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">关联标签：</label>
			<div class="controls">
				<form:textarea path="tags" htmlEscape="false" rows="5" maxlength="1024" class="input-xlarge"/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">自动标注：</label>
			<div class="controls">
				<form:select path="autoLabelType" items="${fns:getDictList('autoLabelType')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">从字典标注：</label>
			<div class="controls">
				<form:select path="autoLabelDict" items="${fns:getDictList('autoLabelDictItem')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">Refer引用标注：</label>
			<div class="controls">
				 <sys:treeselect id="autoLabelCategory" name="autoLabelCategory.id" value="${measure.autoLabelCategory.id}" labelName="autoLabelCategory.name" labelValue="${measure.autoLabelCategory.name}"
					title="商品分类" url="/mod/itemCategory/treeData" notAllowSelectRoot="true"/>
			</div>
		</div>			
		<div class="form-actions">
			<shiro:hasPermission name="mod:measure:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>