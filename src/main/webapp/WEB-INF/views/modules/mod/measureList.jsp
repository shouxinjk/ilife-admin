<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品属性管理</title>
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
		<li class="active"><a href="${ctx}/mod/measure/">商品属性列表</a></li>
		<shiro:hasPermission name="mod:measure:edit"><li><a href="${ctx}/mod/measure/form?category.id=${treeId}">商品属性添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="measure" action="${ctx}/mod/measure/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
<!-- 			<li><label>类别：</label> -->
<%-- 				<sys:treeselect id="category" name="category.id" value="${measure.category.id}" labelName="category.name" labelValue="${measure.category.name}"  --%>
<%-- 				title="商品分类" url="/mod/itemCategory/treeData" cssClass="input-small" allowClear="true"/> --%>
<!-- 			</li> -->
			<li><label>关键字：</label>
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
				<th>类别</th>
				<!--th>维度</th-->
				<th>key</th>
				<th>名称</th>
				<!--th>占比</th>
				<th>参照值</th>
				<th>默认等级</th-->
				<th>alpha</th>
				<th>beta</th>
				<th>gamma</th>
				<th>delte</th>
				<th>epsilon</th>	
				<th>zeta</th>
				<th>eta</th>
				<th>theta</th>	
				<th>lambda</th>					
				<th>标签</th>
				<th>归一化类型</th>
				<th>多值策略</th>
				<th>标注类型</th>
				<th>Dict</th>
				<th>Refer</th>
				<shiro:hasPermission name="mod:measure:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="measure">
			<tr>
				<td>
					${measure.category.name}
				</td>
				<!--td>
					${measure.itemDimension.name}
				</td-->
				<td>
					${measure.property}
				</td>
				<td><a href="${ctx}/mod/measure/form?id=${measure.id}">
					${measure.name}
				</a></td>
				<!--td>
					${measure.percentage}
				</td>
				<td>
					${measure.controlValue}
				</td-->
				<td>
					${measure.defaultScore}
				</td>				
				<td>
					${measure.alpha}
				</td>
				<td>
					${measure.beta}
				</td>
				<td>
					${measure.gamma}
				</td>
				<td>
					${measure.delte}
				</td>
				<td>
					${measure.epsilon}
				</td>
				<td>
					${measure.zeta}
				</td>
				<td>
					${measure.eta}
				</td>
				<td>
					${measure.theta}
				</td>												
				<td>
					${measure.lambda}
				</td>				
				<td>
					${measure.tags}
				</td>	
				<td>${fns:getDictLabel(measure.normalizeType, 'normalizeType', '-')}</td>
				<td>${fns:getDictLabel(measure.multiValueFunc, 'multiValueFunc', '-')}</td>
				<td>${fns:getDictLabel(measure.autoLabelType, 'autoLabelType', '-')}</td>
				<td>${fns:getDictLabel(measure.autoLabelDict, 'autoLabelDictItem', '-')}</td>	
				<td>${measure.autoLabelCategory.name}</td>			
				<shiro:hasPermission name="mod:measure:edit"><td>
    				<a href="${ctx}/mod/measure/form?id=${measure.id}">修改</a>
					<a href="${ctx}/mod/measure/delete?id=${measure.id}" onclick="return confirmx('确认要删除该商品属性吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>