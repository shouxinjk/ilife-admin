package com.pcitech.iLife.task;

import javax.annotation.PostConstruct;

public class AutoRunSample {
	
	@PostConstruct
	public void applicationStart(){
		System.out.println("=================\napplication start\n================");
	}
}
