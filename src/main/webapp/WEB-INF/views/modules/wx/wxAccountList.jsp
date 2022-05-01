<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>微信公众号管理</title>
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
		<li class="active"><a href="${ctx}/wx/wxAccount/">微信公众号列表</a></li>
		<shiro:hasPermission name="wx:wxAccount:edit"><li><a href="${ctx}/wx/wxAccount/form">微信公众号添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wxAccount" action="${ctx}/wx/wxAccount/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>公众号名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>原始ID：</label>
				<form:input path="originalId" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>状态：</label>
				<form:select path="status" class="input-medium">
					<form:option value="" label=""/>
					<form:option value="" label="不限"/>
					<form:options items="${fns:getDictList('active_inactive')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
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
				<th>公众号名称</th>
				<th>发布达人</th>
				<th>微信ID</th>
				<th>公众号描述</th>
				<th>状态</th>
				
				<th>更新时间</th>
				<shiro:hasPermission name="wx:wxAccount:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wxAccount">
			<tr>
				<td><a href="https://open.weixin.qq.com/qr/code?username=${wxAccount.originalId}">
					${wxAccount.name}
				</a></td>
				<td>
					${wxAccount.broker.nickname}
				</td>				
				<td>
					${wxAccount.originalId}
				</td>
				<td>
					${wxAccount.description}
				</td>
				<td>
					${fns:getDictLabel(wxAccount.status, 'active_inactive', '')}
				</td>
				<td>
					<fmt:formatDate value="${wxAccount.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="wx:wxAccount:edit"><td>
    				<a href="${ctx}/wx/wxAccount/form?id=${wxAccount.id}">修改</a>
					<a href="${ctx}/wx/wxAccount/delete?id=${wxAccount.id}" onclick="return confirmx('确认要删除该微信公众号吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>