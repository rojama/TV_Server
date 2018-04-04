package com.fstar.help;

import java.util.List;
import java.util.Map;

import com.fstar.sys.DB;

public class OperationHelper {
	
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
			myLocale = "OPE_ENGNAME";
		} else if (locale.startsWith("zh_CN")) {
			myLocale = "OPE_NAME";
		} else if (("zh_TW").equals(locale)) {
			myLocale = "OPE_TWNAME";
		} else {
			myLocale = "OPE_NAME";
		}
		
		return myLocale;
	}
	
	/**
	 * 查询所有操作
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> findOperationData(String locale) throws Exception
	{
		String myLocale=getLocale(locale);
		List<Map<String, Object>> list = DB.query("select *,"+myLocale+" as NAME from AUT_OPERATION ");
		
		return list;
	}
	
	public List<Map<String, Object>> findOperationData(Map<String, Object> map) throws Exception
	{
		 
		List<Map<String, Object>> list = DB.query("select * from AUT_OPERATION ");
		
		return list;
	}
}
