<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户属性VALS标注</title>
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
		<li class="active"><a href="${ctx}/mod/personaMeasure/?treeId=${pid}&treeModule=${pType}">VALS标注列表</a></li>
		<shiro:hasPermission name="mod:personaMeasure:edit"><li><a href="${ctx}/mod/personaMeasure/form?pid=${pid}&pType=${pType}">VALS标注添加</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>阶段</th>
				<th>属性</th>
				<th>权重</th>
				<th>效益：生理</th>
				<th>效益：安全</th>
				<th>效益：情感</th>
				<th>效益：尊重</th>
				<th>效益：价值</th>	
				<th>成本：经济</th>
				<th>成本：社会</th>
				<th>成本：文化</th>	
				<th>偏好脚本</th>						
				<th>描述</th>
				<th>创建时间</th>
				<th>最后更新</th>
				<shiro:hasPermission name="mod:personaMeasure:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="personaMeasure">
			<tr>
				<td>
					${personaMeasure.phase.name}
				</td>
				<td>
					${personaMeasure.measure.name}
				</td>
				<td>
					${personaMeasure.weight}
				</td>
				<td>
					${personaMeasure.alpha}
				</td>
				<td>
					${personaMeasure.beta}
				</td>
				<td>
					${personaMeasure.gamma}
				</td>
				<td>
					${personaMeasure.delte}
				</td>
				<td>
					${personaMeasure.epsilon}
				</td>
				<td>
					${personaMeasure.zeta}
				</td>
				<td>
					${personaMeasure.eta}
				</td>
				<td>
					${personaMeasure.theta}
				</td>												
				<td>
					${personaMeasure.lambda}
				</td>				
				<td>
					${personaMeasure.description}
				</td>
				<td><fmt:formatDate value="${personaMeasure.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td><fmt:formatDate value="${personaMeasure.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<shiro:hasPermission name="mod:personaMeasure:edit"><td>
    				<a href="${ctx}/mod/personaMeasure/form?id=${personaMeasure.id}&pid=${pid}&pType=${pType}">修改</a>
					<a href="${ctx}/mod/personaMeasure/delete?id=${personaMeasure.id}&pid=${pid}&pType=${pType}" onclick="return confirmx('确认要删除该标注吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>