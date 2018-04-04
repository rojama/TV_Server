package com.fstar.aut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fstar.sys.DB;
import com.fstar.sys.Message;
import com.fstar.utility.Machine;
import com.ibm.json.java.JSONObject;

/**
 * 机构
 * @author HuangKai
 *
 */
public class Institution {
	private static Logger log = LoggerFactory.getLogger(Institution.class);
	
	private Map<String, Object> message=new HashMap<String, Object>();
	
	private String g_str_INS_ID; 			//机构ID
	
	private String g_str_INS_CODE;			//机构编号
	
	private String g_str_INS_NAME;			//机构简体中文名称
	
	private String g_str_INS_ENGNAME;		//机构英文名称
	
	private String g_str_INS_TWNAME;        //机构繁体中文名称
	
	private String g_str_INS_DESCRIBE;		//机构描述
	
	private String g_str_IS_START;			//是否启用
	
	private String g_str_SYSTEM_DATE;		//创建日期时间
	
	private String g_str_INS_SUPER_ID;		//上级机构编号
	
	private int g_i_INS_ORDER;				//排序

	public String getINS_ID() {
		return g_str_INS_ID;
	}

	public void setINS_ID(String g_str_INS_ID) {
		this.g_str_INS_ID = g_str_INS_ID;
	}

	public String getINS_CODE() {
		return g_str_INS_CODE;
	}

	public void setINS_CODE(String g_str_INS_CODE) {
		this.g_str_INS_CODE = g_str_INS_CODE;
	}
	
	public String getINS_NAME() {
		return g_str_INS_NAME;
	}

	public void setINS_NAME(String g_str_INS_NAME) {
		this.g_str_INS_NAME = g_str_INS_NAME;
	}

	public String getINS_ENGNAME() {
		return g_str_INS_ENGNAME;
	}

	public void setINS_ENGNAME(String g_str_INS_ENGNAME) {
		this.g_str_INS_ENGNAME = g_str_INS_ENGNAME;
	}

	public String getINS_TWNAME() {
		return g_str_INS_TWNAME;
	}

	public void setINS_TWNAME(String g_str_INS_TWNAME) {
		this.g_str_INS_TWNAME = g_str_INS_TWNAME;
	}

	public String getINS_DESCRIBE() {
		return g_str_INS_DESCRIBE;
	}

	public void setINS_DESCRIBE(String g_str_INS_DESCRIBE) {
		this.g_str_INS_DESCRIBE = g_str_INS_DESCRIBE;
	}

	public String getIS_START() {
		return g_str_IS_START;
	}

	public void setIS_START(String g_str_IS_START) {
		this.g_str_IS_START = g_str_IS_START;
	}

	public String getSYSTEM_DATE() {
		return g_str_SYSTEM_DATE;
	}

	public void setSYSTEM_DATE(String g_str_SYSTEM_DATE) {
		this.g_str_SYSTEM_DATE = g_str_SYSTEM_DATE;
	}

	public String getINS_SUPER_ID() {
		return g_str_INS_SUPER_ID;
	}

	public void setINS_SUPER_ID(String g_str_INS_SUPER_ID) {
		this.g_str_INS_SUPER_ID = g_str_INS_SUPER_ID;
	}

	public int getINS_ORDER() {
		return g_i_INS_ORDER;
	}

	public void setINS_ORDER(int g_i_INS_ORDER) {
		this.g_i_INS_ORDER = g_i_INS_ORDER;
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
		this.setINS_ID(trimNull(map.get("INS_ID")));
		
		this.setINS_CODE(trimNull(map.get("INS_CODE")));
		
		this.setINS_NAME(trimNull(map.get("INS_NAME")));
		
		this.setINS_ENGNAME(trimNull(map.get("INS_ENGNAME")));
		
		this.setINS_TWNAME(trimNull(map.get("INS_TWNAME")));
		
		this.setINS_DESCRIBE(trimNull(map.get("INS_DESCRIBE")));
		
		this.setIS_START(trimNull(map.get("IS_START")));
		
		this.setSYSTEM_DATE(trimNull(map.get("SYSTEM_DATE")));
		
		this.setINS_SUPER_ID(trimNull(map.get("INS_SUPER_ID")));
		
	}
	
