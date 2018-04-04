<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ include file="/pages/common/commonhead.jsp" %>
<%
	String functionName = "参数维护"; //功能名称
	String processBO = "com.fstar.cms.ServerBO";
	String processMETHOD = "setting";
	String serverUrl = request.getContextPath()+"/cm?ProcessMETHOD="+processMETHOD+"&ProcessBO="+processBO;
%>

</head>
<script type="text/javascript">  
var $form;
        $(function ()
        {        	
        	
			$("#pageloading").hide();	
            
			$form = $("#mainform").ligerForm({
	        	inputWidth: 500, labelWidth: 140, space:50, validate:true, align: 'center',width: '98%',
	        	fields:[	//表单栏位
	        		{ display: "启动时清除缓存",name:"ClearCacheOnStart",type:"select", width: 60 ,validate:{required:true},
	        			editor: {
		    				type:'select',
		    				data:[{id:'0',text:'否'},{id:'1',text:'是'}],
		    				initValue:''
		    			}
	        		}        		
	        	],
	        	buttons:[{text:"确认修改",click:doAction}]
	        });
	        
            action = 'search';
    		doAction();
        });  
        
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
    						$form.set({ data: result });
    	               	}else{
    	               		$.ligerDialog.error(result.ERR);
    	               	}
    					$("#pageloading").hide();
    			    }
    			}); 
    	       	
    	       	action = 'modify';
    	    } else {
    	    	form.showInvalid();
    		}
    	}
</script>	 

<body style="padding:4px">
    <center>
	<div class="l-loading" id="pageloading"></div>
	<div style="font-size: 14pt"><%=functionName%></div>
	<hr/>
	<form id="mainform"></form>
</center>
</body>
</html>