<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>个性化定制指南管理</title>
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
		<li class="active"><a href="${ctx}/diy/guideBook/">个性化定制指南列表</a></li>
		<shiro:hasPermission name="diy:guideBook:edit"><li><a href="${ctx}/diy/guideBook/form">个性化定制指南添加</a></li></shiro:hasPermission>
	</ul>

	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th width="60">#</th><!-- 复选框 -->
				<th>全称</th>
				<th>简称</th>
				<th>来源</th>
				<th>版本</th>
				<th>标签</th>
				<th>描述</th>
				<th>类型</th>
				<th>URL</th>
				<th>激活</th>
				<th>更新时间</th>
				<shiro:hasPermission name="diy:guideBook:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="guideBook">
			<tr>
				<td>
					<input type="checkbox" id="${guideBook.id}" value="${guideBook.id}" class="needBox"/>
				</td>			
				<td><a href="${ctx}/diy/guideBook/form?id=${guideBook.id}">
					${guideBook.name}
				</a></td>
				<td>
					${guideBook.alias}
				</td>
				<td>
					${guideBook.origin}
				</td>
				<td>
					${guideBook.revision}
				</td>
				<td>
					${guideBook.tags}
				</td>
				<td>
					${guideBook.description}
				</td>
				<td>
					${fns:getDictLabel(guideBook.type, 'guide_type', '')}
				</td>
				<td>
					${guideBook.url}
				</td>
				<td>
					${fns:getDictLabel(guideBook.status, 'yes_no', '')}
				</td>
				<td>
					<fmt:formatDate value="${guideBook.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="diy:guideBook:edit"><td>
    				<a href="${ctx}/diy/guideBook/form?id=${guideBook.id}">修改</a>
					<a href="${ctx}/diy/guideBook/delete?id=${guideBook.id}" onclick="return confirmx('确认要删除该个性化定制指南吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>