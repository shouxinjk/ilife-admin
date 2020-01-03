package com.pcitech.iLife.task;

import java.util.Date;

public class AutoRunSample{
	
	public void execute(String type) {
		System.err.println("=================\nAuto job runs....[type]"+type+"[next fire]"+new Date()+"\n================");
	}
}
