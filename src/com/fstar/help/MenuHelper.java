package com.fstar.help;

import java.util.List;
import java.util.Map;

import com.fstar.sys.DB;


public class MenuHelper 
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
			myLocale = "ENGNAME";
		} else if (locale.startsWith("zh_CN")) {
			myLocale = "NAME";
		} else if (("zh_TW").equals(locale)) {
			myLocale = "INS_TWNAME";
		} else {
			myLocale = "NAME";
		}
		
		return myLocale;
	}
	
	/**
	 * 角色功能
	 * @param locale
	 * @param ROLE_ID
	 * @param SUPER_ROLE_ID
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> innerFindMenu(String locale,String ROLE_ID,String SUPER_ROLE_ID) throws Exception
	{
		String myLocale=getLocale(locale);
		
		StringBuilder strSql = new StringBuilder();
		strSql.append("select a.ID,a."+myLocale+" as TEXT,a.ISVALID,a.TOPMENUID,");
		strSql.append("(select distinct case  ROLE_ID when '' then 'false' ");
		strSql.append("when null then 'false' else 'true' end from AUT_ROLE_MENU_OPERATION b ");
		strSql.append("where a.ID=b.MENU_ID and b.ROLE_ID='"+ROLE_ID+"') as ischecked ");
		strSql.append("from AUT_MENU a where a.TOPMENUID='' ");
		
		if(!"".equals(SUPER_ROLE_ID))
		{
			strSql.append("and a.ID in(select DISTINCT MENU_ID from ");
			strSql.append("AUT_ROLE_MENU_OPERATION where ROLE_ID='"+SUPER_ROLE_ID+"') ");
		}
		
		strSql.append("order by SORT_ORDER ");
		
		List<Map<String, Object>> list_map =DB.query(strSql.toString());
		
		for(Map<String, Object> map:list_map)
		{	
			innerFindMenuChildren(map,myLocale,ROLE_ID,SUPER_ROLE_ID);
		}
		
		return list_map;
	}
	
	public void innerFindMenuChildren(Map<String, Object> parent,String myLocale,String ROLE_ID,String SUPER_ROLE_ID) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select a.ID,a."+myLocale+" as TEXT,a.ISVALID,a.TOPMENUID,");
		strSql.append("(select distinct case  ROLE_ID when '' then 'false' ");
		strSql.append("when null then 'false' else 'true' end from AUT_ROLE_MENU_OPERATION b ");
		strSql.append("where a.ID=b.MENU_ID and b.ROLE_ID='"+ROLE_ID+"') as ischecked ");
		strSql.append("from AUT_MENU a where a.TOPMENUID='"+parent.get("ID")+"' ");
		
		if(!"".equals(SUPER_ROLE_ID))
		{
			strSql.append("and a.ID in(select DISTINCT MENU_ID from ");
			strSql.append("AUT_ROLE_MENU_OPERATION where ROLE_ID='"+SUPER_ROLE_ID+"') ");
		}
		
		strSql.append("order by SORT_ORDER ");
		
		List<Map<String, Object>> list_map=DB.query(strSql.toString());
		
		for(Map<String, Object> map:list_map)
		{
			innerFindMenuChildren(map,myLocale,ROLE_ID,SUPER_ROLE_ID);
		}
		
		if(list_map.size()>0)
		{
			parent.put("children", list_map);
		}
	}
}
