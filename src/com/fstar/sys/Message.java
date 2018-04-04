package com.fstar.sys;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;

public class Message {
	public static final String ERR_STATUS = "ERR";
    public static final String MSG_STATUS = "MSG";
    
    public static String getMessage(String key, String locale){
    	Locale myLocale = Locale.getDefault();//获得系统默认的国家/语言环境    	
    	if (("en-US").equalsIgnoreCase(locale)){
    		myLocale = Locale.US;
    	}else if (("zh_CN").equalsIgnoreCase(locale)){
    		myLocale = Locale.CHINA;
    	}else if (("zh_TW").equalsIgnoreCase(locale)){
    		myLocale = Locale.TAIWAN;
    	}
	    ResourceBundle bundle = ResourceBundle.getBundle("Messages",myLocale);//根据指定的国家/语言环境加载对应的资源文件	
	    return bundle.getString(key);//获得本地化字符串
    }
    
    public static String getMessage(String key,Map map){
    	return getMessage(key, getLocale(map));
    }
    
    public static String getLocale(Map map){
		try {
			return (String)((Session)map.get("SESSION")).getAttribute("locale");
		} catch (Exception e) {
			return "";
		}
    }
}
