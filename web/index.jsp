<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/pages/common/header.jsp" %>
<%@ taglib prefix="shiro" uri="/WEB-INF/shiro.tld" %>  

<title><fmt:message key="sys_title"/></title>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/auth-index.css" />

<script type="text/javascript">

var tab = null;
var accordion = null;  
//tabid计数器，保证tabid不会重复
var tabidcounter = 0;
$(function() {

	// 布局
	$("#main-content").ligerLayout({
		leftWidth : 200,
		height : '100%',
		heightDiff : -20,
		space : 4,
		onHeightChanged : layoutHeiheightChangeEvent
	});

	var height = $(".l-layout-center").height();
	// Tab
	$("#framecenter").ligerTab({
		height : height
	});
	// 面板
	$("#accordion").ligerAccordion({
		height : height - 24,
		speed : null
	});

	$(".l-link").hover(function() {
		$(this).addClass("l-link-over");
	}, function() {
		$(this).removeClass("l-link-over");
	});

	tab = $("#framecenter").ligerGetTabManager();
	accordion = $("#accordion").ligerGetAccordionManager();
	//加载导航菜单
	loadLeftMenu();
	
	window['f_addTab'] = addTabEvent;

});

function loadLeftMenu() {
	 var $leftmenu = $("#leftmenu");
	 //加载栏目
     $.getJSON('${pageContext.request.contextPath}/cm?ProcessBO=com.fstar.sys.Authority&ProcessMETHOD=findTopMenuByUser&time=' + new Date().getTime(), function (menus)
     {
         $(menus.data).each(function (i, menu)
         {
        	 $leftmenu.append('<div id="main_'+menu.ID+'" title="' + menu.NAME + '" class="l-scroll"><ul id="sub_'+menu.ID+'"></ul></div>');
        	 //加载栏目菜单
        	 $.getJSON('${pageContext.request.contextPath}/cm?ProcessBO=com.fstar.sys.Authority&ProcessMETHOD=findAllSubMenuByParent&TopMenuID=' + menu.ID + '&time=' + new Date().getTime(), function(submenu) {
             	var tree = $("#sub_"+menu.ID).ligerTree({
             		data:submenu.data,
             		isExpand:false,              		
             		checkbox:false,
             		needCancel:false,
             		textFieldName:'TEXT',
             		idFieldName:'ID'
             	});
             	$("#sub_"+menu.ID).css("margin-top","3px").css("width","200px");
             	
             	tree.bind("select", function(node) {
             		var url = node.data.URL;
             		var text = node.data.TEXT;
             		var tabid = $(node.target).attr("tabid");
             		if (!url) {
             			return;
             		}
             		if (node.data.TYPE == "2") {
             			return;
             		}
                if (!tabid) {
                    tabidcounter++;
                    tabid = "tabid" + tabidcounter;
                    $(node.target).attr("tabid", tabid);
                }
                addTabEvent(tabid, text, url);
             	});
             });//加载栏目菜单end
         });
          
         //Accordion
         accordion = $leftmenu.ligerAccordion({ height: $(".l-layout-center").height() - 24, speed: null });
         $("#pageloading").hide();
     });//加载栏目end 
}

function layoutHeiheightChangeEvent(options) {
	if (tab)
		tab.addHeight(options.diff);
	if (accordion && options.middleHeight - 24 > 0)
		accordion.setHeight(options.middleHeight - 24);
}

function addTabEvent(tabid, text, url) {
	tab.addTabItem({
		tabid : tabid,
		text : text,
		url : url
	});
	tab.reload(tabid);
}

Koala.changepassword = function ()
{
    $(document).bind('keydown.changepassword', function (e)
    {
        if (e.keyCode == 13)
        {
            doChangePassword();
        }
    });

    var changepasswordPanel = null;
    if (!window.changePasswordWin)
    {
    	changepasswordPanel = $("#changepasswordPanel");

        window.changePasswordWin = $.ligerDialog.open({
            width: 400,
            height: 190, 
            top: 200,
            isResize: true,
            title: '用户修改密码',
            target: changepasswordPanel,
            buttons: [
            { text: '<fmt:message key="ok"/>', onclick: function ()
            {
                doChangePassword();
            }
            },
            { text: '<fmt:message key="cancel"/>', onclick: function ()
            {
                window.changePasswordWin.hide();
                $(document).unbind('keydown.changepassword');
            }
            }
            ]
        });
    }
    else
    {
        window.changePasswordWin.show();
    }

    function doChangePassword()
    {
        var OldPassword = $("#oldPassword").val();
        var newPassword = $("#newPassword").val();
        var confirmPassword = $("#confirmPassword").val();
        
        if (confirmPassword != newPassword){
        	$.ligerDialog.error("两次密码输入不一致");
        	return;
        }        
        
        var data = "oldPassword=" + OldPassword + "&newPassword=" + newPassword;
        //验证
        var form = document.forms[0];
        if(!Validator.Validate(form,3))return;
        $.ajax({
        	method:"post",
        	url:"cm?ProcessMETHOD=changeUserPassword&ProcessBO=com.fstar.sys.Authority",
        	data:data,
        	success:function(result) {
        		if (result.ERR == null){
        			$.ligerDialog.alert("密码修改成功");
        			changePasswordWin.hidden();
               	}else{
               		$.ligerDialog.error(result.ERR);
               	}
        	}
        });
    }

};

</script>

<style type="text/css">
.l-tree{
width: 400px;
}
</style> 

</head>
<body style="padding:0px;background:#EAEEF5;">  
<div id="pageloading"></div>  
<div id="topmenu" class="l-topmenu">
        <div class="l-topmenu-logo"><fmt:message key="sys_title"/></div>
        <shiro:user>
	        <div class="l-topmenu-welcome"> 
	            <span class="l-topmenu-username">[<fmt:message key="welcome"/>, <shiro:principal />]</span>  &nbsp; 
	            [<a href="javascript:Koala.changepassword()"><fmt:message key="update"/><fmt:message key="password"/></a>] &nbsp; 
	            [<a href="${pageContext.request.contextPath}/logout"><fmt:message key="logout"/></a>]
	        </div>
        </shiro:user>
  </div>
  
  <div id="main-content" style="width:99.2%; margin:0 auto; margin-top:4px; "> 
        <div position="left"  title='<fmt:message key="system"/><fmt:message key="menu"/>' id="leftmenu">
        </div>
        <div position="center" id="framecenter"> 
            <div tabid="home" title='<fmt:message key="home"/>' style="height:300px" >
                <iframe frameborder="0" name="home" id="home" src="${pageContext.request.contextPath}/pages/common/welcome.jsp"></iframe>
            </div> 
        </div> 
        
    </div>
    <div  style="height:20px; line-height:20px; text-align:center;">
            Copyright © 2000-2015 SkySoft
    </div>
    <div style="display:none"></div>
    <form id="changepasswordPanel" style="display:none;">
		<table cellpadding="0" cellspacing="0" class="form2column" >
			<tr>
				<td class="label">旧密码:</td>
				<td class="content">
					<input name="oldPassword" type="password" id="oldPassword" class="input-common" dataType="Require" />
				</td>
			</tr>
			<tr>
				<td class="label">新密码:</td>
				<td class="content">
					<input name="newPassword" type="password" id="newPassword" class="input-common" dataType="Require" maxLength="16" />
				</td>
			</tr>
			<tr>
				<td class="label">确认密码:</td>
				<td class="content">
					<input name="confirmPassword" type="password" id="confirmPassword" class="input-common" dataType="Require" maxLength="16" />
				</td>
			</tr>
		</table>
	</form>
</body>

</html>