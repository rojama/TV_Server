package com.fstar.sys;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理helper请求，必须参数为ProcessHelper、ProcessMethod 返回JSON串
 */
@WebServlet("/qs")
public class QueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public QueryServlet() {
		super();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		Map<String, Object> args = new HashMap<String, Object>();
		Enumeration<String> e = req.getParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			args.put(name, req.getParameter(name));
		}
		String helperName = ((String) args.get(WebConstant.PROCESSHELPER)).trim();
		String helperMethod = ((String) args.get(WebConstant.PROCESSMETHOD)).trim();
		if (helperName == null || helperMethod == null)
			return;
		try {
			Class<?> o_class = Class.forName(helperName);
			Method method = o_class.getMethod(helperMethod, Map.class);
			Object result = (List<Map<String, Object>>) method.invoke(
					o_class.newInstance(), args);
			res.setContentType("application/json;charset=utf-8");
			PrintWriter out = res.getWriter();
			out.print(JsonUtil.object2json(result));
			out.flush();
			out.close();
		} catch (Exception exc) {
			throw new ServletException(exc);
		}
	}

}
