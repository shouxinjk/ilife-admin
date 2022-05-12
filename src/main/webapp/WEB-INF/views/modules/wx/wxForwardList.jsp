<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>开白请求管理</title>
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
		<li class="active"><a href="${ctx}/wx/wxForward/">开白请求列表</a></li>
		<shiro:hasPermission name="wx:wxForward:edit"><li><a href="${ctx}/wx/wxForward/form">开白请求添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wxForward" action="${ctx}/wx/wxForward/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>请求达人：</label>
				<form:input path="requester.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>被请求达人：</label>
				<form:input path="responder.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>开白类别：</label>
				<form:select path="type" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('publisher_forward_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>开白状态：</label>
				<form:select path="status" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('publisher_forward_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
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
				<th>请求达人</th>
				<th>请求公众号</th>
				<th>被请求达人</th>
				<th>开白内容类型</th>
				<th>开白内容ID</th>
				<th>开白类别</th>
				<th>开白状态</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasPermission name="wx:wxForward:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wxForward">
			<tr>
				<td>
					${wxForward.requester.nickname}
				</td>
				<td>
					${wxForward.requestAccount.name}
				</td>				
				<td>
					${wxForward.responder.nickname}
				</td>
				<td>
					${fns:getDictLabel(wxForward.subjectType, 'publisher_forward_subject_type', '')}
				</td>
				<td>
					<c:if test="${wxForward.subjectType eq 'article'}">
						<a href="${wxForward.article.url}">${wxForward.article.title}</a>
					</c:if>
					<c:if test="${wxForward.subjectType eq 'account'}">
						${wxForward.account.name}
					</c:if>	
				</td>
				<td>
					${fns:getDictLabel(wxForward.type, 'publisher_forward_type', '')}
				</td>
				<td>
					${fns:getDictLabel(wxForward.status, 'publisher_forward_status', '')}
				</td>
				<td>
					<fmt:formatDate value="${wxForward.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${wxForward.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="wx:wxForward:edit"><td>
    				<a href="${ctx}/wx/wxForward/form?id=${wxForward.id}">修改</a>
					<a href="${ctx}/wx/wxForward/delete?id=${wxForward.id}" onclick="return confirmx('确认要删除该开白请求吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>