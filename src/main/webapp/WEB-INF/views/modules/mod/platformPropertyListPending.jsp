<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>电商平台属性映射管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			//遍历所有标准属性下拉组件，初始化，并注册打分事件
			$("select[id^=measure]").each(function(index){
				var itemId = $(this).data("id");
				var categoryId = $(this).data("categoryid");
				console.log("now walk through all items.",itemId,categoryId);
				
				//根据categoryId查询属性及继承属性，并添加到对应的select下
				console.log("try to load props by categoryId.",categoryId);
		        $.ajax({
		            type: "GET",
		            url: "${ctx}/mod/measure/measures?category="+categoryId+"&cascade=true",
		            headers:{
		                "Content-Type":"application/json",
		                "Accept":"application/json"
		            },        
		            success:function(result){
		                console.log("got measure list.",result);
		                result.forEach(item =>{
		    		        $("#measure"+itemId).append('<option value="'+item.id+'">'+item.name+'</option>');
		    		    });
		            }                
		        }); 
				
				//注册change事件：提交修改
				$(this).on('change', function(evt, params) {
					console.log("now change measure...",evt, params);
			        $.ajax({
			            type: "PATCH",
			            url: "${ctx}/mod/platformProperty/rest/mapping?id="+itemId+"&measureId="+evt.val,
			            headers:{
			                "Content-Type":"application/json",
			                "Accept":"application/json"
			            },        
			            success:function(result){
			                console.log("got measure list.",result);
			                if(result.success){
			                	siiimpleToast.message('已更新',{
					                  position: 'bottom|center'
					                });
			                }else{
			                	siiimpleToast.message('啊哦，好像出错了',{
					                  position: 'bottom|center'
					                });
			                }
			                
			            }                
			        }); 
				});
			});
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		//显示所有属性，而不只是props.xxx
        function filterProps() {
            loading('正在提交，请稍等...');
            $("#searchForm").attr("action", "${ctx}/mod/platformProperty/list?filterAll=true");
            $("#searchForm").submit();
        }	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mod/platformProperty/listPending">待标注</a></li>
		<li><a href="${ctx}/mod/platformProperty/list">已标注</a></li>
		<shiro:hasPermission name="mod:platformProperty:edit"><li><a href="${ctx}/mod/platformProperty/form?platform=${treeId}">属性映射添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="platformProperty" action="${ctx}/mod/platformProperty/listPending" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-small"/>
			</li>
			<li><label>类目：</label>
				<form:input path="cname" htmlEscape="false" maxlength="100" class="input-small"/>
			</li>
			<li><label>平台：</label>
				<form:select path="platform" class="input-small">
					<form:option value="" label="不限"/>
					<form:options items="${fns:getDictList('platform')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>	
			</li>							
			<!--li><label>原始类目：</label>
				<sys:treeselect id="platformCategory" name="platformCategory.id" value="${platformProperty.platformCategory.id}" labelName="platformCategory.name" labelValue="${platformProperty.platformCategory.name}"
					title="原始类目" url="/mod/platformCategory/treeData" notAllowSelectRoot="false"/>	
			</li-->
			<li><label>属性：</label>
				<sys:treeselect id="measure" name="measure.id" value="${platformProperty.measure.id}" labelName="measure.name" labelValue="${platformProperty.measure.name}"
					title="关键属性" url="/mod/measure/treeData"/>
			</li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>				
				<input id="FilterProps" class="btn btn-primary" type="button" value="查询非Props属性" onclick="filterProps();"/>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>名称</th>
				<th>来源平台</th>
				<th>原始类目</th>
				<th>标准类目</th>
				<th>标准属性</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:platformProperty:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="platformProperty">
			<tr>
				<td><a href="${ctx}/mod/platformProperty/form?id=${platformProperty.id}">
					<c:choose>
				         <c:when test = "${platformProperty.propName == platformProperty.name}">
				            ${platformProperty.propName}
				         </c:when>				         			         			         			         
				         <c:otherwise>
				            ${platformProperty.propName}(${platformProperty.name})
				         </c:otherwise>		
			         </c:choose>			
				</a></td>
				<td>${fns:getDictLabel(platformProperty.platform, 'platform', platformProperty.platform)}</td>
				<td>
					<c:choose>
						<c:when test = "${empty platformProperty.platformCategory.name}">
				            ${platformProperty.cname}
				         </c:when>				         			         			         			         
				         <c:otherwise>
				            ${platformProperty.cname}(${platformProperty.platformCategory.name})
				         </c:otherwise>	
			         </c:choose>
				</td>
				<td>${platformProperty.category.name}</td>
				<td>
					<c:choose>
						<c:when test = "${not empty platformProperty.measure.name}">
							<select data-placeholder="${platformProperty.measure.name}" class="select" tabindex="1" id="measure${platformProperty.id}" data-id="${platformProperty.id}" data-categoryid="${platformProperty.category.id}">
					            <option value=""></option>
					            <option value="${platformProperty.measure.id}">${platformProperty.measure.name}</option>
				            </select>	
			             </c:when>				         			         			         			         
				         <c:otherwise>	
							<select data-placeholder="请选择" class="select" tabindex="1" id="measure${platformProperty.id}" data-id="${platformProperty.id}" data-categoryId="${platformProperty.category.id}">
					            <option value=""></option>
				            </select>				         	
				         </c:otherwise>	
			         </c:choose>	
				</td>
				<td>
					<fmt:formatDate value="${platformProperty.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:platformProperty:edit"><td>
    				<a href="${ctx}/mod/platformProperty/form?id=${platformProperty.id}">修改</a>
					<a href="${ctx}/mod/platformProperty/delete?id=${platformProperty.id}" onclick="return confirmx('确认要删除该电商平台属性映射吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>