package com.fstar.aut;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fstar.utility.Machine;

/**
 * 机构用户
 * @author HuangKai
 *
 */
public class Institution_User {
	
	private static Logger log = LoggerFactory.getLogger(Institution.class);
	
	private Map<String, Object> message=new HashMap<String, Object>();
	
	private String g_str_INS_ID;		//机构ID
	
	private String g_str_USER_ID;		//用户ID
	
	private String g_str_SYSTEM_DATETIME;
	
	public String getINS_ID() {
		return g_str_INS_ID;
	}

	public void setINS_ID(String g_str_INS_ID) {
		this.g_str_INS_ID = g_str_INS_ID;
	}

	public String getUSER_ID() {
		return g_str_USER_ID;
	}

	public void setUSER_ID(String g_str_USER_ID) {
		this.g_str_USER_ID = g_str_USER_ID;
	}

	public String getSYSTEM_DATETIME() {
		return g_str_SYSTEM_DATETIME;
	}
	
	public void setSYSTEM_DATETIME(String g_str_SYSTEM_DATETIME) {
		this.g_str_SYSTEM_DATETIME = g_str_SYSTEM_DATETIME;
	}
	
	
	public String trimNull(Object str_valueObject) {
		String str_value = (String) str_valueObject;
		if (str_value == null) {
			return "";
		} else {
			return str_value.trim();
		}
	}
}
