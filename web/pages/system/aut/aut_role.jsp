<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.fstar.sys.*"%>
<%@page import="com.fstar.help.TempMenuHelper"%>
<%@page import="com.fstar.help.OperationHelper"%>
<%@page import="com.fstar.help.MenuHelper"%>
<%@page import="com.fstar.help.RoleHelper"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>

<%
	String action = (String)request.getParameter("action");
	String locale=(String)session.getAttribute("locale");
	
	RoleHelper obj_Role = new RoleHelper();
	if("findAll".equals(action))
	{
		List<Map<String,Object>> list = obj_Role.innerFindRole(locale, "");
		String dataAsJson=JsonUtil.list2json(list);
		PrintWriter pw = response.getWriter();
		pw.print(dataAsJson);
		pw.flush();
		pw.close(); 
	}
	else if("menuData".equals(action))
	{
		String ROLE_ID=(String)request.getParameter("ROLE_ID");
		String ROLE_SUPER_ID=(String)request.getParameter("ROLE_SUPER_ID");
		
		MenuHelper obj_MenuHelper = new MenuHelper();
		List<Map<String,Object>> list = obj_MenuHelper.innerFindMenu(locale, ROLE_ID,ROLE_SUPER_ID);
		String dataAsJson=JsonUtil.list2json(list);
		dataAsJson=dataAsJson.replace("ID", "id");
		dataAsJson=dataAsJson.replace("TEXT", "text");
		dataAsJson=dataAsJson.replace("null", "false");
		dataAsJson=dataAsJson.replace("ISCHECKED", "ischecked");
		PrintWriter pw = response.getWriter();
		pw.print(dataAsJson);
		pw.flush();
		pw.close(); 
	}
	else if("OperData".equals(action))
	{
		OperationHelper obj_Oper= new OperationHelper();
		List<Map<String,Object>> list = obj_Oper.findOperationData(locale);
		String dataAsJson=JsonUtil.list2json(list);
		PrintWriter pw = response.getWriter();
		pw.print(dataAsJson);
		pw.flush();
		pw.close();
	}
	else if("TempData".equals(action))
	{
		String ROLE_ID = request.getParameter("ROLE_ID").toString();
		
		TempMenuHelper ojb_TempMenu = new TempMenuHelper();
		List<Map<String,Object>> list = ojb_TempMenu.innerFindTempData(ROLE_ID);
		
		String dataAsJson=JsonUtil.list2json(list);
		dataAsJson=dataAsJson.replace("ID", "id");
		dataAsJson=dataAsJson.replace("TEXT", "text");
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
var start_Data=[{IS_START:'Y',text:'<fmt:message key="yes"/>'},{IS_START:'N',text:'<fmt:message key="no"/>'}];
var _menuTree;
var _initButton
var _initOper
   
$(function(){
	PageLoader.initTopToolBar();
	manager=PageLoader.initGridPanel();
	PageLoader.initGridData();
	_menuTree=PageLoader.initMenuTree();
	_initButton=PageLoader.initButton();
});   

PageLoader = {
	initTopToolBar:function()
	{
          $("#toptoolbar").ligerToolBar({ items: [
               { text: '<fmt:message key="add"/>',click:Add, icon:'add'},
               { line: true },
        	   { text: '<fmt:message key="remove"/>',click:Remove,icon: 'delete' },
        	   { line: true },
        	   { text: '<fmt:message key="edit"/>',click:Edit,icon: 'modify' },
        	   { line: true },
        	   { text: '<fmt:message key="save"/>',click:Save,icon: 'save' },
        	   { line: true },
        	   { text: '角色权限',click:Role,icon: 'settings' }
            ]
          });
	},
	initGridPanel:function()
	{
		  var g=$("#maingrid").ligerGrid({
		  		columns:[
		  			{display:'ROLE_ID',name:'ROLE_ID',width:'200',align:'left'},
		  			{display:'<fmt:message key="zh_ch_name"/>',name:'ROLE_NAME',width:'200',align:'left',editor: { type: 'text' }},
		  			{display:'<fmt:message key="en_us_name"/>',name:'ROLE_ENGNAME',width:'200',align:'left',editor: { type: 'text' }},
		  			{display:'<fmt:message key="zh_tw_name"/>',name:'ROLE_TWNAME',width:'200',align:'left',editor: { type: 'text' }},
		  			{display:'<fmt:message key="start"/>',name:'IS_START',width:'200',align:'center',
		  			          editor:{type: 'select',data:start_Data,valueColumnName: 'IS_START'},
		  			          render:function(rowdata){
	 	                
			 	                 if(rowdata.IS_START=='Y')
					 	         {
					 	         	return "<fmt:message key='yes'/>";
					 	         }
					 	         else if(rowdata.IS_START=='N')
					 	         {
					 	         	return "<fmt:message key='no'/>";
					 	         }
					 	         else
					 	         {
					 	         	return "<fmt:message key='yes'/>";
					 	         }
			 	            }  
		  			},
	 	       	    {display:'<fmt:message key="operating"/>',width: 150,
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
	 	  	    usePager :false,
	 	        checkbox : true,
	 	        alternatingRow: false,
	 	        autoCheckChildren: false,
	 	        enabledEdit: true,
	 	        clickToEdit:false,
	 	        enabledSort:false,
	 	        tree: { columnName: 'ROLE_NAME' }
		  });
		  
		  g.toggleCol("ROLE_ID",false);//隐藏id列
		  return g;
	},
	 initTab:function(){//初始化功能选项卡
	 	$("#navtab").ligerTab();
	 },
	 initButton:function()
	 {
	 	var g=$("#buttonFunction").ligerGrid({
	 		usePager :false,
	 		enabledEdit: true,
	 		enabledSort:false,
	 		tree: { columnName: 'text' }
	 	});
	 	return g;
	 },
	 initMenuTree:function()//初始化菜单树
	 {
	 	var g=$("#menuTree").ligerTree({
		 		 nodeDraggable: true
		 	});
		return g;
	 },
	 initGridData:function(){//角色数据
	 	$.getJSON('${pageContext.request.contextPath}/pages/system/aut/aut_role.jsp?action=findAll&time=' + new Date().getTime(), function(result) {
	  
			  var jsonObj = {};   
			     
			  jsonObj.Rows = result;
			  
			  manager.set({data:jsonObj});
		});
	 },
	 initOperData:function()
	 {
	 	$.getJSON('${pageContext.request.contextPath}/pages/system/aut/aut_role.jsp?action=OperData&time=' + new Date().getTime(), function(result) {
			  
			  var columns =[];
			  var jsonObj = {};
	 	  	  jsonObj.Rows = result;
	 	  	  
	 	  	  columns.push({display:'ID',name:'id',width:'200',align:'left'});
	 	  	  columns.push({display:'<fmt:message key="name"/>',name:'text',width:'200',align:'left'});
	 	  	  
			  for(var i=0;i<jsonObj.Rows.length;i++)
			  {
			  	 var name=jsonObj.Rows[i].NAME;
			  	 var Code=jsonObj.Rows[i].OPE_CODE;
			  	 columns.push({ display: name, name: Code, align: 'center', width: 55,editor: { type: 'checkbox' }});
			  }
			  _initButton.set('columns', columns); 
			  _initButton.toggleCol("id",false);//隐藏id列
              _initButton.reRender();
		});
	 },
	 initTempData:function()//获取零时表中的数据
	 {
	 	var row = manager.getSelectedRow();
	 	var ROLE_ID = row.ROLE_ID;
	 	$.getJSON('${pageContext.request.contextPath}/pages/system/aut/aut_role.jsp?action=TempData&ROLE_ID='+ROLE_ID+'&time=' + new Date().getTime(), function(result) {
	 		  
	 		  var columns =[];
			  var jsonObj = {};
	 	  	  jsonObj.Rows = result;
	 	  	  
	 		  _initButton.set({data:jsonObj});
	 	});
	 }
}
//新增
function Add()
{
	var selectRow = manager.getSelectedRow();
    var parentRow = selectRow;
    
    if (manager.isLeaf(parentRow))//如果是叶节点  则将叶节点升级
    {
        manager.upgrade(parentRow);
    }
    
    manager.add(null, null, true, parentRow);
}

//修改
function Edit()
{
	var row = manager.getSelectedRow();
	if(checkSelectRow(row))
	{
		manager.beginEdit(row);
	}
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
	var data = manager.getData();
	var RoleData ="";
	
	if(parseInt(data.length)>0)
	{
		for(var i=0;i<parseInt(data.length);i++)
		{
				var isParent = manager.getParent(manager.getRow(i));
				
				if(isParent==null)
				{
					RoleData+=JSON.stringify(manager.getRow(i))+";";
				}
		}
		
		RoleData=RoleData.substring(0,RoleData.length-1);
	}
	
	$("#ProcessMETHOD").attr("value","Save");
	$("#ProcessBO").attr("value","com.fstar.aut.Role");
	$("#Data").attr("value",RoleData);
	var param=$("#thisForm").serialize();
	
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
//消息提示
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

function checkSelectRow(row)
{
	if (!row) { alert('请选择行'); return false; }
		
	var i=0;
	$.each(manager.getCheckedRows(), function(index, element) {
 		i++;
	});
   
    if(i>1)
    {
   	  alert('请只选择一行'); 
   	  return false;
    }
    
    return true;
}

//设置角色权限
var _RoleDialog;
function Role()
{
	var row = manager.getSelectedRow();
	if(checkSelectRow(row))
	{
		getMenuTree(row);
	    _RoleDialog=$.ligerDialog.open({ 
   		  width: 550,
          height: 400, 
          top: 100,
          isResize: true,
          title:"<fmt:message key='role'/>",
   		  target: $("#menuTree"),
   		  buttons:[
   			{text:"<fmt:message key='next' />",onclick:RoleSave},
   			{text:"<fmt:message key='reset'/>",onclick:Cancle}
   		]
   	   });
   	}
}

function getMenuTree(row)
{
	  
	 var ROLE_ID=row.ROLE_ID;
	 var ROLE_SUPER_ID=row.ROLE_SUPER_ID
	 
	 $.getJSON('${pageContext.request.contextPath}/pages/system/aut/aut_role.jsp?action=menuData&ROLE_ID='+ROLE_ID+'&ROLE_SUPER_ID='+ROLE_SUPER_ID+'&time='+ new Date().getTime(),function(result){
	 	 _menuTree.setData(result);
	 });
}

function RoleSave()
{
	_RoleDialog.hide();
	
	//获取未完成选中的ID
    var nodes = [];
    var g=_menuTree;
    $(".l-checkbox-incomplete", g.tree).parent().parent("li").each(function ()
    {
       var treedataindex = parseInt($(this).attr("treedataindex"));
       nodes.push({ target: this, data: g._getDataNodeByTreeDataIndex(g.data, treedataindex) });
    });
    
   //获取已完成选中的ID
   var note = _menuTree.getChecked();
   var id="";
   
   var ID="";
   var TOPMENUID="";
   var TEXT="";
   if(nodes.length>0)
   {
   	  for(var i=0;i<nodes.length;i++)
   	  {
   		 ID+=nodes[i].data.id+";";
   		 TOPMENUID+=nodes[i].data.TOPMENUid+";";
   		 TEXT+=nodes[i].data.text+";";
   	  }
   }
   
   if(note.length>0)
   {
   	  for(var l=0;l<note.length;l++)
   	  {
   	  	 ID+=note[l].data.id+";";
   		 TOPMENUID+=note[l].data.TOPMENUid+";";
   		 TEXT+=note[l].data.text+";"
   	  }
   }
	
   if(ID!=""&&TOPMENUID!="")
   {
   		ID=ID.substring(0,ID.length-1);
   		TOPMENUID=TOPMENUID.substring(0,TOPMENUID.length-1);
   		TEXT=TEXT.substring(0,TEXT.length-1);
   		
	    $("#ProcessMETHOD").attr("value","Save");
		$("#ProcessBO").attr("value","com.fstar.aut.TempMenu");
		$("#ID").attr("value",ID);
		$("#TEXT").attr("value",TEXT);
		$("#TOPMENUID").attr("value",TOPMENUID);
		var param=$("#thisForm").serialize();
		
		$.ajax({
			type:'POST',
			url:'<%=contextPath%>/cm',
			dataType:'json',
			data:param,
			success:function(json)
		    {	
				ButtonFunction();
		    }
		});
	}	
}

function Cancle()
{
	_menuTree.selectNode("");
}

var _ButtonDialog;
function ButtonFunction()
{
	_ButtonDialog=$.ligerDialog.open({ 
   		width: 1000,
        height: 300, 
        top: 100,
        isResize: true,
        title:"<fmt:message key='role'/>",
   		target: $("#buttonFunction"),
   		buttons:[
   			{text:"<fmt:message key='save' />",onclick:ButtonSave}
   		]
   	});
   	
   	_initOper=PageLoader.initOperData();
   	PageLoader.initTempData();
}  

function ButtonSave()
{
	var data=_initButton.getData();
	
	var Ope_Data = "";
	
	if(data.length>0)
	{
		for(var i=0;i<data.length;i++)
		{
			var isParent =_initButton.getParent(_initButton.getRow(i));
			
			if(isParent==null)
			{
				Ope_Data+=JSON.stringify(_initButton.getRow(i))+";";
			}
		}
		
		Ope_Data=Ope_Data.substring(0,Ope_Data.length-1);
	} 
	
	var row=manager.getSelectedRow();
	var role_id=row.ROLE_ID;
	$("#Data").attr("value",Ope_Data); 
	$("#ID").attr("value",""); 
	$("#TOPMENUID").attr("value",""); 
	$("#TEXT").attr("value","");
	$("#ROLE_ID").attr("value",role_id); 
	$("#ProcessMETHOD").attr("value","SaveAuthority"); 
	$("#ProcessBO").attr("value","com.fstar.aut.Role"); 
	var param=$("#thisForm").serialize();
	
	$.ajax({
		type:'POST',
		url:'<%=contextPath%>/cm',
		dataType:'json',
		data:param,
		success:function(json)
	    {	
			_ButtonDialog.hide();
	    }
	});
}
</script>
</head>
<body>
  <form name="thisForm" id="thisForm">
  	  <input type="hidden" name="Data" id="Data"/>
  	  <input type="hidden" name="ID" id="ID"/>
  	  <input type="hidden" name="TEXT" id="TEXT"/>
  	  <input type="hidden" name="ROLE_ID" id="ROLE_ID"/>
  	  <input type="hidden" name="TOPMENUID" id="TOPMENUID"/>
      <input type="hidden" name="ProcessMETHOD" id="ProcessMETHOD"/>
	  <input type="hidden" name="ProcessBO" id="ProcessBO"/>
  </form>
   <div id="toptoolbar"></div> 
   <div id="maingrid"></div>
   
   <div id="navtab" style="display: none">
   	  <div title="<fmt:message key='menu' />权限">
   	  	 <ul id="menuTree">
   	  </div>
   	  <div title="按钮权限">
   	     <div id="buttonFunction"></div>
   	  </div>
   </div>

</body>
</html>