<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>指南规则条目关联管理</title>
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
		<li><a href="${ctx}/diy/guideTerm/">指南规则列表</a></li>
		<shiro:hasPermission name="diy:guideTerm:edit"><li><a href="${ctx}/diy/guideTerm/form?category.id=${guideTermItem.category.id}">指南规则添加</a></li></shiro:hasPermission>	
		<li class="active"><a href="${ctx}/diy/guideTermItem/?term.id=${guideTermItem.term.id}">已关联条目</a></li>
		<shiro:hasPermission name="diy:guideTermItem:edit"><li><a href="${ctx}/ope/item/list2?termId=${guideTermItem.term.id}">待关联条目</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="guideTermItem" action="${ctx}/diy/guideTermItem/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>指南条目：</label>
				<form:input path="term.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>关联的条目：</label>
				<form:input path="item.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>关联设置</th>			
				<th>选项设置</th>
				<th>匹配规则</th>
				<th>排序</th>
				<th>更新时间</th>
				<shiro:hasPermission name="diy:guideTermItem:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="guideTermItem">
			<tr>			
				<td>
					指南：${guideTermItem.term.name}<br/>
					选项：${guideTermItem.item.name}<br/>
					描述：${guideTermItem.description}
				</td>
				<td>
					选项类型：${guideTermItem.type.name}<br/>
					注意事项：${guideTermItem.tips}<br/>
					选项标签：${guideTermItem.tags}
				</td>								
				<td>
					优先级：${guideTermItem.sort}<br/>
					匹配规则：${guideTermItem.scriptDesc}
				</td>
				<td>
					<fmt:formatDate value="${guideTermItem.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="diy:guideTermItem:edit"><td>
    				<a href="${ctx}/diy/guideTermItem/form?id=${guideTermItem.id}">修改</a>
					<a href="${ctx}/diy/guideTermItem/delete?id=${guideTermItem.id}" onclick="return confirmx('确认要删除该指南规则条目关联吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>