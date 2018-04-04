<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld" %>
<%@ taglib prefix="c" uri="/WEB-INF/c.tld" %>
<%
 String contextPath = request.getContextPath();
%>

<META HTTP-EQUIV="Pragma" CONTENT="no-cache"> 
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache"> 
<META HTTP-EQUIV="Expires" CONTENT="0"> 

<!-- base jquery -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/layer.js"></script>
<!-- ligerUI -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ligerUI/js/ligerui.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ligerUI/js/plugins/ligerTree.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/json2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/LigerUIRepair.js"></script>
<%-- <link href="${pageContext.request.contextPath}/js/ligerUI/skins/koala/css/style-all.css" rel="stylesheet" type="text/css" />  --%> 
<link href="${pageContext.request.contextPath}/js/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" /> 
<link href="${pageContext.request.contextPath}/js/ligerUI/skins/ligerui-icons.css" rel="stylesheet" type="text/css" />
<!-- common js --> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/koala/koala-ui.plugin.js"></script>
<script src="${pageContext.request.contextPath}/js/koala/Koala.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/koala/Validate.js" type="text/javascript"></script>
<!-- common css -->
<link href="${pageContext.request.contextPath}/css/koala-common.css" rel="stylesheet" type="text/css" /> 
<!-- auth -->
<script src="${pageContext.request.contextPath}/js/common/common.js" type="text/javascript"></script>
<link href="${pageContext.request.contextPath}/css/auth-common.css" rel="stylesheet" type="text/css" /> 

<script type="text/javascript">
//设置 process全局相关参数
var processHelper = "ProcessHelper";
var processMethod = "ProcessMethod";
var processBO = "ProcessBO";
var processMETHOD = "ProcessMETHOD";
//设置 完整上下文
var rootPath = "${pageContext.request.contextPath}";
var groupicon = rootPath+"/js/ligerUI/skins/icons/communication.gif";
//设置 Query和Common控制器
var queryController = rootPath+"/qs?_index="+new Date().getTime();
var commonController = rootPath+"/cm?_index="+new Date().getTime();
/**
* HOParam
 * 为了降低耦合 拼接HelperProcess 
 * return json
 */
$.extend({
	HOParam:function(helper,method,array){
		if("undefined" == typeof array){
			return [{name:processHelper,value:helper},{name:processMethod,value:method}];
		}else{
			return $.merge([{name:processHelper,value:helper},{name:processMethod,value:method}],array);
		}
	}
});
/**
 * BOParam
 * 为了降低耦合 拼接BoProcess 
 * return json
 */
$.extend({
	BOParam:function(helper,method,array){
		if("undefined" == typeof array){
			return [{name:processBO,value:helper},{name:processMETHOD,value:method}];
		}else{
			return $.merge([{name:processBO,value:helper},{name:processMETHOD,value:method}],array);
		}
	}
});
/**
 * formatData
 * 格式化数据 
 * 根据数据末尾regular 将key结尾处加regular符号，若不传regular则默认加“_”
 */
$.extend({
	formatData:function(obj,regular){
		var rtn = [];
		$.each(obj,function(n,value) {
			var tmp = {};
			$.each(value,function(n2,value2){
				if("undefined" == typeof regular){
					tmp[n2+"_"] = value2;
				}else{
					tmp[n2+regular] = value2;
				}
			});
			rtn.push(tmp);
		});
		return rtn;
	}
});

/**
 * formatSerializeArray
 * 将form表单序列号的json对象（【{name：A,value:B}…】）转化为ligerForm可setData的json对象（{A：B,…}）
 */
$.extend({
	formatSerializeArray:function(obj){
		var rtn = {};
		$.each(obj,function(n,value) {
			rtn[value.name] = value.value;
		});
		return rtn;
	}
});
$(function(){
   	$("*[ligertipid]").live('click',function(){
   		$(this).ligerHideTip();
   	});
   	$(".l-dialog-tc .l-dialog-close").live('click',function(){
      $("*[ligertipid]").each(function(){
  	  	  $(this).ligerHideTip();
  	  });
    });
    $.ligerDefaults.DialogString = {
        title: '<fmt:message key="prompt"/>',                     //提示文本标题
        ok: '<fmt:message key="ok"/>',
        yes: '<fmt:message key="yes"/>',
        no: '<fmt:message key="no"/>',
        cancel: '<fmt:message key="cancel"/>',
        waittingMessage: '<fmt:message key="waitting"/>'
    };
});
</script>
