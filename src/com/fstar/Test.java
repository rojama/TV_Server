package com.fstar;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.*;

import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fstar.sys.DB;
import com.fstar.sys.Message;

import javax.ejb.Stateless;

@Stateless
public class Test {	
	private static Logger log = LoggerFactory.getLogger(Test.class);
	
	public Map<String,Object> test (Map<String,Object> map) throws Exception{
		Map<String,Object> returndata = new HashMap<String,Object>();
		System.out.println(map);
		
//		EntityManager em = (EntityManager) map.get("EntityManager");
//		
//		Query query = em.createNativeQuery("select * from DepBizInfo");
//		List<Object[]> objecArraytList = query.getResultList();
//        for(int i=0;i<objecArraytList.size();i++) {
//            Object[] obj = objecArraytList.get(i);
//            System.out.println(ToStringBuilder.reflectionToString(obj, ToStringStyle.MULTI_LINE_STYLE));
//            returndata.put("DepNo_"+i, obj[0]);
//        }
//        
//        query = em.createNativeQuery("INSERT INTO AreaInfo (Area, ContentsDesc, UpArea, MaintainUserID, SystemDate, SystemTime, ModifyType," +
//        		" ApproveID, ApproveDate, ApproveTime, ApproveStatus) VALUES ('Aaa', 'test     ', '   ', 'YFAN  ', '20041110', '153638', 'A', 'LSQIN ', '20041110', '153638', 'Y')");
//        query.executeUpdate();

//		 DB.update("INSERT INTO AreaInfo (Area, ContentsDesc, UpArea, MaintainUserID, SystemDate, SystemTime, ModifyType," +
//	        		" ApproveID, ApproveDate, ApproveTime, ApproveStatus) VALUES ('Aa2', 'test     ', '   ', 'YFAN  ', '20041110', '153638', 'A', 'LSQIN ', '20041110', '153638', 'Y')");
//	     DB.update("INSERT INTO AreaInfo (Area, ContentsDesc, UpArea, MaintainUserID, SystemDate, SystemTime, ModifyType," +
//        		" ApproveID, ApproveDate, ApproveTime, ApproveStatus) VALUES ('Aa3', 'test     ', '   ', 'YFAN  ', '20041110', '153638', 'A', 'LSQIN ', '20041110', '153638', 'Y')");
//        
        //throw new Exception("sss");
	     
		 ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	     if (processEngine == null){
	    	 System.out.println("ProcessEngines.init()");
	    	 processEngine = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
		    		  .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE)
		    		  .setJdbcUrl("jdbc:h2:mem:my-own-db;DB_CLOSE_DELAY=1000")
		    		  .setJobExecutorActivate(false)
		    		  .setDatabaseSchemaUpdate("true")
		    		  .buildProcessEngine();
	     }
	     
	     System.err.println(processEngine);
	     
	     
	     //发布流程
	     RepositoryService repositoryService = processEngine.getRepositoryService();
//	     repositoryService.createDeployment()
//	       .addClasspathResource("VacationRequest.bpmn20.xml")
//	       .deploy();
	           
	     System.out.println("Number of process definitions: " + repositoryService.createProcessDefinitionQuery().count());   
	     
	     //启动一个流程实例
	     Map<String, Object> variables = new HashMap<String, Object>();
	     variables.put("employeeName", "Kermit");
	     variables.put("numberOfDays", new Integer(4));
	     variables.put("vacationMotivation", "I'm really tired!");

	     RuntimeService runtimeService = processEngine.getRuntimeService();
	     ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("vacationRequest", variables);

	     // Verify that we started a new process instance
	     log.info("Number of process instances: " + runtimeService.createProcessInstanceQuery().count());
	     
	     //完成任务
	  // Fetch all tasks for the management group
	     TaskService taskService = processEngine.getTaskService();
	     List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();
	     for (Task task : tasks) {
	    	 log.info("Task available: " + task.getName());
	     }
	     
	     Task task = tasks.get(0);

	     Map<String, Object> taskVariables = new HashMap<String, Object>();
	     taskVariables.put("vacationApproved", "false");
	     taskVariables.put("managerMotivation", "We have a tight deadline!");
	     taskService.complete(task.getId(), taskVariables);
	     
	    // if (true) throw new Exception("sss");
	     
	     //System.out.println(Message.getMessage("login", (String)((Session)map.get("SESSION")).getAttribute("locale")));
		return null;
	}
	
	//@Schedule(second="*",minute="*",dayOfWeek="*",hour="*")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) 
	public void batchJob(){
		System.out.println("TimingManager run");
	}
	
}
