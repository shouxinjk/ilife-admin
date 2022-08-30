<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>人生阶段管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	var sxdebug = false;
	var sxData = {};//存储vals和cost值，结构为：key:value，key为alpha/beta/gamma...
	var ratio = 1000;
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
	
	
	function changeSlide(event, ui) {
		var sliderId = event.target.id;
		var sliderVal = ui.value;
		sxData[sliderId.split("-")[1]] = sliderVal/ratio;//缓存修改后的数值
		if(sxdebug)console.log("slider changed...", sliderId, sliderVal );
    	$( '#'+sliderId +" .ui-slider-range" ).css( "background", colors[Math.floor(sliderVal/ratio*10)] );
    	$( '#'+sliderId +" .ui-slider-handle" ).css( "border-color", colors[Math.floor(sliderVal/ratio*10)] );
    	//根据具体条目修改：
    	var targetFactor = sliderId.split("-")[1];//取中间一截作为识别码：slider-alpha/beta/gamma-xxxxx
    	var valsFactors = ["alpha","beta","gamma","delte","epsilon"];
    	var costFactors = ["zeta","eta","theta"];
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
    		//计算总和
    		var total = 0;
    		costFactors.forEach(function(item){
    			total += sxData[item];
    		});
    		//重新计算归一化数值
    		costFactors.forEach(function(item){
    			sxData[item] = sxData[item]/total;
    			//设置到input框
    			$("#"+item).val(sxData[item]);
    			//更新slider：注意当前不做更新，否则导致死循环
    		}); 
    	}else{
    		//do nothing
    	}
    	
	};
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mod/phase/">人生阶段列表</a></li>
		<li class="active"><a href="${ctx}/mod/phase/form?id=${phase.id}">人生阶段<shiro:hasPermission name="mod:phase:edit">${not empty phase.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mod:phase:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="phase" action="${ctx}/mod/phase/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div style="display:flex;flex-direction:row;flex-wrap:nowrap">
			<div style="width:50%">			
				<div class="control-group">
					<label class="control-label">上级阶段：</label>
					<div class="controls">
						 <sys:treeselect id="phase" name="parent.id" value="${phase.parent.id}" labelName="parent.name" labelValue="${phase.parent.name}"
							title="人生阶段" url="/mod/phase/treeData" extId="${phase.id}" cssClass="required"/>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">名称：</label>
					<div class="controls">
						<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge "/>
					</div>
				</div>					
				<div class="control-group">
					<label class="control-label">判定条件：</label>
					<div class="controls">
						<form:input path="expression" htmlEscape="false" maxlength="500" class="input-xlarge "/>
					</div>
				</div>	
				<div class="control-group">
					<label class="control-label">简要描述：</label>
					<div class="controls">
						<form:textarea path="description" htmlEscape="false" rows="3" maxlength="512" class="input-xlarge "/>
					</div>
				</div>						
				<div class="control-group">
					<label class="control-label">排列次序:</label>
					<div class="controls">
						<form:input path="sort" htmlEscape="false" maxlength="11" class="input-xlarge required digits"/>
					</div>
				</div>
				<div class="form-actions">
					<shiro:hasPermission name="mod:phase:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
		
		</div>

		<!-- VALS及能力模型滑块 -->
		<div style="width:25%;border-left:1px solid silver; ">	
			<label class="formLabel" style="line-height: 30px;margin:5px 0; padding:5px 0;background-color:#f2f3d9;width:100%;">VALS需要分布（总和为1）</label>
			<div id="slider-alpha" data-slider-value="${phase.alpha}" style="width:90%;margin:10px;"></div>
			<div id="slider-beta" data-slider-value="${phase.beta}" style="width:90%;margin:10px;"></div>
			<div id="slider-gamma" data-slider-value="${phase.gamma}" style="width:90%;margin:10px;"></div>
			<div id="slider-delte" data-slider-value="${phase.delte}" style="width:90%;margin:10px;"></div>
			<div id="slider-epsilon" data-slider-value="${phase.epsilon}" style="width:90%;margin:10px;"></div>
		</div>
		
		<!-- VALS及能力分布input -->
		<div style="width:20%;">	
			<label class="formLabel" style="line-height: 30px;margin:5px 0; padding:5px 0;background-color:#f2f3d9;width:100%;">&nbsp;</label>
			<div>
				生存需要：<form:input path="alpha" htmlEscape="false" class="input-small  number" style="height:12px;margin:1px;"/><br/>
				安全需要：<form:input path="beta" htmlEscape="false" class="input-small  number" style="height:12px;margin:1px;"/><br/>
				情感需要：<form:input path="gamma" htmlEscape="false" class="input-small  number" style="height:12px;margin:1px;"/><br/>
				尊重需要：<form:input path="delte" htmlEscape="false" class="input-small  number" style="height:12px;margin:1px;"/><br/>
				价值需要：<form:input path="epsilon" htmlEscape="false" class="input-small  number" style="height:12px;margin:1px;"/>
				<div style="line-height:2px;">&nbsp;&nbsp;</div>
			</div>
		</div>
		
		</div>			
		
	</form:form>
</body>
</html>