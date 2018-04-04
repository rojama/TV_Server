<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/pages/common/header.jsp" %>
<%@ taglib prefix="shiro" uri="/WEB-INF/shiro.tld" %>  
<%@ page import="org.apache.shiro.SecurityUtils"
import="org.apache.shiro.session.Session"
import="org.apache.shiro.subject.Subject"%> 

<%
Subject subject = SecurityUtils.getSubject(); 
Session subject_session= subject.getSession();
String user_locale = request.getParameter("user_locale");

if (user_locale == null || user_locale.isEmpty()){
//read from cookie
	String cookie_user_locale = "";
	Cookie cookie[]=request.getCookies();
	for(int n=0;n<cookie.length;n++){
		Cookie newCookie= cookie[n];
		if(newCookie.getName().equals("topform_user_locale")){ 
			cookie_user_locale = newCookie.getValue();
			break;
		}
	}
	if (cookie_user_locale.isEmpty()){
		user_locale = request.getHeader("ACCEPT-LANGUAGE");
	}else{
		user_locale = cookie_user_locale;
	}	
}else{
//save to cookie
	Cookie savecookie=new Cookie("topform_user_locale",user_locale);
	response.addCookie(savecookie);
}
request.setAttribute("user_locale",user_locale);
subject_session.setAttribute("locale", user_locale); 
%>

<%-- Interpret user's locale choice --%> 
<fmt:setLocale value="${user_locale}" scope="session" /> 

<title><fmt:message key="sys_title"/></title>

<style type="text/css">
@charset "UTF-8";
/* CSS Document */
* .* {
	margin: 0;
	padding: 0;
}

body {
	margin: 0;
	padding: 0;
}

.head {
	height: 30px;
	width: 100%;
	background: #f2f2f2;
	padding: 0;
	margin: 0;
}

.logo {
	height: 90px;
	width: 960px;
	margin: 0 auto;
	overflow: hidden;
	clear: both;
}

.logo img {
	height: 70px;
	width: 70px;
	overflow: hidden;
	margin-top:20px;
	float: left;
}

.logo div {
	font-size: 24px;
	color: #666;
	height: 40px;
	float: left;
	line-height: 60px;
	margin: 20px 10px;
	padding: 10px;
	border-left: 1px solid #d2d2d2;
}

.login_con {
	width: 890px;
	height: 332px;
	margin: 10px auto;
	clear: both;
}

.login_con_L {
	float: left;
	width: 497px;
	height: 332px;
	overflow: hidden;
}

.login_con_R {
	float: left;
	width: 390px;
	height: 332px;
	border: 1px solid #dce7f4;
}

.login_con_R h3 {
	background: #f0f3f6;
	line-height: 36px;
	height: 36px;
	width: 376px;
	font-size: 18px;
	color: #666;
	font-weight: 100;
	padding: 0px 6px;
	border: 1px solid #fff;
	border-bottom: 1px solid #d4d4d4;
	margin-top: 0px;
}

.login_con_R table {
	margin-top: 50px;
	margin-left: 60px;
}

.login_con_R tr {
	margin-top: 20px;
	margin-left: 20px;
	height: 50px;
}

.login_con_R td {
	width: 80px;
	margin-top: 12px;
	font-size: 14px;	
}

.login_con_R td input {
	height: 30px;
	border: 1px solid #d2d2d2;
	width: 192px;
}

.login_bnt {
	width: 211px;
	height: 42px;	    
	background: url(images/background/loginbnt.gif) no-repeat;
	margin: 30px auto;
	margin-left: 90px;
	cursor: pointer;
	border-style: none;
	text-align: center;
	font-size: 20px;
	color: rgb(242, 242, 242);
}

.login_footer {
	clear: both;
	line-height: 40px;
	color: #999;
	margin: 20px auto;
	font-size: 12px;
	width: 500px;
}

.err_message {
	margin-left: 40px;
	color: rgb(242, 63, 15);
}
</style>
<script type="text/javascript">

if (self != top) top.location.href = ".";

function login(){
	$('#loginFormId').submit();
}

/**
 * 按回车键时，触发登录按钮
 */
function keyDown(e){
	//这样写是为了兼容FireFox和IE，因为IE的onkeydown在FF中不起作用
	var ev =window.event||e; 
 	//按回车键
  	if (ev.keyCode==13) {
   		ev.returnValue=false;
   		ev.cancel = true;
   		var sub =  document.getElementById("btnSubmit");
  		sub.click();
  	} 
}

</script>

<shiro:user>
	<script type="text/javascript">
	location.href = '.';
	</script>
</shiro:user>

</head>
<body>
	<div class="head"></div>
	<div class="logo">
		<img src="images/background/logo.png" />
		<div><fmt:message key="sys_title"/></div>
	</div>
	<div class="login_con">
		<div class="login_con_L">
			<img src="images/background/background.png" />
		</div>
		<div class="login_con_R">
			<FORM id="loginFormId"  method="post">
				<h3></h3>				
				<table>
					<tbody>
						<tr>
							<td><fmt:message key="username"/>：
							</td>
							<td><input type="text" name="username" id="username" autocomplete="off"/>
							</td>
						</tr>
						<tr>
							<td><fmt:message key="password"/>：
							</td>
							<td><input type="password" name="password" id="password" autocomplete="off" onpaste="return false" onkeydown="keyDown(event)"/>
							</td>
						</tr>
					</tbody>
				</table>
				<input class="login_bnt" type="button" id="btnSubmit" value="<fmt:message key="login"/>" onclick="javascript:login()"/>
				<div class="err_message">
				<%					
					Object obj = request.getAttribute(org.apache.shiro.web.filter.authc.
					FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
					String msg = "";
					if( obj != null ){
					    if( "org.apache.shiro.authc.UnknownAccountException".equals( obj ) )
					        msg = "未知帐号错误！";
					    else if("org.apache.shiro.authc.IncorrectCredentialsException".equals( obj ))
					        msg = "密码错误！";                   
					    else if( "org.apache.shiro.authc.AuthenticationException".equals( obj ))
					        msg = "认证失败！";
					}
					out.print(msg); 
				%>
				</div>
			</FORM>
		</div>
	</div>
	<div class="login_footer">
		<%-- Offer locale choice to user --%> 
		<a href="login.jsp?user_locale=en-US">English</a> - 
		<a href="login.jsp?user_locale=zh_CN">中文简体</a> - 
		<a href="login.jsp?user_locale=zh_TW">中文繁体</a> 
		&nbsp;&nbsp;&nbsp;天软电脑系统有限公司 版权信息 2015</div>
</body>
</html>