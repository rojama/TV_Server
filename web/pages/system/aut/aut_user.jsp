<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.fstar.sys.JsonUtil"%>
<%@page import="com.fstar.help.RoleHelper"%>
<%@page import="com.fstar.help.InstitutionHelper"%>
<%@page import="org.apache.shiro.session.Session"%>
<%@page import="org.apache.shiro.SecurityUtils"%>
<%@page import="org.apache.shiro.subject.Subject"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%
	String action = (String)request.getParameter("action");
	String locale=(String)session.getAttribute("locale");
	
	InstitutionHelper obj_InstitutionHelper = new InstitutionHelper();
	RoleHelper obj_role = new RoleHelper();
	if("getInsData".equals(action))//获取机构树
	{
		String USER_ID=(String)request.getParameter("USER_ID");
		List<Map<String, Object>> list=obj_InstitutionHelper.innerFindInstitutionUser(locale, USER_ID, "");
		String dataAsJson=JsonUtil.list2json(list);
		
		dataAsJson=dataAsJson.replace("INS_ID", "id");
		dataAsJson=dataAsJson.replace("NAME", "text");
		dataAsJson=dataAsJson.replace("null", "false");
		dataAsJson=dataAsJson.replace("ISCHECKED", "ischecked");
		
		PrintWriter pw = response.getWriter();
		pw.print(dataAsJson);
		pw.flush();
		pw.close(); 
	}
	else if("getRoleData".equals(action))//获取角色树
	{
		String USER_ID=(String)request.getParameter("USER_ID"); 
		List<Map<String, Object>> list=obj_role.innerFindUserRole(locale, USER_ID, "");
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ligerUI/js/plugins/ligerGrid.js"></script>
<script type="text/javascript">  
  
  var manager;
  var _ins;
  var _insDiv;
  var _role;
  var _roleDiv;
  var _dialog;
  var _edit;
  var start_Data=[{IS_START:'Y',text:'<fmt:message key="yes"/>'},{IS_START:'N',text:'<fmt:message key="no"/>'}];
  
   $(function(){
   		PageLoader.initTopToolBar();//初始化Tool控件
   		manager=PageLoader.initGridPanel();//初始化Grid控件
   		PageLoader.initGridData();//初始化Grid数据
   		_ins=PageLoader.initInsTree();//初始化机构用户
   		_role=PageLoader.initRoleTree();//初始化机构角
   });
   
   PageLoader = {
   		initTopToolBar:function()//初始化Tool控件
		{
	          $("#toptoolbar").ligerToolBar({ items: [
	               { text: '<fmt:message key="add"/>',click:Add, icon:'add'},
	               { line:true },
	               { text: '<fmt:message key="remove"/>',click:Remove,icon: 'delete' },
	               { line: true },
	        	   { text: '<fmt:message key="edit"/>',click:Edit,icon:'modify'},
	        	   { line: true },
	        	   { text: '<fmt:message key="save"/>',click:Save,icon:'save'},
	        	   { line: true },
	        	   { text: '<fmt:message key="resetpsd"/>',click:Resetpsd,icon:'settings'},
	        	   { line: true },
                 	{ text: '<fmt:message key="institution"/>',click:Institution,icon:'memeber'},
	        	   { line: true },
                   { text: '<fmt:message key="role"/>',click:Role,icon:'role'}
	            ]
	          });
		},
		initGridPanel:function()//初始化表格
		{
			  var g=$("#maingrid").ligerGrid({
			  		columns:[
			  			{display:'<fmt:message key="user"/>ID',name:'USER_ID',width:'150',align:'left',editor: { type: 'text' }},
			  			{display:'<fmt:message key="zh_ch_name"/>',name:'USER_NAME',width:'150',align:'left',editor: { type: 'text' }},
			  			{display:'<fmt:message key="en_us_name"/>',name:'USER_ENGNAME',width:'150',align:'left',editor: { type: 'text' }},
			  			{display:'<fmt:message key="zh_tw_name"/>',name:'USER_TWNAME',width:'150',align:'left',editor: { type: 'text' }},
			  			{display:'<fmt:message key="start"/>',name:'IS_START',width:'100',align:'center',
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
			  
			  return g;
		},
		initGridData:function()//初始化用户数据
		{
		 	$.getJSON('${pageContext.request.contextPath}/cm?ProcessBO=com.fstar.aut.User&ProcessMETHOD=findAll&time=' + new Date().getTime(), function(result) {
		  
				  var jsonObj = {};   
				     
				  jsonObj.Rows = result.data;
				  
				  manager.set({data:jsonObj});
			});
		 },
		 initInsTree:function()//初始化机构用户树
		 {
		 	var g=$("#InsTree").ligerTree({
		 		 nodeDraggable: true,
		 		 autoCheckboxEven:false
		 	});
		 	return g;
		 },
		 initRoleTree:function()//初始化用户角色树
		 {
		 	var g=$("#roleTree").ligerTree({
		 		 nodeDraggable: true,
		 		 autoCheckboxEven:false
		 	});
		 	return g;
		 }
   }

   //新增
   function Add()
   {
   		manager.addEditRow();
   }
   
   //编辑
   function Edit()
   {	
   		var row=manager.getSelectedRow();
   		if(checkSelectRow(row))
   		{
	   		_edit=$.ligerDialog.open({ 
		    		width: 350,
		            height: 300, 
		            top: 100,
		            isResize: true,
		            title:"<fmt:message key='edit'/>",
		    		target: $("#editForm"),
		    		buttons:[
		    			{text:"<fmt:message key='save' />",onclick:saveEdit}
		    		]
		    });
		    
		    $("#USID").attr("value",row.USER_ID);
		    $("#USER_NAME").attr("value",row.USER_NAME);
		    $("#USER_ENGNAME").attr("value",row.USER_ENGNAME);
		    $("#USER_TWNAME").attr("value",row.USER_TWNAME);
		    $("#IS_START").find("option[value='"+row.IS_START+"']").attr("selected","selected");	
   		}
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

   
   //保存编辑的数据
   function saveEdit()
   {
   	  var row=manager.getSelectedRow();
   	  manager.updateRow(row,
   	    {USER_NAME:$("#USER_NAME").val(),
   	     USER_ENGNAME:$("#USER_ENGNAME").val(),
   	     USER_TWNAME:$("#USER_TWNAME").val(),
   	     IS_START:$("#IS_START").val()
   	  })
   	  
   	  _edit.hide();
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
   
   //重置密码
   function Resetpsd()
   {
   	  var row=manager.getSelectedRow();
  		if(checkSelectRow(row))
  		{
	   		_edit=$.ligerDialog.open({ 
		    		width: 350,
		            height: 300, 
		            top: 100,
		            isResize: true,
		            title:"<fmt:message key='reset'/><fmt:message key='password'/>",
		    		target: $("#passwordForm"),
		    		buttons:[
		    			{text:"<fmt:message key='save' />",onclick:savePsd}
		    		]
		    });
	    
		    $("#USERID").attr("value",row.USER_ID);
		    $("#PASSWORD").attr("value","");
		    $("#REPASSWORD").attr("value","");
  		}
   }
   //保存密码
   function savePsd()
   {
   		var param=$("#passwordForm").serialize();
		ajax(param);
		_edit.hide();
   }
    
    //保存用户数据
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
   
   //用户机构树弹窗 
   function Institution()
   {
    	var row=manager.getSelectedRow();
    	if(checkSelectRow(row))
    	{
   	    	getTreeData("getInsData",row);
    		_insDiv=$.ligerDialog.open({ 
	    		width: 500,
	            height: 300, 
	            top: 100,
	            isResize: true,
	            title:"<fmt:message key='institution'/>",
	    		target: $("#InsTree"),
	    		buttons:[
	    			{text:"<fmt:message key='save' />",onclick:saveUserIns},
	    			{text:"<fmt:message key='reset' />",onclick:resetTree}
	    		]
    		});
    	}
    }
    
    //用户角色树弹窗
    function Role()
    {
    	
    	var row=manager.getSelectedRow();
    	if(checkSelectRow(row))
    	{	
    		getTreeData("getRoleData",row)
	    	_roleDiv=$.ligerDialog.open({ 
	    		width: 500,
	            height: 300, 
	            top: 100,
	            isResize: true,
	            title:"<fmt:message key='role'/>",
	    		target: $("#roleTree"),
	    		buttons:[
	    			{text:"<fmt:message key='save' />",onclick:saveUserRole},
	    			{text:"<fmt:message key='reset' />",onclick:resetTree}
	    		]
	    	});
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
	
	//将用户已经存在的用户机构和用户角色打勾
	function getTreeData(action,row)
	{
		var id=row.USER_ID
	
		$.getJSON('${pageContext.request.contextPath}/pages/system/aut/aut_user.jsp?action='+action+'&USER_ID='+id+'&time='+ new Date().getTime(),function(result){
				
				if(action=="getInsData")
				{	
					 _ins.setData(result);
				}
				else if(action=="getRoleData")
				{
					 _role.setData(result);
				}
		});
	}
   
   //保存用户角色
   function saveUserRole()
   {
   		var id=getSelectID(_role);
   		var row=manager.getSelectedRow();
   		
   		$("#ProcessMETHOD").attr("value","SaveRole");
   		$("#RoleData").attr("value",id);
   		$("#USER_ID").attr("value",row.USER_ID);
   		
   		var param=$("#thisForm").serialize();
   		ajax(param);
   		_roleDiv.hide();
   }
   
   //保存机构用户
   function saveUserIns()
   {
   		var id=getSelectID(_ins);
   		var row=manager.getSelectedRow();
   		
   		$("#ProcessMETHOD").attr("value","SaveIns");
   		$("#InsData").attr("value",id);
   		$("#USER_ID").attr("value",row.USER_ID);
   		
   		var param=$("#thisForm").serialize();
   		ajax(param);
   		_insDiv.hide();
   }
   
   //重置选中的节点
   function resetTree()
   {
   		_ins.selectNode("");
   		_role.selectNode("");
   }
   
   //获取已选中的节点
   function getSelectID(ids)
   {
   		
   		//获取未完成选中的ID
         var nodes = [];
         var g=ids;
         $(".l-checkbox-incomplete", g.tree).parent().parent("li").each(function ()
         {
            var treedataindex = parseInt($(this).attr("treedataindex"));
            nodes.push({ target: this, data: g._getDataNodeByTreeDataIndex(g.data, treedataindex) });
         });
         
   		var note=ids.getChecked();
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
			for(var i=0;i<note.length;i++)
			{
				id +=note[i].data.id + ";";
			}
			
			id=id.substring(0,id.length-1);
		}
		
		return id
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
   <div id="toptoolbar"></div> 
   <div id="maingrid"></div>
   
   <div id="divIns" style="display: none;">
   		<ul id="InsTree">
   </div>
   
   <div id="divRole" style="display: none;">
   		<ul id="roleTree">
   </div>
   
   <form id="thisForm">
     <input type="hidden" name="AddData" id="AddData"/>
     <input type="hidden" name="EditData" id="EditData"/>
     <input type="hidden" name="DelData" id="DelData"/>
     <input type="hidden" name="InsData" id="InsData"/>
     <input type="hidden" name="RoleData" id="RoleData"/>
     <input type="hidden" name="USER_ID" id="USER_ID"/>
     <input type="hidden" name="ProcessMETHOD" id="ProcessMETHOD"  value="Save"/>
	 <input type="hidden" name="ProcessBO" id="ProcessBO"  value="com.fstar.aut.User"/>
   </form>
   <form id="editForm" style="display: none">
   	 <table border="0" cellpadding="0" cellspacing="0" class="form2column">
   	   <tr>
   	     <td class="label"><fmt:message key='user' />ID</td>
   	     <td><input type="text" name="USID" id="USID" class="input-common" disabled/></td>
   	   </tr>
   	   <tr>
	       <td class="label"><fmt:message key='zh_ch_name' /></td>
	       <td><input type="text" name="USER_NAME" id="USER_NAME" class="input-common"/></td>
	    </tr>
	    <tr>
	       <td class="label"><fmt:message key='en_us_name' /></td>
	       <td><input type="text" name="USER_ENGNAME" id="USER_ENGNAME" class="input-common"/></td>
	    </tr>
	    <tr>
	      <td class="label"><fmt:message key='zh_tw_name' /></td>
	      <td><input type="text" name="USER_TWNAME" id="USER_TWNAME" class="input-common"/></td>
	    </tr>
	    <tr>
	      <td class="label"><fmt:message key='start'/></td>
	      <td>
	        <select name="IS_START" id="IS_START" class="select-common">
               <option value="Y"><fmt:message key='yes' /></option>
               <option value="N"><fmt:message key='no' /></option>
	        </select>
	      </td>
	    </tr>
   	 </table>
   </form>
   <form id="passwordForm" style="display: none">
      <input type="hidden" name="ProcessMETHOD" id="ProcessMETHOD"  value="ResetPassword"/>
	  <input type="hidden" name="ProcessBO" id="ProcessBO"  value="com.fstar.aut.User"/>
      <table border="0" cellpadding="0" cellspacing="0" class="form2column">
        <tr>
   	     <td class="label"><fmt:message key='user' />ID</td>
   	     <td><input type="text" name="USERID" id="USERID" class="input-common" readonly/></td>
   	   </tr>
   	   <tr>
   	     <td class="label"><fmt:message key='password' /></td>
   	     <td><input type="password" name="PASSWORD" id="PASSWORD" class="input-common"/></td>
   	   </tr>
   	   <tr>
   	     <td class="label"><fmt:message key='ok' /><fmt:message key='password' /></td>
   	     <td><input type="password" name="REPASSWORD" id="REPASSWORD" class="input-common"/></td>
   	   </tr>
      </table>
   </form>
</body>
</html>