<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>微信机器人管理</title>
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
		<li class="active"><a href="${ctx}/wx/wxBot/">微信机器人列表</a></li>
		<shiro:hasPermission name="wx:wxBot:edit"><li><a href="${ctx}/wx/wxBot/form">微信机器人添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wxBot" action="${ctx}/wx/wxBot/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>所属达人：</label>
				<form:input path="broker.id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>wechaty id：</label>
				<form:input path="wechatyId" htmlEscape="false" maxlength="512" class="input-medium"/>
			</li>
			<li><label>状态：</label>
				<form:select path="status" class="input-medium">
					<form:option value="" label=""/>
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
				<th>所属达人</th>
				<th>机器人名称</th>
				<th>wechaty id</th>
				<th>状态</th>
				<th>心跳时间</th>
				<th>错误信息</th>
				<th>生效时间</th>
				<th>截止时间</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasPermission name="wx:wxBot:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wxBot">
			<tr>
				<td><a href="${ctx}/wx/wxBot/form?id=${wxBot.id}">
					${wxBot.broker.nickname}
				</a></td>
				<td>
					${wxBot.name}
				</td>
				<td>
					${wxBot.wechatyId}
				</td>
				<td>
					${fns:getDictLabel(wxBot.status, 'active_inactive', '')}
				</td>
				<td>
					<fmt:formatDate value="${wxBot.heartBeat}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${wxBot.errMsg}
				</td>
				<td>
					<fmt:formatDate value="${wxBot.effectFrom}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${wxBot.expireOn}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${wxBot.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${wxBot.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="wx:wxBot:edit"><td>
    				<a href="${ctx}/wx/wxBot/form?id=${wxBot.id}">修改</a>
					<a href="${ctx}/wx/wxBot/delete?id=${wxBot.id}" onclick="return confirmx('确认要删除该微信机器人吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>