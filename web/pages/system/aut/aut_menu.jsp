<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/pages/common/header.jsp" %>
<%@ taglib prefix="shiro" uri="/WEB-INF/shiro.tld" %>  

<script type="text/javascript">  
	 var manager;
	 var ISVALID_Data=[{ISVALID:'Y',text:'<fmt:message key="yes"/>'},{ISVALID:'N',text:'<fmt:message key="no"/>'}];
	 
	 $(function (){
	 	PageLoader.initGridPanel();
	 });
	  
	  
	  PageLoader = {
		 initSearchPanel:function(){},
		 initGridPanel:function()
		 {
		      manager=$("#maingrid").ligerGrid({
		 	  columns:[
		 	    {display:"ID",name:'ID',width: 280,align:'left'},
		 	    {display:"<fmt:message key='zh_ch_name'/>",name:'NAME',editor: { type: 'text' },width: 200,align:'left'},
		 	    {display:"<fmt:message key='en_us_name'/>",name:'ENGNAME',editor: { type: 'text' },width: 200,align:'left'},
		 	    {display:"<fmt:message key='zh_tw_name'/>",name:'TWNAME',editor: { type: 'text' },width: 200,align:'left'},
		 	    {display:'URL',name:'URL',editor: { type: 'text' },width: 300,align:'left'},
		 	    {display:"<fmt:message key='visible'/>",name:'ISVALID',editor: { type: 'text' },width: 100,align:'center',
		 	      editor:{type: 'select',data:ISVALID_Data,valueColumnName: 'ISVALID'},
		 	      render:function(rowdata)
		 	      {
		 	         if(rowdata.ISVALID=='Y')
		 	         {
		 	         	return "<fmt:message key='yes'/>";
		 	         }
		 	         else if(rowdata.ISVALID=='N')
		 	         {
		 	         	return "<fmt:message key='no'/>";
		 	         }
		 	         else
		 	         {
		 	         	return "<fmt:message key='yes'/>";
		 	         }
		 	      }
		 	    },
		 	    {display:"<fmt:message key='operating'/>",width: 150,
		 	    	render:function(rowdata,rowindex)
		 	    	{
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
	 	      tree: { columnName: 'NAME' }
		 	}); 
		 	
		 	manager.toggleCol("ID",false);//隐藏id列
		 	get_MENU_Data(); 
		 	init_toolbar();
		 }
	 }
	  
	//获取菜单数据
	function get_MENU_Data()
	{
		$.getJSON('${pageContext.request.contextPath}/cm?ProcessBO=com.fstar.sys.Authority&ProcessMETHOD=findAllSubMenuByParent&TopMenuID='+''+'&time=' + new Date().getTime(), function(result) {
		  
		  var jsonObj = {};   
		     
		  jsonObj.Rows = result.data;
		  
		  manager.set({data:jsonObj});
		  
		});
	}
	
	//初始化工具栏
	function init_toolbar()
	{
		
		 $("#toptoolbar").ligerToolBar({ items: [
                {text: '<fmt:message key="add"/>', click: Add,icon:'add'},
                { line:true },
                { text: '<fmt:message key="remove"/>',click:Remove,icon: 'delete' },
	            { line: true },
	            { text: '<fmt:message key="edit"/>',click:Edit,icon: 'modify' },
	            { line: true },
                { text: '<fmt:message key="save"/>', click: Save ,icon:'save'},
            ]
         });
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
	
	//保存
	function Save()
	{
	 	var data = manager.getData();
		var MenuData="";
		
		if(parseInt(data.length)>0)
		{
			for(var i=0;i<parseInt(data.length);i++)
			{
				var isParent = manager.getParent(manager.getRow(i));
				
				if(isParent==null)
				{
					MenuData+=JSON.stringify(manager.getRow(i))+";";
				}
			}
			MenuData=MenuData.substring(0,MenuData.length-1);
		}
		
		//MenuData=JSON.stringify(manager.getRow(0));
	 	$("#MenuData").attr("value",MenuData);
	 	
	    var param=$("#thisForm").serialize();
	    
	 	$.ajax({
			type:'POST',
			url:'<%=contextPath%>/cm',
			dataType:'json',
			data:param,
			success:function(json)
		    {
		     	 message(json);
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
	
	function message(json)
	{
		get_MENU_Data();//重新获取新数据
		
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

<body style="padding:4px">
    <form name="thisForm" id="thisForm">
      <input type="hidden" name="MenuData" id="MenuData"/>
      <input type="hidden" name="ProcessMETHOD" id="ProcessMETHOD"  value="save"/>
	  <input type="hidden" name="ProcessBO" id="ProcessBO"  value="com.fstar.sys.Authority"/>
    </form>
    <div id="toptoolbar"></div> 
	<div id="maingrid"></div>
</body>
</html>