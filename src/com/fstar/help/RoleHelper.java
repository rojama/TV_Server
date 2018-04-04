package com.fstar.help;

import java.util.List;
import java.util.Map;

import com.fstar.sys.DB;

public class RoleHelper {
	
	public String getLocale(String locale)
	{
		String myLocale="";
		
		if (("en-US").equals(locale)) {
			myLocale = "ROLE_ENGNAME";
		} else if (locale.startsWith("zh_CN")) {
			myLocale = "ROLE_NAME";
		} else if (("zh_TW").equals(locale)) {
			myLocale = "ROLE_TWNAME";
		} else {
			myLocale = "ROLE_NAME";
		}
		
		return myLocale;
	}
	
	public String trimNull(Object str_valueObject) {
		String str_value = (String) str_valueObject;
		if (str_value == null) {
			return "";
		} else {
			return str_value.trim();
		}
	}
	
	public List<Map<String, Object>> innerFindRole(String Locale,String ROLE_SUPER_ID) throws Exception
	{
		String myLocale=getLocale(Locale);
		
		List<Map<String, Object>> list =DB.query("select *,"+myLocale+" as NAME from AUT_ROLE where ROLE_SUPER_ID='"+trimNull(ROLE_SUPER_ID)+"' order by ROLE_ORDER ");
		
		for(Map<String, Object> map:list)
		{
			innerFindRoleChildren(map,myLocale);
		}
		
		return list;
	}
	
	public void innerFindRoleChildren(Map<String, Object> parent,String myLocale) throws Exception
	{
		List<Map<String, Object>> list =DB.query("select *,"+myLocale+" as NAME from AUT_ROLE where ROLE_SUPER_ID='"+parent.get("ROLE_ID")+"' order by ROLE_ORDER ");
		
		for(Map<String, Object> map:list)
		{
			innerFindRoleChildren(map,myLocale);
		}
		
		if(list.size()>0)
		{
			parent.put("children", list);
		}
	}
	
	/**
	 * 查询父类机构角色树
	 * @param Locale
	 * @param INS_ID 本级机构编号
	 * @param INS_SUPER_ID上级机构编号
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> innerFindInsRole(String Locale,String INS_ID,String INS_SUPER_ID) throws Exception
	{
		String myLocale=getLocale(Locale);
		
		StringBuilder strSql = new StringBuilder();
		strSql.append("select a.ROLE_ID,a.ROLE_NAME,a.ROLE_SUPER_ID,"+myLocale+" as NAME, ");
		strSql.append("(select case INS_ID when '' then 'false' when null then 'false' else 'true' end ");
		strSql.append("from AUT_INSTITUTION_ROLE b where a.ROLE_ID=b.ROLE_ID and b.INS_ID='"+trimNull(INS_ID)+"')  as ischecked ");
		strSql.append("from AUT_ROLE a ");
		
		if (!"".equals(trimNull(INS_SUPER_ID)))
		{
			strSql.append(",AUT_INSTITUTION_ROLE c where a.ROLE_ID=c.ROLE_ID and c.INS_ID='"+INS_SUPER_ID+"' ");
			strSql.append("and a.ROLE_SUPER_ID='' order by a.ROLE_ORDER");
		}
		else
		{
			strSql.append("where a.ROLE_SUPER_ID='' order by a.ROLE_ORDER");
		}
		
		List<Map<String, Object>> lhm_map =DB.query(strSql.toString());
		
		for(Map<String, Object> maps:lhm_map)
		{
			innerFindInsRoleChildren(maps,myLocale,INS_ID,INS_SUPER_ID);
		}
		
		return lhm_map;
	}
	
	/**
	 * 查询子类机构角色树
	 * @param parent
	 * @param myLocale
	 * @param INS_ID 本级机构
	 * @param INS_SUPER_ID 上级机构
	 * @throws Exception
	 */
	public void innerFindInsRoleChildren(Map<String, Object> parent,String myLocale,String INS_ID,String INS_SUPER_ID) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select a.ROLE_ID,a.ROLE_NAME,a.ROLE_SUPER_ID,"+myLocale+" as NAME, ");
		strSql.append("(select case INS_ID when '' then 'false' when null then 'false' else 'true' end ");
		strSql.append("from AUT_INSTITUTION_ROLE b where a.ROLE_ID=b.ROLE_ID and b.INS_ID='"+INS_ID+"')  as ischecked ");
		strSql.append("from AUT_ROLE a ");
		
		if(!"".equals(trimNull(INS_SUPER_ID)))
		{
			strSql.append(",AUT_INSTITUTION_ROLE c where a.ROLE_ID=c.ROLE_ID and  ");
			strSql.append("a.ROLE_SUPER_ID='"+trimNull(parent.get("ROLE_ID"))+"' ");
			strSql.append("and c.INS_ID='"+INS_SUPER_ID+"' order by a.ROLE_ORDER");
		}
		else
		{
			strSql.append("where a.ROLE_SUPER_ID='"+trimNull(parent.get("ROLE_ID"))+"' order by a.ROLE_ORDER ");
		}
		 
		List<Map<String, Object>> lhm_map =DB.query(strSql.toString());
		
		for(Map<String, Object> maps:lhm_map)
		{
			innerFindInsRoleChildren(maps,myLocale,INS_ID,INS_SUPER_ID);
		}
		
		if(lhm_map.size()>0)
		{
			parent.put("children", lhm_map);
		} 
	}
	
	/**
	 * 查询用户角色
	 * @param locale
	 * @param USER_ID 用户ID
	 * @param ROLE_SUPER_ID 角色ID
	 * @return
	 * @throws Exception
	 */
	public  List<Map<String, Object>> innerFindUserRole(String locale,String USER_ID,String ROLE_SUPER_ID) throws Exception
	{
		String myLocale=getLocale(locale);
		
		StringBuilder strSql = new StringBuilder();
		strSql.append("select a.*, (select case b.ROLE_ID when '' then 'false' when null then 'false' else 'true' end ");
		strSql.append("from AUT_USER_ROLE b where b.ROLE_ID=a.ROLE_ID and b.USER_ID='"+trimNull(USER_ID)+"') as ischecked, "+myLocale+" as NAME ");
		strSql.append("from AUT_ROLE a where a.ROLE_SUPER_ID='"+trimNull(ROLE_SUPER_ID)+"' ");
		
		List<Map<String, Object>> list=DB.query(strSql.toString());
		
		for(Map<String, Object> map :list)
		{
			innerFindUserRoleChildren(map,myLocale,USER_ID);
		}
		
		return list;
	}
	
	/**
	 * 查询用户角色
	 * @param parent
	 * @param myLocale
	 * @param USER_ID
	 * @throws Exception 
	 */
	public void innerFindUserRoleChildren(Map<String, Object> parent,String myLocale,String USER_ID) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select a.*, (select case b.ROLE_ID when '' then 'false' when null then 'false' else 'true' end ");
		strSql.append("from AUT_USER_ROLE b where b.ROLE_ID=a.ROLE_ID and b.USER_ID='"+USER_ID+"') as ischecked, "+myLocale+" as NAME ");
		strSql.append("from AUT_ROLE a where a.ROLE_SUPER_ID='"+trimNull(parent.get("ROLE_ID"))+"' ");
		
		List<Map<String, Object>> list=DB.query(strSql.toString());
		
		for(Map<String, Object> map:list)
		{
			innerFindUserRoleChildren(map,myLocale,USER_ID);
		}
		
		if(list.size()>0)
		{
			parent.put("children", list);
		}
	}
}
