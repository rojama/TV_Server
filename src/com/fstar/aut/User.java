package com.fstar.aut;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fstar.sys.DB;
import com.fstar.sys.Message;
import com.fstar.utility.Machine;
import com.fstar.utility.PasswordEncode;
import com.ibm.json.java.JSONArray;

/**
 * 用户管理
 * @author HuangKai
 *
 */
public class User {
	private static Logger log = LoggerFactory.getLogger(Institution.class);
	
	private Map<String, Object> message=new HashMap<String, Object>();
	
	private String g_str_USER_ID;			//用户ID
	
	private String g_str_USER_NAME;			//用户简体中文名称
	
	private String g_str_USER_ENGNAME;		//用户英文名称
	
	private String g_str_USER_TWNAME;		//用户繁体中文名称
	
	private String g_str_PASSWORD;			//密码
	
	private String g_str_IS_START;			//是否启用
	
	private String g_str_LAST_LOGIN_TIME;	//上次登录时间
	
	private String g_str_SYSTEM_DATETIME;   //创建时间

	public String getUSER_ID() {
		return g_str_USER_ID;
	}

	public void setUSER_ID(String g_str_USER_ID) {
		this.g_str_USER_ID = g_str_USER_ID;
	}

	public String getUSER_NAME() {
		return g_str_USER_NAME;
	}

	public void setUSER_NAME(String g_str_USER_NAME) {
		this.g_str_USER_NAME = g_str_USER_NAME;
	}

	public String getUSER_ENGNAME() {
		return g_str_USER_ENGNAME;
	}

	public void setUSER_ENGNAME(String g_str_USER_ENGNAME) {
		this.g_str_USER_ENGNAME = g_str_USER_ENGNAME;
	}

	public String getUSER_TWNAME() {
		return g_str_USER_TWNAME;
	}

	public void setUSER_TWNAME(String g_str_USER_TWNAME) {
		this.g_str_USER_TWNAME = g_str_USER_TWNAME;
	}

	public String getPASSWORD() {
		return g_str_PASSWORD;
	}

	public void setPASSWORD(String g_str_PASSWORD) {
		this.g_str_PASSWORD = g_str_PASSWORD;
	}

	public String getIS_START() {
		return g_str_IS_START;
	}

	public void setIS_START(String g_str_IS_START) {
		this.g_str_IS_START = g_str_IS_START;
	}

	public String getLAST_LOGIN_TIME() {
		return g_str_LAST_LOGIN_TIME;
	}

