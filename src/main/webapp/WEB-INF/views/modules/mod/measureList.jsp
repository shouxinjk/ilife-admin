<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品属性管理</title>
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
		<li class="active"><a href="${ctx}/mod/measure/">商品属性列表</a></li>
		<shiro:hasPermission name="mod:measure:edit"><li><a href="${ctx}/mod/measure/form?category.id=${treeId}">商品属性添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="measure" action="${ctx}/mod/measure/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
<!-- 			<li><label>类别：</label> -->
<%-- 				<sys:treeselect id="category" name="category.id" value="${measure.category.id}" labelName="category.name" labelValue="${measure.category.name}"  --%>
<%-- 				title="商品分类" url="/mod/itemCategory/treeData" cssClass="input-small" allowClear="true"/> --%>
<!-- 			</li> -->
			<li><label>关键字：</label>
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
				<th>类别</th>
				<!--th>维度</th-->
				<th>key</th>
				<th>名称</th>
				<!--th>占比</th>
				<th>参照值</th-->
				<th>默认数值</th>
				<!--th>a</th>
				<th>b</th>
				<th>c</th>
				<th>d</th>
				<th>e</th>	
				<th>x</th>
				<th>y</th>
				<th>z</th>	
				<th>style</th-->					
				<th>VALS满足分布</th>
				<th>能力需要分布</th>
				<th>数据算法</th>
				<th>标签类目</th>
				<th>关联标签</th>
				<shiro:hasPermission name="mod:measure:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="measure">
			<tr>
				<td>
					${measure.category.name}
				</td>
				<!--td>
					${measure.itemDimension.name}
				</td-->
				<td>
					${measure.property}
				</td>
				<td><a href="${ctx}/mod/measure/form?id=${measure.id}">
					${measure.name}
				</a></td>
				<!--td>
					${measure.percentage}
				</td>
				<td>
					${measure.controlValue}
				</td-->
				<td>
					${measure.defaultScore}
				</td>				
				<!--td>
					${measure.alpha}
				</td>
				<td>
					${measure.beta}
				</td>
				<td>
					${measure.gamma}
				</td>
				<td>
					${measure.delte}
				</td>
				<td>
					${measure.epsilon}
				</td>
				<td>
					${measure.zeta}
				</td>
				<td>
					${measure.eta}
				</td>
				<td>
					${measure.theta}
				</td>												
				<td>
					${measure.lambda}
				</td-->				
	
				<td align="center" width="150px">
					<div id="slider-alpha-${measure.id}" data-slider-value="${measure.alpha}" style="width:90%;margin:10px;"></div>
					<div id="slider-beta-${measure.id}" data-slider-value="${measure.beta}" style="width:90%;margin:10px;"></div>
					<div id="slider-gamma-${measure.id}" data-slider-value="${measure.gamma}" style="width:90%;margin:10px;"></div>
					<div id="slider-delte-${measure.id}" data-slider-value="${measure.delte}" style="width:90%;margin:10px;"></div>
					<div id="slider-epsilon-${measure.id}" data-slider-value="${measure.epsilon}" style="width:90%;margin:10px;"></div>
				</td>
				<td align="center" width="150px;">
					<div id="slider-zeta-${measure.id}" data-slider-value="${measure.zeta}" style="width:90%;margin:10px;"></div>
					<div id="slider-eta-${measure.id}" data-slider-value="${measure.eta}" style="width:90%;margin:10px;"></div>
					<div id="slider-theta-${measure.id}" data-slider-value="${measure.theta}" style="width:90%;margin:10px;"></div>
				</td>		
				<td>
				归一化类型：${fns:getDictLabel(measure.normalizeType, 'normalizeType', measure.normalizeType)}<br/>
				多值策略：${fns:getDictLabel(measure.multiValueFunc, 'multiValueFunc', measure.multiValueFunc)}<br/>
				自动标注：${fns:getDictLabel(measure.autoLabelType, 'autoLabelType', measure.autoLabelType)}<br/>
				Dict字典：${fns:getDictLabel(measure.autoLabelDict, 'autoLabelDictUser', measure.autoLabelDict)}<br/>
				Refer类目：${measure.autoLabelCategory.name}</td>									
				<td>${measure.autoLabelTagCategory.name}</td>	
				<td>
					${measure.tags}
				</td>				
				<shiro:hasPermission name="mod:measure:edit"><td>
    				<a href="${ctx}/mod/measure/form?id=${measure.id}">修改</a>
					<a href="${ctx}/mod/measure/delete?id=${measure.id}" onclick="return confirmx('确认要删除该商品属性吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>