<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>主观评价-维度管理</title>
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
                    		evaluationId:"${evaluationId}",
                    		dimensionIds:selectedNeeds
                    };
		        console.log("try to sending request.",data);
		        $.ajax({
		            type: "POST",
		            url: "${ctx}/mod/itemEvaluationDimension/rest/batch",
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
		                	window.location = "${ctx}/mod/itemEvaluation/?treeId=${categoryId}";
		                }else{
		                	siiimpleToast.message('添加客观评价出错',{
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
		<li><a href="${ctx}/mod/itemEvaluation/list?treeId=${categoryId}">主观评价列表</a></li>
		<shiro:hasPermission name="mod:itemEvaluation:edit"><li><a href="${ctx}/mod/itemEvaluation/form?category.id=${categoryId}">主观评价添加</a></li></shiro:hasPermission>
		<shiro:hasPermission name="mod:itemEvaluationDimension:edit"><li class="active"><a href="${ctx}/mod/itemEvaluationDimension/list2?evaluation.id=${evaluationId}&category.id=${categoryId}&categoryId=${categoryId}">添加客观评价节点</a></li></shiro:hasPermission>
	</ul>
	<div class="breadcrumb form-search">
		<ul class="ul-form">
			<li class="btns"><input id="btnCheckAll" class="btn btn-primary" type="button" value="选中全部"/></li>
			<li class="btns"><input id="btnUncheckAll" class="btn btn-primary" type="button" value="取消选中"/></li>
			<li class="btns"><input id="btnAddSelected" class="btn btn-primary" type="button" value="添加选中的属性"/></li>
		</ul>
	</div>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align:center" width="20">#</th><!-- 复选框 -->
				<th>类目</th>
				<th>名称</th>	
				<th>KEY</th>	
				<th>更新时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${dimensions}" var="dimension">
			<tr>
				<td>
					<input type="checkbox" id="${dimension.id}" value="${dimension.id}" class="checkBox"/>
				</td>											
				<td>
					${dimension.category.name}
				</td>		
				<td>
					${dimension.parent.name} ${dimension.name}
				</td>	
				<td>
					${dimension.propKey}
				</td>							
				<td>
					<fmt:formatDate value="${dimension.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>