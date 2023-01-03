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
		<shiro:hasPermission name="diy:guideTerm:edit"><li><a href="${ctx}/diy/guideTerm/form">指南规则添加</a></li></shiro:hasPermission>	
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
				<th>类型</th>
				<th>描述</th>
				<th>指南条目</th>
				<th>关联的条目</th>
				<th>注意事项</th>
				<th>Tags</th>
				<th>规则描述</th>
				<th>排序</th>
				<th>更新时间</th>
				<shiro:hasPermission name="diy:guideTermItem:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="guideTermItem">
			<tr>
				<td>
					${guideTermItem.type.name}
				</td>			
				<td>
					${guideTermItem.description}
				</td>
				<td>
					${guideTermItem.term.name}
				</td>
				<td>
					${guideTermItem.item.name}
				</td>
				<td>
					${guideTermItem.tips}
				</td>
				<td>
					${guideTermItem.tags}
				</td>								
				<td>
					${guideTermItem.scriptDesc}
				</td>
				<td>
					${guideTermItem.sort}
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