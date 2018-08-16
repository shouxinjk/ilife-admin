package com.pcitech.iLife.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

/**
 * <p>
 * This is just a simple job that gets fired off many times by example 1
 * </p>
 * 
 * @author Bill Kratzer
 */
public class OccasionJob implements Job {
	private Logger kafka = LoggerFactory.getLogger("kafkaLogger");
    private static Logger _log = LoggerFactory.getLogger(OccasionJob.class);

    /**
     * Quartz requires a public empty constructor so that the
     * scheduler can instantiate the class whenever it needs.
     */
    public OccasionJob() {
    }

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a
     * <code>{@link org.quartz.Trigger}</code> fires that is associated with
     * the <code>Job</code>.
     * </p>
     * 
     * @throws JobExecutionException
     *             if there is an exception while executing the job.
     */
    public void execute(JobExecutionContext context)
        throws JobExecutionException {
    		//send kafka message
		//inform kafka
		Gson gson = new Gson();
		Map map = new HashMap();
		map.put("_action", context.getJobDetail().getKey().getGroup());
		map.put("timestamp", new Date());
	    kafka.info(gson.toJson(map));    	
    		//system log
        JobKey jobKey = context.getJobDetail().getKey();
        _log.info("Occasion job executed: " + gson.toJson(map));
    }

}
