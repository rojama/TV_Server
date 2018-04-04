package com.fstar.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取系统日期与时间
 * 
 * @author HuangKai
 * 
 */
public class Machine {
	//当前日期 yyyymmdd
	public static final String SYSTEM_DATE = "SystemDate";
	//当前时间 hhmmss
	public static final String SYSTEM_TIME = "SystemTime";
	//当前具体时间  yyyy-mm-dd hh:mm:ss
	public static final String SYSTEM_DATETIME = "SystemDateTime";
	
	public static SimpleDateFormat formatdate=new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat formattime=new SimpleDateFormat("HHmmss");
	public static SimpleDateFormat formatdatetime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String getDate(){
		return getSystemDateTime().get(SYSTEM_DATE);
	}
	public static String getTime(){
		return getSystemDateTime().get(SYSTEM_TIME);
	}
	public static String getDateTime(){
		return getSystemDateTime().get(SYSTEM_DATETIME);
	}
	
	/**
	 * 获取系统日期与时间
	 * 
	 * @return SystemDate 系统日期，SystemTime 系统时间
	 * 
	 */
	public static Map<String,String> getSystemDateTime() {
		String str_SystemDate, str_SystemTime, str_month, str_date;
		Map<String,String> htl_SystemDateTime = new HashMap<String,String>();

		Calendar obj_Calendar = Calendar.getInstance();

		/* 取得系統日期 格式: yyyymmdd */
		if ((obj_Calendar.get(Calendar.MONTH) + 1) < 10) {
			str_month = "0" + (obj_Calendar.get(Calendar.MONTH) + 1);
		} else {
			str_month = String.valueOf(obj_Calendar.get(Calendar.MONTH) + 1);
		}

		if (obj_Calendar.get(Calendar.DATE) < 10) {
			str_date = "0" + obj_Calendar.get(Calendar.DATE);
		} else {
			str_date = String.valueOf(obj_Calendar.get(Calendar.DATE));
		}

		str_SystemDate = obj_Calendar.get(Calendar.YEAR) + str_month + str_date;

		/* 取得系統時間 格式(24小時進制): hhmmss */
		String str_hour, str_minute, str_second;
		if (obj_Calendar.get(Calendar.HOUR) < 10
				&& obj_Calendar.get(Calendar.AM_PM) == Calendar.AM) {
			str_hour = "0" + obj_Calendar.get(Calendar.HOUR);
		} else if (obj_Calendar.get(Calendar.AM_PM) == Calendar.PM) {
			str_hour = String.valueOf(obj_Calendar.get(Calendar.HOUR) + 12);
		} else {
			str_hour = String.valueOf(obj_Calendar.get(Calendar.HOUR));
		}

		if (obj_Calendar.get(Calendar.MINUTE) < 10) {
			str_minute = "0" + obj_Calendar.get(Calendar.MINUTE);
		} else {
			str_minute = String.valueOf(obj_Calendar.get(Calendar.MINUTE));
		}

		if (obj_Calendar.get(Calendar.SECOND) < 10) {
			str_second = "0" + obj_Calendar.get(Calendar.SECOND);
		} else {
			str_second = String.valueOf(obj_Calendar.get(Calendar.SECOND));
		}

		str_SystemTime = str_hour + str_minute + str_second;

		htl_SystemDateTime.put(SYSTEM_DATE, str_SystemDate);
		htl_SystemDateTime.put(SYSTEM_TIME, str_SystemTime);

		String SystemDateTime = obj_Calendar.get(Calendar.YEAR) + "-"
				+ str_month + "-" + str_date + " ";

		SystemDateTime += str_hour + ":" + str_minute + ":" + str_second;

		htl_SystemDateTime.put(SYSTEM_DATETIME, SystemDateTime);

		return htl_SystemDateTime;
	}
	public static void main(String[] args){
		System.out.println(getDate());
		System.out.println(getTime());
		System.out.println(getDateTime());
	}
}
