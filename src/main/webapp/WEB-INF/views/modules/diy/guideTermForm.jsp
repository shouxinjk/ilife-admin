<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>个性化定制指南条目管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
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
			//加载用户属性
			loadUserProps();
			
		    //显示tabs
		    //$( "#tabs" ).tabs();
		    
		    //注册事件：转换为脚本
		    $("#generateScriptBtn").click(function(){
		    	var script = $("#criteriaDesc").val();
		    	//直接遍历prop，将属性名称替换为属性property
		    	props.forEach(function(prop){
		    		const re = new RegExp(prop.name, 'g');
		    		script = script.replace(re, prop.property);
		    	});
		    	$("#criteria").val(script);
		    });
		    
		    //注册事件：生成动态表单
		    $("#generateFormBtn").click(function(){
		    	generateJsonForm();
		    });
		    
		    /**
		    //初始化tinyMCE编辑器
		    tinymce.init({
		        selector: '#criteria',
		        branding: false,
		        menubar: false,
		        toolbar: [
		            'styleselect | fontselect | fontsizeselect | bold italic underline strikethrough | link h2 h3 h4 blockquote | forecolor backcolor | link image | alignleft aligncenter alignright'
		          ],
		        plugins: 'autoresize',
		        autoresize_bottom_margin: 50, 
		        autoresize_max_height: 800, // 编辑区域的最大高
		        autoresize_min_height: 400, //编辑区域的最小高度
		        autoresize_on_init: true,
		        autoresize_overflow_padding: 50
		      });	
		    //**/
		});
		
		//加载用户属性并显示到界面
		var props = [];
		function loadUserProps(){
		    //获取所有用户属性
		    $.ajax({
		        url:"${ctx}/mod/userMeasure/rest/measures",
		        type:"get",
		        data:{},
		        success:function(res){
		            console.log("\n===got user measures ===\n",res);
		            //遍历模板
		            props = res;
		            props.forEach(function(prop){
		            	//找到属性分类div，如果没有则新建
		            	if($("#propcat"+prop.category.id).length==0){
		            		$("#props").append("<div id='propcat"+prop.category.id+"' style='display:flex;flex-direction:row;flex-wrap:wrap;margin-top:5px;'></div>");
		            		$("#propcat"+prop.category.id).append("<div style='width:100%;border-bottom:1px solid silver;'>"+prop.category.name+"</div>");//显示分类标签
		            	}
		            	//然后在属性分类div下增加属性
		            	$("#propcat"+prop.category.id).append("<div id='prop"+prop.id+"' data-property='"+prop.property+"' data-id='"+prop.id+"' data-name='"+prop.name+"' style='border:1px solid silver;border-radius:5px;margin:5px 2px;padding:2px 5px;line-height:18px;'>"+prop.name+"</div>");
		            	//注册事件：点击后自动填写到textarea
		            	$("#prop"+prop.id).click(function(){
		            		var property = $(this).data("property");
		            		var name = $(this).data("name");
		            		//填充到脚本区域
		            		$("#criteriaDesc").val($("#criteriaDesc").val()+" "+name);
		            	});
		            	
		            });
		        }
		    }); 			
		}
		
		//生成用户动态表单
		var formSchemeTpl = {
				schema:{},
				form:[],
				value:{}//默认value为空
		};
		var formTabTpl = { //构建tab
                "title":"",
                "type":"tab",
                "items":[]
              };
		var formScheme = {};
		var formValues = {};//表单值，默认采用默认值填写
		var formFields = {};//记录动态表单的属性列表，kv对，key为属性property，v为组织后的的字段条目
		var formTabs = {};//记录动态表单的tab下属性列表,每一个tab一个kv对。key为tab名称，及属性类目名称，value为其下包含的filed列表
		function generateJsonForm(){
			formFields = {};//清空字段
			formValues = {};//清空表单值
			formTabs = {};//清空tabs
			formScheme = JSON.parse(JSON.stringify(formSchemeTpl));
			
			//默认隐藏表单测试结果
			$("#testResult").css("display","none");
			$("#testMsg").css("display","none");
			
			checkScriptFields();//先检查脚本中引用的字段
			
			//遍历属性组织formScheme
			var enumTypes = ["string","array"];//支持预先生成候选项
	    	props.forEach(function(prop){
				if(scriptFields==null || scriptFields.length==0 || scriptFields.indexOf(prop.property)>-1){//有引用时加入
					//表单字段
					var formItem ={
							type: prop.type,
							title: prop.name
						};
					if(prop.tags && prop.tags.trim().length>0 && enumTypes.indexOf(prop.type)>-1){ //仅对字符类提供选项
						formItem["enum"] = prop.tags.split(" ");
					}
					//array列表装载:当前默认为string类型多选
					if("array"==prop.type){
						formItem.type = "string";//默认为string类型
						formItem = { //增加array结构
							type: prop.type, //直接为array
							title: prop.name,
							items : formItem
						};
					}
					formFields[prop.property]=formItem;
					//value项
					if(prop.defaultValue && prop.defaultValue.trim().length>0){
						formValues[prop.property]=prop.defaultValue;
					}
					//tab项
					if(!formTabs[prop.category.name]){
						formTabs[prop.category.name] = [];
					}
					formTabs[prop.category.name].push({
						key: prop.property,
						type: prop.field,
						activeClass: "btn-success"//default
					});	
				}else{//否则不加入
					//do nothing
				}
	    	});
			
			//组织得到formScheme
	    	formScheme.schema = formFields;
	    	//formScheme.type = "object";
	    	//formScheme.properties = formFields;
			
			//组织tabs
			var tabItems = [];
			Object.keys(formTabs).forEach(function(tabTitle){
				var tabItem = JSON.parse(JSON.stringify(formTabTpl));
				tabItem.title = tabTitle;
				tabItem.items = formTabs[tabTitle];
				tabItems.push(tabItem);
			});
			
			var tabScheme = {
					"type": "fieldset",
					"title": "填写表单验证脚本",
					"items": [{
						"type": "tabs",
						"id": "navtabs",
						"items": tabItems
					}]
				};
			formScheme.form.push(tabScheme);
			
			//组织提交按钮
			var btnScheme = {
					"type": "actions",
					"items": [{
						"type": "submit",
						"value": "提交验证"
					}]
				};
			formScheme.form.push(btnScheme);
			
            //将值设置进入form scheme
            formScheme.value = formValues;   //默认值        
            
            //设置提交事件
            formScheme.onSubmit = jsonFormSubmit;
            console.log("form scheme.\n",JSON.stringify(formScheme),"\n",formScheme);
            
            //可以显示表单了
            $("#jsonform").empty();//清空原来的表单
            $("#jsonform").jsonForm(formScheme);
		}
		
		//检查脚本内容中是否包含有属性字段，如果没有属性字段则显示完整表单，否则显示包含的指定字段集
		var scriptFields = [];//脚本中包含的字段列表，记录property值
		function checkScriptFields(){
			scriptFields = [];//清空先
	    	var script = $("#criteria").val();
	    	props.forEach(function(prop){
	    		const re = new RegExp(prop.property, 'g');
	    		if(script.match(re) && scriptFields.indexOf(prop.property)<0){ //仅放入一次
	    			scriptFields.push(prop.property);
	    		}
	    	});		
	    	console.log("fields in script.",scriptFields);
		}
		
		//jsonForm 提交响应事件
		var jsonFormSubmit = function (errors, values) {//jsonform 提交函数
			//清空结果显示
			$("#testResult").empty();
			$("#testMsg").empty();
          if (errors) {
            console.log("got erros.",errors);
            siiimpleToast.message('提交表单出错',{
                position: 'bottom|center'
              }); 
          }else {
        	var data = {
		        	script: $("#criteria").val(),
		        	values: values
		        };
            console.log("try to submit data.",data);
		    //带数据值，验证表单计算结果
		    $.ajax({
		        url:"${ctx}/diy/guideTerm/rest/test",
		        type:"post",
		        data:JSON.stringify(data),
		        headers:{
		            "Content-Type":"application/json"
		        },  		        
		        success:function(res){
		            console.log("\n=== script result ===\n",res);
		            if(res.success){ //计算完成，直接更新结果
		                $("#testResult").css("display","block");
		                $("#testResult").append("<div>验证结果："+res.data+"</div>");
		                if(res.data){
		                	$("#testResult").css("background-color","#c9fddd");
		                }else{
		                	$("#testResult").css("background-color","#fdbbc2");
		                }
		            }else{ //否则提示出错，并更新信息到界面
		            	$("#testMsg").css("display","block");
			            if(res.msg)$("#testMsg").append("<div>提示："+res.msg+"</div>");
			            if(res.err)$("#testMsg").append("<div>错误："+res.err+"</div>");
		                siiimpleToast.message('计算错误，请检查脚本及数值',{
		                    position: 'bottom|center'
		                  }); 
		            }
		        }
		    }); 
          }
        };
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/diy/guideTerm/?book.id=${guideTerm.book.id}">指南规则列表</a></li>
		<li class="active"><a href="${ctx}/diy/guideTerm/form?id=${guideTerm.id}">指南规则<shiro:hasPermission name="diy:guideTerm:edit">${not empty guideTerm.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="diy:guideTerm:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
			<form:form id="inputForm" modelAttribute="guideTerm" action="${ctx}/diy/guideTerm/save" method="post" class="form-horizontal">
				<form:hidden path="id"/>
				<sys:message content="${message}"/>		
	<div style="display:flex;flex-direction:row;flex-wrap:nowrap">
		<div style="width:40%">		
				<div class="control-group">
					<label class="control-label">指南：</label>
					<div class="controls">
						 <sys:treeselect id="book" name="book.id" value="${guideTerm.book.id}" labelName="book.name" labelValue="${guideTerm.book.name}"
							title="指南列表" url="/diy/guideBook/listData"  cssClass="required"/>					
					</div>			
				</div>
				<div class="control-group">
					<label class="control-label">章节：</label>
					<div class="controls">
						 <sys:treeselect id="section" name="section.id" value="${guideTerm.section.id}" labelName="section.name" labelValue="${guideTerm.section.name}"
							title="章节列表" url="/diy/proposalSection/listData?category.id=${guideTerm.category.id}"  cssClass="required"/>					
					</div>			
				</div>
				<div class="control-group">
					<label class="control-label">名称：</label>
					<div class="controls">
						<form:input path="name" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">描述：</label>
					<div class="controls">
						<form:textarea path="description" htmlEscape="false" rows="4" maxlength="512" class="input-xlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">注意事项：</label>
					<div class="controls">
						<form:textarea path="tips" htmlEscape="false" rows="4" maxlength="512" class="input-xlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">标签：</label>
					<div class="controls">
						<form:textarea path="tags" htmlEscape="false" rows="2" maxlength="256" class="input-xlarge "/>
					</div>
				</div>	

				<!-- 
				<div class="control-group">
					<label class="control-label">脚本规则：</label>
					<div class="controls">
						<form:textarea path="script" htmlEscape="false" rows="4" class="input-xxlarge "/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">脚本描述：</label>
					<div class="controls">
						<form:textarea path="scriptDesc" htmlEscape="false" rows="4" class="input-xxlarge "/>
					</div>
				</div>
				-->
				<div class="control-group">
					<label class="control-label">优先级：</label>
					<div class="controls">
						<form:input path="priority" htmlEscape="false" maxlength="11" class="input-xlarge  digits"/>
					</div>
				</div>
		</div>
		<!-- 公式编辑 -->
	    <div id="tabs" style="width:60%; display:flex;flex-direction:row;flex-wrap:nowrap">
        	<div style="width:60%">
				<div>适用条件：选择属性编辑适用条件，结果为true或false。 <span id="generateScriptBtn" style="color:blue;">生成脚本</span></div>
				<div>
					<form:textarea path="criteriaDesc" id="criteriaDesc" htmlEscape="false" rows="10" class="input-xxlarge "/>
				</div>		
				<div style="margin-top:10px;">规则脚本：支持数学运算及逻辑运算。<span id="generateFormBtn" style="color:blue;">测试脚本</span></div>
				<div>
					<form:textarea path="criteria" id="criteria" htmlEscape="false" rows="10" class="input-xxlarge "/>
				</div>   
			</div>  
        	<div style="width:40%;border-left:1px solid silver;padding-left:5px;min-height:400px;">
				<div>用户属性列表：</div>
				<div id="props"></div>		 
			</div> 					 
	    </div>  	
	</div>	
	<div class="form-actions">
		<shiro:hasPermission name="diy:guideTerm:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>	
			</form:form>	 
			
    <!-- 公式检查 -->
    <div style="padding-left:10px;width:100%;">
    	<div>填写表单数据，并验证规则 </div>
    	<div id="testResult" style="display:none;font-weight:bold;line-height:18px;background-color:#b3fdd5;color:green;border:1px solid darkgreen;"></div>
    	<div id="testMsg" style="display:none;color:red;line-height:18px;background-color:#fb8d98;border:1px solid darkred;"></div>
    	<form id="jsonform"></form>
    </div>			              		
</body>
</html>