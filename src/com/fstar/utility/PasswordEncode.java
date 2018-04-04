package com.fstar.utility;

import java.security.MessageDigest;

import org.apache.shiro.codec.Hex;

public class PasswordEncode {
	
	/**
	 * 使用SHA-256 加密
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static String EncodeSHA256(String password) throws Exception
	{
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		
		byte[] by = sha256.digest(password.getBytes("GBK"));
		
		return  Hex.encodeToString(by);
	}
	

	public static void main(String[] args) {
		try {
			System.out.println(EncodeSHA256("123"));
			
		} catch (Exception e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
	}
}
