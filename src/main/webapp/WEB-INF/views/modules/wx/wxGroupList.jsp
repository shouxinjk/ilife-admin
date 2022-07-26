<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>微信群管理</title>
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
		<li class="active"><a href="${ctx}/wx/wxGroup/">微信群列表</a></li>
		<shiro:hasPermission name="wx:wxGroup:edit"><li><a href="${ctx}/wx/wxGroup/form">微信群添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wxGroup" action="${ctx}/wx/wxGroup/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>所属达人：</label>
				<form:input path="broker.id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>对应分群：</label>
				<form:input path="persona.id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>微信群名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="512" class="input-medium"/>
			</li>
			<li><label>群主：</label>
				<form:input path="owner" htmlEscape="false" maxlength="512" class="input-medium"/>
			</li>
			<li><label>激活码：</label>
				<form:input path="token" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>类型：</label>
				<form:select path="type" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('wx_group_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
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
				<th>对应分群</th>
				<!--th>推送规则CRON</th-->
				<th>微信群名称</th>
				<th>合作类型</th>
				<th>群主</th>
				<!--th>微信群ID</th-->
				<th>成员人数</th>
				<th>激活码</th>
				<th>状态</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasPermission name="wx:wxGroup:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wxGroup">
			<tr>
				<td><a href="${ctx}/wx/wxGroup/form?id=${wxGroup.id}">
					${wxGroup.broker.nickname}
				</a></td>
				<td>
					${wxGroup.persona.name}
				</td>
				<!--td>
					${wxGroup.schedule}
				</td-->
				<td>
					${wxGroup.name}
				</td>
				<td>
					${fns:getDictLabel(wxGroup.type, 'wx_group_type', '')}
				</td>				
				<td>
					${wxGroup.owner}
				</td>
				<!--td>
					${wxGroup.gid}
				</td-->
				<td>
					${wxGroup.members}
				</td>
				<td>
					${wxGroup.token}
				</td>
				<td>
					${fns:getDictLabel(wxGroup.status, 'active_inactive', '')}
				</td>
				<td>
					<fmt:formatDate value="${wxGroup.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${wxGroup.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="wx:wxGroup:edit"><td>
    				<a href="${ctx}/wx/wxGroup/form?id=${wxGroup.id}">修改</a>
					<a href="${ctx}/wx/wxGroup/delete?id=${wxGroup.id}" onclick="return confirmx('确认要删除该微信群吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>