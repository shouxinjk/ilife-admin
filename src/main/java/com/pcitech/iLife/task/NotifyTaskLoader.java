package com.pcitech.iLife.task;

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
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.service.OccasionCategoryService;
import com.pcitech.iLife.modules.mod.service.OccasionService;

@Service
public class NotifyTaskLoader{
	private Logger logger = LoggerFactory.getLogger(NotifyTaskLoader.class);
	
	//通过PostConstruct注解在启动时自动执行。主要需要设置：default-lazy-init="false"，否则由于不会被实例化而不能调用
    @PostConstruct
    public void loadNotifyTasks() {
    		//订单通知任务
    		loadClearingNotifyJobs();
    		
    		//自动汇总初始化任务
    		loadCalcTaskInitJobs("daily");
    		loadCalcTaskInitJobs("weekly");
    		loadCalcTaskInitJobs("monthly");
    		
    		//自动汇总任务
    		loadCalcTaskJobs("daily");
    		loadCalcTaskJobs("weekly");
    		loadCalcTaskJobs("monthly");
    }
    
    //订单自动通知任务：每5分钟执行一次
    public void loadClearingNotifyJobs() {
		SchedulerFactory sf = new StdSchedulerFactory();
		try {
    			Scheduler sched = sf.getScheduler();
    			String cron = Global.getConfig("task.cron.order-notify");
    			//增加订单通知任务
    			JobDetail job = newJob(BrokerClearingNotifyTask.class)
    				    .withIdentity("job-clearing-notify")
    				    .build();
			CronTrigger trigger = newTrigger()
			    .withIdentity("trigger-clearing-notify")
			    .withSchedule(cronSchedule(cron))
			    .build();
			sched.scheduleJob(job, trigger);		
			logger.info("clearing notify task loaded.[cron]"+cron+"[next fire]"+trigger.getNextFireTime());
		}catch(SchedulerException e) {
			logger.error("Load order notify task failed.",e);
		}
    }
    
    //自动汇总记录创建任务：根据类型自动进行
    public void loadCalcTaskInitJobs(String type) {
		SchedulerFactory sf = new StdSchedulerFactory();
		String cron = Global.getConfig("task.cron.init-calc."+type);
		try {
    			Scheduler sched = sf.getScheduler();
    			//增加创建汇总记录任务
    			JobDetail job = newJob(BrokerPerformanceTaskCreator.class)
    				    .withIdentity("job-init-calc-"+type)
    				    .build();
    			job.getJobDataMap().put("task_type", type);//传递参数到任务内部
			CronTrigger trigger = newTrigger()
			    .withIdentity("trigger-init-calc-"+type)
			    .withSchedule(cronSchedule(cron))
			    .build();
			sched.scheduleJob(job, trigger);		
			logger.info(type+" init auto-calc task loaded.[type]"+type+"[cron]"+cron+"[next fire]"+trigger.getNextFireTime());
		}catch(SchedulerException e) {
			logger.error("Load init calc task failed.[type]"+type,e);
		}
    }
    
    
    //自动汇总任务
    public void loadCalcTaskJobs(String type) {
		SchedulerFactory sf = new StdSchedulerFactory();
		String cron = Global.getConfig("task.cron.auto-calc."+type);
		try {
    			Scheduler sched = sf.getScheduler();
    			//增加创建汇总记录任务
    			JobDetail job = newJob(BrokerPerformanceNotifyTask.class)
    				    .withIdentity("job-auto-calc-"+type)
    				    .build();
    			job.getJobDataMap().put("task_type", type);//传递参数到任务内部
			CronTrigger trigger = newTrigger()
			    .withIdentity("trigger-auto-calc-"+type)
			    .withSchedule(cronSchedule(cron))
			    .build();
			sched.scheduleJob(job, trigger);		
			logger.info(type+" auto-calc task loaded.[type]"+type+"[cron]"+cron+"[next fire]"+trigger.getNextFireTime());
		}catch(SchedulerException e) {
			logger.error("Load auto calc task failed.[type]"+type,e);
		}
    }
}