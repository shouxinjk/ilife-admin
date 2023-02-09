<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>批量添加指南</title>
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
                    		schemeId:"${schemeId}",
                    		guideIds:selectedNeeds
                    };
		        console.log("try to sending request.",data);
		        $.ajax({
		            type: "POST",
		            url: "${ctx}/diy/guideBookProposal/rest/batch",
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
		                	window.location = "${ctx}/diy/guideBookProposal/?scheme.id=${schemeId}";
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
		<li><a href="${ctx}/diy/guideBook/">主题列表</a></li>
		<shiro:hasPermission name="diy:guideBook:edit"><li><a href="${ctx}/diy/guideBook/form">添加主题</a></li></shiro:hasPermission>	
		<li><a href="${ctx}/diy/guideBookProposal/?scheme.id=${schemeId}">已关联指南</a></li>
		<shiro:hasPermission name="diy:guideBookProposal:edit"><li class="active"><a href="${ctx}/diy/guideBook/list2?schemeId=${schemeId}">待关联指南</a></li></shiro:hasPermission>
	</ul>
	<div class="breadcrumb form-search">
		<ul class="ul-form">
			<li class="btns"><input id="btnCheckAll" class="btn btn-primary" type="button" value="选中全部"/></li>
			<li class="btns"><input id="btnUncheckAll" class="btn btn-primary" type="button" value="取消选中"/></li>
			<li class="btns"><input id="btnAddSelected" class="btn btn-primary" type="button" value="添加选中的指南"/></li>
		</ul>
	</div>	
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th width="60">#</th>
				<th>所属类目</th>
				<th>全称</th>
				<th>简称</th>
				<th>来源</th>
				<th>版本</th>
				<th>标签</th>
				<th>描述</th>
				<th>类型</th>
				<!--th>URL</th-->
				<th>激活</th>
				<th>更新时间</th>
				<!--
				<shiro:hasPermission name="diy:guideBook:edit"><th>操作</th></shiro:hasPermission>
				-->
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${guideBooks}" var="guideBook">
			<tr>
				<td>
					<input type="checkbox" id="${guideBook.id}" value="${guideBook.id}" class="needBox"/>
				</td>	
				<td>${guideBook.category.name}</td>		
				<td><a href="${ctx}/diy/guideBook/form?id=${guideBook.id}">
					${guideBook.name}
				</a></td>
				<td>
					${guideBook.alias}
				</td>
				<td>
					${guideBook.origin}
				</td>
				<td>
					${guideBook.revision}
				</td>
				<td>
					${guideBook.tags}
				</td>
				<td>
					${guideBook.description}
				</td>
				<td>
					${fns:getDictLabel(guideBook.type, 'guide_type', '')}
				</td>
				<!--td>
					<c:if test="${fn:contains(guideBook.url,'http')}">
						<a href="${guideBook.url}">查看原文</a>
					</c:if>			
				</td-->
				<td>
					${fns:getDictLabel(guideBook.status, 'yes_no', '')}
				</td>
				<td>
					<fmt:formatDate value="${guideBook.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<!--
				<shiro:hasPermission name="diy:guideBook:edit"><td>
    				<a href="${ctx}/diy/guideBook/form?id=${guideBook.id}">修改</a>
					<a href="${ctx}/diy/guideBook/delete?id=${guideBook.id}" onclick="return confirmx('确认要删除该个性化定制指南吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
				-->
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>