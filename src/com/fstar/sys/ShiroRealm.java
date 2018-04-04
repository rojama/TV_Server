package com.fstar.sys;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.*;
import org.apache.shiro.realm.*;
import org.apache.shiro.subject.*;
import org.apache.shiro.util.ByteSource;

import com.fstar.utility.PasswordEncode;

public class ShiroRealm extends AuthorizingRealm {

	/**
	 * 授权，给用户分配角色或者权限资源
	 */
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		try {
			String username = (String) principals.getPrimaryPrincipal();

			if (username != null) {			
				SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();		

				List<Object> con = new ArrayList();
				con.add(username);
				con.add(username);
				List<Map<String, Object>> data = DB.query(
						"select ROLE_ID from aut_user_role where aut_user_role.USER_ID=? UNION " +
						"select ROLE_ID from aut_institution_user a, aut_institution_role b where a.INS_ID=b.INS_ID and a.USER_ID=?", con);
				
				HashMap<String, Object> key = new HashMap<String, Object>();
				
//				key.put("USER_ID", username);
//				List<Map<String, Object>> data = DB.seleteByKey("aut_user_role", key);			
				if (!data.isEmpty()) {
					for (Map<String, Object> each : data) {
						String ROLE_ID = (String)each.get("ROLE_ID");
						info.addRole(ROLE_ID);
						
						key.put("ROLE_ID", ROLE_ID);
						List<Map<String, Object>> autdata = DB.seleteByKey("aut_role_menu_operation", key);	
						List<String> permissions = new ArrayList<String>();
						for (Map<String, Object> auteach : autdata){
							permissions.add(auteach.get("MENU_ID")+":"+auteach.get("OPE_ID"));
						}

						info.addStringPermissions(permissions);
					}
					return info;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 身份验证
	 */
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException{
		
			//token中储存着输入的用户名和密码
			UsernamePasswordToken upToken = (UsernamePasswordToken)token;
			//获得用户名与密码
			String username = upToken.getUsername();
			String password = String.valueOf(upToken.getPassword());
			
			boolean isAuth = false;
			
			try {
				if (!"".equals(username)){
					HashMap<String, Object> key = new HashMap<String, Object>();
					key.put("USER_ID", username);
					List<Map<String, Object>> data = DB.seleteByKey("aut_user", key);
					if (data.size() == 1){
						if ((PasswordEncode.EncodeSHA256(password)).equals(data.get(0).get("PASSWORD"))){
							isAuth = true;					
						}else{
							throw new IncorrectCredentialsException();	
						}
					}
				}
			} catch (IncorrectCredentialsException e) {
				throw e;
			} catch (Exception e) {
				throw new UnknownAccountException();
			}
			
			if (isAuth)
			{
				System.out.println(username+" login.");
				SimpleAuthenticationInfo info=new SimpleAuthenticationInfo (upToken.getPrincipal(), upToken.getPassword(), upToken.getUsername());
				info.setCredentialsSalt(ByteSource.Util.bytes(username));
				return info;
			}else{
				return null;
			}
	}
}