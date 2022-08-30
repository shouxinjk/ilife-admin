<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>社会分层管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	var sxdebug = true;
	var sxData = {};//存储vals和cost值，结构为：key:value，key为alpha/beta/gamma...
	var ratio = 1;
	var colors = ['#8b0000', '#dc143c', '#ff4500', '#ff6347', '#1e90ff','#00ffff','#40e0d0','#9acd32','#32cd32','#228b22'];
	$(document).ready(function() {
		//$("#name").focus();
		$("#inputForm").validate({
			submitHandler: function(form){
				loading('正在提交，请稍等...');
				form.submit();
			},
			errorContainer: "#messageBox",
			errorPlacement: function(error, element) {
				$("#messageBox").text("输入有误，请先更正。");
				if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
					error.appendTo(element.parent().parent());
				} else {
					error.insertAfter(element);
				}
			}
		});

        //将vals slider实例化
        $("div[id^='slider-']").each((index, item) => {
        	var sliderId = $(item).attr("id");
        	var sliderVal = Number($('#'+sliderId).attr("data-slider-value"))*ratio;
        	sxData[sliderId.split("-")[1]] = Number($('#'+sliderId).attr("data-slider-value"));//缓存原始数值
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
	
	
	function changeSlide(event, ui) {
		var sliderId = event.target.id;
		var sliderVal = ui.value;
		sxData[sliderId.split("-")[1]] = sliderVal/ratio;//缓存修改后的数值
		if(sxdebug)console.log("slider changed...", sliderId, sliderVal );
    	$( '#'+sliderId +" .ui-slider-range" ).css( "background", colors[Math.floor(sliderVal/ratio)] );
    	$( '#'+sliderId +" .ui-slider-handle" ).css( "border-color", colors[Math.floor(sliderVal/ratio)] );
    	//根据具体条目修改：
    	var targetFactor = sliderId.split("-")[1];//取中间一截作为识别码：slider-alpha/beta/gamma-xxxxx
    	var valsFactors = ["alpha","beta","gamma","delte","epsilon"];
    	var costFactors = ["economyLevel","cultureLevel","societyLevel"];
    	//如果是vals则需要满足加和为1，分别计算后填充到input
    	if(valsFactors.indexOf(targetFactor)>-1){//需要计算后重新赋值
    		//计算总和
    		var total = 0;
    		valsFactors.forEach(function(item){
    			total += sxData[item];
    		});
    		//重新计算归一化数值
    		valsFactors.forEach(function(item){
    			sxData[item] = sxData[item]/total;
    			//设置到input框
    			$("#"+item).val(sxData[item]);
    			//更新slider：注意当前不做更新，否则导致死循环
    		});  
    	}else if(costFactors.indexOf(targetFactor)>-1){//需要计算后重新赋值
    		//如果是capability则直接填充到input
    		$("#"+targetFactor).val(sxData[targetFactor]);
    	}else{
    		//do nothing
    	}
    	
	};
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mod/hierarchy/">社会分层列表</a></li>
		<li class="active"><a href="${ctx}/mod/hierarchy/form?id=${hierarchy.id}">社会分层<shiro:hasPermission name="mod:hierarchy:edit">${not empty hierarchy.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:hierarchy:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="hierarchy" action="${ctx}/mod/hierarchy/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div style="display:flex;flex-direction:row;flex-wrap:nowrap">
			<div style="width:50%">			
				<div class="control-group">
					<label class="control-label">名称：</label>
					<div class="controls">
						<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">别名：</label>
					<div class="controls">
						<form:input path="displayName" htmlEscape="false" maxlength="100" class="input-xlarge "/>
					</div>
				</div>		
				<!--div class="control-group">
					<label class="control-label">经济资本等级：</label>
					<div class="controls">
						<form:input path="economyLevel" htmlEscape="false" maxlength="8" class="input-xlarge  digits"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">经济资本评分：</label>
					<div class="controls">
						<form:input path="economyScoreMin" htmlEscape="false" class=" number"/> -
						<form:input path="economyScoreMax" htmlEscape="false" class=" number"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">社会资本等级：</label>
					<div class="controls">
						<form:input path="societyLevel" htmlEscape="false" maxlength="8" class="input-xlarge  digits"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">社会资本评分：</label>
					<div class="controls">
						<form:input path="societyScoreMin" htmlEscape="false" class=" number"/> -
						<form:input path="societyScoreMax" htmlEscape="false" class=" number"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">文化资本等级：</label>
					<div class="controls">
						<form:input path="cultureLevel" htmlEscape="false" maxlength="8" class="input-xlarge  digits"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">文化资本评分：</label>
					<div class="controls">
						<form:input path="cultureScoreMin" htmlEscape="false" class=" number"/> -
						<form:input path="cultureScoreMax" htmlEscape="false" class=" number"/>
					</div>
				</div-->
				<div class="control-group">
					<label class="control-label">识别规则：</label>
					<div class="controls">
						<form:input path="expression" htmlEscape="false" maxlength="1000" class="input-xlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">matrix曲线：</label>
					<div class="controls">
						<form:textarea path="matrix" htmlEscape="false" maxlength="1000" rows="5" class="input-xlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">描述：</label>
					<div class="controls">
						<form:textarea path="description" htmlEscape="false" maxlength="1000" rows="5" class="input-xlarge "/>
					</div>
				</div>
				<div class="form-actions">
					<shiro:hasPermission name="mod:hierarchy:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
		</div>

		<!-- VALS及能力模型滑块 -->
		<div style="width:25%;border-left:1px solid silver; ">	
			<label class="formLabel" style="line-height: 30px;margin:5px 0; padding:5px 0;background-color:#f2f3d9;width:100%;">资本/能力构成</label>
			<div id="slider-economyLevel" data-slider-value="${hierarchy.economyLevel}" style="width:90%;margin:10px;"></div>
			<div id="slider-cultureLevel" data-slider-value="${hierarchy.cultureLevel}" style="width:90%;margin:10px;"></div>
			<div id="slider-societyLevel" data-slider-value="${hierarchy.societyLevel}" style="width:90%;margin:10px;"></div>
		
		</div>
		
		<!-- VALS及能力分布input -->
		<div style="width:20%;">	
			<label class="formLabel" style="line-height: 30px;margin:5px 0; padding:5px 0;background-color:#f2f3d9;width:100%;">&nbsp;</label>
			<div>
				经济资本：<form:input path="economyLevel" htmlEscape="false" class="input-small  number" style="height:12px;margin:1px;width:30px;"/>(<form:input path="economyScoreMin" htmlEscape="false" class="input-xsmall number" style="height:12px;margin:1px;width:30px;"/> -
						<form:input path="economyScoreMax" htmlEscape="false" class="input-small number" style="height:12px;margin:1px;width:30px;"/>)<br/>
				文化资本：<form:input path="cultureLevel" htmlEscape="false" class="input-small  number" style="height:12px;margin:1px;width:30px;"/>(<form:input path="cultureScoreMin" htmlEscape="false" class="input-small number" style="height:12px;margin:1px;width:30px;"/> -
						<form:input path="cultureScoreMax" htmlEscape="false" class="input-small number" style="height:12px;margin:1px;width:30px;"/>)<br/>
				社会资本：<form:input path="societyLevel" htmlEscape="false" class="input-small  number" style="height:12px;margin:1px;width:30px;"/>(<form:input path="societyScoreMin" htmlEscape="false" class="input-small number" style="height:12px;margin:1px;width:30px;"/> -
						<form:input path="societyScoreMax" htmlEscape="false" class="input-small number" style="height:12px;margin:1px;width:30px;"/>)
			</div>	
		</div>
		
		</div>			
		
	</form:form>
</body>
</html>