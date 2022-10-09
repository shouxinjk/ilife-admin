<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户标注管理</title>
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
				            url: "${ctx}/ope/userPerformance/rest/updateMarkedValue?id="+itemId+"&level="+currentRating+"&markedValue="+currentRating,
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
		<li><a href="${ctx}/ope/userPerformance/listPending?treeId=${pid}&treeModule=${pType}">待标注</a></li>
		<li class="active"><a href="${ctx}/ope/userPerformance/list?treeId=${pid}&treeModule=${pType}">已标注</a></li>
		<shiro:hasPermission name="ope:userPerformance:edit"><li><a href="${ctx}/ope/userPerformance/form?pid=${pid}&pType=${pType}">标注添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="userPerformance" action="${ctx}/ope/userPerformance/list?treeId=${pid}&treeModule=${pType}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>原始值：</label>
				<form:input path="originalValue" htmlEscape="false" maxlength="100" class="input-small"/>
			</li>
			<li><label>属性：</label>
				<sys:treeselect id="measure" name="measure.id" value="${userPerformance.measure.id}" labelName="measure.name" labelValue="${userPerformance.measure.name}"
					title="关键属性" url="/mod/userMeasure/treeData"/>
			</li>										
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>				
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>关键属性</th>
				<th>原始值</th>
				<th>标注值</th>				
				<!--th>归一值</th>
				<th>对照值</th>
				<th>等级</th>
				<th>所属维度</th-->
				<th>已同步?</th>
				<th>更新者</th>
				<th>更新时间</th>
				<shiro:hasPermission name="ope:userPerformance:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userPerformance">
			<tr>
				<td><a href="${ctx}/ope/userPerformance/form?id=${userPerformance.id}">
					${userPerformance.measure.name}
				</a></td>
				<td>
					${userPerformance.originalValue}
				</td>
				<td>
					<div class="rating" id="rate-${userPerformance.id}" data-id="${userPerformance.id}" data-markedValue="${userPerformance.markedValue}"></div>
				</td>				
				<!--td>
					${userPerformance.normalizedValue}
				</td>
				<td>
					${userPerformance.controlValue}
				</td>
				<td>
					${userPerformance.level}
				</td>
				<td>
					${userPerformance.dimension.name}
				</td-->
				<td>
					${fns:getDictLabel(userPerformance.isReady, 'yes_no', userPerformance.isReady)}
				</td>				
				<td>
					${userPerformance.updateBy.name}
				</td>
				<td>
					<fmt:formatDate value="${userPerformance.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="ope:userPerformance:edit"><td>
    				<a href="${ctx}/ope/userPerformance/form?id=${userPerformance.id}&pid=${pid}&pType=${pType}">修改</a>
					<a href="${ctx}/ope/userPerformance/delete?id=${userPerformance.id}&pid=${pid}&pType=${pType}" onclick="return confirmx('确认要删除该标注吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>