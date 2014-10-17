package com.mama100.monitorcenter.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.ehcache.Cache;
import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mama100.monitorcenter.constants.EHCacheConstants;
import com.mama100.monitorcenter.message.ExceptionMessage;
import com.mama100.monitorcenter.message.PoolStatusMessage;
import com.mama100.monitorcenter.utils.CacheUtil;

@Controller
public class PoolStatusController {
	Logger logger = Logger.getLogger(getClass().getName());
	
	static String FORMAT_STR = "yyyy/MM/dd HH:mm:ss";
	
	@Autowired
	private CacheUtil cacheUtil;
	
	/**
	 * 池状态首页
	 * key,list
	 * 拿每个key对应的list的最后一条， 组成list返回
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/monitor/pool/index")
	public String index(Model model){
		Cache cache = cacheUtil.getCache(EHCacheConstants.CACHE_NAME_POOL_STATUS_MAP);
		
		List<PoolStatusMessage> psList = new ArrayList<PoolStatusMessage>(cache.getKeys().size());
		
		for(Iterator<Object> it = cache.getKeys().iterator(); it.hasNext(); ){
			Object key = it.next();
			Vector<PoolStatusMessage> vector = (Vector<PoolStatusMessage>) cache.get(key).getValue();
			
			if(null != vector && !vector.isEmpty()){
				psList.add(vector.get(vector.size()-1));
			}
		}
		
		Collections.sort(psList, new Comparator<PoolStatusMessage>() {
			@Override
			public int compare(PoolStatusMessage o1, PoolStatusMessage o2) {
				return o1.getFullName().compareTo(o2.getFullName());
			}
		});
		
		model.addAttribute("psList", psList);
		
		return "pool_status";
	}
	
	
	/**
	 * 图表详情页
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/monitor/pool/chart/{key}")
	public String chart(Model model, @PathVariable String key){
		if(StringUtils.isBlank(key))
			return "pool_status";
		
		Vector<PoolStatusMessage> vector = (Vector<PoolStatusMessage>) cacheUtil.getElementValue(EHCacheConstants.CACHE_NAME_POOL_STATUS_MAP, key);
		if(null == vector || vector.isEmpty())
			return "pool_status";
		
		model.addAttribute("key", vector.get(0).getKey());
		model.addAttribute("name", vector.get(0).getFullName());
		
		return "pool_status_detail";
	}
	
	
	/**
	 * ajax请求
	 * 最新的数据
	 * @param key
	 * @param lastTime 上一次最后一条数据的时间
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/monitor/pool/detail/{key}")
	public  @ResponseBody String detail(@PathVariable String key, @RequestParam String lastTime){
		if(StringUtils.isBlank(key))
			return "";
		
		Vector<PoolStatusMessage> vector = (Vector<PoolStatusMessage>) cacheUtil.getElementValue(EHCacheConstants.CACHE_NAME_POOL_STATUS_MAP, key);
		
		if(vector == null || vector.isEmpty())
			return "";
		
		
		//返回最后100个
		int count = 100;
		
		List<Map<String, String>> returnList = new ArrayList<Map<String, String>>(count);
		SimpleDateFormat format = new SimpleDateFormat(FORMAT_STR);
		
		for(int i = vector.size()-1; i >= 0; i--){
			PoolStatusMessage psm = vector.get(i);
			
			
			if(!"0".equals(lastTime)){
				
				try {
					Date dateLastTime = format.parse(lastTime);
					if(dateLastTime.getTime() - psm.getCreatedTime().getTime() >= -1000){//应为格式化的时候丢失了毫秒
//						continue;//已显示了的，不用返回
						break;
					}
				} catch (ParseException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
					continue;
				}
			}
			
			Map<String, String> map = new HashMap<String, String>(psm.getStatusMap().size()+1);
			map.putAll(psm.getStatusMap());
			
			
			map.put("createdTime", format.format(psm.getCreatedTime()));
			
			returnList.add(map);
			count--;
			
			if(count == 0)
				break;
		}
		
		Collections.sort(returnList, new Comparator<Map<String, String>>() {
			@Override
			public int compare(Map<String, String> o1, Map<String, String> o2) {
				SimpleDateFormat format = new SimpleDateFormat(FORMAT_STR);
				
				try {
					Date date1 = format.parse(o1.get("createdTime"));
					Date date2 = format.parse(o2.get("createdTime"));
					
					return date1.before(date2) ? -1 : 1;
				} catch (ParseException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
					return 0;
				}
			}
		});
		
		return JSONArray.fromObject(returnList).toString();
	}
	
	
	/**
	 * ajax请求
	 * 某范围的数据
	 * @param key
	 * @param startTime	开始时间点
	 * @param count		记录条数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/monitor/pool/detail_range/{key}")
	public  @ResponseBody String detailRange(@PathVariable String key, @RequestParam String startTime,  @RequestParam Integer count){
		if(StringUtils.isBlank(key) || StringUtils.isBlank(startTime))
			return "";
		
		Vector<PoolStatusMessage> vector = (Vector<PoolStatusMessage>) cacheUtil.getElementValue(EHCacheConstants.CACHE_NAME_POOL_STATUS_MAP, key);
		if(vector == null || vector.isEmpty())
			return "";
		
		if(count == null || count == 0)
			count = 100;
		
		List<Map<String, String>> returnList = new ArrayList<Map<String, String>>(count);
		
		try {
			SimpleDateFormat format = new SimpleDateFormat(FORMAT_STR);
			Date dateStartTime = format.parse(startTime);
//			System.out.println(dateStartTime);
			
			//最后一条数据的时间都比 startTime要小的话，直接返回空
			if(dateStartTime.getTime() - vector.get(vector.size()-1).getCreatedTime().getTime() >= -1000)
				return "";
			
			for(int i = 0; i < vector.size(); i++){
				
				if(dateStartTime.getTime() - vector.get(i).getCreatedTime().getTime() <= 1000){//应为格式化的时候丢失了毫秒
					
					int start = i;
					int end = Math.min(vector.size(), i + count);
					
					for(int j = start; j < end; j++){
						PoolStatusMessage psm = vector.get(j);
						
						Map<String, String> map = new HashMap<String, String>(psm.getStatusMap().size()+1);
						map.putAll(psm.getStatusMap());
						
						map.put("createdTime", format.format(psm.getCreatedTime()));
						
						returnList.add(map);
					}
					
					break;
				}
			}
			
			return JSONArray.fromObject(returnList).toString();
		} catch (ParseException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return "";
	}
	
	
}
