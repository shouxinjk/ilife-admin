<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>频道管理管理</title>
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
		<li class="active"><a href="${ctx}/mod/channel/">频道列表</a></li>
		<shiro:hasPermission name="mod:channel:edit"><li><a href="${ctx}/mod/channel/form">频道添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="channel" action="${ctx}/mod/channel/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>CODE：</label>
				<form:input path="code" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>关键字：</label>
				<form:input path="tagging" htmlEscape="false" maxlength="1024" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>名称</th>
				<th>CODE</th>
				<th>状态</th>
				<th>概要描述</th>
				<th>关键字</th>
				<th>排序</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:channel:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="channel">
			<tr>
				<td><a href="${ctx}/mod/channel/form?id=${channel.id}">
					${channel.name}
				</a></td>
				<td>
					${channel.code}
				</td>
				<td>
					${fns:getDictLabel(channel.status, 'active_inactive', channel.status)}
				</td>
				<td>
					${channel.description}
				</td>
				<td>
					${channel.tagging}
				</td>
				<td>
					${channel.sort}
				</td>
				<td>
					<fmt:formatDate value="${channel.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
				<shiro:hasPermission name="mod:channel:edit">
    				<a href="${ctx}/mod/channel/form?id=${channel.id}">修改</a>
					<a href="${ctx}/mod/channel/delete?id=${channel.id}" onclick="return confirmx('确认要删除该频道吗？', this.href)">删除</a>
				</shiro:hasPermission>
				<shiro:hasPermission name="mod:channelNeed:view">
					<a href="${ctx}/mod/channelNeed/?channel.id=${channel.id}">需要构成</a>
				</shiro:hasPermission>
				<shiro:hasPermission name="mod:channelNeed:edit">
					<a href="${ctx}/mod/channelNeed/form?channel.id=${channel.id}">增加需要</a>
				</shiro:hasPermission>				
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>