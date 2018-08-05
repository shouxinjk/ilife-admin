<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>资本标注管理</title>
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
		<li class="active"><a href="${ctx}/mod/capability/">资本标注列表</a></li>
		<shiro:hasPermission name="mod:capability:edit"><li><a href="${ctx}/mod/capability/form">资本标注添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="capability" action="${ctx}/mod/capability/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>属性名：</label>
				<form:input path="capital.name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>类别：</label>
				<form:select path="capital.category" class="input-xlarge">
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
				<th>资本标注</th>
				<th>类别</th>
				<th>属性定义</th>
				<th>归一化参照值</th>
				<th>默认值</th>
				<th>描述值</th>
				<th>设置值</th>
				<th>归一化值</th>
				<th>标注值</th>
				<th>标注等级</th>
				<th>描述</th>
				<shiro:hasPermission name="mod:capability:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="capability">
			<tr>
				<td>
					${capability.capabilityName}
				</td>
				<td>
					${fns:getDictLabel(capability.capital.category, 'capital_category', '无')}
				</td>
				<td><a href="${ctx}/mod/capability/form?id=${capability.id}">
					${capability.capital.name}
				</a></td>
				<td>
					${capability.capital.controlValue}
				</td>
				<td>
					${capability.capital.defaultValue}
				</td>
				<td>
					${capability.descValue}
				</td>
				<td>
					${capability.originalValue}
				</td>
				<td>
					${capability.normalizedValue}
				</td>
				<td>
					${capability.markedValue}
				</td>
				<td>
					${capability.level}
				</td>
				<td>
					${capability.description}
				</td>
				<shiro:hasPermission name="mod:capability:edit"><td>
    				<a href="${ctx}/mod/capability/form?id=${capability.id}">修改</a>
					<a href="${ctx}/mod/capability/delete?id=${capability.id}" onclick="return confirmx('确认要删除该资本标注吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>