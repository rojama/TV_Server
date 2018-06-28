<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/pages/common/commonhead.jsp" %>
</head>

<%
	String functionName = "终端维护"; //功能名称
	String processBO = "com.fstar.cms.ServerBO";
	String processMETHOD = "terminal";
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
	                { display: '终端编号', name: 'device_info', align: 'left', width: 250 },
	                { display: '终端MAC', name: 'mac', width: 130 },
	                { display: '终端IMEI', name: 'imei', width: 130 },
	                { display: '终端SN', name: 'serial' , width: 130},
	                { display: 'VIP到期日', name: 'validity' , width: 100},
                    { display: '启用日期', name: 'adddate' , width: 150},
	                { display: '备注', name: 'remark' , width: 200}
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
	        		{ display: "终端编号",name:"device_info",type:"text",validate:{required:true}},
	        		{ display: "终端MAC",name:"mac",type:"text",validate:{required:false}},
	        		{ display: "终端IMEI",name:"imei",type:"text",validate:{required:false}},
	        		{ display: "终端SN",name:"serial",type:"text",validate:{required:false}},
	        		{ display: "VIP到期日",name:"validity",type:"date",validate:{required:false}},
                    { display: "启用日期",name:"adddate",type:"date",validate:{required:false}},
	        		{ display: "备注",name:"remark",type:"text",validate:{required:false}}
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
        		$form.setData({device_info:'自动编号',mac:'',imei:'',serial:'',validity:'',adddate:'',remark:''});
        		$form.setEnabled(['device_info','mac','imei','serial','validity','adddate','remark'],true);
        		$form.setEnabled(['device_info'],false);
        	}else if (action == "modify"){
        		var select = $grid.getSelectedRow();
        		if (select == null){
        			 var manager = $.ligerDialog.tip({ title: '提示',content:'请选择一条数据！'});
        			 setTimeout(function () { manager.close(); }, 3000);
        			 return;
        		}
        		$form.setData(select);
        		$form.setEnabled(['device_info','mac','imei','serial','validity','adddate','remark'],true);
        		$form.setEnabled(['device_info'],false);
        	}else if (action == "delete"){
        		var select = $grid.getSelectedRow();
        		if (select == null){
        			 var manager = $.ligerDialog.tip({ title: '提示',content:'请选择一条数据！'});
        			 setTimeout(function () { manager.close(); }, 3000);
        			 return;
        		}
        		$form.setData(select);
        		$form.setEnabled(['device_info','mac','imei','serial','validity','adddate','remark'],false);
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
        					if ($dialog) $dialog.hide();
    	                	$grid.set({ data: result });
    	                	$grid.sortedData = '';
    	               	}else{
    	               		$.ligerDialog.error(result.ERR);
    	               	}
    					$("#pageloading").hide();
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