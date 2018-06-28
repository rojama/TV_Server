package com.fstar.cms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fstar.sys.DB;

public class ServerBO {
	
	
	public Map<String, Object> server(Map<String, Object> map) throws Exception
	{
		Map<String, Object> returnmap = new HashMap<String, Object>();
		String action = "";
		if (map.containsKey("ACTION")){
			action = (String) map.get("ACTION");
		}
		System.out.println("inputMAP="+map);
		
		if (action.equals("add")){
			String server_id = String.valueOf(map.get("server_id"));
			int server_type = Integer.parseInt((String)map.get("server_type"));
			String server_name = String.valueOf(map.get("server_name"));
			String ip = String.valueOf(map.get("ip"));
			String url_prefix = String.valueOf(map.get("url_prefix"));
			DB.update("INSERT INTO fs_server (server_id, server_type, server_name, ip, url_prefix) "
					+ "VALUES ('"+server_id+"', "+server_type+", '"+server_name+"', '"+ip+"', '"+url_prefix+"')");
		}else if (action.equals("delete")){
			String server_id = String.valueOf(map.get("server_id"));
			DB.update("DELETE FROM fs_server where server_id = '"+server_id+"'");
		}else if (action.equals("modify")){
			String server_id = String.valueOf(map.get("server_id"));
			int server_type = Integer.parseInt((String)map.get("server_type"));
			String server_name = String.valueOf(map.get("server_name"));
			String ip = String.valueOf(map.get("ip"));
			String url_prefix = String.valueOf(map.get("url_prefix"));
			DB.update("UPDATE fs_server SET server_type = "+server_type+",server_name = '"+server_name
					+"',ip = '"+ip+"',url_prefix = '"+url_prefix+"' where server_id = '"+server_id+"'");
		}
		returnmap.put("Rows", DB.query("select * from fs_server"));
		return returnmap;
	}
	
	
	static public String TABLE_TERMINAL = "fs_terminal";
	
	public Map<String, Object> terminal(Map<String, Object> map) throws Exception
	{
		Map<String, Object> returnmap = new HashMap<String, Object>();
		String action = "";
		if (map.containsKey("ACTION")){
			action = (String) map.get("ACTION");
		}
		System.out.println("inputMAP="+map);
		
		//mac imei serial 必需输入一项
		if ((action.equals("add")||action.equals("modify"))
				&& ((String)map.get("mac")).isEmpty()
				&& ((String)map.get("imei")).isEmpty()
				&& ((String)map.get("serial")).isEmpty()){
			throw new Exception("(终端MAC 终端IMEI 终端SN) 必需至少输入其中一项");
		}
		
		if (action.equals("add")){
			map.remove("device_info");
			DB.insert(TABLE_TERMINAL, map);
		}else if (action.equals("delete")){
			DB.delete(TABLE_TERMINAL, map);
		}else if (action.equals("modify")){
			DB.update(TABLE_TERMINAL, map);
		}
		returnmap.put("Rows", DB.selete(TABLE_TERMINAL));
		
		return returnmap;
	}
	
	static public String TABLE_SETTING = "fs_setting";
	
	public Map<String, Object> setting(Map<String, Object> map) throws Exception
	{
		Map<String, Object> returnmap = new HashMap<String, Object>();
		String action = "";
		if (map.containsKey("ACTION")){
			action = (String) map.get("ACTION");
		}
		System.out.println("inputMAP="+map);
		
		String[] keys = {"ClearCacheOnStart"};
		
		if (action.equals("modify")){
			Map<String, Object> mapupdate = new HashMap<String, Object>();
			for (String key:keys){
				mapupdate.put("set_id", key);
				mapupdate.put("set_value", map.get(key));
				DB.update(TABLE_SETTING, mapupdate);
			}
		}
		
		List<Map<String, Object>> alls = DB.selete(TABLE_SETTING);
		for (Map<String, Object> one : alls){
			returnmap.put((String) one.get("set_id"), one.get("set_value"));
		}
		
		return returnmap;
	}
}
