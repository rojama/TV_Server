<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/pages/common/commonhead.jsp" %>
</head>

<%
	String functionName = "平台运营商2"; //功能名称
%>


<script type="text/javascript">  

        $(function ()
        {        	
        	
			$("#pageloading").hide();	
            
            $("#searchForm").ligerForm({
	        	inputWidth: 500, labelWidth: 140, space:50, validate:true, align: 'center',width: '98%',
	        	fields:[	//表单栏位
	        		{ display: "运营商名称 ",name:"operId",type:"text",validate:{required:true}},
	        		{ display: "联系人",name:"operName",type:"text",validate:{required:false}},
	        		{ display: "联系电话",name:"opHolderAccount",type:"text",validate:{required:false}},
	        		{ display: "Email",name:"tradeId",type:"text",validate:{required:false}},
	        		{ display: "地址",name:"tradeId",type:"text",validate:{required:false}},
	        		{ display: "描述",name:"tradeId",type:"textarea",validate:{required:false}}	        		
	        	],
	        	buttons:[{text:"确认修改",click:submitAction}]
	        });
	        
            
        });  
        
        function submitAction()
		{		
			var form = liger.get('searchForm');
			if (form.valid()) {
              	$("#pageloading").show();                	  	           
               	var param=$("#searchForm").serialize();         
               	param+="&"+Math.random();    
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
	<form id="searchForm"></form>
</center>
</body>
</html>