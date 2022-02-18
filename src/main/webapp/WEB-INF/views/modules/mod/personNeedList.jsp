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
				var itemValue = Number($(this).data("weight"));//*10;//默认采用1-10存储
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
				            url: "${ctx}/mod/personNeed/rest/weight?id="+itemId+"&weight="+(currentRating),
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
		<li><a href="${ctx}/ope/person/">用户列表</a></li>
		<li class="active"><a href="${ctx}/mod/personNeed/">需要列表</a></li>
		<shiro:hasPermission name="mod:personNeed:edit"><li><a href="${ctx}/mod/personNeed/form?person.id=${personNeed.person.id}">需要添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="personNeed" action="${ctx}/mod/personNeed/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>关联的用户：</label>
				<form:input path="person.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>关联的动机：</label>
				<form:input path="need.id" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户</th>
				<th>需要</th>
				<th>强度</th>
				<th>描述</th>
				<th>表达式</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:personNeed:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="personNeed">
			<tr>
				<td>
					${personNeed.person.nickname}
				</td>
				<td>
					${fns:getDictLabel(personNeed.need.type, 'need_type', personNeed.need.type)} ${personNeed.need.name} (${personNeed.need.displayName})
				</td>
				<td>
					<div class="rating" id="rate-${personNeed.id}" data-id="${personNeed.id}" data-weight="${personNeed.weight}"></div>
				</td>					
				<td>
					${personNeed.description}
				</td>							
				<td>
					${personNeed.expression}
				</td>
				<td>
					<fmt:formatDate value="${personNeed.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:personNeed:edit"><td>
    				<a href="${ctx}/mod/personNeed/form?id=${personNeed.id}">修改</a>
					<a href="${ctx}/mod/personNeed/delete?id=${personNeed.id}" onclick="return confirmx('确认要删除该需要吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>