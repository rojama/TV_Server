package com.fstar.sys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fstar.utility.Machine;
import com.fstar.utility.PasswordEncode;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;





public class Authority {
	private static Logger log = LoggerFactory.getLogger(Authority.class);
	
	private Map<String, Object> message=new HashMap<String, Object>();
	
	public Map<String, Object> changeUserPassword(Map<String, Object> map)
			throws Exception {
		Map<String, Object> dataMap = new HashMap<String,Object>();		
		String userName = (String) map.get("PRINCIPAL");
		Subject subject = (Subject) map.get("SUBJECT");
		String oldPassword = (String) map.get("oldPassword");
		String newPassword = (String) map.get("newPassword");
		
		HashMap<String, Object> key = new HashMap<String, Object>();
		key.put("USER_ID", userName);
		List<Map<String, Object>> data = DB.seleteByKey("aut_user", key);
		if (data.size() == 1){
			if ((PasswordEncode.EncodeSHA256(oldPassword)).equals(data.get(0).get("PASSWORD"))){
				key.put("PASSWORD", PasswordEncode.EncodeSHA256(newPassword));
				DB.update("aut_user", key);
			}else{
				throw new Exception("原密码不正确");	
			}
		}else{
			throw new Exception("用户不存在");
		}
		return null;
	}
	
	public Map<String, Object> findTopMenuByUser(Map<String, Object> map)
			throws Exception {
		Map<String, Object> dataMap = new HashMap<String,Object>();		
		String userName = (String) map.get("PRINCIPAL");
		Subject subject = (Subject) map.get("SUBJECT");
		log.info("User Name is "+userName);
		String myLocale=this.getLocale(map);
		
		List<Map<String,Object>> result;
		
		if (userName.equals("admin")){
			result = DB.query("select ID,"+myLocale+" as NAME from AUT_MENU  where TOPMENUID =''  order by SORT_ORDER ");
		}else{
			String roleid = "";
			result = DB.query("select * from aut_role");
			for (Map<String,Object> sub : result){
				if (subject.hasRole((String)sub.get("ROLE_ID"))){
					roleid += (String)sub.get("ROLE_ID")+",";
				}
			}
			if (roleid.endsWith(",")){
				roleid = roleid.substring(0, roleid.length()-1);
			}
			result = DB.query("select a.ID, "+myLocale+" as NAME from AUT_MENU a, aut_role_menu_operation b "
					+ "where a.ID = b.MENU_ID and a.TOPMENUID ='' and b.ROLE_ID in ('"+roleid+"')  order by a.SORT_ORDER ");
		}
		
		dataMap.put("data", result);
		return dataMap;

	}

	public Map<String, Object> findAllSubMenuByParent(Map<String, Object> map)
			throws Exception {
		Map<String, Object> dataMap = new HashMap<String,Object>();
		String userName = (String) map.get("PRINCIPAL");
		String topMenuID = (String) map.get("TopMenuID");
		
		String myLocale=this.getLocale(map);
		
		log.info("User Name is "+userName);
		log.info("topMenuID Name is "+topMenuID);
		List<Map<String, Object>> menus = DB.query("select ID,URL,NAME,ENGNAME,TWNAME,"+myLocale+" as TEXT,ISVALID from AUT_MENU where TOPMENUID='"+topMenuID+"'  order by SORT_ORDER "); //DB.query("select AREA as URL,NAME as TEXT,'1' TYPE from AREAINFO WHERE TOPMENUID='"+topMenuID+"'");
		for (Map<String, Object> menu : menus) {
			innerFindMenuByParentAndUser(menu, userName,myLocale);
		}
		dataMap.put("data", menus);
		return dataMap;
	}
	
	private void innerFindMenuByParentAndUser(Map<String, Object> parent, String userName,String myLocale) throws Exception {
				
		List<Map<String, Object>> menus = new ArrayList<Map<String, Object>>();
		menus = DB.query("select ID,URL,NAME,ENGNAME,TWNAME,"+myLocale+" as TEXT,ISVALID from AUT_MENU where TOPMENUID='"+parent.get("ID")+"'  order by SORT_ORDER ");
		for (Map<String, Object> menu : menus) {
			innerFindMenuByParentAndUser(menu, userName,myLocale);
		}
		if (menus.size()>0){
			parent.put("children", menus);
		}
	}
	
