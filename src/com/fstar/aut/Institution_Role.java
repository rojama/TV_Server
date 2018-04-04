package com.fstar.aut;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 机构角色
 * @author HuangKai
 *
 */
public class Institution_Role {
	
	private static Logger log = LoggerFactory.getLogger(Institution.class);
	
	private Map<String, Object> message=new HashMap<String, Object>();
	
	private String g_str_INS_ID;	//机构编号
	
	private String g_str_ROLE_ID;	//角色编号
	
	private String g_str_SYSTEM_DATETIME;//创建日期时间

	public String getINS_ID() {
		return g_str_INS_ID;
	}

	public void setINS_ID(String g_str_INS_ID) {
		this.g_str_INS_ID = g_str_INS_ID;
	}

	public String getROLE_ID() {
		return g_str_ROLE_ID;
	}

	public void setROLE_ID(String g_str_ROLE_ID) {
		this.g_str_ROLE_ID = g_str_ROLE_ID;
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
	
	/**
	 * 新增
	 * @param map
	 * @return
	 */
	public Map<String, Object> save(Map<String, Object> map)
	{
		return null;
	}
	
}