	public void setLAST_LOGIN_TIME(String g_str_LAST_LOGIN_TIME) {
		this.g_str_LAST_LOGIN_TIME = g_str_LAST_LOGIN_TIME;
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
	 * set Property
	 * @param map
	 */
	public void setProperty(Map<String, Object> map)
	{
		this.setUSER_ID(trimNull(map.get("USER_ID")));
		
		this.setUSER_NAME(trimNull(map.get("USER_NAME")));
		
		this.setUSER_ENGNAME(trimNull(map.get("USER_ENGNAME")));
		
		this.setUSER_TWNAME(trimNull(map.get("USER_TWNAME")));
		
		this.setPASSWORD(trimNull(map.get("PASSWORD")));
		
		this.setIS_START(trimNull(map.get("IS_START")));
		
		this.setLAST_LOGIN_TIME(trimNull(map.get("LAST_LOGIN_TIME")));
		
		this.setSYSTEM_DATETIME(trimNull(map.get("SYSTEM_DATETIME")));
	}
	
	/**
	 * 查询所有用户信息
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> findAll(Map<String, Object> map) throws Exception
	{
		Map<String, Object> lhm_map = new HashMap<String, Object>();
		List<Map<String, Object>> result =DB.query("select * from AUT_USER order by SYSTEM_DATETIME desc");
		
		lhm_map.put("data", result);		
		return lhm_map;
	}
	
	/**
	 * 新增
	 * @param map
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> Save(Map<String, Object> map) throws Exception
	{
		System.out.println(map);
		
		String str_AddData = trimNull(map.get("AddData"));
		String str_EditData = trimNull(map.get("EditData"));
		String str_DelData = trimNull(map.get("DelData"));
		
		if (!"".equals(str_AddData))//新增
		{
			JSONArray addArray = JSONArray.parse(str_AddData);
			
			for (int i = 0; i < addArray.size(); i++)
			{
				Map lhm_map = (Map) addArray.get(i);
				this.setProperty(lhm_map);
				
				if(isExist(g_str_USER_ID))
				{
					throw new Exception("the user :"+g_str_USER_ID+" is exist");
				}
				else
				{
					if (g_str_PASSWORD.isEmpty()){
						//默认密码
						g_str_PASSWORD = "000000";
					}
					String str_PASSWORD=PasswordEncode.EncodeSHA256(g_str_PASSWORD); //加密密码
					String SYSTEM_MDATETIME=trimNull(Machine.getSystemDateTime().get("SystemDateTime"));
					
					StringBuilder strSql = new StringBuilder();
					strSql.append("INSERT INTO AUT_USER (USER_ID, USER_NAME, USER_ENGNAME, USER_TWNAME,   ");
					strSql.append("PASSWORD, IS_START,LAST_LOGIN_TIME, SYSTEM_DATETIME) VALUES ");
					strSql.append("('"+g_str_USER_ID+"', '"+g_str_USER_NAME+"', '"+g_str_USER_ENGNAME+"', ");
					strSql.append("'"+g_str_USER_TWNAME+"', '"+str_PASSWORD+"', '"+g_str_IS_START+"', ");
					strSql.append("'"+g_str_LAST_LOGIN_TIME+"', '"+SYSTEM_MDATETIME+"')");
					
					DB.update(strSql.toString());
				}
				
			}
		}
		
		if(!"".equals(str_EditData))//修改
		{
			JSONArray editArray = JSONArray.parse(str_EditData);
			
			for (int l = 0; l < editArray.size(); l++)
			{
				Map lhm_map = (Map) editArray.get(l);
				this.setProperty(lhm_map);
				
				String SYSTEM_MDATETIME=trimNull(Machine.getSystemDateTime().get("SystemDateTime"));
				
				StringBuilder strSql = new StringBuilder();
				strSql.append("update AUT_USER set USER_NAME='"+g_str_USER_NAME+"',USER_ENGNAME='"+g_str_USER_ENGNAME+"', ");
				strSql.append("USER_TWNAME='"+g_str_USER_TWNAME+"',IS_START='"+g_str_IS_START+"', ");
				strSql.append("SYSTEM_DATETIME='"+SYSTEM_MDATETIME+"' where USER_ID='"+g_str_USER_ID+"' ");
				
				DB.update(strSql.toString());
			}
		}
		
		if(!"".equals(str_DelData))
		{
			JSONArray delArray = JSONArray.parse(str_DelData);
			
			for (int j = 0; j < delArray.size(); j++)
			{
				Map lhm_map = (Map) delArray.get(j);
				this.setProperty(lhm_map);
				
				DB.update("delete from AUT_USER where USER_ID='"+g_str_USER_ID+"' ");
				DB.update("delete from AUT_USER_ROLE where USER_ID='"+g_str_USER_ID+"' ");
				DB.update("delete from AUT_INSTITUTION_USER where USER_ID='"+g_str_USER_ID+"' ");
			}
		}
		
		message.put(Message.MSG_STATUS, "Success");
		return message;
	}
	
	/**
	 * 新增用户机构
	 * @param map
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> SaveIns(Map<String, Object> map) throws Exception
	{
		String USER_ID = trimNull(map.get("USER_ID"));
		String InsData = trimNull(map.get("InsData"));
		
		if(isExist(USER_ID))
		{
			DB.update("delete from AUT_INSTITUTION_USER where USER_ID='"+USER_ID+"'");
			if (!"".equals(InsData))
			{
				String[] array = InsData.split(";");
				
				for (int i = 0; i < array.length; i++)
				{
					String SYSTEM_MDATETIME=trimNull(Machine.getSystemDateTime().get("SystemDateTime"));
					String INS_ID=array[i];
					
					DB.update("insert into AUT_INSTITUTION_USER(INS_ID,USER_ID,SYSTEM_DATETIME) values('"+INS_ID+"','"+USER_ID+"','"+SYSTEM_MDATETIME+"')");
				}
			}
		}
		else
		{
			throw new Exception("the user :"+g_str_USER_ID+" is not exist");
		}
		
		message.put(Message.MSG_STATUS, "Success");
		return message;
	}
	
	/**
	 * 新增用户角色
	 * @param map
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> SaveRole(Map<String, Object> map) throws Exception
	{
		String USER_ID = trimNull(map.get("USER_ID"));
		String RoleData = trimNull(map.get("RoleData"));
		
		if(isExist(USER_ID))
		{
			DB.update("delete from AUT_USER_ROLE where USER_ID='"+USER_ID+"' ");
			if (!"".equals(RoleData))
			{
				String[] array =RoleData.split(";");
				
				for (int i = 0; i < array.length; i++)
				{
					String SYSTEM_MDATETIME=trimNull(Machine.getSystemDateTime().get("SystemDateTime"));
					String ROLE_ID=array[i];
					
					DB.update("insert into AUT_USER_ROLE (USER_ID,ROLE_ID,SYSTEM_DATETIME) values ('"+USER_ID+"','"+ROLE_ID+"','"+SYSTEM_MDATETIME+"')");
				}
			}
		}
		else
		{
			throw new Exception("the user :"+g_str_USER_ID+" dose not exist");
		}
		
		message.put(Message.MSG_STATUS, "Success");
		return message;
	}
	
	public Map<String, Object> ResetPassword(Map<String, Object> map) throws Exception
	{
		String USER_ID = trimNull(map.get("USERID"));
		String PASSWORD = trimNull(map.get("PASSWORD"));
		
		if (!"".equals(PASSWORD))
		{
			PASSWORD=PasswordEncode.EncodeSHA256(PASSWORD);
			DB.update("update AUT_USER set PASSWORD='"+PASSWORD+"' where USER_ID='"+USER_ID+"'");
		}
		
		message.put(Message.MSG_STATUS, "Success");
		return message;
	}
	
	public boolean isExist(String USER_ID) throws Exception
	{
		List<Map<String, Object>> list=DB.query("select * from AUT_USER where USER_ID ='"+USER_ID+"' ");
		
		if(list.size()>0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
