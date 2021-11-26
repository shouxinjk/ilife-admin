<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>品牌字典管理</title>
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
				            url: $("#sxContextPath").val()+"/dict/dictBrand/rest/updateMarkedValue?id="+itemId+"&markedValue="+currentRating,
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
		<li class="active"><a href="${ctx}/dict/dictBrand/">品牌字典列表</a></li>
		<shiro:hasPermission name="dict:dictBrand:edit"><li><a href="${ctx}/dict/dictBrand/form?treeId=${treeId}&pId=${pId}&pType=${pType}">品牌字典添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="dictBrand" action="${ctx}/dict/dictBrand/?treeId=${treeId}&pId=${pId}&treeModule=${pType}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="sxContextPath" name="sxContextPath" type="hidden" value="${ctx}"/>
		<ul class="ul-form">
			<li><label>国家：</label>
				<form:input path="country" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>等级描述：</label>
				<form:select path="description" class="input-medium">
					<form:option value="" label=""/>
					<form:option value="" label="不限"/>
					<form:options items="${fns:getDictList('brandGrade')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>品牌名称：</label>
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
				<th>品牌名称</th>			
				<th>Logo</th>
				<th>国家</th>
				<th>等级描述</th>
				<th>所属类目</th>
				<th>字典分</th>
				<th>修改时间</th>
				<shiro:hasPermission name="dict:dictBrand:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="dictBrand">
			<tr>
				<td><a href="${ctx}/dict/dictBrand/form?id=${dictBrand.id}">
					${dictBrand.label}
				</a></td>			
				<td>
					<img src="${dictBrand.logo}" alt="${dictBrand.label}" style="height:40px"/>
				</td>
				<td>
					${dictBrand.country}
				</td>
				<td>
					${fns:getDictLabel(dictBrand.description, 'brandGrade', '')}
				</td>
				<td>
					${dictBrand.category.name}
				</td>
				<td>
					<div class="rating" id="rate-${dictBrand.id}" data-id="${dictBrand.id}" data-markedValue="${dictBrand.score}"></div>
				</td>				
				<td>
					<fmt:formatDate value="${dictBrand.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="dict:dictBrand:edit"><td>
    				<a href="${ctx}/dict/dictBrand/form?id=${dictBrand.id}&treeId=${treeId}&pId=${pId}&pType=${pType}">修改</a>
					<a href="${ctx}/dict/dictBrand/delete?id=${dictBrand.id}&treeId=${treeId}&pId=${pId}&pType=${pType}" onclick="return confirmx('确认要删除该品牌字典吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>