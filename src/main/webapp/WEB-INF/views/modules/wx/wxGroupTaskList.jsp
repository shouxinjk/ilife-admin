<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>微信群任务管理</title>
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
		<li class="active"><a href="${ctx}/wx/wxGroupTask/">微信群任务列表</a></li>
		<shiro:hasPermission name="wx:wxGroupTask:edit"><li><a href="${ctx}/wx/wxGroupTask/form">微信群任务添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wxGroupTask" action="${ctx}/wx/wxGroupTask/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>所属达人：</label>
				<form:input path="broker.id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>所属微信群：</label>
				<form:input path="wxgroup.id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>任务名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>任务类型：</label>
				<form:select path="type" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('wx_group_task_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
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
				<th>所属微信群</th>
				<th>任务名称</th>
				<th>任务类型</th>
				<th>任务CRON</th>
				<th>任务关键字</th>
				<th>任务状态</th>
				<th>更新时间</th>
				<shiro:hasPermission name="wx:wxGroupTask:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wxGroupTask">
			<tr>
				<td><a href="${ctx}/wx/wxGroupTask/form?id=${wxGroupTask.id}">
					${wxGroupTask.broker.nickname}
				</a></td>
				<td>
					${wxGroupTask.wxgroup.name}
				</td>
				<td>
					${wxGroupTask.name}
				</td>
				<td>
					${fns:getDictLabel(wxGroupTask.type, 'wx_group_task_type', '')}
				</td>
				<td>
					${wxGroupTask.cron}
				</td>
				<td>
					${wxGroupTask.tags}
				</td>
				<td>
					${fns:getDictLabel(wxGroupTask.status, 'active_inactive', '')}
				</td>
				<td>
					<fmt:formatDate value="${wxGroupTask.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="wx:wxGroupTask:edit"><td>
    				<a href="${ctx}/wx/wxGroupTask/form?id=${wxGroupTask.id}">修改</a>
					<a href="${ctx}/wx/wxGroupTask/delete?id=${wxGroupTask.id}" onclick="return confirmx('确认要删除该微信群任务吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>