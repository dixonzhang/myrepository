package com.mama100.monitorcenter.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@Service
public class CacheUtil {
	
	@Autowired
	private CacheManager cacheManager;
	
	public Cache getCache(String cacheKey){
		return cacheManager.getCache(cacheKey);
	}
	
	public void putElement(String cacheKey, String elementKey, Object value){
		Cache cache = cacheManager.getCache(cacheKey);
		if(cache != null){
			cache.put(new Element(elementKey, value));
		}
	}
	
	public Object getElementValue(String cacheKey, String elementKey){
		Cache cache = cacheManager.getCache(cacheKey);
		if(cache != null){
			Element element = cache.get(elementKey);
			if(element != null)
				return element.getValue();
		}
		
		return null;
	}
	
	
}
