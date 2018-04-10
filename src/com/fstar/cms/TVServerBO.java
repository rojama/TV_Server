package com.fstar.cms;

import java.sql.Blob;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fstar.cms.modle.MediaType;
import com.fstar.cms.modle.VideoTypeInfo;
import com.fstar.sys.DB;
import com.fstar.utility.Machine;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TVServerBO {
	public Map<String, Object> getMediaType(Map<String, Object> map)
			throws Exception {
		Map<String, Object> returnmap = new HashMap<String, Object>();
		returnmap.put("DATA", DB.selete(MediaBO.TABLE_MEDIA_TYPE));
		return returnmap;
	}

	// 视频分类
	public Map<String, Object> parseTopCate(Map<String, Object> map)
			throws Exception {
		Map<String, Object> returnmap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("super_id", "");
		List<Map<String, Object>> re = DB.seleteByColumn(MediaBO.TABLE_MEDIA_TYPE, data, "type_id,type_name,component", "order by order_no");
		TVServerImageBO.changeImageToURL(re, MediaBO.TABLE_MEDIA_TYPE);
		returnmap.put("type", re);
		return returnmap;
	}
	
	// 视频子类
	public Map<String, Object> parseSubCate(Map<String, Object> map)
			throws Exception {
		Map<String, Object> returnmap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("super_id", map.get("super_id"));
		List<Map<String, Object>> re = DB.seleteByColumn(MediaBO.TABLE_MEDIA_TYPE, data, "type_id,type_name", "order by order_no");
		TVServerImageBO.changeImageToURL(re, MediaBO.TABLE_MEDIA_TYPE);
		returnmap.put("subtype", re);
		return returnmap;
	}
	
	// 视频列表
	public Map<String, Object> parseMediaList(Map<String, Object> map)
			throws Exception {
		Map<String, Object> returnmap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("type_id", map.get("type_id"));
		List<Map<String, Object>> re = DB.seleteByColumn(MediaBO.TABLE_MEDIA, data, "media_id,media_name", "order by order_no ,media_id desc");
		TVServerImageBO.changeImageToURL(re, MediaBO.TABLE_MEDIA);
		returnmap.put("mediaList", re);
		return returnmap;
	}
	
	// 视频子类背景
	public Map<String, Object> getDetailBg(Map<String, Object> map)
			throws Exception {
		Map<String, Object> returnmap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("type_id", map.get("type_id"));
		List<Map<String, Object>> re = DB.seleteByColumn(MediaBO.TABLE_MEDIA_TYPE, data, "type_id,type_name", "order by order_no");
		TVServerImageBO.changeImageToURL(re, MediaBO.TABLE_MEDIA_TYPE);
		returnmap.put("subbg", re);
		return returnmap;
	}
	
	// 视频信息
	public Map<String, Object> parseMediaInfo(Map<String, Object> map)
			throws Exception {
		Map<String, Object> returnmap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("media_id", map.get("media_id"));
		List<Map<String, Object>> re = DB.seleteByColumn(MediaBO.TABLE_MEDIA, data);
		TVServerImageBO.changeImageToURL(re, MediaBO.TABLE_MEDIA);
		returnmap.put("mediaInfo", re);
		
		List<Map<String, Object>> name = DB.seleteByColumn(MediaBO.TABLE_MEDIA_URL, data);
		returnmap.put("mediaUrl", name);
		return returnmap;
	}
	
	// 视频地址
	public Map<String, Object> parseMediaUrl(Map<String, Object> map)
			throws Exception {
		Map<String, Object> returnmap = new HashMap<String, Object>();
//		Map<String, Object> data = new HashMap<String, Object>();
//		data.put("media_id", map.get("media_id"));
		List<Object> data = new ArrayList<Object>();
		data.add(map.get("media_id"));
		data.add(map.get("series_no"));
		List<Map<String, Object>> re = DB.query("select * from fs_media_url a , fs_server b where a.server_id=b.server_id and a.media_id=? and a.series_no=?", data);
//		List<Map<String, Object>> re = DB.seleteByColumn(MediaBO.TABLE_MEDIA_URL, data);
		returnmap.put("mediaUrl", re);
		return returnmap;
	}

	// 推荐视频
	public Map<String, Object> parseRecommend(Map<String, Object> map)
			throws Exception {
		Map<String, Object> returnmap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("recommend", "Y");
		List<Map<String, Object>> re = DB.seleteByColumn(MediaBO.TABLE_MEDIA, data);
		TVServerImageBO.changeImageToURL(re, MediaBO.TABLE_MEDIA);
		returnmap.put("video", re);
		return returnmap;
	}
	
	//板块标题
	public Map<String, Object> getTitle(Map<String, Object> map)
			throws Exception {
		Map<String, Object> returnmap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("block_id", map.get("block_id"));
		List<Map<String, Object>> re = DB.seleteByColumn(TitleBO.TABLE_TITLE, data);
		returnmap.put("title", re);
		return returnmap;
	}
	
	//验证设备
	public Map<String, Object> parseDeviceId(Map<String, Object> map)
			throws Exception {
		Map<String, Object> returnmap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		String deviceId = (String) map.get("DeviceId");
		data.put("device_id", deviceId);
		data.put("time", Machine.getDateTime());
		DB.insert("fs_access_history", data);
		
		returnmap.put("ok", true);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String[] ids = deviceId.split("-");
		String sqlOther = "and (mac='"+checkID(ids[0])+"' or imei='"+checkID(ids[1])+"' or serial='"+checkID(ids[2])+"')";		
		List<Map<String, Object>> result = DB.seleteByColumn(ServerBO.TABLE_TERMINAL, data, "validity", sqlOther);
		for (Map<String, Object> d :result){
			String validity = (String) d.get("validity");
			if (!validity.isEmpty()){
				if (format.parse(validity).before(new Date())){
					returnmap.put("ok", false);
				}
			}
		}
		return returnmap;
	}
	
	// 查询视频
	public Map<String, Object> searchMedia(Map<String, Object> map)
			throws Exception {
		Map<String, Object> returnmap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> re = DB.seleteByColumn(MediaBO.TABLE_MEDIA, data, "media_name,media_id", "and GET_FIRST_PINYIN(media_name) like '%"+map.get("key_word")+"%' order by play_times desc LIMIT 50");
		TVServerImageBO.changeImageToURL(re, MediaBO.TABLE_MEDIA);
		returnmap.put("mediaList", re);
		return returnmap;
	}
	
	//设置
	public Map<String, Object> getSetting(Map<String, Object> map)
			throws Exception {
		Map<String, Object> returnmap = new HashMap<String, Object>();
		List<Map<String, Object>> list = DB.selete("fs_setting");
		for (Map<String, Object> one : list){
			returnmap.put((String) one.get("set_id"), one.get("set_value"));
		}
		return returnmap;
	}
	
	private String checkID(String id){
		if (id == null || id.isEmpty() || id.equals("unknown")){
			return "INVALUE";
		}else{
			return id;
		}
	}
	
}
