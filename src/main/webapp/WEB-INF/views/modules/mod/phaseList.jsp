<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>人生阶段管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
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
		<li class="active"><a href="${ctx}/mod/phase/">人生阶段列表</a></li>
		<shiro:hasPermission name="mod:phase:edit"><li><a href="${ctx}/mod/phase/form">人生阶段添加</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
	<form id="listForm" method="post">
		<table id="treeTable" class="table table-striped table-bordered table-condensed">
			<tr>
				<th>名称</th>
				<th>判定条件</th>
				<th colspan="2">VALS需要构成</th>
				<th>描述</th>
				<th style="text-align:center;">排序</th>
				<th>匹配用户</th>
				<th>操作</th>
			</tr>
			<c:forEach items="${list}" var="tpl">
				<tr id="${tpl.id}" pId="${tpl.parent.id ne '1'?tpl.parent.id:'0'}">
					<td><a href="${ctx}/mod/phase/form?id=${tpl.id}">${tpl.name}</a></td>
				<td>
 					${tpl.expression}
 				</td>
				<td align="center" width="250px">
					<div id="slider-alpha-${tpl.id}" data-slider-value="${tpl.alpha}" style="width:90%;margin:10px;"></div>
					<div id="slider-beta-${tpl.id}" data-slider-value="${tpl.beta}" style="width:90%;margin:10px;"></div>
					<div id="slider-gamma-${tpl.id}" data-slider-value="${tpl.gamma}" style="width:90%;margin:10px;"></div>
					<div id="slider-delte-${tpl.id}" data-slider-value="${tpl.delte}" style="width:90%;margin:10px;"></div>
					<div id="slider-epsilon-${tpl.id}" data-slider-value="${tpl.epsilon}" style="width:90%;margin:10px;"></div>
				</td> 				
				<td width="300px">
					生存需要：${tpl.alpha}<br/>
					安全需要：${tpl.beta}<br/>
					情感需要：${tpl.gamma}<br/>
					尊重需要：${tpl.delte}<br/>
					价值需要：${tpl.epsilon}
				</td>
				<td>
 					${tpl.description}
 				</td>				
				<td style="text-align:center;">
					<shiro:hasPermission name="mod:phase:edit">
						<input type="hidden" name="ids" value="${tpl.id}"/>
						<input name="sorts" type="text" value="${tpl.sort}" style="width:50px;margin:0;padding:0;text-align:center;">
					</shiro:hasPermission><shiro:lacksPermission name="mod:phase:edit">
						${tpl.sort}
					</shiro:lacksPermission>
				</td>
				<td>0</td>
				<td>
					<shiro:hasPermission name="mod:phase:edit">
						<a href="${ctx}/mod/phase/form?id=${tpl.id}">修改</a>
						<a href="${ctx}/mod/phase/delete?id=${tpl.id}" onclick="return confirmx('要删除该阶段及所有子阶段吗？', this.href)">删除</a>
						<a href="${ctx}/mod/phase/form?parent.id=${tpl.id}">添加下级栏目</a>
					</shiro:hasPermission>
				</td>
			</tr>
			</c:forEach>
		</table>
		<shiro:hasPermission name="mod:phase:edit"><div class="form-actions pagination-left">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="保存排序" onclick="updateSort();"/>
		</div></shiro:hasPermission>
	</form>
</body>
</html>