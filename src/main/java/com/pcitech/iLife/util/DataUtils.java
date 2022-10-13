/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/iLife">iLife</a> All rights reserved.
 */
package com.pcitech.iLife.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pcitech.iLife.common.mapper.JsonMapper;
import com.pcitech.iLife.common.utils.CacheUtils;
import com.pcitech.iLife.common.utils.SpringContextHolder;
import com.pcitech.iLife.modules.mod.dao.DictMetaDao;
import com.pcitech.iLife.modules.mod.entity.DictMeta;
import com.pcitech.iLife.modules.sys.dao.DictDao;
import com.pcitech.iLife.modules.sys.entity.Dict;

/**
 * 字典工具类
 * @author ThinkGem
 * @version 2013-5-29
 */
public class DataUtils {
	
	private static DictMetaDao dictMetaDao = SpringContextHolder.getBean(DictMetaDao.class);

	public static final String CACHE_DICT_MAP = "sxDictMap";
	
	//获取业务字典表
	public static List<DictMeta> getDictMetaList(String type){
		@SuppressWarnings("unchecked")
		Map<String, List<DictMeta>> dictMap = (Map<String, List<DictMeta>>)CacheUtils.get(CACHE_DICT_MAP);
		if (dictMap==null){
			dictMap = Maps.newHashMap();
			for (DictMeta dict : dictMetaDao.findAllList(new DictMeta())){
				//构建一个全集
				List<DictMeta> dictListAll = dictMap.get("_all");//所有业务字典
				if (dictListAll != null){
					dictListAll.add(dict);
				}else{
					dictMap.put("_all", Lists.newArrayList(dict));
				}
				
				//按类型组织
				List<DictMeta> dictList = dictMap.get(dict.getType());
				if (dictList != null){
					dictList.add(dict);
				}else{
					dictMap.put(dict.getType(), Lists.newArrayList(dict));
				}
			}
			CacheUtils.put(CACHE_DICT_MAP, dictMap);
		}
		List<DictMeta> dictList = dictMap.get(type);
		if (dictList == null){
			dictList = Lists.newArrayList();
		}
		return dictList;
	}
	
	public static String getDictMetaLabelName(String value, String type, String defaultValue){
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(value)){
			for (DictMeta dict : getDictMetaList(type)){
				if (type.equals(dict.getType()) && value.equals(dict.getDictKey())){
					return dict.getName();
				}
			}
		}
		return defaultValue;
	}
	
	public static String getDictMetaLabelNames(String values, String type, String defaultValue){
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(values)){
			List<String> valueList = Lists.newArrayList();
			for (String value : StringUtils.split(values, ",")){
				valueList.add(getDictMetaLabelName(value, type, defaultValue));
			}
			return StringUtils.join(valueList, ",");
		}
		return defaultValue;
	}

	public static String getDictMetaLabelKey(String value, String type, String defaultValue){
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(value)){
			for (DictMeta dict : getDictMetaList(type)){
				if (type.equals(dict.getType()) && value.equals(dict.getDictKey())){
					return dict.getDictKey();
				}
			}
		}
		return defaultValue;
	}
	
	public static String getDictMetaLabelKeys(String values, String type, String defaultValue){
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(values)){
			List<String> valueList = Lists.newArrayList();
			for (String value : StringUtils.split(values, ",")){
				valueList.add(getDictMetaLabelKey(value, type, defaultValue));
			}
			return StringUtils.join(valueList, ",");
		}
		return defaultValue;
	}

	public static String getDictMetaId(String label, String type, String defaultLabel){
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(label)){
			for (DictMeta dict : getDictMetaList(type)){
				if (type.equals(dict.getType()) && label.equals(dict.getDictKey())){
					return dict.getId();
				}
			}
		}
		return defaultLabel;
	}
	
	public static String getDictMetaKey(String label, String type, String defaultLabel){
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(label)){
			for (DictMeta dict : getDictMetaList(type)){
				if (type.equals(dict.getType()) && label.equals(dict.getDictKey())){
					return dict.getDictKey();
				}
			}
		}
		return defaultLabel;
	}
	
	/**
	 * 返回字典列表（JSON）
	 * @param type
	 * @return
	 */
	public static String getDictMetaListJson(String type){
		return JsonMapper.toJsonString(getDictMetaList(type));
	}
	
}
