<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			//点击添加全部时提交
			$("#btnAddSelected").click(function(){
				// 获取所有已选中条目，如果没有则提示
				var selectedNeeds = [];
				$("input[type='checkbox'].needBox:checked").each(function(){
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
                    		termId:"${termId}",
                    		itemIds:selectedNeeds
                    };
		        console.log("try to sending request.",data);
		        $.ajax({
		            type: "POST",
		            url: "${ctx}/diy/guideTermItem/rest/batch",
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
		                	window.location = "${ctx}/diy/guideTermItem/?term.id=${termId}";
		                }else{
		                	siiimpleToast.message('出错了',{
				                  position: 'bottom|center'
				                });                  
		                }
		            }                
		        }); 				
			});
			
		    //选中全部
		    $("#btnCheckAll").click(function(){
		        $("input[type='checkbox'].needBox").prop("checked","true"); 
		    });
		    //取消选中
		    $("#btnUncheckAll").click(function(){
		        $("input[type='checkbox'].needBox").removeAttr("checked"); 
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
		<li><a href="${ctx}/diy/guideTerm/">指南规则列表</a></li>
		<shiro:hasPermission name="diy:guideTerm:edit"><li><a href="${ctx}/diy/guideTerm/form">指南规则添加</a></li></shiro:hasPermission>	
		<li><a href="${ctx}/diy/guideTermItem/?term.id=${termId}">已关联条目</a></li>
		<shiro:hasPermission name="diy:guideTermItem:edit"><li class="active"><a href="${ctx}/ope/item/list2?termId=${termId}">待关联条目</a></li></shiro:hasPermission>
	</ul>
	<div class="breadcrumb form-search">
		<ul class="ul-form">
			<li class="btns"><input id="btnCheckAll" class="btn btn-primary" type="button" value="选中全部"/></li>
			<li class="btns"><input id="btnUncheckAll" class="btn btn-primary" type="button" value="取消选中"/></li>
			<li class="btns"><input id="btnAddSelected" class="btn btn-primary" type="button" value="添加选中的条目"/></li>
		</ul>
	</div>	
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th width="60">#</th>
				<th>商品类别</th>
				<th>来源</th>
				<th>名称</th>
				<th>导购连接</th>
				<th>摘要</th>
				<th>状态</th>
				<th>更新者</th>
				<th>更新时间</th>
				<shiro:hasPermission name="ope:item:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${items}" var="item">
			<tr>
				<td>
					<input type="checkbox" id="${item.id}" value="${item.id}" class="needBox"/>
				</td>			
				<td><a href="${ctx}/ope/item/form?id=${item.id}">
					${item.itemCategory.name}
				</a></td>
				<td>
					${item.source}
				</td>
				<td>
					${item.name}
				</td>
				<td>
					${item.url}
				</td>
				<td>
					${item.summary}
				</td>
				<td>
					${fns:getDictLabel(item.status, 'is_active', '无')}
				</td>
				<td>
					${item.updateBy.id}
				</td>
				<td>
					<fmt:formatDate value="${item.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="ope:item:edit"><td>
    				<a href="${ctx}/ope/item/form?id=${item.id}">修改</a>
					<a href="${ctx}/ope/item/delete?id=${item.id}" onclick="return confirmx('确认要删除该商品吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>