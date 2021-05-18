package com.pcitech.iLife.cps.kaola;

public class CategoryInfo {

	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	private 	Long	 categoryId	;//	类目id
	private 	String	categoryName	;//	类目名
	private 	Integer	level	;//	层级
}
