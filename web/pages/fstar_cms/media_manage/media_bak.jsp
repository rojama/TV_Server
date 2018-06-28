<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/pages/common/commonhead.jsp" %>
<script src="${pageContext.request.contextPath}/js/common/ImagePreview.js" type="text/javascript"></script>
</head>

<%
	String functionName = "点播媒体维护"; //功能名称
	String processBO = "com.fstar.cms.MediaBO";
	String processMETHOD = "media";
	String serverUrl = request.getContextPath()+"/cm?ProcessMETHOD="+processMETHOD+"&ProcessBO="+processBO;
%>


<script type="text/javascript">  
		var action = "";
		var type_id = "";
		var $grid;
		var $urlgrid;
		var $dialog;
		var $tree;
		var $form;
		
        $(function ()
        {        	
			$("#pageloading").show();	
			
			$("#mainlayout").ligerLayout({ leftWidth: 200});
			
			$("#upload").uploadPreview({ Img: "ImgPreview", Width: 120, Height: 120 });
			
			//加载树
			$tree = $("#maintree").ligerTree({
				needCancel: false,
				checkbox: false,
				single: true,
	            idFieldName :'type_id',
	            textFieldName : 'type_name',
	            parentIDFieldName :'super_id',            
	            onSelect: loadGrid
            });				
			
			$.ajax({
    			type:'POST',
    			url:'<%=serverUrl%>'+'&ACTION=getMediaType', 
    			dataType:'json',
    			success:function(result)
    		    {
    				if (result.ERR == null){
	                	$tree.set({ data: result.Rows });
	                	$tree.tree.width('100%');  //自动拓展
	               	}else{
	               		$.ligerDialog.error(result.ERR);
	               	}
    				$("#pageloading").hide();
    		    }
    		}); 

			//加载表格
			$grid = $("#maingrid").ligerGrid({
                height:'99%',width:'100%',
                columns: [
	                { display: '媒体编号', name: 'media_id', align: 'left', width: 80 },
	                { display: '媒体名称', name: 'media_name', width: 150 },
	                { display: '媒体描述', name: 'media_describe', width: 150 },
	                { display: '媒体类型', name: 'type_id', width: 150 },
	                { display: '媒体封面', name: 'image', width: 150, 
	                	render:function(rowdata,rowindex)
			 	    	{
							return "<img src='data:image/jpeg;base64,"+rowdata.image+"' onclick=\"bigImg(this,'"+rowdata.media_name+"')\" width='150' height='100'/>";
			 	    	}
	                },
	                { display: '媒体长度', name: 'length', width: 150 },
	                { display: '总集数', name: 'total_series', width: 150 },
                    { display: '整套收费', name: 'total_fee', width: 80 },
	                { display: '排序', name: 'order_no', width: 150 },
	                { display: '导演', name: 'director', width: 150 },
	                { display: '演员', name: 'actor', width: 150 },
	                { display: '评分', name: 'score', width: 150 },
	                { display: '总播放次数', name: 'play_times', width: 150 }
                ], pageSize:20 ,rownumbers:true,               
                toolbar: { items: [
	                { text: '新增', click: toolbarAction, icon: 'add' },
	                { line: true },
	                { text: '修改', click: toolbarAction, icon: 'modify' },
	                { line: true },
	                { text: '删除', click: toolbarAction, icon: 'delete' },
	                { line: true },
	                { text: '地址维护', click: toolbarAction, icon: 'url' },
	                { line: true },
	                { text: '设置推荐', click: toolbarAction, icon: 'recommend' },
	                { line: true },
	                { text: '取消推荐', click: toolbarAction, icon: 'unrecommend' }
                ]
                }
            });
			
			//加载表单
			$form = $("#mainform").ligerForm({
	        	inputWidth: 200, labelWidth: 150, space:50, validate:true, align: 'center',width: '98%',
	        	fields:[	//表单栏位
	        		{ display: "媒体编号",name:"media_id",type:"text",validate:{required:true}},
	        		{ display: "媒体名称",name:"media_name",type:"text",validate:{required:false}},
	        		{ display: "媒体描述",name:"media_describe",type:"textarea",validate:{required:false}},
	        		{ display: "媒体类型",name:"type_id",type:"text",validate:{required:false}},
	        		{ display: "媒体封面",name:"image",type:"popup",validate:{required:false},
		        		editor: {
	        				onButtonClick: selectImage
	        			}
	        		},
	        		{ display: '媒体长度(分钟)', name: 'length',type:"int",validate:{required:false}},
	        		{ display: "总集数",name:"total_series",type:"int",validate:{required:false}},
                    { display: "整套收费",name:"total_fee",type:"int",validate:{required:false}},
	        		{ display: "排序",name:"order_no",type:"int",validate:{required:false}},
	        		{ display: "导演",name:"director",type:"text",validate:{required:false}},
	        		{ display: "演员",name:"actor",type:"text",validate:{required:false}},
	        		{ display: "评分",name:"score",type:"number",validate:{required:false}}
	        	],
	        	buttons:[{text:"确认",click: doAction}]
	        });
			
			//加载url
			$urlgrid = $("#urlgrid").ligerGrid({
                width:'99%',
                columns: [
	                { display: '媒体编号', name: 'media_id', align: 'left', width: 80 },
	                { display: '集数', name: 'series_no', width: 50,editor: { type: 'int' } },
	                { display: '名称', name: 'series_name', width: 80,editor: { type: 'text' } },
                    { display: '单集收费', name: 'fee', width: 50,editor: { type: 'int' } },
	                { display: '服务器编号', name: 'server_id', width: 70,editor: { type: 'text' } },
	                { display: '视频地址', name: 'full', width: 500,editor: { type: 'text' } }
	           
                ], rownumbers:true, enabledEdit: true,  usePager:false,       
                toolbar: { items: [
	                { text: '新增', click: urlgridAction, icon: 'add' },
	                { line: true },
	                { text: '删除', click: urlgridAction, icon: 'delete' },
	                { line: true },
	                { text: '保存', click: urlgridAction, icon: 'save' },
	                { line: true }	                
                ]
                }
            });
			
			
        });  
        
        function urlgridAction(item)
        {
        	var urlaction = item.icon;
        	if (urlaction == "add"){
        		var row = $urlgrid.getSelectedRow();
        		$urlgrid.addRow({media_id:urlselect.media_id, fee:0});
        	}else if (urlaction == "delete"){
        		$urlgrid.deleteSelectedRow();
        	}else if (urlaction == "save"){
        		var data = $urlgrid.getData();        		
        		var urldata = JSON.stringify(data);
                $.ajax({
        			type:'POST',
        			url:'<%=serverUrl%>'+'&ACTION=saveUrl' + '&media_id='+urlselect.media_id  ,
        			dataType:'json',
        			data:{urldata:urldata},
        			success:function(result)
        		    {
        				if (result.ERR == null){
        					$.ligerDialog.success('保存成功')
    	               	}else{
    	               		$.ligerDialog.error(result.ERR);
    	               	}
    					$("#pageloading").hide();
    					if ($dialog != null) $dialog.hide();
        		    }
        		});  
        	}
        }
        
        function loadGrid(node){
        	type_id = node.data.type_id;
			doAction('search', 'type_id='+type_id);			
        }
        
        var urlselect;
        
        function toolbarAction(item)
        {
        	if (type_id == ''){        		
        		var manager = $.ligerDialog.tip({ title: '提示',content:'请选择点播媒体！'});
        		setTimeout(function () { manager.close(); }, 3000);
   			 	return;
        	}
        	
        	$form.getEditor('image').setText("");  //清空图片地址
        	
        	action = item.icon;
        	if (action == "add"){
        		$form.setData({media_id:'自动获取',media_name:'',media_describe:'',type_id:type_id,image:'',length:'0',total_series:'1',total_fee:'0',order_no:'0',director:'',actor:'',score:'0.00'});
        		$form.setEnabled(['media_id','media_name','media_describe','type_id','image','length','total_series','total_fee','order_no','director','actor','score'],true);
        		$form.setEnabled(['media_id','type_id'],false);
        	}else if (action == "modify"){
        		var select = $grid.getSelectedRow();
        		if (select == null){
        			var manager = $.ligerDialog.tip({ title: '提示',content:'请选择一条数据！'});
        			setTimeout(function () { manager.close(); }, 3000);
        			return;
        		}
        		$form.setData(select);
        		$form.setEnabled(['media_id','media_name','media_describe','type_id','image','length','total_series','total_fee','order_no','director','actor','score'],true);
        		$form.setEnabled(['media_id','type_id'],false);
        	}else if (action == "delete" || action == "recommend" || action == "unrecommend"){
        		var select = $grid.getSelectedRow();
        		if (select == null){
        			var manager = $.ligerDialog.tip({ title: '提示',content:'请选择一条数据！'});
        			setTimeout(function () { manager.close(); }, 3000);
        			return;
        		}
        		$form.setData(select);
        		$form.setEnabled(['media_id','media_name','media_describe','type_id','image','length','total_series','total_fee','order_no','director','actor','score'],false);
        	}else if (action == "url"){
        		urlselect = $grid.getSelectedRow();
        		if (urlselect == null){
        			var manager = $.ligerDialog.tip({ title: '提示',content:'请选择一条数据！'});
        			setTimeout(function () { manager.close(); }, 3000);
        			return;
        		}
        		$dialog = $.ligerDialog.open({title: urlselect.media_name+item.text ,height: 500,width: 850,target: $("#urlgrid")});
        		$.ajax({
        			type:'POST',
        			url:'<%=serverUrl%>'+'&ACTION=getUrlData',
        			dataType:'json',
        			data:urlselect,
        			success:function(result)
        		    {
        				if (result.ERR == null){
        					$urlgrid.set({ data: result });
        					$urlgrid.sortedData = '';
    	               	}else{
    	               		$.ligerDialog.error(result.ERR);
    	               	}
    					$("#pageloading").hide();
        		    }
        		});       	
        		return;
        	}
        	$dialog = $.ligerDialog.open({title: item.text ,height: 500,width: 500,target: $("#mainform")});
        }
        
        function doAction(act, input)
		{		
        	if (act != null) action = act;
			var form = liger.get('mainform');
			if (action == 'search' || form.valid()) {
              	$("#pageloading").show();
               	var param=$("#mainform").serialize();
               	if (input != null) param = input;
               
               	if (type_id != null) param += '&type_id='+type_id;  	//特殊处理
               	
               	$.ajax({
        			type:'POST',
        			url:'<%=serverUrl%>'+'&ACTION='+action,
        			dataType:'json',
        			data:param,
        			success:function(result)
        		    {
        				if (result.ERR == null){
    	                	$grid.set({ data: result });
    	                	$grid.sortedData = '';
    	               	}else{
    	               		$.ligerDialog.error(result.ERR);
    	               	}
    					$("#pageloading").hide();
    					if ($dialog != null) $dialog.hide();
        		    }
        		});       	
            } else {
            	form.showInvalid();
			}
		}
        
        function getBase64Image(img) {
            var canvas = document.createElement("canvas");
            canvas.width = img.width;
            canvas.height = img.height;

            var ctx = canvas.getContext("2d");
            ctx.drawImage(img, 0, 0, img.width, img.height);

            var dataURL = canvas.toDataURL("image/jpeg");
            return dataURL
        }
        
        var $imageDialog; //图片上传框
        
        //打开图片上传框
        function selectImage(){
        	$("#ImgPreview")[0].src="";
        	$("#upload")[0].value="";
        	$imageDialog = $.ligerDialog.open({height: 700,width: 500,target: $("#Imgupload")});
        }
        
        //设置值
        function selectImageOk(){
        	$form.getEditor('image').setValue(getBase64Image($("#ImgPreview")[0]));
        	$form.getEditor('image').setText($("#upload")[0].value);
        	if ($imageDialog != null) $imageDialog.hide();
        }
        
        //打开大的图片
        function bigImg(imgObj, title){
	       	$("#imgBig")[0].src = imgObj.src; 
	       	$.ligerDialog.open({title: title, width: null, target: $("#Imgview")});
       	}
</script>	 

<body style="padding:4px">
	<div class="l-loading" id="pageloading"></div>		
	<div id="mainlayout">	
		<div position="left" title="点播媒体类别" id="maintree" style="width:100%;height:95%;overflow-y:auto;">
		</div>
		<div position="center" title="<%=functionName%>" id="maingrid"></div>
	</div> 		
	<form id="mainform" style="display:none;"></form>
	<div id="urlgrid" style="display:none;"></div>
	<div id="Imgview" style="display:none;">
	   <img id="imgBig" src=""/>
	</div>
	<div id="Imgupload" style="display:none;">
		<table>
			<tr><td><input type="file" id="upload" /></td></tr>
			<tr><td><input type="button" onclick="selectImageOk();" value="确定"/></td></tr>
			<tr><td><img id="ImgPreview"/></td></tr>
		</table>
	</div>
</body>
</html>