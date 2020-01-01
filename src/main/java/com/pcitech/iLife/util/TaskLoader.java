package com.pcitech.iLife.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.service.OccasionCategoryService;
import com.pcitech.iLife.modules.mod.service.OccasionService;

@Service
public class TaskLoader{
	private Logger logger = LoggerFactory.getLogger(TaskLoader.class);
	
	@Autowired
	private OccasionService occasionService;
	
	@Autowired
	private OccasionCategoryService occasionCategoryService;
	
   // @PostConstruct
    public void loadOccasionTasks() {
    		List<Occasion> occasions = occasionService.findList(new Occasion());
    		//schedule tasks
    		SchedulerFactory sf = new StdSchedulerFactory();
    		try {
	    		Scheduler sched = sf.getScheduler();
	    		for(Occasion occasion:occasions) {
	    			//增加推荐任务：注意group将作为kafka消息的action
	    			JobDetail insertJob = newJob(OccasionJob.class)
	    				    .withIdentity("job-active-"+occasion.getId(), "active-occasion")
	    				    .build();
				CronTrigger insertTrigger = newTrigger()
				    .withIdentity("trigger-active-"+occasion.getId(), "active-occasion")
				    .withSchedule(cronSchedule(occasion.getExpression()))
				    .build();
				sched.scheduleJob(insertJob, insertTrigger);
	    			//取消推荐任务：注意group将作为kafka消息的action
				JobDetail removeJob = newJob(OccasionJob.class)
					    .withIdentity("job-deactive-"+occasion.getId(), "deactive-occasion")
					    .build();
				CronTrigger removeTrigger = newTrigger()
				    .withIdentity("trigger-deactive-"+occasion.getId(), "deactive-occasion")
				    .withSchedule(cronSchedule(occasion.getExprDuration()))
				    .build();
				sched.scheduleJob(removeJob, removeTrigger);			
	    		}
    		}catch(SchedulerException e) {
    			logger.error("Load occasion tasks failed.",e);
    		}
    		
    		//inform kafka
    		Gson gson = new Gson();
		Map map = new HashMap();
		map.put("_action", "load_tasks");
		map.put("timestamp", new Date());
		map.put("count", occasions.size());
        logger.debug("自动任务启动。"+gson.toJson(map));
    }
}