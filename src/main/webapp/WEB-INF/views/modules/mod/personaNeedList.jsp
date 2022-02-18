<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户需要构成管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			//遍历所有rate组件，初始化，并注册打分事件
			$(".rating").each(function(index){
				var itemId = $(this).data("id");
				var itemValue = Number($(this).data("weight"))*0.1;//默认采用1-100存储
				console.log("now walk through all pending items.",itemId,itemValue);
				//初始化
				$("#rate-"+itemId).starRating({
					totalStars: 10,
					starSize:20,
				    useFullStars:false,
				    initialRating: itemValue,
				    ratedColors:['#8b0000', '#dc143c', '#ff4500', '#ff6347', '#1e90ff','#00ffff','#40e0d0','#9acd32','#32cd32','#228b22'],
				    callback: function(currentRating, $el){
				        // make a server call here
				        console.log("dude, now try update rating.[old]"+itemValue,itemId,currentRating);
				        $.ajax({
				            type: "GET",
				            url: "${ctx}/mod/personaNeed/rest/weight?id="+itemId+"&weight="+(currentRating*10),
				            headers:{
				                "Content-Type":"application/json",
				                "Accept":"application/json"
				            },        
				            success:function(result){
				                if(result.result=="error"){
				                   console.log("update weight failed.",result);   
				                }else{
				                	console.log("update weight succeed.",result);                   
				                }
				            }                
				        }); 
				    }
				});
			});
			
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
		<li class="active"><a href="${ctx}/mod/personaNeed/?treeId=${pid}&treeModule=${pType}">需要列表</a></li>
		<shiro:hasPermission name="mod:personaNeed:edit"><li><a href="${ctx}/mod/personaNeed/form?pid=${pid}&pType=${pType}">需要添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="personaNeed" action="${ctx}/mod/personaNeed/?treeId=${pid}&treeModule=${pType}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<!--th>阶段</th-->
				<th>需要类别</th>
				<th>需要</th>
				<th>权重</th>
				<th>表达式</th>
				<th>描述</th>
				<!--th>创建时间</th-->
				<th>最后更新</th>
				<shiro:hasPermission name="mod:personaNeed:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="personaNeed">
			<tr>
				<!--td>
					${personaNeed.phase.name}
				</td-->
				<td>
					${personaNeed.needCategory}
				</td>				
				<td>
					${fns:getDictLabel(personaNeed.need.type, 'need_type', personaNeed.need.type)} ${personaNeed.need.name} (${personaNeed.need.displayName})
				</td>
				<td>
					<div class="rating" id="rate-${personaNeed.id}" data-id="${personaNeed.id}" data-weight="${personaNeed.weight}"></div>
				</td>
				<td>
					${personaNeed.expression}
				</td>
				<td>
					${personaNeed.description}
				</td>
				<!--td><fmt:formatDate value="${personaNeed.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td-->
				<td><fmt:formatDate value="${personaNeed.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<shiro:hasPermission name="mod:personaNeed:edit"><td>
    				<a href="${ctx}/mod/personaNeed/form?id=${personaNeed.id}&pid=${pid}&pType=${pType}">修改</a>
					<a href="${ctx}/mod/personaNeed/delete?id=${personaNeed.id}&pid=${pid}&pType=${pType}" onclick="return confirmx('确认要删除该需要吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>