package com.fstar.cms;

import java.sql.Blob;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fstar.cms.modle.MediaType;
import com.fstar.cms.modle.VideoTypeInfo;
import com.fstar.sys.DB;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TVServerImageBO {

	// 获取图片
	public byte[] getImage(Map<String, Object> map) throws Exception {
		String table = (String) map.get("TABLE_NAME");
		String col = (String) map.get("IMAGE_COLUMN");
		List<Map<String, Object>> data = DB.seleteByKey(table, map);
		if (data.size() >= 1) {
			byte[] bytes = (byte[]) data.get(0).get(col);
			return bytes;
		} else {
			return null;
		}
	}

//	static String processBO = "com.fstar.cms.TVServerImageBO";
//	static String processMETHOD = "getImage";
//	static String url = "http://192.168.1.102:8080/FStarWeb/cm?ProcessMETHOD=" + processMETHOD + "&ProcessBO="
//			+ processBO;

	public static void changeImageToURL(List<Map<String, Object>> data, String table) {
		String returnURL = "";
		Map<String, Integer> col = DB.columnLabelMap.get(table);
		List<String> bytecol = new ArrayList<String>();
		List<String> keys = DB.keyLabelMap.get(table);
		for (String c : col.keySet()) {
			if (col.get(c) == Types.LONGVARBINARY
					|| col.get(c) == Types.VARBINARY
					|| col.get(c) == Types.BINARY) {
				bytecol.add(c);
			}
		}
		for (Map<String, Object> sub : data) {
			for (String cul : bytecol) {
//				returnURL = url + "&TABLE_NAME=" + table;
				returnURL = "&TABLE_NAME=" + table;
				returnURL += "&IMAGE_COLUMN=" + cul;
				for (String key : keys) {
					returnURL += "&" + key + "=" + sub.get(key);
				}
				sub.put(cul, returnURL);
			}
		}
	}
}
