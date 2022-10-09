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
				var itemValue = Number($(this).data("weight"));//默认采用1-10存储
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
				            url: "${ctx}/mod/phaseNeed/rest/weight?id="+itemId+"&weight="+(currentRating),
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
            $("#searchForm").attr("action", "${ctx}/mod/phaseNeed/inherit?id=${pid}");
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
		<li class="active"><a href="${ctx}/mod/phaseNeed/">阶段需要构成</a></li>
		<!--
			<shiro:hasPermission name="mod:phaseNeed:edit"><li><a href="${ctx}/mod/phaseNeed/form?id=${phaseNeed.id}&pid=${pid}&pType=${pType}">阶段需要添加</a></li></shiro:hasPermission>
		-->
		<shiro:hasPermission name="mod:phaseNeed:edit"><li><a href="${ctx}/mod/phaseNeed/list2?pid=${pid}&pType=${pType}">阶段需要添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="phaseNeed" action="${ctx}/mod/phaseNeed/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<form:hidden path="phase.id" htmlEscape="false" maxlength="100" class="input-small"/>
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
			<shiro:hasPermission name="mod:phaseNeed:edit">
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
				<th>阶段</th>
				<th>类型</th>
				<th>类别</th>
				<th>需要</th>
				<!--th>别名</th-->
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
				<td width="80">
			      <c:choose>
			         <c:when test = "${phaseNeed.need.type == 'alpha'}">
			            <span style="background-color:#A49EE2;padding:2px 5px;color:#fff;">生存需要</span>
			         </c:when>			
			         <c:when test = "${phaseNeed.need.type == 'beta'}">
			            <span style="background-color:#40B4E7;padding:2px 5px;color:#fff;">安全需要</span>
			         </c:when>	
			         <c:when test = "${phaseNeed.need.type == 'gamma'}">
			            <span style="background-color:#8BCE2D;padding:2px 5px;color:#fff;">爱与归属</span>
			         </c:when>	
			         <c:when test = "${phaseNeed.need.type == 'delte'}">
			            <span style="background-color:#F6B100;padding:2px 5px;color:#fff;">尊重需要</span>
			         </c:when>	
			         <c:when test = "${phaseNeed.need.type == 'epsilon'}">
			            <span style="background-color:#E85552;padding:2px 5px;color:#fff;">自我实现</span>
			         </c:when>	
			         			         			         			         			         
			         <c:otherwise>
			            <span style="background-color:#000;padding:2px 5px;color:#fff;">未知</span>
			         </c:otherwise>
			      </c:choose>				
				</td>					
				<td>
					${phaseNeed.needCategory}
				</td>				
				<td>
					${fns:getDictLabel(phaseNeed.need.type, 'need_type', phaseNeed.need.type)} ${phaseNeed.need.name} (${phaseNeed.need.displayName})
				</td>
				<!--td>
					${phaseNeed.need.displayName}
				</td-->				
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