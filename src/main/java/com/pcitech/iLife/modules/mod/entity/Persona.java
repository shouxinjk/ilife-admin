/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.modules.mod.entity;

import java.util.List;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import com.pcitech.iLife.common.persistence.TreeEntity;

/**
 * 用户分群Entity
 * @author chenci
 * @version 2017-09-15
 */
public class Persona extends TreeEntity<Persona> {
	
	private static final long serialVersionUID = 1L;
	private Phase phase;		// phase_id
	private Hierarchy hierarchy;		// hierarchy_id
	private Double zeta;		// zeta
	private Double eta;		// eta
	private Double theta;		// theta
	private String lambda;		// lambda
	private String logo;		// logo
	private String description;		// description
	private Double alpha;		// alpha
	private Double beta;		// beta
	private Double gamma;		// gamma
	private Double delte;		// delte
	private Double epsilon;		// epsilon
	private String name;		// name
	private String expression;		// expression
	
	public Persona() {
		super();
	}

	public Persona(String id){
		super(id);
	}

	@NotNull(message="父级编号不能为空")
	public Persona getParent() {
		return parent;
	}

	public void setParent(Persona parent) {
		this.parent = parent;
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
	
	@Length(min=0, max=500, message="lambda长度必须介于 0 和 500 之间")
	public String getLambda() {
		return lambda;
	}

	public void setLambda(String lambda) {
		this.lambda = lambda;
	}
	
	@Length(min=0, max=1000, message="logo长度必须介于 0 和 1000 之间")
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	@Length(min=0, max=1000, message="description长度必须介于 0 和 1000 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	
	public static void sortList(List<Persona> list, List<Persona> sourcelist, String parentId,boolean cascade){
		for (int i=0; i<sourcelist.size(); i++){
			Persona e = sourcelist.get(i);
			if (e.getParent()!=null && e.getParent().getId()!=null
					&& e.getParent().getId().equals(parentId)){
				list.add(e);
				if (cascade){
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j=0; j<sourcelist.size(); j++){
						Persona child = sourcelist.get(j);
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