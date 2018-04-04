<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/pages/common/commonhead.jsp" %>
<script src="${pageContext.request.contextPath}/js/common/ImagePreview.js" type="text/javascript"></script>
</head>

<%
	String functionName = "点播媒体分类分类维护"; //功能名称
	String processBO = "com.fstar.cms.MediaBO";
	String processMETHOD = "media_type";
	String serverUrl = request.getContextPath()+"/cm?ProcessMETHOD="+processMETHOD+"&ProcessBO="+processBO;
%>


<script type="text/javascript">  
		var $grid;
		var $dialog;
		var $form;
		
		var imageID;
		
        $(function ()
        {        	
			$("#pageloading").hide();	
			
			$("#mainlayout").ligerLayout();
			
			$("#upload").uploadPreview({ Img: "ImgPreview", Width: 120, Height: 120 });
			
			$grid = $("#maingrid").ligerGrid({
                height:'99%',width:'100%',
                columns: [
	                { display: '点播媒体分类编号', name: 'type_id', id:'type_id', align: 'left', width: 80 },
	                { display: '点播媒体分类名称', name: 'type_name', id:'type_name', width: 150 },
	                { display: '点播媒体分类封面', name: 'image', width: 150, 
	                	render:function(rowdata,rowindex)
			 	    	{
							return "<img src='data:image/png;base64,"+rowdata.image+"' onclick=\"bigImg(this,'"+rowdata.type_name+"')\" width='150' height='100'/>";
			 	    	}
	                },
	                { display: '点播媒体分类高亮封面', name: 'image_sel', width: 150, 
	                	render:function(rowdata,rowindex)
			 	    	{
							return "<img src='data:image/png;base64,"+rowdata.image_sel+"' onclick=\"bigImg(this,'"+rowdata.type_name+"')\" width='150' height='100'/>";
			 	    	}
	                },
	                { display: '排序', name: 'order_no', id:'order_no', align: 'left', width: 80 },	                
	                { display: '维护时间', name: 'maintain_time', width: 150 }
                ], usePager: false, rownumbers:true,
                tree: {
                	columnId: 'type_name',
                    idField: 'type_id',
                    parentIDField: 'super_id'
                },
                toolbar: { items: [
	                { text: '新增', click: toolbarAction, icon: 'add' },
	                { line: true },
	                { text: '修改', click: toolbarAction, icon: 'modify' },
	                { line: true },
	                { text: '删除', click: toolbarAction, icon: 'delete' }
                ]
                }
            });
			
			$form = $("#mainform").ligerForm({
	        	inputWidth: 200, labelWidth: 150, space:50, validate:true, align: 'center',width: '98%',
	        	fields:[	//表单栏位	        	    
	        		{ display: "点播媒体分类编号",name:"type_id",type:"text",validate:{required:true}},
	        		{ display: "点播媒体分类名称",name:"type_name",type:"text",validate:{required:false}},
	        		{ display: "点播媒体分类封面",name:"image",type:"popup",validate:{required:false},
		        		editor: {
	        				onButtonClick: selectImage
	        			}
	        		},
	        		{ display: "点播媒体分类高亮封面",name:"image_sel",type:"popup",validate:{required:false},
		        		editor: {
	        				onButtonClick: selectImageSel
	        			}
	        		},
	        		{ display: "排序",name:"order_no",type:"int",validate:{required:false}},
	        		{ display: "上级点播媒体分类编号",name:"super_id",type:"text",validate:{required:false}}
	        	],
	        	buttons:[{text:"确认",click: doAction}]
	        });
			
			action = 'search';
			doAction();
        });  
        
        var action = "";
        function toolbarAction(item)
        {
        	action = item.icon;
        	if (action == "add"){
        		$form.setData({type_id:'',server_type:'',type_name:'',ip:'',url_prefix:'',image:'',image_sel:'',order_no:''});
        		$form.setEnabled(['type_id','type_name','super_id','image','image_sel','order_no'],true);
        	}else if (action == "modify"){
        		var select = $grid.getSelectedRow();
        		if (select == null){
        			 var manager = $.ligerDialog.tip({ title: '提示',content:'请选择一条数据！'});
        			 setTimeout(function () { manager.close(); }, 3000);
        			 return;
        		}
        		$form.setData(select);
        		$form.setEnabled(['type_name','super_id','image','image_sel','order_no'],true);
        		$form.setEnabled(['type_id'],false);
        	}else if (action == "delete"){
        		var select = $grid.getSelectedRow();
        		if (select == null){
        			 var manager = $.ligerDialog.tip({ title: '提示',content:'请选择一条数据！'});
        			 setTimeout(function () { manager.close(); }, 3000);
        			 return;
        		}
        		$form.setData(select);
        		$form.setEnabled(['type_id','type_name','super_id','image','image_sel','order_no'],false);        		
        	}
        	$dialog = $.ligerDialog.open({height: 400,width: 500,target: $("#mainform")});
        }
        
        function doAction()
		{		
			var form = liger.get('mainform');
			if (action == 'search' || form.valid()) {
              	$("#pageloading").show();
               	var param=$("#mainform").serialize();
               	
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

            var dataURL = canvas.toDataURL("image/png");
            return dataURL
        }
        
        var $imageDialog; //图片上传框
        
        //打开图片上传框
        function selectImage(){
        	$("#ImgPreview")[0].src="";
        	$("#upload")[0].value="";
        	imageID = 'image';
        	$imageDialog = $.ligerDialog.open({height: 700,width: 500,target: $("#Imgupload")});
        }
        
        //打开图片上传框
        function selectImageSel(){
        	$("#ImgPreview")[0].src="";
        	$("#upload")[0].value="";
        	imageID = 'image_sel';
        	$imageDialog = $.ligerDialog.open({height: 700,width: 500,target: $("#Imgupload")});
        }
        
        //设置值
        function selectImageOk(){
        	$form.getEditor(imageID).setValue(getBase64Image($("#ImgPreview")[0]));
        	$form.getEditor(imageID).setText($("#upload")[0].value);
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
		<div id="maingrid" position="center" title="<%=functionName%>"></div>
	</div>
	<form id="mainform" style="display:none;"></form>
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