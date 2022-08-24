<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户属性VALS标注</title>
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
		<li class="active"><a href="${ctx}/mod/personaMeasure/?treeId=${pid}&treeModule=${pType}">VALS标注列表</a></li>
		<shiro:hasPermission name="mod:personaMeasure:edit"><li><a href="${ctx}/mod/personaMeasure/form?pid=${pid}&pType=${pType}">VALS标注添加</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>阶段</th>
				<th>属性</th>
				<th>权重</th>
				<th>VALS需要分布</th>
				<th>能力分布</th>
				<th>偏好表达式</th>						
				<th>描述</th>
				<th>最后更新</th>
				<shiro:hasPermission name="mod:personaMeasure:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="personaMeasure">
			<tr>
				<td>
					${personaMeasure.phase.name}
				</td>
				<td>
					${personaMeasure.measure.name}
				</td>
				<td>
					${personaMeasure.weight}
				</td>
				<td align="center" width="150px">
					<div id="slider-alpha-${personaMeasure.id}" data-slider-value="${personaMeasure.alpha}" style="width:90%;margin:10px;"></div>
					<div id="slider-beta-${personaMeasure.id}" data-slider-value="${personaMeasure.beta}" style="width:90%;margin:10px;"></div>
					<div id="slider-gamma-${personaMeasure.id}" data-slider-value="${personaMeasure.gamma}" style="width:90%;margin:10px;"></div>
					<div id="slider-delte-${personaMeasure.id}" data-slider-value="${personaMeasure.delte}" style="width:90%;margin:10px;"></div>
					<div id="slider-epsilon-${personaMeasure.id}" data-slider-value="${personaMeasure.epsilon}" style="width:90%;margin:10px;"></div>
				</td>
				<td align="center" width="150px;">
					<div id="slider-zeta-${personaMeasure.id}" data-slider-value="${personaMeasure.zeta}" style="width:90%;margin:10px;"></div>
					<div id="slider-eta-${personaMeasure.id}" data-slider-value="${personaMeasure.eta}" style="width:90%;margin:10px;"></div>
					<div id="slider-theta-${personaMeasure.id}" data-slider-value="${personaMeasure.theta}" style="width:90%;margin:10px;"></div>
				</td>																
				<td>
					${personaMeasure.lambda}
				</td>				
				<td>
					${personaMeasure.description}
				</td>
				<td><fmt:formatDate value="${personaMeasure.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<shiro:hasPermission name="mod:personaMeasure:edit"><td>
    				<a href="${ctx}/mod/personaMeasure/form?id=${personaMeasure.id}&pid=${pid}&pType=${pType}">修改</a>
					<a href="${ctx}/mod/personaMeasure/delete?id=${personaMeasure.id}&pid=${pid}&pType=${pType}" onclick="return confirmx('确认要删除该标注吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>