<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>阶段需要构成管理</title>
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
				            url: "${ctx}/mod/phaseNeed/rest/weight?id="+itemId+"&weight="+(currentRating*10),
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
		<li class="active"><a href="${ctx}/mod/phaseNeed/">阶段需要构成列表</a></li>
		<shiro:hasPermission name="mod:phaseNeed:edit"><li><a href="${ctx}/mod/phaseNeed/form?id=${phaseNeed.id}&pid=${pid}&pType=${pType}">阶段需要构成添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="phaseNeed" action="${ctx}/mod/phaseNeed/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>阶段：</label>
				<form:input path="phase.id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>需要：</label>
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
				<th>阶段</th>
				<th>类别</th>
				<th>需要</th>
				<th>别名</th>
				<th>权重</th>
				<th>表达式</th>
				<th>描述</th>			
				<th>更新时间</th>
				<shiro:hasPermission name="mod:phaseNeed:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="phaseNeed">
			<tr>
				<td>
					${phaseNeed.phase.name}
				</td>
				<td>
					${phaseNeed.needCategory}
				</td>				
				<td>
					${phaseNeed.need.name}
				</td>
				<td>
					${phaseNeed.need.displayName}
				</td>				
				<td>
					<div class="rating" id="rate-${phaseNeed.id}" data-id="${phaseNeed.id}" data-weight="${phaseNeed.weight}"></div>
				</td>
				<td>
					${phaseNeed.expression}
				</td>
				<td>
					${phaseNeed.description}
				</td>		
				<td>
					<fmt:formatDate value="${phaseNeed.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:phaseNeed:edit"><td>
    				<a href="${ctx}/mod/phaseNeed/form?id=${phaseNeed.id}">修改</a>
					<a href="${ctx}/mod/phaseNeed/delete?id=${phaseNeed.id}" onclick="return confirmx('确认要删除该阶段需要构成吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>