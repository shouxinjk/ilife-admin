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
				<!--th width="60">#</th--><!-- 复选框 -->
				<th>基本信息</th>
				<th>类别</th>
				<th>指南版本</th>
				<th>标签</th>
				<th>描述</th>
				<th>状态</th>
				<shiro:hasPermission name="diy:guideBook:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="guideBook">
			<tr>
				<!--td>
					<input type="checkbox" id="${guideBook.id}" value="${guideBook.id}" class="needBox"/>
				</td-->	
				<td>
				全称：${guideBook.name}	
				<c:if test="${fn:contains(guideBook.url,'http')}">
					<a target="_blank" href="${guideBook.url}">查看原文</a>
				</c:if>
				<br/>
				简称：${guideBook.alias}
				</td>	
				<td>
				类目：${guideBook.category.name}<br/>
				类型：${fns:getDictLabel(guideBook.type, 'guide_type', '')}
				</td>					
				<td>
				来源：${guideBook.origin}<br/>
				版本：${guideBook.revision}
				</td>
				<td>
					${guideBook.tags}
				</td>
				<td>
					${guideBook.description}
				</td>
				<td>
					状态：${fns:getDictLabel(guideBook.status, 'yes_no', '')}<br/>
					更新时间：<fmt:formatDate value="${guideBook.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="diy:guideBook:edit"><td>
    				<a href="${ctx}/diy/guideBook/form?id=${guideBook.id}">修改</a><br/>
					<a href="${ctx}/diy/guideBook/delete?id=${guideBook.id}" onclick="return confirmx('确认要删除该个性化定制指南吗？', this.href)">删除</a><br/>
					<a href="${ctx}/diy/guideTerm/list?book.id=${guideBook.id}&book.name=${guideBook.name}&category.id=${guideBook.category.id}">查看指南规则</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>