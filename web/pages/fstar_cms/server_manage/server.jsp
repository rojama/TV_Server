<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/pages/common/commonhead.jsp" %>
</head>

<%
	String functionName = "服务器维护"; //功能名称
	String processBO = "com.fstar.cms.ServerBO";
	String processMETHOD = "server";
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
	                { display: '服务器编号', name: 'server_id', align: 'left', width: 80 },
	                { display: '服务器种类', name: 'server_type', width: 80 },
	                { display: '服务器名称', name: 'server_name', width: 150 },
	                { display: '服务器IP地址', name: 'ip' , width: 150},
	                { display: '服务器URL前缀', name: 'url_prefix' , width: 300}
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
	        		{ display: "服务器编号",name:"server_id",type:"text",validate:{required:true}},
	        		{ display: "服务器种类",name:"server_type",type:"select",validate:{required:false},
	        			editor: {
	        				type:'select',
	        				data:[{id:'1',text:'内部服务器'},{id:'2',text:'外部服务器'}],
	        				initValue:'1'
	        			}
	        		},
	        		{ display: "服务器名称",name:"server_name",type:"text",validate:{required:false}},
	        		{ display: "服务器IP地址",name:"ip",type:"text",validate:{required:false}},
	        		{ display: "服务器URL前缀",name:"url_prefix",type:"text",validate:{required:false}}     		
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
        		$form.setData({server_id:'',server_type:'',server_name:'',ip:'',url_prefix:''});
        		$form.setEnabled(['server_id','server_type','server_name','ip','url_prefix'],true);
        	}else if (action == "modify"){
        		var select = $grid.getSelectedRow();
        		if (select == null){
        			 var manager = $.ligerDialog.tip({ title: '提示',content:'请选择一条数据！'});
        			 setTimeout(function () { manager.close(); }, 3000);
        			 return;
        		}
        		$form.setData(select);
        		$form.setEnabled(['server_type','server_name','ip','url_prefix'],true);
        		$form.setEnabled(['server_id'],false);
        	}else if (action == "delete"){
        		var select = $grid.getSelectedRow();
        		if (select == null){
        			 var manager = $.ligerDialog.tip({ title: '提示',content:'请选择一条数据！'});
        			 setTimeout(function () { manager.close(); }, 3000);
        			 return;
        		}
        		$form.setData(select);
        		$form.setEnabled(['server_id','server_type','server_name','ip','url_prefix'],false);        		
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
    					$dialog.hide();
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