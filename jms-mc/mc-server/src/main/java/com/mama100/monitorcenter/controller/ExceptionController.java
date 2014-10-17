package com.mama100.monitorcenter.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import net.sf.ehcache.Cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mama100.monitorcenter.constants.EHCacheConstants;
import com.mama100.monitorcenter.message.ExceptionMessage;
import com.mama100.monitorcenter.service.TelnetService;
import com.mama100.monitorcenter.utils.CacheUtil;

@Controller
public class ExceptionController {
	Logger logger = Logger.getLogger(getClass().getName());
	
	@Autowired
	private TelnetService telnetService;
	
	@Autowired
	private CacheUtil cacheUtil;
	
	/**
	 * 主页， 显示连接情况
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/monitor/exception/index")
	public String index(Model model){
		List<ExceptionMessage> list = new ArrayList<ExceptionMessage>();
		
		Cache cache = cacheUtil.getCache(EHCacheConstants.CACHE_NAME_EXCEPTION_MAP);
		
		for(Iterator<Object> it = cache.getKeys().iterator(); it.hasNext(); ){
			Object key = it.next();
			Vector<ExceptionMessage> vector = (Vector<ExceptionMessage>) cache.get(key).getValue();
			
			if(null != vector && !vector.isEmpty()){
				list.addAll(vector);
			}
		}
		
		Collections.sort(list);
		
		model.addAttribute("excepList", list);
//		model.addAttribute("type", StringUtils.isBlank(type) ? "all" : "cat");
		
		return "exception";
	}
}
