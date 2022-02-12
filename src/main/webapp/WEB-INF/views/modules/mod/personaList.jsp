<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户分群管理</title>
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
		<li class="active"><a href="${ctx}/mod/persona/?treeId=${pid}&treeModule=${pType}">用户分群列表</a></li>
		<shiro:hasPermission name="mod:persona:edit"><li><a href="${ctx}/mod/persona/form?pid=${pid}&pType=${pType}">用户分群添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="persona" action="${ctx}/mod/persona/?treeId=${pid}&treeModule=${pType}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li>
				<label>社会分层：</label><sys:treeselect id="hierarchy" name="hierarchy.id" value="${persona.hierarchy.id}" labelName="hierarchy.name" labelValue="${persona.hierarchy.name}" 
				title="社会分层" url="/mod/hierarchy/listData" cssClass="input-small" allowClear="true"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>默认头像</th>
				<th>画像名称</th>
				<th>基本信息</th>
				<th>VALS模型</th>
				<th>能力模型</th>
				<th>偏好标签</th>
				<th>识别规则</th>
				<shiro:hasPermission name="mod:persona:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="persona">
			<tr>
				<td>
					<img src="${persona.logo}"  style="max-width:75px;max-height:75px;border:0;padding:3px;">
				</td>			
				<td>
				<a href="${ctx}/mod/persona/form?id=${persona.id}&pid=${pid}&pType=${pType}">
					${persona.name}
				</a><br/>
				${persona.description}
				</td>
				<td>
					阶段：${persona.phase.name}<br/>
					阶层：${persona.hierarchy.name}(${persona.hierarchy.displayName})<br/>
					分群：${persona.parent.name}
				</td>
				<td>
					生理：${persona.alpha}<br/>
					安全：${persona.beta}<br/>
					情感：${persona.gamma}<br/>
					尊重：${persona.delte}<br/>
					价值：${persona.epsilon}
				</td>
				<td>
					经济：${persona.zeta} (${persona.hierarchy.economyScoreMin }-${persona.hierarchy.economyScoreMax })<br/>
					社会：${persona.eta} (${persona.hierarchy.cultureScoreMin }-${persona.hierarchy.cultureScoreMax })<br/>
					文化：${persona.theta} (${persona.hierarchy.societyScoreMin }-${persona.hierarchy.societyScoreMax })
				</td>
				<td>
					${persona.lambda}
				</td>
				<td>
					${persona.expression}
				</td>
				<shiro:hasPermission name="mod:persona:edit"><td>
    				<a href="${ctx}/mod/persona/form?id=${persona.id}&pid=${pid}&pType=${pType}">修改</a><br/>
					<a href="${ctx}/mod/persona/delete?id=${persona.id}&pid=${pid}&pType=${pType}" onclick="return confirmx('确认要删除该用户分群吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>