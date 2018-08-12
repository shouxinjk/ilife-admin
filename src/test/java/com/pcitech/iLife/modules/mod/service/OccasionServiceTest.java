package com.pcitech.iLife.modules.mod.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.pcitech.iLife.modules.mod.entity.Occasion;
import com.pcitech.iLife.modules.mod.entity.OccasionCategory;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;


@RunWith(SpringJUnit4ClassRunner.class) 
//@RunWith(DataProviderRunner.class) //cannot work. it will disable @Autowired 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class OccasionServiceTest {

	@Autowired
	private OccasionService occasionService;
	
	@Autowired
	private OccasionCategoryService occasionCategoryService;
	
    @DataProvider
    public static Object[][] occasions() {
        return new Object[][] {
                {"test001", 0, "test1", "forever","***","itemexpr","none","none","inside","passive" },
                {"test002", 0, "test2", "forever","***","itemexpr","none","none","inside","passive" },
                {"test003", 0, "test3", "forever","***","itemexpr","none","none","inside","passive" },
        };
    }

	@Test
	public void save() {
		List<OccasionCategory> categories = occasionCategoryService.findTree();
		Occasion occasion = new Occasion();
		//occasion.setId("test00001");
		occasion.setName("test01");
		occasion.setExprDuration("forever");
		occasion.setExpression("***");
		occasion.setExprItem("none");
		occasion.setExprTrigger("none");
		occasion.setExprUser("none");
		occasion.setTriggerDirection("outside");
		occasion.setTriggerType("passive");
		occasion.setOccasionCategory(categories.get(0));
		occasionService.save(occasion);
		
		Occasion result = occasionService.get(occasion);
		assert result.getName().equals("test01");
	}
	
	/*
	 * below codes cannot work
	 */
	/*
	@Test
    @UseDataProvider("occasions")
	public void save(String id,int index,String name,String duration,String expression,String exprItem,String exprTrigger,String exprUser,String direction,String type) {
		List<OccasionCategory> categories = occasionCategoryService.findTree();
		Occasion occasion = new Occasion();
		occasion.setId(id);
		occasion.setName(name);
		occasion.setExprDuration(duration);
		occasion.setExpression(expression);
		occasion.setExprItem(exprItem);
		occasion.setExprTrigger(exprTrigger);
		occasion.setExprUser(exprUser);
		occasion.setTriggerDirection(direction);
		occasion.setTriggerType(type);
		occasion.setOccasionCategory(categories.get(index));
		occasionService.save(occasion);
		
		Occasion result = occasionService.get(id);
		assert result.getName().equals(name);
	}
	//*/
}
