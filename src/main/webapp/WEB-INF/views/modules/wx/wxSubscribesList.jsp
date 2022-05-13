<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>关注记录管理</title>
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
		<li class="active"><a href="${ctx}/wx/wxSubscribes/">关注记录列表</a></li>
		<shiro:hasPermission name="wx:wxSubscribes:edit"><li><a href="${ctx}/wx/wxSubscribes/form">关注记录添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wxSubscribes" action="${ctx}/wx/wxSubscribes/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>公众号：</label>
				<form:input path="account.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>号主：</label>
				<form:input path="broker.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>粉丝openid：</label>
				<form:input path="openid" htmlEscape="false" maxlength="128" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>公众号</th>
				<th>微信原始ID</th>
				<th>号主</th>
				<th>粉丝openid</th>
				<th>更新时间</th>
				<shiro:hasPermission name="wx:wxSubscribes:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wxSubscribes">
			<tr>
				<td><a href="https://open.weixin.qq.com/qr/code?username==${wxSubscribes.account.originalId}">
					${wxSubscribes.account.name}
				</a></td>
				<td>
					${wxSubscribes.account.originalId}
				</td>				
				<td>
					${wxSubscribes.broker.nickname}
				</td>
				<td>
					${wxSubscribes.openid}
				</td>
				<td>
					<fmt:formatDate value="${wxSubscribes.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="wx:wxSubscribes:edit"><td>
    				<a href="${ctx}/wx/wxSubscribes/form?id=${wxSubscribes.id}">修改</a>
					<a href="${ctx}/wx/wxSubscribes/delete?id=${wxSubscribes.id}" onclick="return confirmx('确认要删除该关注记录吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>