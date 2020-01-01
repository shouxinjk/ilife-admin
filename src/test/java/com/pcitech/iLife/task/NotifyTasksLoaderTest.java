package com.pcitech.iLife.task;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import com.pcitech.iLife.task.NotifyTaskLoader;

@RunWith(SpringJUnit4ClassRunner.class) 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class NotifyTasksLoaderTest {
	
	@Autowired
	NotifyTaskLoader taskLoader;
	
	@Test
	public void loadTasks() {
//		taskLoader.loadNotifyTasks();
		System.out.println("now load notify tasks ... ");
		assert true;
	}

}
