<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>标注管理</title>
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
				            url: "${ctx}/ope/performance/rest/updateMarkedValue?id="+itemId+"&level="+currentRating+"&markedValue="+currentRating,
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
		<li class="active"><a href="${ctx}/ope/performance/listPending?treeId=${treeId}&treeModule=${pType}">待标注</a></li>
		<li><a href="${ctx}/ope/performance/list?treeId=${treeId}&treeModule=${pType}">已标注</a></li>
		<shiro:hasPermission name="ope:performance:edit"><li><a href="${ctx}/ope/performance/form?treeId=${treeId}&pId=${pId}&pType=${pType}">标注添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="performance" action="${ctx}/ope/performance/listPending?treeId=${treeId}&pId=${pId}&treeModule=${pType}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>原始值：</label>
				<form:input path="originalValue" htmlEscape="false" maxlength="100" class="input-small"/>
			</li>
			<li><label>属性：</label>
				<sys:treeselect id="measure" name="measure.id" value="${performance.measure.id}" labelName="measure.name" labelValue="${performance.measure.name}"
					title="关键属性" url="/mod/measure/treeData"/>
			</li>										
			<li><label>类目：</label>
				<sys:treeselect id="category" name="category.id" value="${performance.category.id}" labelName="category.name" labelValue="${performance.category.name}"
					title="标准类目" url="/mod/itemCategory/treeData" notAllowSelectRoot="false"/>	
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
				<th>所属类目</th>
				<th>关键属性</th>
				<th>原始值/Label</th>
				<!--th>所属维度</th>
				<th>等级</th>
				<th>归一值</th>
				<th>对照值</th-->
				<th>标注值/mvalue</th>
				<th>已同步?</th>
				<th>更新者</th>
				<th>更新时间</th>
				<shiro:hasPermission name="ope:performance:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="performance">
			<tr>
				<td>
					${performance.category.name}
				</td>			
				<td>
					<a href="${ctx}/mod/measure/form?id=${performance.measure.id}&category.id=${performance.category.id}">
						${performance.measure.name}
					</a>
				</td>
				<td>
					${performance.originalValue}
				</td>				
				<!--td>
					${performance.dimension}
				</td>								
				<td>
					${performance.level}
				</td>
				<td>
					${performance.normalizedValue}
				</td>
				<td>
					${performance.controlValue}
				</td>
				<td>
					${performance.markedValue}
				</td-->
				<td>
					<div class="rating" id="rate-${performance.id}" data-id="${performance.id}" data-markedValue="${performance.markedValue}"></div>
				</td>	
				<td>
					${fns:getDictLabel(performance.isReady, 'yes_no', '')}
				</td>							
				<td>
					${performance.updateBy.name}
				</td>
				<td>
					<fmt:formatDate value="${performance.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="ope:performance:edit"><td>
    				<a href="${ctx}/ope/performance/form?id=${performance.id}&treeId=${treeId}&pId=${pId}&pType=${pType}">修改</a>
					<a href="${ctx}/ope/performance/delete?id=${performance.id}&treeId=${treeId}&pId=${pId}&pType=${pType}" onclick="return confirmx('确认要删除该标注吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>