	/**
	 * 新增根节点菜单
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> save(Map<String, Object> map) throws Exception
	{
		String str_MenuData=trimNull(map.get("MenuData"));
		
		String[] array = str_MenuData.split(";");
		
		DB.update("delete from AUT_MENU");
		
		int l=0;
		
		if (!"".equals(str_MenuData) && array.length > 0)
		{
			for (int i = 0; i < array.length; i++)
			{
				Map<String, Object> lhm_map = JSONObject.parse(array[i]);
				
				String str_ID=trimNull(lhm_map.get("ID")); //产生主键UUID；
				
				if ("".equals(str_ID))
				{
					str_ID = UUID.randomUUID().toString();
				}
				
				String str_Name = trimNull(lhm_map.get("NAME"));
	
				String str_ENGNAME = trimNull(lhm_map.get("ENGNAME"));
				
				String str_TWNAME = trimNull(lhm_map.get("TWNAME"));
				
				String str_URL = trimNull(lhm_map.get("URL"));
				
				String str_ISVALID = trimNull(lhm_map.get("ISVALID"));
				
				String str_Status= trimNull(lhm_map.get("__status"));
				
				if("delete".equals(str_Status))
				{
					continue;
				}
				
				l++;
				
				String CREATE_DATE = trimNull(Machine.getSystemDateTime().get("SystemDateTime"));
				
				//新增根节点菜单
				StringBuilder strSql = new StringBuilder();
				strSql.append("insert into AUT_MENU (ID,NAME,ENGNAME,TWNAME,URL,ISVALID,CREATE_DATE,SORT_ORDER) ");
				strSql.append("values('"+str_ID+"','"+str_Name+"','"+str_ENGNAME+"','"+str_TWNAME+"','"+str_URL+"','"+str_ISVALID+"','"+CREATE_DATE+"',"+l+") ");
				
				DB.update(strSql.toString());
	
				
				//如果包含子节点 则新增子节点菜单
				if (lhm_map.get("children") != null)
				{
					this.addChildrenMenu((List<Map<String, Object>>) lhm_map.get("children"), str_ID);
				}
			}
		}
		message.put(Message.MSG_STATUS, "success");
		
		return message;
	}
	
	/**
	 * 新增子节点菜单
	 * @param list
	 * @param ID
	 * @throws Exception 
	 */
	public void addChildrenMenu(List<Map<String, Object>> list,String str_TOPMENUID) throws Exception
	{
		int l=0;
		for(Map<String, Object> map:list)
		{
			
			String str_ID=trimNull(map.get("ID")); //产生主键UUID；
			
			if ("".equals(str_ID)) 
			{
				str_ID = UUID.randomUUID().toString();
			}
			
			String str_Name = trimNull(map.get("NAME"));

			String str_ENGNAME = trimNull(map.get("ENGNAME"));
			
			String str_TWNAME = trimNull(map.get("TWNAME"));
			
			String str_URL = trimNull(map.get("URL"));
			
			String str_ISVALID = trimNull(map.get("ISVALID"));
			
			String str_Status= trimNull(map.get("__status"));
			
			if("delete".equals(str_Status))
			{
				continue;
			}
			
			l++;
			
			String CREATE_DATE = trimNull(Machine.getSystemDateTime().get("SystemDateTime"));
			
			StringBuilder strSql = new StringBuilder();
			strSql.append("insert into AUT_MENU (ID,NAME,ENGNAME,TWNAME,URL,ISVALID,CREATE_DATE,TOPMENUID,SORT_ORDER) ");
			strSql.append("values('"+str_ID+"','"+str_Name+"','"+str_ENGNAME+"','"+str_TWNAME+"',");
			strSql.append("'"+str_URL+"','"+str_ISVALID+"','"+CREATE_DATE+"','"+str_TOPMENUID+"',"+l+") ");
			
			DB.update(strSql.toString());
			
			
			if (map.get("children") != null)
			{
				addChildrenMenu((List<Map<String, Object>>)map.get("children"),str_ID);
			}
		}
	}
	
	public String getLocale(Map<String, Object> map)
	{
		
		String locale= (String)((Session)map.get("SESSION")).getAttribute("locale");
		
		String myLocale="";
		
		if (("en-US").equals(locale)) {
			myLocale = "ENGNAME";
		} else if (("zh_CN").equals(locale)) {
			myLocale = "NAME";
		} else if (("zh_TW").equals(locale)) {
			myLocale = "TWNAME";
		} else {
			myLocale = "NAME";
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
}
