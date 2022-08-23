<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户分群管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var sxdebug = true;
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
		<li class="active"><a href="${ctx}/mod/persona/?treeId=${pid}&treeModule=${pType}">用户分群列表</a></li>
		<shiro:hasPermission name="mod:persona:edit"><li><a href="${ctx}/mod/persona/form?pid=${pid}&pType=${pType}">用户分群添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="persona" action="${ctx}/mod/persona/?treeId=${pid}&treeModule=${pType}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li>
				<label>社会分层：</label><sys:treeselect id="hierarchy" name="hierarchy.id" value="${persona.hierarchy.id}" labelName="hierarchy.name" labelValue="${persona.hierarchy.name}" 
				title="社会分层" url="/mod/hierarchy/listData" cssClass="input-small" allowClear="true"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>默认头像</th>
				<th>画像名称</th>
				<th>基本信息</th>
				<th colspan="2" align="center">VALS模型</th>
				<th colspan="2" align="center">能力模型</th>
				<th>偏好标签</th>
				<th>识别规则</th>
				<shiro:hasPermission name="mod:persona:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="persona">
			<tr>
				<td>
					<img src="${persona.logo}"  style="max-width:75px;max-height:75px;border:0;padding:3px;">
				</td>			
				<td>
				<a href="${ctx}/mod/persona/form?id=${persona.id}&pid=${pid}&pType=${pType}">
					${persona.name}
				</a><br/>
				${persona.description}
				</td>
				<td>
					阶段：${persona.phase.name}<br/>
					阶层：${persona.hierarchy.name}(${persona.hierarchy.displayName})<br/>
					分群：${persona.parent.name}
				</td>
				<td align="right" width="40px">
					生存：<br/>
					安全：<br/>
					情感：<br/>
					尊重：<br/>
					价值：<br/>
				</td>				
				<td align="center" width="150px">
					<div id="slider-alpha-${persona.id}" data-slider-value="${persona.alpha}" style="width:90%;margin:10px;"></div>
					<div id="slider-beta-${persona.id}" data-slider-value="${persona.beta}" style="width:90%;margin:10px;"></div>
					<div id="slider-gamma-${persona.id}" data-slider-value="${persona.gamma}" style="width:90%;margin:10px;"></div>
					<div id="slider-delte-${persona.id}" data-slider-value="${persona.delte}" style="width:90%;margin:10px;"></div>
					<div id="slider-epsilon-${persona.id}" data-slider-value="${persona.epsilon}" style="width:90%;margin:10px;"></div>
				</td>
				<!--td>
					${persona.alpha}<br/>
					${persona.beta}<br/>
					${persona.gamma}<br/>
					${persona.delte}<br/>
					${persona.epsilon}<br/>
				</td-->					
				<td width="40px">
					经济：<br/>
					社会：<br/>
					文化：<br/>
				</td>
				<td align="center" width="150px;">
					<div id="slider-zeta-${persona.id}" data-slider-value="${persona.zeta}" style="width:90%;margin:10px;"></div>
					<div id="slider-eta-${persona.id}" data-slider-value="${persona.eta}" style="width:90%;margin:10px;"></div>
					<div id="slider-theta-${persona.id}" data-slider-value="${persona.theta}" style="width:90%;margin:10px;"></div>
				</td>				
				<td>
					${persona.lambda}
				</td>
				<td>
					${persona.expression}
				</td>
				<shiro:hasPermission name="mod:persona:edit"><td>
    				<a href="${ctx}/mod/persona/form?id=${persona.id}&pid=${pid}&pType=${pType}">修改</a><br/>
					<a href="${ctx}/mod/persona/delete?id=${persona.id}&pid=${pid}&pType=${pType}" onclick="return confirmx('确认要删除该用户分群吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>