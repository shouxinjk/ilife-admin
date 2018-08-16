package com.pcitech.iLife.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.pcitech.iLife.util.TaskLoader;

@RunWith(SpringJUnit4ClassRunner.class) 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class TasksLoaderTest {
	
	@Autowired
	TaskLoader taskLoader;
	
	@Test
	public void loadTasks() {
		//taskLoader.loadOccasionTasks();
		System.out.println("now load occasion tasks ... ");
		assert true;
	}

}
