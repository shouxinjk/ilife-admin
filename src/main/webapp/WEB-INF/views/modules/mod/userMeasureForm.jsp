<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户属性定义管理</title>
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
		<li><a href="${ctx}/mod/userMeasure/">用户属性定义列表</a></li>
		<li class="active"><a href="${ctx}/mod/userMeasure/form?id=${userMeasure.id}">用户属性定义<shiro:hasPermission name="mod:userMeasure:edit">${not empty userMeasure.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:userMeasure:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="userMeasure" action="${ctx}/mod/userMeasure/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">所属分类：</label>
			<div class="controls">
				 <sys:treeselect id="category" name="category.id" value="${userMeasure.category.id}" labelName="category.name" labelValue="${userMeasure.category.name}"
					title="用户分类" url="/mod/userCategory/treeData" notAllowSelectRoot="true" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">属性定义：</label>
			<div class="controls">
				<form:input path="property" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">属性类型：</label>
			<div class="controls">
				<form:select path="type" items="${fns:getDictList('propertyType')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				<!--form:input path="type" htmlEscape="false" maxlength="100" class="input-xlarge "/-->
			</div>
		</div>				
		<!--div class="control-group">
			<label class="control-label">所属维度：</label>
			<div class="controls">
				<form:input path="dimension" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div-->
		<div class="control-group">
			<label class="control-label">关联标签：</label>
			<div class="controls">
				<form:textarea path="tags" htmlEscape="false" rows="5" maxlength="1024" class="input-xlarge"/>
			</div>
		</div>		
		<!--div class="control-group">
			<label class="control-label">权重：</label>
			<div class="controls">
				<form:input path="weight" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">参考值：</label>
			<div class="controls">
				<form:input path="controlValue" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div-->
		<div class="control-group">
			<label class="control-label">默认分值：</label>
			<div class="controls">
				<form:input path="defaultScore" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<!--div class="control-group">
			<label class="control-label">默认等级：</label>
			<div class="controls">
				<form:input path="defaultRank" htmlEscape="false" maxlength="8" class="input-xlarge "/>
			</div>
		</div-->
		<div class="control-group">
			<label class="control-label">效益：生理：</label>
			<div class="controls">
				<form:input path="alpha" htmlEscape="false" class="input-small "/>
				<form:input path="beta" htmlEscape="false" class="input-small "/>
				<form:input path="gamma" htmlEscape="false" class="input-small "/>
				<form:input path="delte" htmlEscape="false" class="input-small "/>
				<form:input path="epsilon" htmlEscape="false" class="input-small "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">成本：经济：</label>
			<div class="controls">
				<form:input path="zeta" htmlEscape="false" class="input-small "/>
				<form:input path="eta" htmlEscape="false" class="input-small "/>
				<form:input path="theta" htmlEscape="false" class="input-small "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">脚本表达式：</label>
			<div class="controls">
				<form:textarea path="expression" htmlEscape="false" rows="5" maxlength="1024" class="input-xlarge "/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">偏好表达式：</label>
			<div class="controls">
				<form:textarea path="lambda" htmlEscape="false" rows="5" maxlength="1024" class="input-xlarge "/>
			</div>
		</div>			
		<div class="control-group">
			<label class="control-label">标注类型：</label>
			<div class="controls">
				<form:select path="autoLabelType" items="${fns:getDictList('autoLabelType')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>				
		<div class="control-group">
			<label class="control-label">从字典标注：</label>
			<div class="controls">
				<form:select path="autoLabelDict" items="${fns:getDictList('autoLabelDictUser')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">Refer引用标注：</label>
			<div class="controls">
				 <sys:treeselect id="autoLabelCategory" name="autoLabelCategory.id" value="${userMeasure.autoLabelCategory.id}" labelName="autoLabelCategory.name" labelValue="${userMeasure.autoLabelCategory.name}"
					title="用户分类" url="/mod/userCategory/treeData" notAllowSelectRoot="true"/>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">标签类目：</label>
			<div class="controls">
				 <sys:treeselect id="autoLabelTagCategory" name="autoLabelTagCategory.id" value="${measure.autoLabelTagCategory.id}" labelName="autoLabelTagCategory.name" labelValue="${measure.autoLabelTagCategory.name}"
					title="标签分类" url="/mod/tagCategory/treeData" notAllowSelectRoot="true"/>
			</div>
		</div>			
		<div class="control-group">
			<label class="control-label">归一化方法：</label>
			<div class="controls">
				<form:select path="normalizeType" items="${fns:getDictList('normalizeType')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">多值策略：</label>
			<div class="controls">
				<form:select path="multiValueFunc" items="${fns:getDictList('multiValueFunc')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</div>
		</div>									
		<div class="form-actions">
			<shiro:hasPermission name="mod:userMeasure:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>