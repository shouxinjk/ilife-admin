<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>VALS模型管理</title>
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
		<li><a href="${ctx}/mod/lifeStyle/">VALS模型列表</a></li>
		<li class="active"><a href="${ctx}/mod/lifeStyle/form?id=${lifeStyle.id}">VALS模型<shiro:hasPermission name="mod:lifeStyle:edit">${not empty lifeStyle.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:lifeStyle:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="lifeStyle" action="${ctx}/mod/lifeStyle/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">分类：</label>
			<div class="controls">
				<sys:treeselect id="lifeStyleCategory" name="lifeStyleCategory.id" value="${lifeStyle.lifeStyleCategory.id}" labelName="lifeStyleCategory.name" labelValue="${lifeStyle.lifeStyleCategory.name}"
								title="VALS分类" url="/mod/lifeStyleCategory/treeData" cssClass="required" disabled="disabled"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">上级模型：</label>
			<div class="controls">
				 <sys:treeselect id="lifeStyle" name="parent.id" value="${lifeStyle.parent.id}" labelName="parent.name" labelValue="${lifeStyle.parent.name}"
					title="vals模型" url="/mod/lifeStyle/treeData" extId="${lifeStyle.id}" cssClass="required" disabled="disabled"/>
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
			<label class="control-label">alpha：</label>
			<div class="controls">
				<form:input path="alpha" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">beta：</label>
			<div class="controls">
				<form:input path="beta" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">gamma：</label>
			<div class="controls">
				<form:input path="gamma" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">delte：</label>
			<div class="controls">
				<form:input path="delte" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">epsilon：</label>
			<div class="controls">
				<form:input path="epsilon" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">zeta：</label>
			<div class="controls">
				<form:input path="zeta" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">eta：</label>
			<div class="controls">
				<form:input path="eta" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">theta：</label>
			<div class="controls">
				<form:input path="theta" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">lambda：</label>
			<div class="controls">
				<form:input path="lambda" htmlEscape="false" maxlength="1000" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">阶段：</label>
			<div class="controls">
				 <sys:treeselect id="phase" name="phase.id" value="${lifeStyle.phase.id}" labelName="phase.name" labelValue="${lifeStyle.phase.name}"
					title="人生阶段" url="/mod/phase/treeData" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">层级：</label>
			<div class="controls">
				<sys:treeselect id="hierarchy" name="hierarchy.id" value="${lifeStyle.hierarchy.id}" labelName="hierarchy.name" labelValue="${lifeStyle.hierarchy.name}"
					title="社会分层" url="/mod/hierarchy/listData"  cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">画像：</label>
			<div class="controls">
				<sys:treeselect id="persona" name="persona.id" value="${lifeStyle.persona.id}" labelName="persona.name" labelValue="${lifeStyle.persona.name}"
					title="用户分群" url="/mod/persona/treeData"  cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">内部动机：</label>
			<div class="controls">
				 <sys:treeselect id="motivationIds" name="motivationIds" value="${lifeStyle.motivationIds}" labelName="motivationNames" labelValue="${lifeStyle.motivationNames}"
					title="内部动机" url="/mod/motivation/listData" checked="true" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">外部诱因：</label>
			<div class="controls">
				<sys:treeselect id="occasionIds" name="occasionIds" value="${lifeStyle.occasionIds}" labelName="occasionNames" labelValue="${lifeStyle.occasionNames}"
					title="外部诱因" url="/mod/occasion/listData" checked="true" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品类别：</label>
			<div class="controls">
				<sys:treeselect id="itemCategoryIds" name="itemCategoryIds" value="${lifeStyle.itemCategoryIds}" labelName="itemCategoryNames" labelValue="${lifeStyle.itemCategoryNames}"
					title="商品分类" url="/mod/itemCategory/treeData" checked="true" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mod:lifeStyle:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>