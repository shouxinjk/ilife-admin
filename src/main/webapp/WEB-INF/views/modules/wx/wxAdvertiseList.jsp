<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>微信广告位管理</title>
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
		<li class="active"><a href="${ctx}/wx/wxAdvertise/">微信广告位列表</a></li>
		<shiro:hasPermission name="wx:wxAdvertise:edit"><li><a href="${ctx}/wx/wxAdvertise/form">微信广告位添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wxAdvertise" action="${ctx}/wx/wxAdvertise/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>广告位名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>广告类型：</label>
				<form:select path="status" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('wx_advertise_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
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
				
				<th>广告类型</th>
				<th>时间段名称</th>
				<th>广告位名称</th>
				<th>时间段开始</th>
				<th>时间段结束</th>
				<th>价格</th>
				<th>权重</th>
				<th>数量</th>
				<th>优惠</th>
				<th>状态</th>
				<th>更新时间</th>
				<shiro:hasPermission name="wx:wxAdvertise:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wxAdvertise">
			<tr>
				<td>
					${fns:getDictLabel(wxAdvertise.type, 'wx_advertise_type', '')}
				</td>
				<td>
					${wxAdvertise.timeSlot}
				</td>
				<td>
					${wxAdvertise.name}
				</td>				
				<td>
					<fmt:formatDate value="${wxAdvertise.timeSlotFrom}" pattern="HH:mm"/>
				</td>
				<td>
					<fmt:formatDate value="${wxAdvertise.timeSlotTo}" pattern="HH:mm"/>
				</td>
				<td>
					${wxAdvertise.price*0.01}
				</td>
				<td>
					${wxAdvertise.weight}
				</td>
				<td>
					${wxAdvertise.quantity}
				</td>				
				<td>
					${wxAdvertise.discount}
				</td>
				<td>
					${fns:getDictLabel(wxAdvertise.status, 'active_inactive', '')}
				</td>
				<td>
					<fmt:formatDate value="${wxAdvertise.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="wx:wxAdvertise:edit"><td>
    				<a href="${ctx}/wx/wxAdvertise/form?id=${wxAdvertise.id}">修改</a>
					<a href="${ctx}/wx/wxAdvertise/delete?id=${wxAdvertise.id}" onclick="return confirmx('确认要删除该微信广告位吗？', this.href)">删除</a>
					<a href="${ctx}/wx/wxAdvertise/clone?id=${wxAdvertise.id}">克隆</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>