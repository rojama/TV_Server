package com.fstar.aut;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fstar.sys.DB;

/**
 * 存放零时菜单
 * @author HuangKai
 *
 */
public class TempMenu {
	
	private static Logger log = LoggerFactory.getLogger(TempMenu.class);
	
	private String g_str_ID;
	
	private String g_str_TOPMENUID;
	
	private int g_i_SORT_ORDER;

	public String getG_str_ID() {
		return g_str_ID;
	}

	public void setG_str_ID(String g_str_ID) {
		this.g_str_ID = g_str_ID;
	}

	public String getG_str_TOPMENUID() {
		return g_str_TOPMENUID;
	}

	public void setG_str_TOPMENUID(String g_str_TOPMENUID) {
		this.g_str_TOPMENUID = g_str_TOPMENUID;
	}

	public int getG_i_SORT_ORDER() {
		return g_i_SORT_ORDER;
	}

	public void setG_i_SORT_ORDER(int g_i_SORT_ORDER) {
		this.g_i_SORT_ORDER = g_i_SORT_ORDER;
	}
	
	public String trimNull(Object str_valueObject) {
		String str_value = (String) str_valueObject;
		if (str_value == null) {
			return "";
		} else {
			return str_value.trim();
		}
	}
	
	public Map<String, Object> Save(Map<String, Object> map) throws Exception
	{
		
		String str_ID = trimNull(map.get("ID"));
		String str_TOPMENUID = trimNull(map.get("TOPMENUID"));
		String str_TEXT = trimNull(map.get("TEXT"));
		
		
		if (!"".equals(str_ID) && !"".equals(str_TOPMENUID))
		{
			String[] arrayID=str_ID.split(";");
			String[] arrayTOPMENUID=str_TOPMENUID.split(";");
			String[] arrayTEXT=str_TEXT.split(";");
			
			DB.update("delete from AUT_TEMPMENU");
			
			for (int i = 0; i < arrayID.length; i++)
			{
				String strSql="insert into AUT_TEMPMENU (ID,TOPMENUID,TEXT) values('"+arrayID[i]+"','"+arrayTOPMENUID[i]+"','"+arrayTEXT[i]+"') ";
				DB.update(strSql);
			}
		}
		
		return null;
	}
	
}
