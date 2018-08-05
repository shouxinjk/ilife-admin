/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.TreeEntity;

/**
 * VALS模型Entity
 * @author chenci
 * @version 2017-09-22
 */
public class LifeStyle extends TreeEntity<LifeStyle> {
	
	private static final long serialVersionUID = 1L;
	private Double alpha;		// alpha
	private Double beta;		// beta
	private Double gamma;		// gamma
	private Double delte;		// delte
	private Double epsilon;		// epsilon
	private Double zeta;		// zeta
	private Double eta;		// eta
	private Double theta;		// theta
	private String lambda;		// lambda
	private Phase phase;		// 阶段
	private Hierarchy hierarchy;		// 层级
	private Persona persona;		// 画像
	private String motivationIds;		// motivation_id
	private String occasionIds;		// occasion_id
	private String itemCategoryIds;		// item_category_id
	private String motivationNames;		// motivation_id
	private String occasionNames;		// occasion_id
	private String itemCategoryNames;
	private LifeStyleCategory lifeStyleCategory;
	
	public LifeStyle() {
		super();
	}

	public LifeStyle(String id){
		super(id);
	}

	@Length(min=0, max=100, message="name长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	public Double getAlpha() {
		return alpha;
	}

	public void setAlpha(Double alpha) {
		this.alpha = alpha;
	}

	public Double getBeta() {
		return beta;
	}

	public void setBeta(Double beta) {
		this.beta = beta;
	}

	public Double getGamma() {
		return gamma;
	}

	public void setGamma(Double gamma) {
		this.gamma = gamma;
	}

	public Double getDelte() {
		return delte;
	}

	public void setDelte(Double delte) {
		this.delte = delte;
	}

	public Double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(Double epsilon) {
		this.epsilon = epsilon;
	}

	public Double getZeta() {
		return zeta;
	}

	public void setZeta(Double zeta) {
		this.zeta = zeta;
	}

	public Double getEta() {
		return eta;
	}

	public void setEta(Double eta) {
		this.eta = eta;
	}

	public Double getTheta() {
		return theta;
	}

	public void setTheta(Double theta) {
		this.theta = theta;
	}

	@Length(min=0, max=1000, message="lambda长度必须介于 0 和 1000 之间")
	public String getLambda() {
		return lambda;
	}

	public void setLambda(String lambda) {
		this.lambda = lambda;
	}

	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public Hierarchy getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(Hierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}


	public String getMotivationIds() {
		return motivationIds;
	}

	public void setMotivationIds(String motivationIds) {
		this.motivationIds = motivationIds;
	}

	public String getOccasionIds() {
		return occasionIds;
	}

	public void setOccasionIds(String occasionIds) {
		this.occasionIds = occasionIds;
	}

	public String getItemCategoryIds() {
		return itemCategoryIds;
	}

	public void setItemCategoryIds(String itemCategoryIds) {
		this.itemCategoryIds = itemCategoryIds;
	}

	public String getMotivationNames() {
		return motivationNames;
	}

	public void setMotivationNames(String motivationNames) {
		this.motivationNames = motivationNames;
	}

	public String getOccasionNames() {
		return occasionNames;
	}

	public void setOccasionNames(String occasionNames) {
		this.occasionNames = occasionNames;
	}

	public String getItemCategoryNames() {
		return itemCategoryNames;
	}

	public void setItemCategoryNames(String itemCategoryNames) {
		this.itemCategoryNames = itemCategoryNames;
	}

	public LifeStyleCategory getLifeStyleCategory() {
		return lifeStyleCategory;
	}

	public void setLifeStyleCategory(LifeStyleCategory lifeStyleCategory) {
		this.lifeStyleCategory = lifeStyleCategory;
	}

	@Override
	public LifeStyle getParent() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public void setParent(LifeStyle parent) {
		this.parent=parent;
		
	}
	
	public static void sortList(List<LifeStyle> list, List<LifeStyle> sourcelist, String parentId,boolean cascade){
		for (int i=0; i<sourcelist.size(); i++){
			LifeStyle e = sourcelist.get(i);
			if (e.getParent()!=null && e.getParent().getId()!=null
					&& e.getParent().getId().equals(parentId)){
				list.add(e);
				if (cascade){
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j=0; j<sourcelist.size(); j++){
						LifeStyle child = sourcelist.get(j);
						if (child.getParent()!=null && child.getParent().getId()!=null
								&& child.getParent().getId().equals(e.getId())){
							sortList(list, sourcelist, e.getId(),true);
							break;
						}
					}
				}
			}
		}
	}
	
	public static boolean isRoot(String id){
		return id != null && id.equals("1");
	}
}