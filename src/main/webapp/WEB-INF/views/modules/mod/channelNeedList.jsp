<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>频道需要构成管理</title>
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
		<li><a href="${ctx}/mod/channel/">频道列表</a></li>
		<shiro:hasPermission name="mod:channel:edit"><li><a href="${ctx}/mod/channel/form">频道添加</a></li></shiro:hasPermission>
		<li class="active"><a href="${ctx}/mod/channelNeed/">需要构成</a></li>
		<shiro:hasPermission name="mod:channelNeed:edit"><li><a href="${ctx}/mod/channelNeed/form?channel.id=${channelNeed.channel.id}">需要添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="channelNeed" action="${ctx}/mod/channelNeed/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>关联的频道：</label>
				<form:input path="channel.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>关联的动机：</label>
				<form:input path="need.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>频道</th>
				<th>需要</th>
				<th>强度</th>
				<th>描述</th>
				<th>表达式</th>				
				<th>更新时间</th>
				<shiro:hasPermission name="mod:channelNeed:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="channelNeed">
			<tr>
				<td>
					${channelNeed.channel.name}
				</td>
				<td>
					${channelNeed.need.name}
				</td>
				<td>
					${channelNeed.weight}
				</td>				
				<td>
					${channelNeed.description}
				</td>	
				<td>
					${channelNeed.expression}
				</td>							
				<td>
					<fmt:formatDate value="${channelNeed.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:channelNeed:edit"><td>
    				<a href="${ctx}/mod/channelNeed/form?id=${channelNeed.id}">修改</a>
					<a href="${ctx}/mod/channelNeed/delete?id=${channelNeed.id}" onclick="return confirmx('确认要删除该需要吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>