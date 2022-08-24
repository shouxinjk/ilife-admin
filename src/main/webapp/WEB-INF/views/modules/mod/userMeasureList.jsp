<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户属性定义管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	var sxdebug = false;
	var ratio = 1000;
	var colors = ['#8b0000', '#dc143c', '#ff4500', '#ff6347', '#1e90ff','#00ffff','#40e0d0','#9acd32','#32cd32','#228b22'];
	$(document).ready(function() {
        //将vals slider实例化
        $("div[id^='slider-']").each((index, item) => {
        	var sliderId = $(item).attr("id");
        	var sliderVal = Number($('#'+sliderId).attr("data-slider-value"))*ratio;
        	$( "#"+sliderId ).slider({
        	      orientation: "horizontal",
        	      range: "min",
        	      min: 0,
        	      max: ratio,
        	      value: sliderVal,
        	      slide: changeSlide,
        	      change: changeSlide
        	    });
        	
        	//设置颜色
        	if(sxdebug)console.log("got silder val:",$('#'+sliderId).val(), Math.floor(sliderVal/ratio*10));
        	$( '#'+sliderId +" .ui-slider-range" ).css( "background", colors[Math.floor(sliderVal/ratio*10)] );
        	$( '#'+sliderId +" .ui-slider-handle" ).css( "border-color", colors[Math.floor(sliderVal/ratio*10)] );
          });
	});
	
	function page(n,s){
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
    	return false;
    }
	
	function changeSlide(event, ui) {
		var sliderId = event.target.id;
		var sliderVal = ui.value;
		if(sxdebug)console.log("slider changed...", sliderId, sliderVal );
    	$( '#'+sliderId +" .ui-slider-range" ).css( "background", colors[Math.floor(sliderVal/ratio*10)] );
    	$( '#'+sliderId +" .ui-slider-handle" ).css( "border-color", colors[Math.floor(sliderVal/ratio*10)] );
	}
	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mod/userMeasure/">用户属性定义列表</a></li>
		<shiro:hasPermission name="mod:userMeasure:edit"><li><a href="${ctx}/mod/userMeasure/form?category.id=${treeId}">用户属性定义添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="userMeasure" action="${ctx}/mod/userMeasure/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>名称</th>
				<!--th>分类</th>
				<th>占比</th-->
				<th>属性定义</th>
				<th>属性类型</th>
				<!--th>归一化参考值</th-->
				<th>默认数值</th>
				<!--th>默认等级</th-->
				<th>VALS分布</th>
				<th>能力分布</th>				
				<th>数据算法</th>
				<th>标签类目</th>		
				<th>标签</th>	
				<!--th>更新时间</th-->
				<shiro:hasPermission name="mod:userMeasure:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="userMeasure">
			<tr>
				<td><a href="${ctx}/mod/userMeasure/form?id=${userMeasure.id}">
					${userMeasure.name}
				</a></td>
				<!--td>
					${userMeasure.category.name}
				</td>	
				<td>
					${userMeasure.weight}
				</td-->
				<td>
					${userMeasure.property}
				</td>	
				<td>
					${fns:getDictLabel(userMeasure.type, 'propertyType', userMeasure.type)}
				</td>									
				<!--td>
					${userMeasure.controlValue}
				</td-->
				<td>
					${userMeasure.defaultScore}
				</td>			
				<!--td>
					${userMeasure.defaultRank}
				</td-->		
				<td align="center" width="150px">
					<div id="slider-alpha-${userMeasure.id}" data-slider-value="${userMeasure.alpha}" style="width:90%;margin:10px;"></div>
					<div id="slider-beta-${userMeasure.id}" data-slider-value="${userMeasure.beta}" style="width:90%;margin:10px;"></div>
					<div id="slider-gamma-${userMeasure.id}" data-slider-value="${userMeasure.gamma}" style="width:90%;margin:10px;"></div>
					<div id="slider-delte-${userMeasure.id}" data-slider-value="${userMeasure.delte}" style="width:90%;margin:10px;"></div>
					<div id="slider-epsilon-${userMeasure.id}" data-slider-value="${userMeasure.epsilon}" style="width:90%;margin:10px;"></div>
				</td>
				<td align="center" width="150px;">
					<div id="slider-zeta-${userMeasure.id}" data-slider-value="${userMeasure.zeta}" style="width:90%;margin:10px;"></div>
					<div id="slider-eta-${userMeasure.id}" data-slider-value="${userMeasure.eta}" style="width:90%;margin:10px;"></div>
					<div id="slider-theta-${userMeasure.id}" data-slider-value="${userMeasure.theta}" style="width:90%;margin:10px;"></div>
				</td>					
				<td>
				归一化类型：${fns:getDictLabel(userMeasure.normalizeType, 'normalizeType', userMeasure.normalizeType)}<br/>
				多值策略：${fns:getDictLabel(userMeasure.multiValueFunc, 'multiValueFunc', userMeasure.multiValueFunc)}<br/>
				自动标注：${fns:getDictLabel(userMeasure.autoLabelType, 'autoLabelType', userMeasure.autoLabelType)}<br/>
				Dict字典：${fns:getDictLabel(userMeasure.autoLabelDict, 'autoLabelDictUser', userMeasure.autoLabelDict)}<br/>
				Refer类目：${userMeasure.autoLabelCategory.name}</td>	
				<td>${userMeasure.autoLabelTagCategory.name}</td>	
				<td>
					${userMeasure.tags}
				</td>											
				<!--td>
					<fmt:formatDate value="${userMeasure.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td-->
				<shiro:hasPermission name="mod:userMeasure:edit"><td>
    				<a href="${ctx}/mod/userMeasure/form?id=${userMeasure.id}">修改</a>
					<a href="${ctx}/mod/userMeasure/delete?id=${userMeasure.id}" onclick="return confirmx('确认要删除该用户属性定义吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>