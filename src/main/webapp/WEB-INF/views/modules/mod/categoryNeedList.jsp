<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>品类需要满足管理</title>
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
				            url: "${ctx}/mod/categoryNeed/rest/weight?id="+itemId+"&weight="+(currentRating),
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
		
		//从父节点复制需要列表
        function inherit() {
            loading('正在提交，请稍等...');
            $("#searchForm").attr("action", "${ctx}/mod/categoryNeed/inherit?id=${pid}");
            $("#searchForm").submit();
        }
		
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
		<li class="active"><a href="${ctx}/mod/categoryNeed/?treeId=${pid}&treeModule=${pType}">需要列表</a></li>
		<shiro:hasPermission name="mod:categoryNeed:edit"><li><a href="${ctx}/mod/categoryNeed/list2?treeId=${pid}&treeModule=${pType}">需要添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="categoryNeed" action="${ctx}/mod/categoryNeed/?treeId=${pid}&treeModule=${pType}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>需要：</label>
				<form:input path="need.name" htmlEscape="false" maxlength="100" class="input-small"/>
			</li>	
			<li><label>类型：</label>
				<form:select path="need.type" class="input-small">
					<form:option value="" label="不限"/>
					<form:options items="${fns:getDictList('need_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>	
			</li>												
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>				
			</li>
			<shiro:hasPermission name="mod:itemCategory:edit">
				<li class="btns">
					<input id="btnCloneFromParent" class="btn btn-primary" type="button" value="从父节点克隆需要" onclick="inherit();"/>
				</li>
			</shiro:hasPermission>		
			<li class="clearfix"></li>
		</ul>		
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>商品类目</th>
				<th>类型</th>
				<th>需要</th>
				<th>满足度</th>
				<th>表达式</th>
				<th>描述</th>
				<!--th>创建时间</th-->
				<th>最后更新</th>
				<shiro:hasPermission name="mod:categoryNeed:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="categoryNeed">
			<tr>
			<td>
				${categoryNeed.category.name}
			</td>			
			<td width="80">
			      <c:choose>
			         <c:when test = "${categoryNeed.need.type == 'alpha'}">
			            <span style="background-color:#A49EE2;padding:2px 5px;color:#fff;">生存需要</span>
			         </c:when>			
			         <c:when test = "${categoryNeed.need.type == 'beta'}">
			            <span style="background-color:#40B4E7;padding:2px 5px;color:#fff;">安全需要</span>
			         </c:when>	
			         <c:when test = "${categoryNeed.need.type == 'gamma'}">
			            <span style="background-color:#8BCE2D;padding:2px 5px;color:#fff;">爱与归属</span>
			         </c:when>	
			         <c:when test = "${categoryNeed.need.type == 'delte'}">
			            <span style="background-color:#F6B100;padding:2px 5px;color:#fff;">尊重需要</span>
			         </c:when>	
			         <c:when test = "${categoryNeed.need.type == 'epsilon'}">
			            <span style="background-color:#E85552;padding:2px 5px;color:#fff;">自我实现</span>
			         </c:when>	
			         			         			         			         			         
			         <c:otherwise>
			            <span style="background-color:#000;padding:2px 5px;color:#fff;">未知</span>
			         </c:otherwise>
			      </c:choose>				
				</td>						
							
				<td>
					${fns:getDictLabel(categoryNeed.need.type, 'need_type', categoryNeed.need.type)} ${categoryNeed.need.name} (${categoryNeed.need.displayName})
				</td>				
				<td>
					<div class="rating" id="rate-${categoryNeed.id}" data-id="${categoryNeed.id}" data-weight="${categoryNeed.weight}"></div>
				</td>
				<td>
					${categoryNeed.expression}
				</td>
				<td>
					${categoryNeed.description}
				</td>
				<!--td><fmt:formatDate value="${categoryNeed.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td-->
				<td><fmt:formatDate value="${categoryNeed.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<shiro:hasPermission name="mod:categoryNeed:edit"><td>
    				<a href="${ctx}/mod/categoryNeed/form?id=${categoryNeed.id}&pid=${pid}&pType=${pType}">修改</a>
					<a href="${ctx}/mod/categoryNeed/delete?id=${categoryNeed.id}&pid=${pid}&pType=${pType}" onclick="return confirmx('确认要删除该需要吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>