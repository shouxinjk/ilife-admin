<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>社会分层管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	var sxdebug = false;
	var ratio = 1;
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
        	      max: 10,
        	      value: sliderVal,
        	      slide: changeSlide,
        	      change: changeSlide
        	    });
        	
        	//设置颜色
        	if(sxdebug)console.log("got silder val:",$('#'+sliderId).val(), Math.floor(sliderVal/ratio));
        	$( '#'+sliderId +" .ui-slider-range" ).css( "background", colors[Math.floor(sliderVal/ratio)] );
        	$( '#'+sliderId +" .ui-slider-handle" ).css( "border-color", colors[Math.floor(sliderVal/ratio)] );
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
		<li class="active"><a href="${ctx}/mod/hierarchy/">社会分层列表</a></li>
		<shiro:hasPermission name="mod:hierarchy:edit"><li><a href="${ctx}/mod/hierarchy/form">社会分层添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="hierarchy" action="${ctx}/mod/hierarchy/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>昵称：</label>
				<form:input path="displayName" htmlEscape="false" maxlength="100" class="input-medium"/>
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
				<th>昵称</th>
				<th colspan="2">构成</th>
				<th>描述</th>
				<th>识别规则</th>
				<th>matrix曲线</th>
				<shiro:hasPermission name="mod:hierarchy:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="hierarchy">
			<tr>
				<td><a href="${ctx}/mod/hierarchy/form?id=${hierarchy.id}" title="${hierarchy.description}">
					${hierarchy.name}
				</a></td>
				<td>
					${hierarchy.displayName}
				</td>	
				<td align="center" width="250px;">
					<div id="slider-economyLevel-${hierarchy.id}" data-slider-value="${hierarchy.economyLevel}" style="width:90%;margin:10px;"></div>
					<div id="slider-cultureLevel-${hierarchy.id}" data-slider-value="${hierarchy.cultureLevel}" style="width:90%;margin:10px;"></div>
					<div id="slider-societyLevel-${hierarchy.id}" data-slider-value="${hierarchy.societyLevel}" style="width:90%;margin:10px;"></div>
				</td>			
				<td>
					经济资本：${hierarchy.economyLevel}(${hierarchy.economyScoreMin } - ${hierarchy.economyScoreMax }) <br/>
					文化资本：${hierarchy.cultureLevel}(${hierarchy.cultureScoreMin } - ${hierarchy.cultureScoreMax })<br/>
					社会资本：${hierarchy.societyLevel}(${hierarchy.societyScoreMin } - ${hierarchy.societyScoreMax })<br/>
				</td>
					
				<td>
					${hierarchy.description}
				</td>				
				<td>
					${hierarchy.expression}
				</td>
				<td>
					${hierarchy.matrix}
				</td>
				<shiro:hasPermission name="mod:hierarchy:edit"><td>
    				<a href="${ctx}/mod/hierarchy/form?id=${hierarchy.id}">修改</a>
					<a href="${ctx}/mod/hierarchy/delete?id=${hierarchy.id}" onclick="return confirmx('确认要删除该社会分层吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>