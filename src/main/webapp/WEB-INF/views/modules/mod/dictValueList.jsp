<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>业务字典管理</title>
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
				            url: "${ctx}/mod/dictValue/rest/updateMarkedValue?id="+itemId+"&level="+currentRating+"&markedValue="+currentRating,
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
			
			 //注册忽略类目事件：点击后提交忽略请求，将根据dict_id将所有带有category的记录删除，同时增加一条不带category的记录
		    $("a[id^=btnIgnore]").click(function(){
		    	var id = $(this).data("id");
		    	var type = $(this).data("type");
	            console.log("ignore category.",id,type);
			    $.ajax({
			        url:"${ctx}/mod/dictValue/rest/category?id="+id+"&type="+type,
			        type:"DELETE",     
			        data:JSON.stringify({id:id}),
			        headers:{
			            "Content-Type":"application/json"
			        },  
			        success:function(ret){
			            console.log("===got ignore result===\n",ret);
			            if(ret.success){
				            siiimpleToast.message('哦耶，已忽略目录',{
					              position: 'bottom|center'
					            });  
				            location.reload();//刷新当前页面
			            }else{
				            siiimpleToast.message('糟糕，出错了，请重新尝试',{
					              position: 'bottom|center'
					            });   
			            }
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
		<li><a href="${ctx}/mod/dictValue/listPending?treeId=${treeId}&pId=${pId}">待标注</a></li>
		<li class="active"><a href="${ctx}/mod/dictValue/list?treeId=${treeId}&pId=${pId}">已标注</a></li>
		<shiro:hasPermission name="mod:dictValue:edit"><li><a href="${ctx}/mod/dictValue/form?dictMeta.id=${treeId}">字典添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="dictValue" action="${ctx}/mod/dictValue/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>数值：</label>
				<form:input path="originalValue" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>		
			<li><label>字典：</label>
				<form:select path="dictMeta.id" class="input-medium required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictMetaList('_all')}" itemLabel="name" itemValue="id" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>类目：</label>
				<sys:treeselect id="category" name="category.id" value="${dictValue.category.id}" labelName="category.name" labelValue="${dictValue.category.name}"
					title="应用类目" url="/mod/itemCategory/treeData" notAllowSelectRoot="false" allowClear="true" allowInput="true"/>	
			</li>			
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>字典类别</th>
				<th>字典名称</th>
				<th>类目</th>
				<th>描述</th>
				<th>原始值/Label</th>
				<th>标注值/mvalue</th>
				<th>标注人数</th>
				<th>更新时间</th>
				<shiro:hasPermission name="mod:dictValue:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="dictValue">
			<tr>
				<td>
			      <c:choose>
			         <c:when test = "${dictValue.dictMeta.type == 'user'}">
			            <span style="background-color:#E85552;padding:2px 5px;color:#fff;">用户字典</span>
			         </c:when>	
			         <c:when test = "${dictValue.dictMeta.type == 'item'}">
			            <span style="background-color:#8BCE2D;padding:2px 5px;color:#fff;">商品字典</span>
			         </c:when>		         			         			         			         
			         <c:otherwise>
			            <span style="background-color:#000;padding:2px 5px;color:#fff;">未知</span>
			         </c:otherwise>
			      </c:choose>						
				</td>
				<td>
					${dictValue.dictMeta.name}
				</td>				
				<td>
					${dictValue.category.name}
				</td>
				<td>
					${dictValue.dictMeta.description} ${dictValue.dictMeta.controlDesc}
				</td>
				<td>
					${dictValue.originalValue}
				</td>	
				<td>
					<div class="rating" id="rate-${dictValue.id}" data-id="${dictValue.id}" data-markedValue="${dictValue.markedValue}"></div>					
				</td>
				<td>
					${dictValue.markers}
				</td>				
				<td><a href="${ctx}/mod/dictValue/form?id=${dictValue.id}">
					<fmt:formatDate value="${dictValue.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</a></td>
				<shiro:hasPermission name="mod:dictValue:edit"><td>
    				<a href="${ctx}/mod/dictValue/form?id=${dictValue.id}">修改</a>
					<a href="${ctx}/mod/dictValue/delete?id=${dictValue.id}" onclick="return confirmx('确认要删除该业务字典吗？', this.href)">删除</a>
					<a href="#" id="btnIgnore${dictValue.id}1" data-id="${dictValue.id}" data-type="1">忽略类目相关</a>
					<a href="#" id="btnIgnore${dictValue.id}0" data-id="${dictValue.id}" data-type="0">恢复类目相关</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>