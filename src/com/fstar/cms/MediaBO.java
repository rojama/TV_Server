package com.fstar.cms;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.shiro.codec.Base64;

import com.fstar.sys.DB;
import com.fstar.utility.ImageUtil;
import com.fstar.utility.Machine;

import static com.fstar.utility.Scalr.*;

public class MediaBO {
	static public String TABLE_MEDIA_TYPE = "fs_media_type";
	static public String TABLE_MEDIA = "fs_media";
	static public String TABLE_MEDIA_URL = "fs_media_url";
	
	public Map<String, Object> media_type(Map<String, Object> map) throws Exception
	{
		Map<String, Object> returnmap = new HashMap<String, Object>();
		String action = "";
		if (map.containsKey("ACTION")){
			action = (String) map.get("ACTION");
		}
		System.out.println("inputMAP="+map);
		
		//图片转换
		if (map.containsKey("image")){
			String base64image = String.valueOf(map.get("image"));			
			if(base64image.startsWith("data:image")){  //去除data:image/jpeg;base64,
				base64image = base64image.substring(base64image.indexOf(","));
			}
			byte[] byteImage = Base64.decode(base64image);
			map.put("image", byteImage);
		}
		
		if (map.containsKey("image_sel")){
			String base64image = String.valueOf(map.get("image_sel"));			
			if(base64image.startsWith("data:image")){  //去除data:image/jpeg;base64,
				base64image = base64image.substring(base64image.indexOf(","));
			}
			byte[] byteImage = Base64.decode(base64image);
			map.put("image_sel", byteImage);
		}
	
		
		if (action.equals("add")){
			DB.insert(TABLE_MEDIA_TYPE, map);
		}else if (action.equals("delete")){
			DB.delete(TABLE_MEDIA_TYPE, map);
		}else if (action.equals("modify")){
			DB.update(TABLE_MEDIA_TYPE, map);
		}
		
		//过滤用户所属机构的数据
		String user = (String) map.get("PRINCIPAL");	
		List<Object> con = new ArrayList();
		con.add(user);
		List<Map<String, Object>> ins = DB.query(
				"select a.INS_CODE from aut_institution a , aut_institution_user b where a.INS_ID=b.INS_ID and b.USER_ID=?", con);
		//List<Map<String, Object>> ins = DB.seleteByColumn("aut_institution_user", selectmap);

		Map<String, Object> selectmap = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> insm : ins){
			String insid = (String) insm.get("INS_CODE");
			selectmap.clear();
			selectmap.put("institution", insid);
			List<Map<String, Object>> mains = DB.seleteByColumn(TABLE_MEDIA_TYPE, selectmap);			
			for (Map<String, Object> main : mains){
				String type_id = (String) main.get("type_id");
				//递归获取下面的数据
				rows.add(main);
				addMediaTypeTree(type_id, rows);
			}
		}
		