	/**
	 * 查询顶级机构
	 * @param map
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> findAllInstitutionParent(Map<String, Object> map) throws Exception
	{
		Map<String, Object> dataMap = new HashMap<String,Object>();
		String userName = trimNull(map.get("PRINCIPAL"));
		String superID = trimNull(map.get("INS_SUPER_ID"));
		
		log.info("User Name is "+userName);
		log.info("Super ID is "+superID);
		
		String myLocale = this.getLocale(map);
		
		StringBuilder strSql = new StringBuilder();
		strSql.append("select INS_ID,INS_CODE,INS_NAME,INS_ENGNAME,INS_TWNAME,INS_DESCRIBE,IS_START,INS_SUPER_ID,"+myLocale+" as NAME from AUT_INSTITUTION ");
		strSql.append("where INS_SUPER_ID='"+superID+"' order by INS_ORDER ");
		
		List<Map<String, Object>> lhm_map =DB.query(strSql.toString());
		
		for(Map<String, Object> maps:lhm_map)
		{
			innerFindInstitutionChildren(maps,userName,myLocale);
		}
		
		dataMap.put("data", lhm_map);
		
		return dataMap;
	}
	
	/**
	 * 查询子集机构
	 * @param parent
	 * @param userName
	 * @param myLocale
	 * @throws Exception
	 */
	public void innerFindInstitutionChildren(Map<String, Object> parent, String userName,String myLocale) throws Exception
	{
		List<Map<String, Object>> lhm_map = new ArrayList<Map<String, Object>>();
		
		StringBuilder strSql = new StringBuilder();
		strSql.append("select INS_ID,INS_CODE,INS_NAME,INS_ENGNAME,INS_TWNAME,INS_DESCRIBE,IS_START,INS_SUPER_ID,"+myLocale+" as NAME from AUT_INSTITUTION ");
		strSql.append("where INS_SUPER_ID='"+parent.get("INS_ID")+"' order by INS_ORDER ");
		
		lhm_map=DB.query(strSql.toString());
		
		for(Map<String, Object> maps:lhm_map)
		{
			innerFindInstitutionChildren(maps,userName,myLocale);
		}
		
		if(lhm_map.size()>0)
		{
			parent.put("children", lhm_map);
		}
	}
	
