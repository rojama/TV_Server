package com.fstar.aut;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fstar.sys.DB;
import com.fstar.sys.Message;
import com.fstar.utility.Machine;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

/**
 * 角色
 * @author HuangKai
 *
 */
public class Role {
	
	private static Logger log = LoggerFactory.getLogger(Institution.class);
	
	private Map<String, Object> message=new HashMap<String, Object>();
	
	private String g_str_ROLE_ID;		//角色编号
	
	private String g_str_ROLE_NAME;		//角色简体中文名称
	
	private String g_str_ROLE_ENGNAME;	//角色英文名称
	
	private String g_str_ROLE_TWNAME;	//角色繁体中文名称
	
	private String g_str_IS_START;		//是否启用
	
	private String g_str_SYSTEM_DATETIME;//创建日期时间
	
	private String g_str_ROLE_SUPER_ID;  //父类ID
	
	private int g_i_ROLE_ORDER;			//排序
	
	public String getROLE_ID() {
		return g_str_ROLE_ID;
	}

	public void setROLE_ID(String g_str_ROLE_ID) {
		this.g_str_ROLE_ID = g_str_ROLE_ID;
	}

	public String getROLE_NAME() {
		return g_str_ROLE_NAME;
	}

	public void setROLE_NAME(String g_str_ROLE_NAME) {
		this.g_str_ROLE_NAME = g_str_ROLE_NAME;
	}

	public String getROLE_ENGNAME() {
		return g_str_ROLE_ENGNAME;
	}

	public void setROLE_ENGNAME(String g_str_ROLE_ENGNAME) {
		this.g_str_ROLE_ENGNAME = g_str_ROLE_ENGNAME;
	}

	public String getROLE_TWNAME() {
		return g_str_ROLE_TWNAME;
	}

	public void setROLE_TWNAME(String g_str_ROLE_TWNAME) {
		this.g_str_ROLE_TWNAME = g_str_ROLE_TWNAME;
	}

	public String getIS_START() {
		return g_str_IS_START;
	}

	public void setIS_START(String g_str_IS_START) {
		this.g_str_IS_START = g_str_IS_START;
	}

	public String getSYSTEM_DATETIME() {
		return g_str_SYSTEM_DATETIME;
	}

	public void setSYSTEM_DATETIME(String g_str_SYSTEM_DATETIME) {
		this.g_str_SYSTEM_DATETIME = g_str_SYSTEM_DATETIME;
	}
	
	public String getROLE_SUPER_ID() {
		return g_str_ROLE_SUPER_ID;
	}

	public void setROLE_SUPER_ID(String g_str_ROLE_SUPER_ID) {
		this.g_str_ROLE_SUPER_ID = g_str_ROLE_SUPER_ID;
	}
	
	public int getROLE_ORDER() {
		return g_i_ROLE_ORDER;
	}

