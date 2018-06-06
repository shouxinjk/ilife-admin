package com.pcitech.iLife.common.persistence;

import java.util.List;

/**
 * 复杂功能的树
 * Title:TreeNode 
 * Description:
 * Company:
 * @author jcx
 * @date 2017年9月19日 上午11:33:47
 */
public class TreeNode extends TreeEntity<TreeNode> {

	private static final long serialVersionUID = 1L;
	
	public TreeNode() {
		super();
	}

	public TreeNode(String id){
		super(id);
	}

	@Override
	public TreeNode getParent() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public void setParent(TreeNode parent) {
		this.parent = parent;
		
	}

	public static void sortList(List<TreeNode> list, List<TreeNode> sourcelist, String parentId,boolean cascade){
		for (int i=0; i<sourcelist.size(); i++){
			TreeNode e = sourcelist.get(i);
			if (e.getParent()!=null && e.getParent().getId()!=null
					&& e.getParent().getId().equals(parentId)){
				list.add(e);
				if (cascade){
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j=0; j<sourcelist.size(); j++){
						TreeNode child = sourcelist.get(j);
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
