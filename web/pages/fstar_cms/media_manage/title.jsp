<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/pages/common/commonhead.jsp" %>
</head>

<%
	String functionName = "板块标题栏维护"; //功能名称
	String processBO = "com.fstar.cms.TitleBO";
	String processMETHOD = "jspServer";
	String serverUrl = request.getContextPath()+"/cm?ProcessMETHOD="+processMETHOD+"&ProcessBO="+processBO;
%>

<script type="text/javascript">  
	var $grid;
	var $dialog;
	var $form;
	
	$(function ()
	{        	
		$("#pageloading").hide();	
		
		$("#mainlayout").ligerLayout();
		
		$grid = $("#maingrid").ligerGrid({
	        height:'99%',width:'100%',
	        columns: [
	            { display: '板块编号', name: 'block_id', align: 'left', width: 80 },
	            { display: '标题序号', name: 'title_no', width: 80 },
	            { display: '标题名称', name: 'title_name', width: 150 },
	            { display: '链接分类', name: 'type_id' , width: 150}
	        ], pageSize:20 ,rownumbers:true,
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
	    	inputWidth: 200, labelWidth: 100, space:50, validate:true, align: 'center',width: '98%',
	    	fields:[	//表单栏位
	    		{ display: "板块编号",name:"block_id",type:"text",validate:{required:true}},
	    		{ display: "标题序号",name:"title_no",type:"select",validate:{required:true},
	    			editor: {
	    				type:'select',
	    				data:[{id:'1',text:'标题一'},{id:'2',text:'标题二'},{id:'3',text:'标题三'},
	    				      {id:'4',text:'标题四'},{id:'5',text:'标题五'},{id:'6',text:'标题六'}],
	    				initValue:''
	    			}
	    		},
	    		{ display: "标题名称",name:"title_name",type:"text",validate:{required:true}},
	    		{ display: "链接分类",name:"type_id",type:"text",validate:{required:true}}  		
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
			$form.setData({block_id:'',title_no:'',title_name:'',type_id:''});
			$form.setEnabled(['block_id','title_no','title_name','type_id'],true);
		}else if (action == "modify"){
			var select = $grid.getSelectedRow();
			if (select == null){
				 var manager = $.ligerDialog.tip({ title: '提示',content:'请选择一条数据！'});
				 setTimeout(function () { manager.close(); }, 3000);
				 return;
			}
			$form.setData(select);
			$form.setEnabled(['title_name','type_id'],true);
			$form.setEnabled(['title_no','block_id'],false);
		}else if (action == "delete"){
			var select = $grid.getSelectedRow();
			if (select == null){
				 var manager = $.ligerDialog.tip({ title: '提示',content:'请选择一条数据！'});
				 setTimeout(function () { manager.close(); }, 3000);
				 return;
			}
			$form.setData(select);
			$form.setEnabled(['block_id','title_no','title_name','type_id'],false);        		
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
</script>	 

<body style="padding:4px">
	<div class="l-loading" id="pageloading"></div>
	<div id="mainlayout">	
		<div id="maingrid" position="center" title="<%=functionName%>"></div>
	</div>
	<form id="mainform" style="display:none;"></form>
</body>
</html>