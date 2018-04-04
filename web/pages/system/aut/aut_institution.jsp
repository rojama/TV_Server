<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.fstar.sys.JsonUtil"%>
<%@page import="com.fstar.help.InstitutionHelper"%>
<%@page import="com.fstar.help.RoleHelper"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>

<%
	String action =request.getParameter("action");
	String locale=(String)session.getAttribute("locale");
	RoleHelper obj_Role = new RoleHelper();
	InstitutionHelper obj_Ins = new InstitutionHelper();
	
	if("getRoleData".equals(action))
	{
		String INS_ID =(String)request.getParameter("INS_ID");
		String INS_SUPER_ID =(String)request.getParameter("INS_SUPER_ID");
		
		List<Map<String,Object>> list=obj_Role.innerFindInsRole(locale,INS_ID,INS_SUPER_ID);
		
		String dataAsJson=JsonUtil.list2json(list);
		dataAsJson=dataAsJson.replace("ROLE_ID", "id");
		dataAsJson=dataAsJson.replace("NAME", "text");
		dataAsJson=dataAsJson.replace("null", "false");
		dataAsJson=dataAsJson.replace("ISCHECKED", "ischecked");
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
  var _roled
  var _roleDiv
  var start_Data=[{IS_START:'Y',text:'<fmt:message key="yes"/>'},{IS_START:'N',text:'<fmt:message key="no"/>'}];
  
  $(function (){
	 	PageLoader.initTopToolBar(); //初始化工具栏
	 	manager=PageLoader.initGridPanel();//初始化表格
	 	PageLoader.initGridData();//初始化数据
	 	_roled=PageLoader.initRoleTree();//初始化角色树
	 });
 
 
 PageLoader = {
		 initTopToolBar:function()//初始化工具栏
		 {
		     $("#toptoolbar").ligerToolBar({ items: [
                 {text: '<fmt:message key="add"/>',click:Add, icon:'add'},
                 { line:true },
                 { text: '<fmt:message key="remove"/>',click:Remove,icon: 'delete' },
	             { line: true },
	             { text: '<fmt:message key="edit"/>',click:Edit,icon: 'modify' },
	             { line: true },
                 { text: '<fmt:message key="save"/>',click:Save,icon:'save'},
                 { line: true },
                 { text: '<fmt:message key="role"/>',click:Role,icon:'role'}
              ]
            });
		 },
		 initGridPanel:function()//初始化表格
		 {
		 	var g=$("#maingrid").ligerGrid({
		 	    columns:[
		 	       {display:'INS_ID',name:'INS_ID',align:'left'},
		 	       {display:'<fmt:message key="institution"/><fmt:message key="code"/>',name:'INS_CODE',width: 150,align:'left',editor: { type: 'text' }},
		 	       {display:'<fmt:message key="zh_ch_name"/>',name:'INS_NAME',width: 200,align:'left',editor: { type: 'text' }},
		 	       {display:'<fmt:message key="en_us_name"/>',name:'INS_ENGNAME',width: 200,align:'left',editor: { type: 'text' }},
		 	       {display:'<fmt:message key="zh_tw_name"/>',name:'INS_TWNAME',width: 200,align:'left',editor: { type: 'text' }},
		 	       {display:'INS_SUPER_ID',name:'INS_SUPER_ID',width: 200,align:'left'},
		 	       {display:'<fmt:message key="start"/>',name:'IS_START',width: 100,align:'center',
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
	 	        rowDraggable :true,
	 	        enabledEdit: true,
	 	        clickToEdit:false,
	 	        enabledSort:false,
	 	        tree: { columnName: 'INS_NAME' }
		 	});
		 	
		 	g.toggleCol("INS_ID",false);//隐藏id列
		 	g.toggleCol("INS_SUPER_ID",false);//隐藏id列
		 	
		 	return g;
		 },
		 initGridData:function(){//获取机构数据
		 	$.getJSON('${pageContext.request.contextPath}/cm?ProcessBO=com.fstar.aut.Institution&ProcessMETHOD=findAllInstitutionParent&INS_SUPER_ID='+''+'&time=' + new Date().getTime(), function(result) {
		  
				  var jsonObj = {};   
				     
				  jsonObj.Rows = result.data;
				   
				  manager.set({data:jsonObj});
			  
			});
		 }
		 ,
		 initRoleTree:function()//初始化角色树空间
		 {
		 	var g=$("#roleTree").ligerTree({
		 		 nodeDraggable: false,
		 		 autoCheckboxEven:false
		 	});
		 	return g;
		 }
    }
	
	//增加
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
	
	//保存
	function Save()
	{
		var data = manager.getData();
		var INS_Data="";
		
		if(parseInt(data.length)>0)
		{
			for(var i=0;i<parseInt(data.length);i++)
			{
				var isParent = manager.getParent(manager.getRow(i));
				
				if(isParent==null)
				{
					INS_Data+=JSON.stringify(manager.getRow(i))+";";
				}
				
			}
			
			INS_Data=INS_Data.substring(0,INS_Data.length-1);
		}
		
		$("#Data").attr("value",INS_Data);
		
		var param=$("#thisForm").serialize();
	    
	 	ajax(param);
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
    
    //检核选中行数
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
    
    //给机构增加角色，初始化角色窗口
    function Role()
    {	
    	var row=manager.getSelectedRow();
    	if(checkSelectRow(row))
    	{
    		initRoleTreeData(row);  //初始化角色树
    		
	   		_roleDiv=$.ligerDialog.open({ 
	    		width: 500,
	            height: 300, 
	            top: 100,
	            isResize: true,
	            title:"<fmt:message key='role' />",
	    		target: $("#divRole"),
	    		buttons:[
	    			{text:"<fmt:message key='save' />",onclick:roleSave},
	    			{text:"<fmt:message key='reset' />",onclick:roleReset}
	    		]
	   		});
    	}
    }
	
	//获取机构角色树的数据
	function initRoleTreeData(row)
	{
		var INS_ID=row.INS_ID;
		var INS_SUPER_ID=row.INS_SUPER_ID;
		$.getJSON('${pageContext.request.contextPath}/pages/system/aut/aut_institution.jsp?action=getRoleData&INS_ID='+INS_ID+'&INS_SUPER_ID='+INS_SUPER_ID+'&time=' + new Date().getTime(), function(result) {
			  _roled.setData(result);
		});
	}
	
	//保存机构角色
	function roleSave()
	{
		 //获取未完成选中的ID
         var nodes = [];
         var g=_roled;
         $(".l-checkbox-incomplete", _roled.tree).parent().parent("li").each(function ()
         {
            var treedataindex = parseInt($(this).attr("treedataindex"));
            nodes.push({ target: this, data: g._getDataNodeByTreeDataIndex(g.data, treedataindex) });
         });  
         
         //获取已完成选中的ID
         var note = $("#roleTree").ligerGetTreeManager().getChecked();
         var id="";
         if(nodes.length>0)
         {
         	for(var i=0;i<nodes.length;i++)
         	{
         		id+=nodes[i].data.id+";";
         	}
        }
        
        if(note.length>0)
        {
        	for(var l=0;l<note.length;l++)
        	{
        		id+=note[l].data.id+";";
        	}
        	
        	id=id.substring(0,id.length-1);
        }
        
        var rows=manager.getSelectedRow();
        $("#ProcessMETHOD").attr("value","SaveInsRole");
        $("#InsID").attr("value",rows.INS_ID);
        $("#InsRoleData").attr("value",id);
   	 	var param=$("#thisForm").serialize();
   	  	ajax(param);
        
        _roleDiv.hide();
	}

	
	//重置机构角色
	function roleReset()
	{	
		_roled.selectNode("");
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
	
</script>
</head>
<body>
  <form name="thisForm" id="thisForm">
      <input type="hidden" name="Data" id="Data"/>
      <input type="hidden" name="InsID" id="InsID"/>
      <input type="hidden" name="InsRoleData" id="InsRoleData"/>
      <input type="hidden" name="ProcessMETHOD" id="ProcessMETHOD"  value="Save"/>
	  <input type="hidden" name="ProcessBO" id="ProcessBO"  value="com.fstar.aut.Institution"/>
  </form>
   <div id="toptoolbar"></div> 
   <div id="maingrid"></div>
   
   <div id="divRole" style="display: none;">
   		<ul id="roleTree">
   </div>	
  	
</body>
</html>