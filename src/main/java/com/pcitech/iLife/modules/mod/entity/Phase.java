/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;


import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.TreeEntity;

/**
 * 人生阶段Entity
 * @author chenci
 * @version 2017-09-15
 */
public class Phase extends TreeEntity<Phase> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// name
	private String expression;		// expression
	private Double alpha;		// alpha
	private Double beta;		// beta
	private Double gamma;		// gamma
	private Double delte;		// delte
	private Double epsilon;		// epsilon
	
	public Phase() {
		super();
	}

	public Phase(String id){
		super(id);
	}
	
	@Length(min=0, max=100, message="name长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=500, message="expression长度必须介于 0 和 500 之间")
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
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

	@Override
	public Phase getParent() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public void setParent(Phase parent) {
		this.parent = parent;
		
	}
	
	public static void sortList(List<Phase> list, List<Phase> sourcelist, String parentId,boolean cascade){
		for (int i=0; i<sourcelist.size(); i++){
			Phase e = sourcelist.get(i);
			if (e.getParent()!=null && e.getParent().getId()!=null
					&& e.getParent().getId().equals(parentId)){
				list.add(e);
				if (cascade){
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j=0; j<sourcelist.size(); j++){
						Phase child = sourcelist.get(j);
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