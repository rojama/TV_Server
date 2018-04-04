package com.fstar.activiti;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;

import com.fstar.sys.DB;

public class ActTask {
	public Map<String, Object> server(Map<String, Object> map) throws Exception
	{
		Map<String, Object> returnmap = new HashMap<String, Object>();
		String action = "";
		if (map.containsKey("ACTION")){
			action = (String) map.get("ACTION");
		}
		System.out.println("inputMAP="+map);
		
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    TaskService taskService = processEngine.getTaskService();
	    //List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();
	    List<Task> tasks = taskService.createTaskQuery().taskAssignee("Kermit").list();
	    
	    List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

		for (Task task : tasks) {
			Map<String, Object> taskmap = new HashMap<String, Object>();
			taskmap.put("Id", task.getId());
			taskmap.put("Name", task.getName());
			taskmap.put("Owner", task.getOwner());
			Date createTime = task.getCreateTime();
			if (createTime != null){
				taskmap.put("CreateTime", createTime.toString());
			}
			Date dueDate = task.getDueDate();
			if (dueDate != null){
				taskmap.put("DueDate", dueDate.toString());
			}
			rows.add(taskmap);
		}
	    
	    returnmap.put("Rows",rows);
		return returnmap;
	}
}
