package com.fstar.help;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.session.Session;

import com.fstar.sys.DB;

public class InstitutionHelper 
{
	public String trimNull(Object str_valueObject) {
		String str_value = (String) str_valueObject;
		if (str_value == null) {
			return "";
		} else {
			return str_value.trim();
		}
	}
	
	public String getLocale(String locale)
	{
		String myLocale="";
		
		if (("en-US").equals(locale)) {
			myLocale = "INS_ENGNAME";
		} else if (locale.startsWith("zh_CN")) {
			myLocale = "INS_NAME";
		} else if (("zh_TW").equals(locale)) {
			myLocale = "INS_TWNAME";
		} else {
			myLocale = "INS_NAME";
		}
		
		return myLocale;
	}
	
	/**
	 * 查询父类用户机构
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> innerFindInstitution(String locale,String INS_SUPER_ID) throws Exception
	{
		String myLocale=getLocale(locale);
		List<Map<String, Object>> list_map =DB.query("select *,"+myLocale+" as NAME from AUT_INSTITUTION where INS_SUPER_ID='"+trimNull(INS_SUPER_ID)+"' ");
		
		for(Map<String, Object> map:list_map)
		{
			innerFindInstitutionChildren(map,myLocale);
		}
		
		return list_map;
	}
	
	/**
	 * 查询子机构
	 * @param parent
	 * @param myLocale
	 * @throws Exception
	 */
	public void innerFindInstitutionChildren(Map<String, Object> parent,String myLocale) throws Exception
	{
		List<Map<String, Object>> list_map=DB.query("select *,"+myLocale+" as NAME from AUT_INSTITUTION where INS_SUPER_ID='"+parent.get("INS_ID")+"'");
		
		for(Map<String, Object> map:list_map)
		{
			innerFindInstitutionChildren(map,myLocale);
		}
		
		if(list_map.size()>0)
		{
			parent.put("children", list_map);
		}
	}
	
	/**
	 * 查询机构用户
	 * @param locale
	 * @param USER_ID 用户ID
	 * @param INS_SUPER_ID 机构ID
	 * @return
	 * @throws Exception
	 */
	public  List<Map<String, Object>> innerFindInstitutionUser(String locale,String USER_ID,String INS_SUPER_ID) throws Exception
	{
		String myLocale=getLocale(locale);
		
		StringBuilder strSql = new StringBuilder();
		strSql.append("select a.* , (select case INS_ID when '' then 'false' when null then 'false' else 'true' end ");
		strSql.append("from AUT_INSTITUTION_USER b where a.INS_ID=b.INS_ID and  b.USER_ID='"+USER_ID+"') as ischecked, ");
		strSql.append(""+myLocale+" as NAME from AUT_INSTITUTION a where INS_SUPER_ID='"+trimNull(INS_SUPER_ID)+"' ");
		
		List<Map<String, Object>> list=DB.query(strSql.toString());
		
		for(Map<String, Object> map :list)
		{
			innerFindInstitutionUserChildren(map,myLocale,USER_ID);
		}
		
		return list;
	}
	
	/**
	 * 查询机构用户
	 * @param parent
	 * @param myLocale
	 * @param USER_ID 用户ID
	 * @throws Exception
	 */
	public void innerFindInstitutionUserChildren(Map<String, Object> parent,String myLocale,String USER_ID) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select a.* , (select case INS_ID when '' then 'false' when null then 'false' else 'true' end ");
		strSql.append("from AUT_INSTITUTION_USER b where a.INS_ID=b.INS_ID and  b.USER_ID='"+USER_ID+"') as ischecked, ");
		strSql.append(""+myLocale+" as NAME from AUT_INSTITUTION a where a.INS_SUPER_ID='"+trimNull(parent.get("INS_ID"))+"' ");
		
		List<Map<String, Object>> list=DB.query(strSql.toString());
		
		for(Map<String, Object> map:list)
		{
			innerFindInstitutionUserChildren(map,myLocale,USER_ID);
		}
		
		if(list.size()>0)
		{
			parent.put("children", list);
		}
	}
}
