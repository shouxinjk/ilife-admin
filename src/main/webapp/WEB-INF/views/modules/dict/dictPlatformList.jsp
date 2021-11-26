<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>电商平台字典管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//遍历所有rate组件，初始化，并注册打分事件
			$(".rating").each(function(index){
				var itemId = $(this).data("id");
				var itemValue = $(this).data("markedvalue");
				console.log("now walk through all pending items.",itemId,itemValue);
				//初始化
				$("#rate-"+itemId).starRating({
					totalStars: 10,
					starSize:20,
				    useFullStars:true,
				    initialRating: itemValue,
				    ratedColors:['#8b0000', '#dc143c', '#ff4500', '#ff6347', '#1e90ff','#00ffff','#40e0d0','#9acd32','#32cd32','#228b22'],
				    callback: function(currentRating, $el){
				        // make a server call here
				        console.log("dude, now try update rating.[old]"+itemValue,itemId,currentRating);
				        $.ajax({
				            type: "GET",
				            url: $("#sxContextPath").val()+"/dict/dictPlatform/rest/updateMarkedValue?id="+itemId+"&markedValue="+currentRating,
				            headers:{
				                "Content-Type":"application/json",
				                "Accept":"application/json"
				            },        
				            success:function(result){
				                if(result.result=="error"){
				                   console.log("update markedvalue failed.",result);   
				                }else{
				                	console.log("update markedvalue succeed.",result);                   
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
		<li class="active"><a href="${ctx}/dict/dictPlatform/">电商平台字典列表</a></li>
		<shiro:hasPermission name="dict:dictPlatform:edit"><li><a href="${ctx}/dict/dictPlatform/form">电商平台字典添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="dictPlatform" action="${ctx}/dict/dictPlatform/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="sxContextPath" name="sxContextPath" type="hidden" value="${ctx}"/>
		<ul class="ul-form">
			<li><label>类别：</label>
				<form:input path="category" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>字典值：</label>
				<form:input path="label" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>字典分：</label>
				<form:input path="score" htmlEscape="false" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>Logo</th>
				<th>类别</th>
				<th>平台名称</th>
				<th>字典分</th>
				<th>描述</th>
				<th>修改时间</th>
				<shiro:hasPermission name="dict:dictPlatform:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="dictPlatform">
			<tr>
				<td>
					<img src="${dictPlatform.logo}" alt="${dictPlatform.label}" style="height:40px"/>
				</td>			
				<td>
					${dictPlatform.category}
				</td>
				<td><a href="${ctx}/dict/dictPlatform/form?id=${dictPlatform.id}">
					${dictPlatform.label}
				</a></td>
				<td>
					<div class="rating" id="rate-${dictPlatform.id}" data-id="${dictPlatform.id}" data-markedValue="${dictPlatform.score}"></div>
				</td>				
				<td>
					${dictPlatform.description}
				</td>
				<td>
					<fmt:formatDate value="${dictPlatform.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="dict:dictPlatform:edit"><td>
    				<a href="${ctx}/dict/dictPlatform/form?id=${dictPlatform.id}">修改</a>
					<a href="${ctx}/dict/dictPlatform/delete?id=${dictPlatform.id}" onclick="return confirmx('确认要删除该电商平台字典吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>