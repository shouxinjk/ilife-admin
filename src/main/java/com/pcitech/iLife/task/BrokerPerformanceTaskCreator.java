package com.pcitech.iLife.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.pcitech.iLife.common.config.Global;
import com.pcitech.iLife.modules.mod.entity.Order;
import com.pcitech.iLife.modules.mod.service.OrderService;
import com.pcitech.iLife.modules.mod.service.BrokerPerformanceService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 创建待汇总达人绩效任务。
 * 在指定时间自动查询所有达人，并生成对应的任务列表，其中汇总状态、通知状态均为pending
 */
public class BrokerPerformanceTaskCreator{
    private static Logger logger = LoggerFactory.getLogger(BrokerPerformanceTaskCreator.class);
    
    @Autowired
    BrokerPerformanceService performanceService;

    public BrokerPerformanceTaskCreator() {
    }

    /**
	 * 查询所有达人并且创建绩效统计任务。
	 * 1，查询所有达人
	 * 2，将任务记录添加到达人绩效任务表。包括brokerId、brokerOpenid、taskType（daily、weekly、monthly等）、分享量、浏览量、购买量、订单数、团队人数、订单佣金、团队佣金。其中taskStatus=false、notifyStatus=false
     * 注意：实际直接通过SQL语句完成。不需要循环处理
     */
    public void execute(String type) throws JobExecutionException {
    		logger.warn("Performance init-calc job start. [type]"+type + new Date());
    		
    		performanceService.insertByBroker(type);
    		logger.info("Performance clac task create job executed.[type]"+type);
    }

}
