package com.fstar.sys;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.fstar.sys.CommonMethodFilter;


/**
 * Servlet implementation class CommonMethodServlet
 */
@WebServlet("/cm")
public class CommonMethodServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@EJB 
	private CommonMethodFilter commonMethodFilter;
       
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException{
		try {
			Map<String, Object> userInput = new HashMap<String, Object>();			
			Enumeration<String> e = req.getParameterNames();
			while (e.hasMoreElements()) {
				String name = (String) e.nextElement();
				userInput.put(name, req.getParameter(name));
			}
	        
			Subject subject = SecurityUtils.getSubject(); 
			Session session= subject.getSession();
			Object principal = subject.getPrincipal();
			userInput.put("SUBJECT", subject);
			userInput.put("SESSION", session);
			userInput.put("PRINCIPAL", principal);
			Object result = commonMethodFilter.commonMethod(userInput);		
			if (result instanceof byte[]){
				res.setContentType("image/jpeg");
				ServletOutputStream sos = res.getOutputStream();
				sos.write((byte[]) result);
				sos.flush();
				sos.close();
			}else{
				res.setContentType("application/json;charset=utf-8");
				PrintWriter out = res.getWriter();			
				out.print(JsonUtil.object2json(result));
				out.flush();
				out.close();
			}			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

}
