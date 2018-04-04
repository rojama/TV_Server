package com.fstar.cms;

import java.util.HashMap;
import java.util.Map;

import com.fstar.sys.DB;

public class TitleBO {

	static public String TABLE_TITLE = "fs_title";
	
	public Map<String, Object> jspServer(Map<String, Object> map) throws Exception
	{
		Map<String, Object> returnmap = new HashMap<String, Object>();
		String action = "";
		if (map.containsKey("ACTION")){
			action = (String) map.get("ACTION");
		}
		System.out.println("inputMAP="+map);
		
		if (action.equals("add")){
			DB.insert(TABLE_TITLE, map);
		}else if (action.equals("delete")){
			DB.delete(TABLE_TITLE, map);
		}else if (action.equals("modify")){
			DB.update(TABLE_TITLE, map);
		}
		returnmap.put("Rows", DB.selete(TABLE_TITLE));
		
		return returnmap;
	}
}
