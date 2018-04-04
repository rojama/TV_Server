<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.fstar.sys.JsonUtil"%>
<%@page import="com.fstar.help.OperationHelper"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>

<%
	String action =(String)request.getParameter("action");
	String locale=(String)session.getAttribute("locale");
	List<Map<String,Object>> list=null;
	OperationHelper obj_oper= new OperationHelper();
	if("findAll".equals(action))
	{
		list = new ArrayList();
		list=obj_oper.findOperationData(locale);
		String dataAsJson=JsonUtil.list2json(list);
		PrintWriter pw = response.getWriter();
		pw.print(dataAsJson);
		pw.flush();
		pw.close(); 
	}
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/pages/common/header.jsp" %>
<%@ taglib prefix="shiro" uri="/WEB-INF/shiro.tld" %>
<script type="text/javascript">
var manager;

$(function(){
	PageLoader.initTopToolBar();
	manager=PageLoader.initGridPanel();
	PageLoader.initGridData();
});

PageLoader = {
	initTopToolBar:function()
	{
          $("#toptoolbar").ligerToolBar({ items: [
               { text: '<fmt:message key="add"/>', click:Add,icon:'add'},
               { line: true },
        	   { text: '<fmt:message key="remove"/>',click:Remove,icon:'delete' },
        	   { line: true },
	           { text: '<fmt:message key="edit"/>',click:Edit,icon:'modify'},
	           { line: true },
        	   { text: '<fmt:message key="save"/>',click:Save,icon: 'save' }
            ]
          });
	},
	initGridPanel:function()
	{
		var g= $("#maingrid").ligerGrid({
			    columns:[
			    	{display:'OPE_ID',name:'OPE_ID',width:'100',align:'left'},
			    	{display:'<fmt:message key="code"/>',name:'OPE_CODE',width:'150',align:'left',editor: { type: 'text' }},
			    	{display:'<fmt:message key="zh_ch_name"/>',name:'OPE_NAME',width:'200',align:'left',editor: { type: 'text' }},
			    	{display:'<fmt:message key="en_us_name"/>',name:'OPE_ENGNAME',width:'200',align:'left',editor: { type: 'text' }},
			    	{display:'<fmt:message key="zh_tw_name"/>',name:'OPE_TWNAME',width:'200',align:'left',editor: { type: 'text' }},
			    	{display:'<fmt:message key="operating"/>',name:'OPE_TWNAME',width:'200',align:'center',
			    		render:function(rowdata,rowindex){
			    			
			    			var h="";
			    			
			    			if(rowdata._editing)
			    			{
			    				h += "<a href='javascript:endEdit(" + rowindex + ")'><fmt:message key='submit'/></a> ";
		                        h += "<a href='javascript:cancelEdit(" + rowindex + ")'><fmt:message key='cancel'/></a> ";
			    			}
			    			
			    			return h;
			    		}
			    	}
			    ],
			   width: '100%',
	 	   	   height: '100%',
	 	   	   rownumbers :true,
	 	   	   checkbox : true,
	 	       enabledEdit: true,
	 	       clickToEdit:false,
	 	       enabledSort:false
		});
		
		g.toggleCol("OPE_ID",false);
		return g;
	},
	initGridData:function()
	{
		$.getJSON('${pageContext.request.contextPath}/pages/system/aut/aut_operation.jsp?action=findAll&time=' + new Date().getTime(), function(result) {
		  
				  var jsonObj = {};   
				     
				  jsonObj.Rows = result;
				   
				  manager.set({data:jsonObj});
		});
	}
}

function Add()
{
	manager.addEditRow();
}

function Edit(row)
{
	var row = manager.getSelectedRow();
	if (!row) { alert('请选择行'); return; }
	
	var i=0;
	$.each(manager.getCheckedRows(), function(index, element) {
 		i++;
	});
   
   if(i>1)
   {
   	 alert('请只选择一行'); 
   	 return;
   }
	
	manager.beginEdit(row);
}

//删除
function Remove()
{
	var row = manager.getSelectedRow();
	if (!row) { alert('请选择行'); return; }
	
	$.ligerDialog.confirm("<fmt:message key='suer_delete'/>", function (yes) 
	{
		if(yes)
		{
			$.each(manager.getCheckedRows(), function(index, element) {
		   		manager.deleteRow(element);
	    	});
		}
	});
}

//提交编辑
function endEdit(rowid)
{
	manager.endEdit(rowid);
}
   
//撤销编辑
function cancelEdit(rowid) 
{ 
    manager.cancelEdit(rowid);
} 

//保存
function Save()
{
	var AddData = manager.getAdded();
	var EditData=manager.getUpdated();
	var DelData=manager.getDeleted();
	
	$("#AddData").attr("value",JSON.stringify(AddData));
	$("#EditData").attr("value",JSON.stringify(EditData));
	$("#DelData").attr("value",JSON.stringify(DelData));
	
	var param=$("#thisForm").serialize();
	ajax(param);
}

function ajax(param)
{
	$.ajax({
		type:'POST',
		url:'<%=contextPath%>/cm',
		dataType:'json',
		data:param,
		success:function(json)
	    {
	    	  message(json);	
	     	  PageLoader.initGridData();
	    }
  	});
}

function message(json)
{
	var mes=JSON.stringify(json);
	
	if(undefined!=json.MSG)
	{	
		$.ligerDialog.success("<fmt:message key='success'/>");
	}
	
	if(undefined!=json.ERR)
	{
		$.ligerDialog.error("<fmt:message key='error'/>");
	}
}
</script>
</head>
<body>
    <form id="thisForm" name="thisForm">
      <input type="hidden" name="AddData" id="AddData"/>
      <input type="hidden" name="EditData" id="EditData"/>
      <input type="hidden" name="DelData" id="DelData"/>
      <input type="hidden" name="ProcessMETHOD" id="ProcessMETHOD" value="Save"/>
	  <input type="hidden" name="ProcessBO" id="ProcessBO"  value="com.fstar.aut.Operation"/>
    </form>
	<div id="toptoolbar"></div> 
   	<div id="maingrid"></div>
</body>
</html>