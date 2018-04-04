package com.fstar.sys;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;

/**
 * Session Bean implementation class CommonMethodFilter
 */
@Stateless
@LocalBean
@TransactionManagement
public class CommonMethodFilter {
	@Resource
	private SessionContext ctx;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Object commonMethod(Map<String, Object> tranMsg) {
		List<Object> returnData = new ArrayList<Object>();
		boolean isDispense = false;
		try {
			List<Map<String, Object>> v_AllCondition = new ArrayList<Map<String, Object>>();
			if (tranMsg.containsKey("DispenseBO")
					&& tranMsg.containsKey("DispenseMETHOD")
					&& !((String) tranMsg.get("DispenseBO")).isEmpty()
					&& !((String) tranMsg.get("DispenseMETHOD")).isEmpty()) {
				isDispense = true;
				String DispenseBO = (String) tranMsg.get("DispenseBO");
				String DispenseMETHOD = (String) tranMsg.get("DispenseMETHOD");
				Class<?> o_class = Class.forName(DispenseBO);
				Class<?>[] parameterTypes = new Class[1];
				parameterTypes[0] = Map.class;
				Method method = o_class.getMethod(DispenseMETHOD,
						parameterTypes);
				Object[] args = new Object[1];
				Map<String, Object> map = new HashMap<String, Object>();
				map.putAll(tranMsg);
				args[0] = map;
				v_AllCondition = (List<Map<String, Object>>) method.invoke(
						o_class.newInstance(), args);
			} else {
				v_AllCondition.add(new HashMap<String, Object>(tranMsg));
			}

			for (Map<String, Object> map : v_AllCondition) {
				Object onereturnData = processCommonMethod(map);
				returnData.add(onereturnData);
			}

			if (!isDispense) {
				return returnData.get(0);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			Map<String, Object> errdata = new HashMap<String, Object>();
			errdata.put(Message.ERR_STATUS, this.getMessage(exc));
			return errdata;
		}

		return returnData;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Object processCommonMethod(Map<String, Object> map) {
		Object returnData = null;
		try {
			if (map.containsKey("ProcessBO")
					&& map.containsKey("ProcessMETHOD")
					&& !((String) map.get("ProcessBO")).isEmpty()
					&& !((String) map.get("ProcessMETHOD")).isEmpty()) {
				String ProcessBO = (String) map.get("ProcessBO");
				String ProcessMETHOD = (String) map.get("ProcessMETHOD");
				Class<?> o_class = Class.forName(ProcessBO);
				Class<?>[] parameterTypes = new Class[1];
				parameterTypes[0] = Map.class;
				Method method = o_class
						.getMethod(ProcessMETHOD, parameterTypes);
				Object[] args = new Object[1];
				args[0] = map;
				returnData = method.invoke(
						o_class.newInstance(), args);
				if (returnData == null) {
					returnData = new HashMap<String, Object>();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ctx.setRollbackOnly();
			returnData = new HashMap<String, Object>();
			((HashMap<String, Object>)returnData).put(Message.ERR_STATUS, this.getMessage(e));
		}
		return returnData;
	}

	private String getMessage(Exception e) {
		String err = "";
		if (e.getCause() != null && e.getCause().getMessage() != null) {
			err = e.getCause().getMessage().replace('"', ' ')
					.replace('\'', ' ');
		} else if (e.getMessage() != null) {
			err = e.getMessage().replace('"', ' ').replace('\'', ' ');
		}
		return err;
	}
}
