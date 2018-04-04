package com.fstar.help;

import java.util.List;
import java.util.Map;

import com.fstar.sys.DB;

public class TempMenuHelper {
	
	public String trimNull(Object str_valueObject) {
		String str_value = (String) str_valueObject;
		if (str_value == null) {
			return "";
		} else {
			return str_value.trim();
		}
	}
	
	public List<Map<String, Object>> innerFindTempData(String ROLE_ID) throws Exception
	{
		List<Map<String, Object>> list=DB.query("select *  from AUT_TEMPMENU where TOPMENUID='' ");
		
		for(Map<String, Object> map:list)
		{
			String MENU_ID=trimNull(map.get("ID"));
			List<Map<String, Object>> lhm_data=getOpeID(ROLE_ID, MENU_ID);
			
			if (lhm_data.size() > 0)
			{
				for(Map<String, Object> lhm_ope:lhm_data)
				{
					String Key = trimNull(lhm_ope.get("OPE_ID"));
					
					if (!"".equals(Key))
					{
						map.put(Key, "true");
					}
				}
			}
			
			innerFindTempChildrenData(map,ROLE_ID);
		}
		
		return list;
	}
	
	public void innerFindTempChildrenData(Map<String, Object> parent,String ROLE_ID) throws Exception
	{
		List<Map<String, Object>> list=DB.query("select *  from AUT_TEMPMENU where TOPMENUID='"+parent.get("ID")+"' ");
		
		for(Map<String, Object> map:list)
		{
			String MENU_ID=trimNull(map.get("ID"));
			List<Map<String, Object>> lhm_data=getOpeID(ROLE_ID, MENU_ID);
			
			if (lhm_data.size() > 0)
			{
				for(Map<String, Object> lhm_ope:lhm_data)
				{
					String Key = trimNull(lhm_ope.get("OPE_ID"));
					
					if (!"".equals(Key))
					{
						map.put(Key, "true");
					}
				}
			}
			
			innerFindTempChildrenData(map,ROLE_ID);
		}
		
		if(list.size()>0)
		{
			parent.put("children", list);
		}
	}
	
	/**
	 * 获取角色功能的操作权限
	 * @param ROLE_ID
	 * @param MENU_ID
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getOpeID(String ROLE_ID,String MENU_ID) throws Exception
	{
		List<Map<String, Object>>lhm_data=DB.query("select * from AUT_ROLE_MENU_OPERATION where ROLE_ID='"+ROLE_ID+"' and MENU_ID='"+MENU_ID+"'");
		
		return lhm_data;
	}
}
