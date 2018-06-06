<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>资本类型管理</title>
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
		<li class="active"><a href="${ctx}/mod/capital/">资本类型列表</a></li>
		<shiro:hasPermission name="mod:capital:edit"><li><a href="${ctx}/mod/capital/form">资本类型添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="capital" action="${ctx}/mod/capital/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>属性名：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>类别：</label>
				<form:select path="category" class="input-xlarge">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('capital_category')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
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
				<th>类别</th>
				<th>属性定义</th>
				<th>属性占比</th>
				<th>归一化参照值</th>
				<th>默认等级</th>
				<th>默认值</th>
				<th>描述</th>
				<shiro:hasPermission name="mod:capital:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="capital">
			<tr>
				<td>
					${fns:getDictLabel(capital.category, 'capital_category', '无')}
				</td>
				<td><a href="${ctx}/mod/capital/form?id=${capital.id}">
					${capital.name}
				</a></td>
				<td>
					${capital.percentage}
				</td>
				<td>
					${capital.controlValue}
				</td>
				<td>
					${capital.defaultLevel}
				</td>
				<td>
					${capital.defaultValue}
				</td>
				<td>
					${capital.description}
				</td>
				<shiro:hasPermission name="mod:capital:edit"><td>
    				<a href="${ctx}/mod/capital/form?id=${capital.id}">修改</a>
					<a href="${ctx}/mod/capital/delete?id=${capital.id}" onclick="return confirmx('确认要删除该资本类型吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>