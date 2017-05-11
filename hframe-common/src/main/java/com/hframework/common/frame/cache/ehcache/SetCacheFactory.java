package com.hframework.common.frame.cache.ehcache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SetCacheFactory {

	private static Map<String,Object> tableCacheMap=new HashMap<String, Object>();//这是一个隐藏的对应与数据库表
	private static Map<String,Object> columnsSetCacheMap=new HashMap<String, Object>();//对应与form，grid
	private static Map<String,Object> fieldsSetCacheMap=new HashMap<String, Object>();//对应于tree,list，menu
	
	private static Map<String,Object> elementCacheMap=new HashMap<String, Object>();//源元素map，用于保存具体某一个基本单元，比如具体某一个field，select，input,等等
	
	
	
	public static  Set<String> getTableSetKey(){
		Set<String> set=tableCacheMap.keySet();
		return set;
	}
	
	public static  Set<String> getColumnSetKey(){
		Set<String> set=columnsSetCacheMap.keySet();
		return set;
	}
	
	public static  Set<String> getFieldSetKey(){
		Set<String> set=fieldsSetCacheMap.keySet();
		return set;
	}
	
	public static  Set<String> getElementSetKey(){
		Set<String> set=elementCacheMap.keySet();
		return set;
	}
	
	public static void put(String key,Map map,String type){
		if(key==null||map==null){
			return;
		}
		if("table".equals(type)){
			tableCacheMap.put(key, map);
		}else if ("fields".equals(type)) {
			fieldsSetCacheMap.put(key, map);
		}else if ("element".equals(type)) {
			elementCacheMap.put(key, map);
		}else{
			columnsSetCacheMap.put(key, map);
		}
	}
	
	public static void put(String key,List elementsList,String title,String type){
		Map map=new HashMap();
		map.put("Title", title);
		
		if ("fields".equals(type)) {
			map.put("fieldsList", elementsList);
		}else if ("element".equals(type)) {
			map.put("optionList", elementsList);
		}else{
			map.put("columnsList", elementsList);
		}		
		
		put(key, map,type);
		
		
	}
	public static void put(String key,List elementsList,String type){
		put(key,elementsList,null,type);
	}
	
	public static Map get(String key){
		
		if(columnsSetCacheMap.get(key)!=null){
			return (Map) columnsSetCacheMap.get(key);
		}
		if(fieldsSetCacheMap.get(key)!=null){
			return (Map) fieldsSetCacheMap.get(key);
		}
		if(tableCacheMap.get(key)!=null){
			return (Map) tableCacheMap.get(key);
		}
		
		if(elementCacheMap.get(key)!=null){
			return (Map) elementCacheMap.get(key);
		}
		
		return null;
		
	}
	
	public static Map get(String key,String type){
		
		if("table".equals(type)){
			return (Map) tableCacheMap.get(key);
		}else if ("fields".equals(type)) {
			return (Map) fieldsSetCacheMap.get(key);
		}else if ("element".equals(type)) {
			return (Map) elementCacheMap.get(key);
		}else if ("columns".equals(type)){
			return (Map) columnsSetCacheMap.get(key);
		}else {
			return null;
		}
	}
	
	
	
	public static Map getCacheMap() {
		return columnsSetCacheMap;
	}

	public static void setCacheMap(Map cacheMap) {
		SetCacheFactory.columnsSetCacheMap = cacheMap;
	}
	
}