	/**
	 * 新增机构
	 * @param map
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> Save(Map<String, Object> map) throws Exception
	{
		
		String str_Data = trimNull(map.get("Data"));
		
		String[] array = str_Data.split(";");
		
		DB.update("delete from AUT_INSTITUTION");
		
		int l = 0;
		
		if (!"".equals(str_Data) && array.length > 0)
		{
			for (int i = 0; i < array.length; i++)
			{
				Map<String, Object> lhm_map = JSONObject.parse(array[i]);
				
				String str_Status= trimNull(lhm_map.get("__status"));
				
				if("delete".equals(str_Status))
				{
					continue;
				}
				
				this.setProperty(lhm_map);
				
				if("".equals(g_str_INS_ID))
				{
					g_str_INS_ID = UUID.randomUUID().toString();
				}
				
				l++;
				
				String SYSTEM_DATETIME=trimNull(Machine.getSystemDateTime().get("SystemDateTime"));
				
				//新增一级机构
				StringBuilder strSql=new StringBuilder();
				strSql.append("insert into AUT_INSTITUTION (INS_ID,INS_CODE,INS_NAME,INS_ENGNAME,INS_TWNAME,");
				strSql.append("INS_DESCRIBE,IS_START,SYSTEM_DATETIME,INS_SUPER_ID,INS_ORDER) values ('"+g_str_INS_ID+"',");
				strSql.append("'"+g_str_INS_CODE+"','"+g_str_INS_NAME+"','"+g_str_INS_ENGNAME+"','"+g_str_INS_TWNAME+"', ");
				strSql.append("'"+g_str_INS_DESCRIBE+"','"+g_str_IS_START+"','"+SYSTEM_DATETIME+"','',"+l+") ");
				
				DB.update(strSql.toString());
				
				if (lhm_map.get("children") != null)
				{
					this.addChildrenInstitution((List<Map<String, Object>>)lhm_map.get("children"),g_str_INS_ID);
				}
			}
		}
		
		//删除机构用户中已不存在的信息
		String updateInsUser="delete from AUT_INSTITUTION_USER where INS_ID not in(select INS_ID from AUT_INSTITUTION)";
		DB.update(updateInsUser);
		
		message.put(Message.MSG_STATUS, "Success");
		return message;
	}
	
	/**
	 * 新增子机构
	 * @param list
	 * @param INS_SUPER_ID
	 * @throws Exception 
	 */
	public void addChildrenInstitution(List<Map<String, Object>> list,String INS_SUPER_ID) throws Exception
	{
		int l=0;
		
		for(Map<String, Object>map:list)
		{
			String str_Status= trimNull(map.get("__status"));
			
			if("delete".equals(str_Status))
			{
				continue;
			}
			
			this.setProperty(map);
			
			if("".equals(g_str_INS_ID))
			{
				g_str_INS_ID=UUID.randomUUID().toString();
			}
			
			l++;
			
			String SYSTEM_DATETIME=trimNull(Machine.getSystemDateTime().get("SystemDateTime"));
			
			//新增一级机构
			StringBuilder strSql=new StringBuilder();
			strSql.append("insert into AUT_INSTITUTION (INS_ID,INS_CODE,INS_NAME,INS_ENGNAME,INS_TWNAME,");
			strSql.append("INS_DESCRIBE,IS_START,SYSTEM_DATETIME,INS_SUPER_ID,INS_ORDER) values ('"+g_str_INS_ID+"',");
			strSql.append("'"+g_str_INS_CODE+"','"+g_str_INS_NAME+"','"+g_str_INS_ENGNAME+"','"+g_str_INS_TWNAME+"', ");
			strSql.append("'"+g_str_INS_DESCRIBE+"','"+g_str_IS_START+"','"+SYSTEM_DATETIME+"','"+INS_SUPER_ID+"',"+l+") ");
			
			DB.update(strSql.toString());
			
			if (map.get("children") != null)
			{
				addChildrenInstitution((List<Map<String, Object>>)map.get("children"),g_str_INS_ID);
			}
		}
	}
	
	/**
	 * 新增机构角色
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> SaveInsRole(Map<String, Object> map) throws Exception
	{
		String INS_ID = trimNull(map.get("InsID"));
		String InsRoleData = trimNull(map.get("InsRoleData"));
		
		if (!"".equals(INS_ID) )
		{
			
			DB.update("delete from AUT_INSTITUTION_ROLE where INS_ID='"+INS_ID+"' ");
			
			if(!"".equals(InsRoleData))
			{
				String[] array =InsRoleData.split(";");
				
				for (int i = 0; i < array.length; i++)
				{
					String SYSTEM_DATETIME=trimNull(Machine.getSystemDateTime().get("SystemDateTime"));
					
					StringBuilder strSql = new StringBuilder();
					strSql.append("insert into AUT_INSTITUTION_ROLE (INS_ID,ROLE_ID,SYSTEM_DATETIME) ");
					strSql.append("values('"+INS_ID+"','"+array[i]+"','"+SYSTEM_DATETIME+"')");
					
					DB.update(strSql.toString());
				}
			}
		}
		else
		{
			throw new Exception("the institution is not exit");
		}
		
		message.put(Message.MSG_STATUS, "Success");
		return message;
	}
	
	/**
	 * get Locale
	 * @param map
	 * @return
	 */
	public String getLocale(Map<String, Object> map)
	{
		String locale= (String)((Session)map.get("SESSION")).getAttribute("locale");
					   
		String myLocale="";
		
		if (("en-US").equals(locale)) {
			myLocale = "INS_ENGNAME";
		} else if (("zh_CN").equals(locale)) {
			myLocale = "INS_NAME";
		} else if (("zh_TW").equals(locale)) {
			myLocale = "INS_TWNAME";
		} else {
			myLocale = "INS_NAME";
		}
		
		return myLocale;
	}
}
