<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户属性定义管理</title>
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
		<li class="active"><a href="${ctx}/mod/userMeasure/">用户属性定义列表</a></li>
		<shiro:hasPermission name="mod:userMeasure:edit"><li><a href="${ctx}/mod/userMeasure/form?category.id=${treeId}">用户属性定义添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="userMeasure" action="${ctx}/mod/userMeasure/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>名称</th>
				<!--th>分类</th>
				<th>占比</th-->
				<th>属性定义</th>
				<th>属性类型</th>
				<!--th>归一化参考值</th-->
				<th>默认数值</th>
				<!--th>默认等级</th-->
				<th>标签</th>
				<th>归一化类型</th>
				<th>多值策略</th>				
				<th>自动标注</th>
				<th>Dict字典</th>
				<th>Refer类目</th>	
				<th>标签类目</th>			
				<!--th>更新时间</th-->
				<shiro:hasPermission name="mod:userMeasure:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userMeasure">
			<tr>
				<td><a href="${ctx}/mod/userMeasure/form?id=${userMeasure.id}">
					${userMeasure.name}
				</a></td>
				<!--td>
					${userMeasure.category.name}
				</td>	
				<td>
					${userMeasure.weight}
				</td-->
				<td>
					${userMeasure.property}
				</td>	
				<td>
					${fns:getDictLabel(userMeasure.type, 'propertyType', userMeasure.type)}
				</td>									
				<!--td>
					${userMeasure.controlValue}
				</td-->
				<td>
					${userMeasure.defaultScore}
				</td>			
				<!--td>
					${userMeasure.defaultRank}
				</td-->	
				<td>
					${userMeasure.tags}
				</td>		
				<td>${fns:getDictLabel(userMeasure.normalizeType, 'normalizeType', userMeasure.normalizeType)}</td>
				<td>${fns:getDictLabel(userMeasure.multiValueFunc, 'multiValueFunc', userMeasure.multiValueFunc)}</td>
				<td>${fns:getDictLabel(userMeasure.autoLabelType, 'autoLabelType', userMeasure.autoLabelType)}</td>
				<td>${fns:getDictLabel(userMeasure.autoLabelDict, 'autoLabelDictUser', userMeasure.autoLabelDict)}</td>	
				<td>${userMeasure.autoLabelCategory.name}</td>	
				<td>${userMeasure.autoLabelTagCategory.name}</td>												
				<!--td>
					<fmt:formatDate value="${userMeasure.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td-->
				<shiro:hasPermission name="mod:userMeasure:edit"><td>
    				<a href="${ctx}/mod/userMeasure/form?id=${userMeasure.id}">修改</a>
					<a href="${ctx}/mod/userMeasure/delete?id=${userMeasure.id}" onclick="return confirmx('确认要删除该用户属性定义吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>