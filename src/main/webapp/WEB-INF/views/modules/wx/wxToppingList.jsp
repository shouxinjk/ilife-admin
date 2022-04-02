<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>置顶记录管理</title>
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
		<li class="active"><a href="${ctx}/wx/wxTopping/">置顶记录列表</a></li>
		<shiro:hasPermission name="wx:wxTopping:edit"><li><a href="${ctx}/wx/wxTopping/form">置顶记录添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="wxTopping" action="${ctx}/wx/wxTopping/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>达人：</label>
				<form:input path="broker.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>购买类型：</label>
				<form:select path="advertiseType" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('wx_topping_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>展示日期：</label>
				<input name="advertiseDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${wxTopping.advertiseDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label>广告位：</label>
				<form:input path="advertise.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>展示内容类型：</label>
				<form:select path="subjectType" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('wx_advertise_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>展示内容ID：</label>
				<form:input path="subjectId" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>达人</th>
				<th>购买类型</th>
				<th>展示日期</th>
				<th>展示开始时间</th>
				<th>展示结束时间</th>
				<th>广告位</th>
				<th>展示权重</th>
				<th>价格(元)</th>
				<th>展示内容类型</th>
				<th>展示内容</th>
				<th>更新时间</th>
				<shiro:hasPermission name="wx:wxTopping:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="wxTopping">
			<tr>
				<td>
					${wxTopping.broker.nickname}
				</td>
				<td>
					${fns:getDictLabel(wxTopping.advertiseType, 'wx_topping_type', '')}
				</td>
				<td>
					<fmt:formatDate value="${wxTopping.advertiseDate}" pattern="yyyy-MM-dd"/>
				</td>
				<td>
					<fmt:formatDate value="${wxTopping.advertiseTimeFrom}" pattern="HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${wxTopping.advertiseTimeTo}" pattern="HH:mm:ss"/>
				</td>
				<td>
					${wxTopping.advertise.name}
				</td>				
				<td>
					${wxTopping.advertiseWeight}
				</td>
				<td>
					${wxTopping.advertisePrice*0.01}
				</td>
				<td>
					${fns:getDictLabel(wxTopping.subjectType, 'wx_advertise_type', '')}
				</td>
				<td>
					<c:if test="${wxTopping.advertise.type eq 'article'}">
						<a href="${wxTopping.article.url}">${wxTopping.article.title}</a>
					</c:if>
					<c:if test="${wxTopping.advertise.type eq 'account'}">
						${wxTopping.account.name}
					</c:if>	
				</td>
				<td>
					<fmt:formatDate value="${wxTopping.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="wx:wxTopping:edit"><td>
    				<a href="${ctx}/wx/wxTopping/form?id=${wxTopping.id}">修改</a>
					<a href="${ctx}/wx/wxTopping/delete?id=${wxTopping.id}" onclick="return confirmx('确认要删除该置顶记录吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>