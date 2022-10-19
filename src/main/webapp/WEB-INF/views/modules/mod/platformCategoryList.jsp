<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>电商平台类目映射管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

			 //注册逐条映射事件：通过顶部选择目标类目ID后完成
		    $("a[id^=btnMapping]").click(function(){
		    	var targetCategoryId = $("#categoryId").val();
		    	if(!targetCategoryId || targetCategoryId.trim().length==0 || targetCategoryId.indexOf("null")>-1){
		    		console.log("no target category.",targetCategoryId);
		            siiimpleToast.message('请选择目标类目',{
		              position: 'bottom|center'
		            }); 
		            return;
		    	}
		    	console.log("got target category.",targetCategoryId);
		        var selectedPlatformCategories = [];
		        selectedPlatformCategories.push($(this).data("id"));  
		        if(selectedPlatformCategories.length>0){
		            console.log("try mapping categories.",selectedPlatformCategories);
				    $.ajax({
				        url:"${ctx}/mod/platformCategory/rest/mapping",
				        type:"PATCH",     
				        data:JSON.stringify({
				            ids: selectedPlatformCategories,
				            targetId: targetCategoryId
				        }),
				        headers:{
				            "Content-Type":"application/json"
				        },  
				        success:function(ret){
				            console.log("===got mapping result===\n",ret);
				            if(ret.success){
					            siiimpleToast.message('哦耶，映射已完成',{
						              position: 'bottom|center'
						            });   
				            }else{
					            siiimpleToast.message('糟糕，出错了，请重新尝试',{
						              position: 'bottom|center'
						            });   
				            }
				        }
				    });          
		        }else{
		            console.log("no category selected.");
		            siiimpleToast.message('请选择要映射的类目先~~',{
		              position: 'bottom|center'
		            });             
		        }
		               
		    });		
			
		    //注册批量提交事件
		    $("#btnBatchMapping").click(function(){
		    	var targetCategoryId = $("#categoryId").val();
		    	if(!targetCategoryId || targetCategoryId.trim().length==0){
		    		console.log("no target category.",targetCategoryId);
		            siiimpleToast.message('请选择目标类目',{
		              position: 'bottom|center'
		            }); 
		            return;
		    	}
		    	console.log("got target category.",targetCategoryId);
		        var selectedPlatformCategories = [];
		        $("input[name='platformcategories']:checked").each(function(){
		            selectedPlatformCategories.push($(this).val());
		        });   
		        if(selectedPlatformCategories.length>0){
		            console.log("try mapping categories.",selectedPlatformCategories);
				    $.ajax({
				        url:"${ctx}/mod/platformCategory/rest/mapping",
				        type:"PATCH",     
				        data:JSON.stringify({
				            ids: selectedPlatformCategories,
				            targetId: targetCategoryId
				        }),
				        headers:{
				            "Content-Type":"application/json"
				        },  
				        success:function(ret){
				            console.log("===got mapping result===\n",ret);
				            if(ret.success){
					            siiimpleToast.message('哦耶，批量映射已完成',{
						              position: 'bottom|center'
						            });   
				            }else{
					            siiimpleToast.message('糟糕，出错了，请重新尝试',{
						              position: 'bottom|center'
						            });   
				            }
				        }
				    });          
		        }else{
		            console.log("no category selected.");
		            siiimpleToast.message('请选择要映射的类目先~~',{
		              position: 'bottom|center'
		            });             
		        }
		               
		    });			
			
		    //注册选中全部事件
		    $("#btnCheckAll").click(function(){
		        $("input[name='platformcategories']").prop("checked","true"); 
		    });
		    //注册取消选中事件
		    $("#btnUncheckAll").click(function(){
		        $("input[name='platformcategories']").removeAttr("checked"); 
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
		<li><a href="${ctx}/mod/platformCategory/listPending?treeId=${treeId}">待标注</a></li>
		<li class="active"><a href="${ctx}/mod/platformCategory/list?treeId=${treeId}">已标注</a></li>
		<shiro:hasPermission name="mod:platformCategory:edit"><li><a href="${ctx}/mod/platformCategory/form?platform=${treeId}">类目映射添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="platformCategory" action="${ctx}/mod/platformCategory/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>		
			<li><label>来源平台：</label>
				<form:select path="platform" class="input-medium">
					<form:option value="" label="不限"/>
					<form:options items="${fns:getDictList('platform')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>	
			</li>				
			<li><label>标准类目：</label>
				<sys:treeselect id="category" name="category.id" value="${platformCategory.category.id}" labelName="category.name" labelValue="${platformCategory.category.name}"
					title="标准类目" url="/mod/itemCategory/treeData" notAllowSelectRoot="true" cssClass="required"/>				
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			
			<li>
				<div><a id='btnCheckAll' href='#' style='margin-left:10px;'>选中全部</a></div>
			</li>
			<li>
	   			<div><a id='btnUncheckAll' href='#' style='margin-left:10px;'>取消选择</a></div>  
	   		</li>
	   		<li>
	    		<div><a id='btnBatchMapping' href='#' style='margin-left:10px;'>批量类目映射</a></div>      
			</li>			
			
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th width="50px" align="center" style="text-align:center">#</th>
				<th>名称</th>
				<th>来源平台</th>
				<th>标准类目</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:platformCategory:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="platformCategory">
			<tr>
				<td align="center" style="text-align:center">
					<input id="pcat${platformCategory.id}" name="platformcategories" type="checkbox" value="${platformCategory.id}" style="vertical-align:middle;" />
				</td>			
				<td><a href="${ctx}/mod/platformCategory/form?id=${platformCategory.id}">
					${platformCategory.name}
				</a></td>
				<td>${fns:getDictLabel(platformCategory.platform, 'platform', '-')}</td>
				<td>${platformCategory.category.name} &nbsp;&nbsp;<a id='btnMapping${platformCategory.id}' data-id="${platformCategory.id}" href='#' style=''>映射为选中类目</a></td>
				<td>
					<fmt:formatDate value="${platformCategory.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="mod:platformCategory:edit"><td>
    				<a href="${ctx}/mod/platformCategory/form?id=${platformCategory.id}">修改</a>
					<a href="${ctx}/mod/platformCategory/delete?id=${platformCategory.id}" onclick="return confirmx('确认要删除该电商平台类目映射吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>