package com.dixon.game.ddz.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dixon.game.ddz.common.bean.Poker;
import com.dixon.game.ddz.common.bean.PokerWraper;
import com.dixon.game.ddz.common.enu.ColourType;
import com.dixon.game.ddz.common.enu.Type;
import com.dixon.game.ddz.common.exceptions.WrongPokerTypeException;
import com.dixon.game.ddz.common.executors.Executor;


public class PokerUtil {
	
	//获取牌型包装器
	public static PokerWraper getPokerWraper(List<Poker> list) throws WrongPokerTypeException{
		if(null == list || list.isEmpty())
			throw new WrongPokerTypeException("wrong poker type");
		
		PokerWraper pw = new PokerWraper();
		pw.setPokerList(list);
		pw.setAmount(list.size());
		
		Map<Integer, Integer> map = toMap(list);
		
		Collection<Integer> values = map.values();
		List<Integer> countList = new ArrayList<Integer>(values.size());
		countList.addAll(values);
		
		Collections.sort(countList, new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				return o2 - o1;
			}
		});
		
		//得到牌型
		String type = "";
		for(Integer c : countList){
			type += c+"";
		}
		type = type.replaceAll("1", "a");
		type = type.replaceAll("2", "b");
		type = type.replaceAll("3", "c");
		type = type.replaceAll("4", "d");
		
		//处理顺子，可能大于5个，使等于5个
		if(type.indexOf("aaaaa") != -1){
			type = "aaaaa";
		}
		//处理连对
		if(type.indexOf("bbb") != -1){
			type = "bbb";
		}
		
		//通过反射实例化这个类型的处理类
		try {
			//Type.valueOf如果找不到就抛出异常， 说明类型不匹配
			String executorClassName = Type.valueOf(type).getName();
			String packagePath = Executor.class.getPackage().getName();
			String fullClassName = packagePath.concat(".").concat(executorClassName);
			
			Class<?> c = Class.forName(fullClassName);
			Executor executor = (Executor) c.newInstance();
			return executor.execute(map, list, pw);
		} catch (Exception e) {
			throw new WrongPokerTypeException("wrong poker type");
		}
	}
	
	/**
	 * 获取目标牌型所有组合
	 * @param targetPW
	 * @param list
	 * @return
	 */
	public static List<PokerWraper> getTipPokerWraperList(PokerWraper targetPW, List<Poker> list){
		return null;
	}
	
	
	/**
	 * map<value, count>
	 * map<值, 牌数量>
	 * @param list
	 * @return
	 */
	public static Map<Integer, Integer> toMap(List<Poker> list){
		Map<Integer, Integer> map = new HashMap<Integer, Integer>(5);
		for(Poker p : list){
			Integer count = map.get(p.getValue());
			if(null == count)
				count = 0;
			
			map.put(p.getValue(), ++count);
		}
		return map;
	}
	
	public static void main(String[] args) {
		List<Poker> list = new ArrayList<Poker>();
		
		list.add(new Poker(ColourType.heitao, 3));
		list.add(new Poker(ColourType.fangzhuan, 3));
		list.add(new Poker(ColourType.hongxin, 3));
		list.add(new Poker(ColourType.meihua, 3));
		list.add(new Poker(ColourType.hongxin, 6));
		list.add(new Poker(ColourType.heitao, 6));
		list.add(new Poker(ColourType.hongxin, 7));
		list.add(new Poker(ColourType.heitao, 7));
		
		//顺子
//		list.add(new Poker(ColourType.heitao, 3));
//		list.add(new Poker(ColourType.fangzhuan, 4));
//		list.add(new Poker(ColourType.hongxin, 5));
//		list.add(new Poker(ColourType.meihua, 6));
//		list.add(new Poker(ColourType.hongxin, 7));
//		list.add(new Poker(ColourType.heitao, 8));
//		list.add(new Poker(ColourType.hongxin, 9));
//		list.add(new Poker(ColourType.heitao, 10));
//		list.add(new Poker(ColourType.heitao, 11));
//		list.add(new Poker(ColourType.heitao, 12));
//		list.add(new Poker(ColourType.heitao, 13));
//		list.add(new Poker(ColourType.heitao, 1));
		//连对
//		list.add(new Poker(ColourType.heitao, 3));
//		list.add(new Poker(ColourType.fangzhuan, 3));
//		list.add(new Poker(ColourType.hongxin, 4));
//		list.add(new Poker(ColourType.meihua, 4));
//		list.add(new Poker(ColourType.hongxin, 5));
//		list.add(new Poker(ColourType.heitao, 5));
//		list.add(new Poker(ColourType.hongxin, 6));
//		list.add(new Poker(ColourType.heitao, 6));
//		list.add(new Poker(ColourType.hongxin, 7));
//		list.add(new Poker(ColourType.heitao, 7));
		//飞机
//		list.add(new Poker(ColourType.heitao, 3));
//		list.add(new Poker(ColourType.fangzhuan, 3));	
//		list.add(new Poker(ColourType.hongxin, 3));
//		list.add(new Poker(ColourType.hongxin, 4));
//		list.add(new Poker(ColourType.meihua, 4));
//		list.add(new Poker(ColourType.heitao, 4));
//		
//		list.add(new Poker(ColourType.hongxin, 7));
//		list.add(new Poker(ColourType.heitao, 7));
//		list.add(new Poker(ColourType.hongxin, 8));
//		list.add(new Poker(ColourType.heitao, 8));
		
//		Map<Integer, Integer> map = new PlayManager().toMap(list);
//		
////		for(Iterator<Entry<Integer, Integer>> it = map.entrySet().iterator(); it.hasNext(); ){
////			Entry<Integer, Integer> entry = it.next();
////			System.out.println(entry.getKey() + " : " + entry.getValue());
////		}
//		
//		Collection<Integer> values = map.values();
//		List<Integer> countList = new ArrayList<Integer>(values.size());
//		countList.addAll(values);
//		
//		Collections.sort(countList, new Comparator<Integer>() {
//			public int compare(Integer o1, Integer o2) {
//				return o2 - o1;
//			}
//		});
//		
//		for(Integer i : countList){
//			System.out.println(i);
//		}
		
		
		try {
			PokerWraper pw = getPokerWraper(list);
			System.out.println(pw);
		} catch (WrongPokerTypeException e) {
			e.printStackTrace();
		}
	}
}