		returnmap.put("Rows", rows);
		return returnmap;
	}
	
	private void addMediaTypeTree (String super_id, List<Map<String, Object>> rows) throws Exception{
		Map<String, Object> selectmap = new HashMap<String, Object>();		
		selectmap.put("super_id", super_id);
		List<Map<String, Object>> subs = DB.seleteByColumn(TABLE_MEDIA_TYPE, selectmap);
		rows.addAll(subs);
		for (Map<String, Object> sub : subs){
			String type_id = (String) sub.get("type_id");
			this.addMediaTypeTree(type_id, rows);			
		}
	}
	
	public Map<String, Object> media_main(Map<String, Object> map) throws Exception
	{
		Map<String, Object> returnmap = new HashMap<String, Object>();
		String action = "";
		if (map.containsKey("ACTION")){
			action = (String) map.get("ACTION");
		}
		System.out.println("inputMAP="+map);
		
		//图片转换
		if (map.containsKey("image")){
			String base64image = String.valueOf(map.get("image"));			
			if(base64image.startsWith("data:image")){  //去除data:image/jpeg;base64,
				base64image = base64image.substring(base64image.indexOf(","));
			}
			byte[] byteImage = Base64.decode(base64image);
			map.put("image", byteImage);
		}
		
		if (map.containsKey("image_sel")){
			String base64image = String.valueOf(map.get("image_sel"));			
			if(base64image.startsWith("data:image")){  //去除data:image/jpeg;base64,
				base64image = base64image.substring(base64image.indexOf(","));
			}
			byte[] byteImage = Base64.decode(base64image);
			map.put("image_sel", byteImage);
		}
		
		if (map.containsKey("image_bg")){
			String base64image = String.valueOf(map.get("image_bg"));			
			if(base64image.startsWith("data:image")){  //去除data:image/jpeg;base64,
				base64image = base64image.substring(base64image.indexOf(","));
			}
			byte[] byteImage = Base64.decode(base64image);
			map.put("image_bg", byteImage);
		}
		
		//设置为根节点
		map.put("super_id", "");
		
		if (action.equals("add")){
			DB.insert(TABLE_MEDIA_TYPE, map);
		}else if (action.equals("delete")){
			DB.delete(TABLE_MEDIA_TYPE, map);
		}else if (action.equals("modify")){
			DB.update(TABLE_MEDIA_TYPE, map);
		}
		
		Map<String, Object> selectmap = new HashMap<String, Object>();
		selectmap.put("super_id", "");
		returnmap.put("Rows", DB.seleteByColumn(TABLE_MEDIA_TYPE, selectmap));
		return returnmap;
	}
	
	public Map<String, Object> media(Map<String, Object> map) throws Exception
	{		
		Map<String, Object> returnmap = new HashMap<String, Object>();
		String action = "";
		if (map.containsKey("ACTION")){
			action = (String) map.get("ACTION");
		}
		System.out.println("inputMAP="+map);
		map.put("maintain_time", Machine.getDateTime());
		
		//图片转换
		if (map.containsKey("image")){
			String base64image = String.valueOf(map.get("image"));			
			if(base64image.startsWith("data:image")){  //去除data:image/jpeg;base64,
				base64image = base64image.substring(base64image.indexOf(","));
			}
			byte[] byteImage = Base64.decode(base64image);
			
			//压缩图片
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(byteImage)); 			
			// Create quickly, then smooth and brighten it.
			BufferedImage imgout = resize(image, Method.AUTOMATIC, 350);			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			boolean flag = ImageIO.write(imgout, "jpg", out);
			byte[] b = out.toByteArray();
			
			map.put("image", b);
		}
		
		if (action.equals("add")){
			map.remove("media_id");
			DB.insert(TABLE_MEDIA, map);
		}else if (action.equals("delete")){
			DB.delete(TABLE_MEDIA, map);
		}else if (action.equals("modify")){
			DB.update(TABLE_MEDIA, map);
		}else if (action.equals("recommend")){
			Map<String, Object> updatemap = new HashMap<String, Object>();
			updatemap.put("media_id", map.get("media_id"));
			updatemap.put("recommend", "Y");
			DB.update(TABLE_MEDIA, updatemap);
		}else if (action.equals("unrecommend")){
			Map<String, Object> updatemap = new HashMap<String, Object>();
			updatemap.put("media_id", map.get("media_id"));
			updatemap.put("recommend", "N");
			DB.update(TABLE_MEDIA, updatemap);
		}else if (action.equals("saveUrl")){
			String urlData = (String) map.get("urldata");
			JSONArray json = new JSONArray(urlData);
			
			Map<String, Object> updatemap = new HashMap<String, Object>();
			updatemap.put("media_id", map.get("media_id"));
			DB.deleteMulti(TABLE_MEDIA_URL, updatemap);
			
			for (int i=0; i<json.length(); i++){
				JSONObject jo = json.getJSONObject(i);
				Map<String, Object> insertmap = new HashMap<String, Object>();
				Iterator it = jo.keys();
				while(it.hasNext()){
					String key = (String) it.next();
					insertmap.put(key, jo.get(key));
				}
				DB.insert(TABLE_MEDIA_URL, insertmap);
			}			
		}
		
		if (action.equals("getMediaType")){
			//获取用户拥有的类型树
			List<Map<String, Object>> mediatype = (List<Map<String, Object>>) this.media_type(map).get("Rows");
			
			Map<String, Object> e = new HashMap<String, Object>();
			e.put("type_id", "recommend");
			e.put("type_name", "推荐视频");
			e.put("super_id", "");
			mediatype.add(0, e);
			returnmap.put("Rows", mediatype);
		}else if (action.equals("getUrlData")){
			Map<String, Object> selectMap = new HashMap<String, Object>();
			selectMap.put("media_id", map.get("media_id"));	
			returnmap.put("Rows", DB.seleteByColumn(TABLE_MEDIA_URL, selectMap));
		}else{
			Map<String, Object> selectMap = new HashMap<String, Object>();
			if ("recommend".equals(map.get("type_id"))){
				selectMap.put("recommend", "Y");
			}else{
				selectMap.put("type_id", map.get("type_id"));
			}			
			returnmap.put("Rows", DB.seleteByColumn(TABLE_MEDIA, selectMap));
		}
		
		return returnmap;
	}
}
