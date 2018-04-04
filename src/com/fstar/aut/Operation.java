package com.fstar.aut;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fstar.sys.DB;
import com.fstar.sys.Message;
import com.fstar.utility.Machine;
import com.ibm.json.java.JSONArray;

/**
 * 操作
 * @author HuangKai
 *
 */
public class Operation {
	
	private static Logger log = LoggerFactory.getLogger(Institution.class);
	
	private Map<String, Object> message=new HashMap<String, Object>();
	
	private String g_str_OPE_ID;
	
	private String g_str_OPE_CODE;
	
	private String g_str_OPE_NAME;
	
	private String g_str_OPE_ENGNAME;
	
	private String g_str_OPE_TWNAME;
	
	private String g_str_SYSTEM_DATETIME;

	public String getOPE_ID() {
		return g_str_OPE_ID;
	}

	public void setOPE_ID(String g_str_OPE_ID) {
		this.g_str_OPE_ID = g_str_OPE_ID;
	}
	
	public String getOPE_CODE() {
		return g_str_OPE_CODE;
	}
	
	public void setOPE_CODE(String g_str_OPE_CODE) {
		this.g_str_OPE_CODE = g_str_OPE_CODE;
	}

	public String getOPE_NAME() {
		return g_str_OPE_NAME;
	}

	public void setOPE_NAME(String g_str_OPE_NAME) {
		this.g_str_OPE_NAME = g_str_OPE_NAME;
	}

	public String getOPE_ENGNAME() {
		return g_str_OPE_ENGNAME;
	}

	public void setOPE_ENGNAME(String g_str_OPE_ENGNAME) {
		this.g_str_OPE_ENGNAME = g_str_OPE_ENGNAME;
	}

	public String getOPE_TWNAME() {
		return g_str_OPE_TWNAME;
	}

	public void setOPE_TWNAME(String g_str_OPE_TWNAME) {
		this.g_str_OPE_TWNAME = g_str_OPE_TWNAME;
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
	
	public void setProperty(Map<String, Object> map)
	{
		this.setOPE_ID(trimNull(map.get("OPE_ID")));
		this.setOPE_NAME(trimNull(map.get("OPE_NAME")));
		this.setOPE_ENGNAME(trimNull(map.get("OPE_ENGNAME")));
		this.setOPE_TWNAME(trimNull(map.get("OPE_TWNAME")));
		this.setOPE_CODE(trimNull(map.get("OPE_CODE")));
	}
	
	/**
	 * 新增/编辑/删除
	 * @param map
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> Save(Map<String, Object> map) throws Exception
	{
		
		String str_AddData = trimNull(map.get("AddData"));
		String str_EditData = trimNull(map.get("EditData"));
		String str_DelData = trimNull(map.get("DelData"));
		
		//新增
		if (!"".equals(str_AddData))
		{	
			JSONArray addArray = JSONArray.parse(str_AddData);
			
			for (int i = 0; i < addArray.size(); i++)
			{
				Map lhm_map = (Map) addArray.get(i);
				this.setProperty(lhm_map);
				String ID = UUID.randomUUID().toString();
				String SYSTEM_DATETIME = trimNull(Machine.getSystemDateTime().get("SystemDateTime"));
				
				StringBuilder strSql = new StringBuilder();
				strSql.append("INSERT INTO AUT_OPERATION ");
				strSql.append("(OPE_ID,OPE_CODE, OPE_NAME, OPE_ENGNAME, OPE_TWNAME, SYSTEM_DATETIME) ");
				strSql.append("VALUES ('"+ID+"', '"+g_str_OPE_CODE+"','"+g_str_OPE_NAME+"', ");
				strSql.append("'"+g_str_OPE_ENGNAME+"', '"+g_str_OPE_TWNAME+"', '"+SYSTEM_DATETIME+"') ");
				DB.update(strSql.toString());
			}
		}
		
		//修改
		if (!"".equals(str_EditData))
		{
			JSONArray editArray = JSONArray.parse(str_EditData);
			
			for (int l = 0; l < editArray.size(); l++)
			{
				Map lhm_map = (Map) editArray.get(l);
				this.setProperty(lhm_map);
				String SYSTEM_DATETIME = trimNull(Machine.getSystemDateTime().get("SystemDateTime"));
				
				StringBuilder strSql = new StringBuilder();
				strSql.append("UPDATE AUT_OPERATION ");
				strSql.append("SET OPE_NAME = '"+g_str_OPE_NAME+"',  ");
				strSql.append("OPE_ENGNAME = '"+g_str_OPE_ENGNAME+"', OPE_TWNAME = '"+g_str_OPE_TWNAME+"', ");
				strSql.append("SYSTEM_DATETIME='"+SYSTEM_DATETIME+"' where OPE_ID='"+g_str_OPE_ID+"' and ");
				strSql.append("OPE_CODE='"+g_str_OPE_CODE+"' ");
				
				DB.update(strSql.toString());
			}
		}
		
		if (!"".equals(str_DelData))
		{
			JSONArray delArray = JSONArray.parse(str_DelData);
			
			for (int j = 0; j < delArray.size(); j++)
			{
				Map lhm_map = (Map) delArray.get(j);
				this.setProperty(lhm_map);
				DB.update("delete from AUT_OPERATION where OPE_ID='"+g_str_OPE_ID+"' and OPE_CODE='"+g_str_OPE_CODE+"' ");
			}
		}
		
		message.put(Message.MSG_STATUS, "Success");
		return message;
	}
}
