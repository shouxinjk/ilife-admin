<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>主观评价管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		var sxdebug=false;
		var sections=[];//记录所有待汇总的section，即父节点列表
	
        $(document).ready(function() {
            $("#treeTableEvaluation").treeTable({expandLevel : 5});
            
            //注册监听事件
            registerEventListener();
            //将所有上层节点放入section列表，等待汇总
            $("input[data-section^='sec-']").each((index, item) => {
            	var sectionId = $(item).data("section");
                if(sxdebug)console.log("got section.[index]"+index,sectionId);
                if(sections.indexOf(sectionId)<0){
                	sections.push(sectionId);
                }
              });
            if(sxdebug)console.log("all sections.",sections);
            //检查sum是否为100
            checkWeightSum();            
        });

		
		//注册监听事件：修改weight后立即生效
		function registerEventListener(){
			//监听所有weight input
            $("input[data-section^='sec-']").each((index, item) => {
            	var inputId = $(item).attr("id");
            	if(sxdebug)console.log("got input.[index]"+index,inputId);
                $("#"+inputId).blur(function(e){
                	var weight2 = $("#"+e.currentTarget.id).val();
                	if(sxdebug)console.log("start change weight.",e.currentTarget.id,weight2);
                    var data = {
                    		id:e.currentTarget.id,
                    		weight:Number(weight2)
                    };
                    $.ajax({
                        type: "POST",
                        url: "${ctx}/mod/userEvaluation/rest/weight",
                        data: data,        
			            success:function(result){
			            	if(sxdebug)console.log("update weight done.",result);   
			                //重新计算
			                checkWeightSum();
			            }
                    });
                }); 
              });
		}
        
		//根据section，汇总其下所有节点的weight，如果不等于100则高亮显示
        function checkWeightSum(){
        	sections.forEach((section, index) => {
        		if(sxdebug)console.log("sum section.[section]"+index,section);
				//获取该section下的所有节点，并汇总其weight值
				var sum = 0;
                $("input[data-entry^='"+section+"-']").each((index, item) => {
                	var weight = Number($(item).val());
                	if(sxdebug)console.log("got entry.[index]"+index,weight,sum);
                	if(weight>0&&weight<=100){
                		$(item).css("border-color","#silver");
                	}else{
                		$(item).css("border-color","#FFA7C5");
                	}
                    sum+=weight;
                });
                if(sum!=100){
                	if(sxdebug)console.log("section sum is not 100. [section]"+section);
                	$("input[data-section='"+section+"']").css("color","red");
                	$("input[data-section='"+section+"']").css("background-color","#FFB7C5");
                }else{
                	$("input[data-section='"+section+"']").css("color","black");
                	$("input[data-section='"+section+"']").css("background-color","#fff");
                }
              });
        }        
        
        function updateSort() {
            loading('正在提交，请稍等...');
            $("#listForm").attr("action", "${ctx}/mod/userEvaluation/updateSort");
            $("#listForm").submit();
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mod/userEvaluation/list">主观评价列表</a></li>
		<shiro:hasPermission name="mod:userEvaluation:edit"><li><a href="${ctx}/mod/userEvaluation/form">添加主观评价</a></li></shiro:hasPermission>
		<!-- 不提供添加客观评价按钮，只能从指定节点后开始操作 -->
		<!--
		<shiro:hasPermission name="mod:userEvaluationDimension:edit"><li><a href="${ctx}/mod/userEvaluationDimension/form?category.id=${treeId}&evaluation.id=${row.id}">添加客观评价</a></li></shiro:hasPermission>
		-->
	</ul>
	<sys:message content="${message}"/>
	<form id="listForm" method="post">
	<table id="treeTableEvaluation" class="table table-striped table-bordered table-condensed">

			<tr>
				<th>名称</th>
				<th>Key</th>
				<th>特征</th>
				<!--th>类型</th-->
				<th>小类</th>
				<th width="60">计算类型</th>
				<th>计算脚本</th>
				<th>占比%</th>
				<th>描述</th>			
                <!--th style="text-align:center;">排序</th-->
				<shiro:hasPermission name="mod:userEvaluation:edit"><th>操作</th></shiro:hasPermission>
			</tr>


		<c:forEach items="${list}" var="row">
			<tr id="${row.id}" pId="${row.parent.id ne '1'?row.parent.id:'0'}">
				<td><a href="${ctx}/mod/userEvaluationDimension/list?evaluationId=${row.id}&evaluation.id=${row.id}">
					${row.name}
				</a></td>
				<td>
					${row.propKey}
				</td>				
				<td>
					<c:choose>
					    <c:when test = "${row.featured == 'true'}">
					       <span style="background-color:#8BCE2D;padding:2px 5px;color:#fff;">是</span>
					    </c:when>		         			         			         			         
					    <c:otherwise>
					    	<!-- ${fns:getDictLabel(row.featured, 'yes_no', row.featured)} -->
					    </c:otherwise>
					 </c:choose>						
				</td>
				<!--td>
					${fns:getDictLabel(row.type, 'evaluation_type', row.type)}
				</td-->
				<td>
					${row.category}
				</td>	
				<td>
					<c:choose>
					    <c:when test = "${row.scriptType == 'manual'}">
					       <span style="background-color:#E85552;padding:2px 5px;color:#fff;">手动</span>
					    </c:when>	
					    <c:when test = "${row.scriptType == 'auto'}">
					       <span style="background-color:#8BCE2D;padding:2px 5px;color:#fff;">自动</span>
					    </c:when>		         			         			         			         
					    <c:otherwise>
					       <span style="background-color:#000;padding:2px 5px;color:#fff;">NA ${row.scriptType}</span>
					    </c:otherwise>
					 </c:choose>				
				</td>							
				<td>
					${row.scriptMemo}
				</td>				
				<td>
					<input type="text" value="${row.weight}" id="${row.type eq 'dimension'?'dim-':'eval-'}${row.id}" data-section="sec-${row.parent.id}" data-entry="sec-${row.parent.id}-${row.id}" style="width:60px;margin:0 auto;padding:0;height:20px;font-size:12px;"/>
				</td>
				<td>
					${row.description}
				</td>			
				<!--td style="text-align:center;">
					<shiro:hasPermission name="mod:itemCategory:edit">
						<input type="hidden" name="ids" value="${row.id}"/>
						<input name="sorts" type="text" value="${row.sort}" style="width:50px;margin:0;padding:0;text-align:center;">
					</shiro:hasPermission><shiro:lacksPermission name="mod:itemCategory:edit">
					${row.sort}
				</shiro:lacksPermission>
				</td-->
				<td>
					<shiro:hasPermission name="mod:userEvaluation:edit">
						<a href="${ctx}/mod/${row.type eq 'dimension'?'userEvaluationDimension':'userEvaluation'}/form?id=${row.id}">修改</a>
						<a href="${ctx}/mod/${row.type eq 'dimension'?'userEvaluationDimension':'userEvaluation'}/delete?id=${row.id}" onclick="return confirmx('确认要删除吗？', this.href)">删除</a>
						<c:if test="${row.type ne 'dimension'}">
							<a href="${ctx}/mod/userEvaluation/form?parent.id=${row.id}">添加下级</a>
							<a href="${ctx}/mod/userEvaluationDimension/list2?evaluation.id=${row.id}&evaluationId=${row.id}">添加客观评价节点</a>
						</c:if>
					</shiro:hasPermission>							
				</td>			
			</tr>
		</c:forEach>

	</table>
	<shiro:hasPermission name="mod:userEvaluation:edit"><div class="form-actions pagination-left">
		<input id="btnSubmit" class="btn btn-primary" type="button" value="保存排序" onclick="updateSort();"/>
	</div></shiro:hasPermission>
	</form>
</body>
</html>