	public void setROLE_ORDER(int g_i_ROLE_ORDER) {
		this.g_i_ROLE_ORDER = g_i_ROLE_ORDER;
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
	 * set Property
	 * @param map
	 */
	public void setProperty(Map<String, Object> map)
	{
		this.setROLE_ID(trimNull(map.get("ROLE_ID")));
		
		this.setROLE_NAME(trimNull(map.get("ROLE_NAME")));
		
		this.setROLE_ENGNAME(trimNull(map.get("ROLE_ENGNAME")));
		
		this.setROLE_TWNAME(trimNull(map.get("ROLE_TWNAME")));
		
		this.setIS_START(trimNull(map.get("IS_START")));
		
		this.setSYSTEM_DATETIME(trimNull(map.get("SYSTEM_DATETIME")));
		
		this.setROLE_SUPER_ID(trimNull(map.get("ROLE_SUPER_ID")));
	}
	
	 
	
	/**
	 * 保存
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> Save(Map<String, Object> map) throws Exception
	{
		//System.out.println(map);
		String str_Data = trimNull(map.get("Data"));
		
		String[] array= str_Data.split(";");
		
		DB.update("delete from AUT_ROLE");
		
		if (!"".equals(str_Data) && array.length > 0)
		{
			int l = 0;
			for (int i = 0; i < array.length; i++)
			{
				Map<String, Object> lhm_map = JSONObject.parse(array[i]);
				
				String str_Status = trimNull(lhm_map.get("__status"));
				
				if ("delete".equals(str_Status))
				{
					continue;
				}
				
				this.setProperty(lhm_map);
				
				if ("".equals(g_str_ROLE_ID))
				{
					g_str_ROLE_ID=UUID.randomUUID().toString();
				}
				
				l++;
				
				String SYSTEM_DATETIME=trimNull(Machine.getSystemDateTime().get("SystemDateTime"));
				StringBuilder strSql = new StringBuilder();
				strSql.append("INSERT INTO AUT_ROLE (ROLE_ID, ROLE_NAME, ROLE_ENGNAME, ROLE_TWNAME, IS_START,");
				strSql.append("SYSTEM_DATETIME, ROLE_SUPER_ID,ROLE_ORDER) VALUES ('"+g_str_ROLE_ID+"','"+g_str_ROLE_NAME+"', ");
				strSql.append("'"+g_str_ROLE_ENGNAME+"','"+g_str_ROLE_TWNAME+"','"+g_str_IS_START+"',");
				strSql.append("'"+SYSTEM_DATETIME+"','',"+l+" )");
				
				DB.update(strSql.toString());
				
				if(lhm_map.get("children") != null)
				{
					addChildrenRole((List<Map<String,Object>>)lhm_map.get("children"),g_str_ROLE_ID);
				}
			}
		}
		
		String updateInsRole="delete from AUT_INSTITUTION_ROLE where ROLE_ID not in(select ROLE_ID from AUT_ROLE)";
		DB.update(updateInsRole);
		
		message.put(Message.MSG_STATUS, "Success");
		return message;
	}
	
	public void addChildrenRole(List<Map<String, Object>> list,String ROLE_SUPER_ID) throws Exception
	{
		for(Map<String, Object>map:list)
		{
			String str_Status= trimNull(map.get("__status"));
			
			if("delete".equals(str_Status))
			{
				continue;
			}
			
			this.setProperty(map);
			
			if("".equals(g_str_ROLE_ID))
			{
				g_str_ROLE_ID=UUID.randomUUID().toString();
			}
			
			String SYSTEM_DATETIME=trimNull(Machine.getSystemDateTime().get("SystemDateTime"));
			StringBuilder strSql = new StringBuilder();
			strSql.append("INSERT INTO AUT_ROLE (ROLE_ID, ROLE_NAME, ROLE_ENGNAME, ROLE_TWNAME, IS_START,");
			strSql.append("SYSTEM_DATETIME, ROLE_SUPER_ID) VALUES ('"+g_str_ROLE_ID+"','"+g_str_ROLE_NAME+"', ");
			strSql.append("'"+g_str_ROLE_ENGNAME+"','"+g_str_ROLE_TWNAME+"','"+g_str_IS_START+"',");
			strSql.append("'"+SYSTEM_DATETIME+"','"+ROLE_SUPER_ID+"') ");
			
			DB.update(strSql.toString());
			
			if(map.get("children") != null)
			{
				addChildrenRole((List<Map<String,Object>>)map.get("children"),g_str_ROLE_ID);
			}
		}
	}
	
	/**
	 * 保存角色权限
	 * @param map
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> SaveAuthority (Map<String, Object> map) throws Exception
	{
		System.out.println("map:::"+map);
		
		String ROLE_ID = trimNull(map.get("ROLE_ID"));
		
		String str_Data = trimNull(map.get("Data"));
		
		String[] array= str_Data.split(";");
		
		if (!"".equals(str_Data)&&array.length>0)
		{
			DB.update("delete from AUT_ROLE_MENU_OPERATION where ROLE_ID='"+ROLE_ID+"' ");
			
			List<Map<String,Object>> lhm_data = getOperation();
			
			for (int i = 0; i < array.length; i++)
			{
				Map<String, Object> lhm_map = JSONObject.parse(array[i]);
				
				String Menu_ID=trimNull(lhm_map.get("id"));
				String strSql="insert into AUT_ROLE_MENU_OPERATION(ROLE_ID,MENU_ID,OPE_ID) values('"+ROLE_ID+"','"+Menu_ID+"','') ";
				DB.update(strSql);
				
				if(lhm_map.get("children") != null)
				{
					SaveChildrenAuthority((List<Map<String,Object>>)lhm_map.get("children"),ROLE_ID,lhm_data);
				}
			}
		}
		
		return null;
	}
	
	public void SaveChildrenAuthority(List<Map<String, Object>> list,String ROLE_ID,List<Map<String,Object>> lhm_data) throws Exception
	{
		for(Map<String, Object>map:list)
		{
			String Menu_ID=trimNull(map.get("id"));
			
			if (map.get("children") != null)
			{
				String strSql="insert into AUT_ROLE_MENU_OPERATION(ROLE_ID,MENU_ID,OPE_ID) values('"+ROLE_ID+"','"+Menu_ID+"','') ";
				DB.update(strSql);
				
				SaveChildrenAuthority((List<Map<String,Object>>)map.get("children"),ROLE_ID,lhm_data);
			}
			else
			{
				boolean CodeFlag=false;
				for(Map<String, Object> oper_map:lhm_data)
				{
					String Key = trimNull(oper_map.get("OPE_CODE"));
					
					if(map.containsKey(Key))
					{
						String bool=String.valueOf(map.get(Key));
						
						if("true".equals(bool))
						{
							CodeFlag=true;
							String strSql="insert into AUT_ROLE_MENU_OPERATION (ROLE_ID,MENU_ID,OPE_ID) values('"+ROLE_ID+"','"+Menu_ID+"','"+Key+"') ";
							DB.update(strSql);
						}
					}
				}
				
				if(!CodeFlag)
				{
					String strSql="insert into AUT_ROLE_MENU_OPERATION (ROLE_ID,MENU_ID,OPE_ID) values('"+ROLE_ID+"','"+Menu_ID+"','') ";
					DB.update(strSql);
				}
			}
		}
	}
	
	public List<Map<String,Object>> getOperation() throws Exception
	{
		List<Map<String,Object>> map=DB.query("select OPE_CODE from AUT_OPERATION");
		
		return map;
	}
}
