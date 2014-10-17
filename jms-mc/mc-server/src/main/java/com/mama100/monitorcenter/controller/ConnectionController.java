package com.mama100.monitorcenter.controller;

import java.util.List;
import java.util.logging.Logger;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mama100.monitorcenter.message.ConnectionMessage;
import com.mama100.monitorcenter.service.TelnetService;

@Controller
public class ConnectionController {
	Logger logger = Logger.getLogger(getClass().getName());
	
	@Autowired
	private TelnetService telnetService;
	
	/**
	 * 主页， 显示连接情况
	 * @param model
	 * @return
	 */
	@RequestMapping("/")
	public String index(Model model){
		return "index";
	}
	
	@RequestMapping(value = "/monitor/conn/count", produces="text/plain;charset=UTF-8")
	public  @ResponseBody String count(){
		return JSONArray.fromObject(telnetService.count()).toString();
	}
	
	@RequestMapping(value = "/monitor/conn/load/{key}", produces="text/plain;charset=UTF-8")
	public  @ResponseBody String load(@PathVariable String key){
		List<ConnectionMessage> list = telnetService.takeByKey(key);
		if(list == null || list.isEmpty())
			return "";
		
		return JSONArray.fromObject(list).toString();
	}
	
}
