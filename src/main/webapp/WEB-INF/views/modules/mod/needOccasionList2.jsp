<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>影响的诱因管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

			//点击添加全部时提交
			$("#btnAddSelected").click(function(){
				// 获取所有已选中条目，如果没有则提示
				var selectedNeeds = [];
				$("input[type='checkbox'].checkBox:checked").each(function(){
					var needId = $(this).attr("id");
					selectedNeeds.push(needId);
				});
				if(selectedNeeds.length==0){
		            siiimpleToast.message('至少选择一个',{
		                  position: 'bottom|center'
		                });
					return;
				}
		        // 提交所有选中条目
		        var data = {
                    		needId:"${pid}",
                    		occasionIds:selectedNeeds
                    };
		        console.log("try to sending request.",data);
		        $.ajax({
		            type: "POST",
		            url: "${ctx}/mod/needOccasion/rest/batch",
		            dataType:"json",
		            data:JSON.stringify(data),
		            headers:{
		                "Content-Type":"application/json",
		                "Accept":"application/json"
		            },        
		            success:function(ret){
		                if(ret.success){
		                	siiimpleToast.message('添加成功',{
				                  position: 'bottom|center'
				                });
		                	window.location = "${ctx}/mod/needOccasion/?treeId=${pid}&treeModule=${pType}";
		                }else{
		                	siiimpleToast.message('添加需要出错',{
				                  position: 'bottom|center'
				                });                  
		                }
		            }                
		        }); 				
			});
			
		    //选中全部
		    $("#btnCheckAll").click(function(){
		        $("input[type='checkbox'].checkBox").prop("checked","true"); 
		    });
		    //取消选中
		    $("#btnUncheckAll").click(function(){
		        $("input[type='checkbox'].checkBox").removeAttr("checked"); 
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
		<li><a href="${ctx}/mod/needOccasion/?treeId=${pid}&treeModule=${pType}">诱因列表</a></li>
		<shiro:hasPermission name="mod:needOccasion:edit"><li class="active"><a href="${ctx}/mod/needOccasion/list2?treeId=${pid}&treeModule=${pType}">诱因添加</a></li></shiro:hasPermission>
	</ul>
	<div class="breadcrumb form-search">
		<ul class="ul-form">
			<li class="btns"><input id="btnCheckAll" class="btn btn-primary" type="button" value="选中全部"/></li>
			<li class="btns"><input id="btnUncheckAll" class="btn btn-primary" type="button" value="取消选中"/></li>
			<li class="btns"><input id="btnAddSelected" class="btn btn-primary" type="button" value="添加选中的诱因"/></li>
		</ul>
	</div>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>#</th><!-- 复选框 -->
				<th>类别</th>
				<th>触发规则</th>			
				<th>更新时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${occasions}" var="occasion">
			<tr>
				<td>
					<input type="checkbox" id="${occasion.id}" value="${occasion.id}" class="checkBox"/>
				</td>			
				<td>
					${occasion.occasionCategory.name}
				</td>					
				<td>
					${occasion.expression}
				</td>		
				<td>
					<fmt:formatDate value="${occasion.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>