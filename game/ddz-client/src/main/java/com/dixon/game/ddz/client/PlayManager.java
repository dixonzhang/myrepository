package com.dixon.game.ddz.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dixon.game.ddz.client.exceptions.WrongPokerTypeException;
import com.dixon.game.ddz.common.bean.Poker;
import com.dixon.game.ddz.common.bean.PokerWraper;
import com.dixon.game.ddz.common.enu.ColourType;
import com.dixon.game.ddz.common.enu.PokerType;


public class PlayManager {
	public PokerWraper getPokerWraper(List<Poker> list) throws WrongPokerTypeException{
		
		if(null == list || list.isEmpty())
			throw new WrongPokerTypeException("wrong poker type");
		
		PokerWraper pw = new PokerWraper();
		pw.setPokerList(list);
		pw.setAmount(list.size());
		
		if(list.size() == 1){
			pw.setAmount(1);
			pw.setPokerLeaderValue(list.get(0).getValue());
			pw.setPokerType(PokerType.one);
		}
		//一对或王炸
		else if(list.size() == 2){
			//王num=0
			if(list.get(0).getNum() != list.get(1).getNum()){
				throw new WrongPokerTypeException("wrong poker type");
			}
			
			//大王value=17 小王16
			if(list.get(0).getValue() != list.get(1).getValue()){
				pw.setPokerLeaderValue(Math.max(list.get(0).getValue(), list.get(1).getValue()));
				pw.setPokerType(PokerType.bomb);
			}
			else{
				pw.setPokerLeaderValue(list.get(0).getValue());
				pw.setPokerType(PokerType.pair);
			}
		}
		else if(list.size() == 3){
			if(list.get(0).getValue() == list.get(1).getValue() && list.get(0).getValue() == list.get(2).getValue()){
				pw.setPokerLeaderValue(list.get(0).getValue());
				pw.setPokerType(PokerType.three);
			}
			else
				throw new WrongPokerTypeException("wrong poker type");
		}
		//three or bomb
		else if(list.size() == 4){
			Map<Integer, Integer> map = toMap(list);
			//bomb
			if(map.size() == 1){
				pw.setPokerLeaderValue(list.get(0).getValue());
				pw.setPokerType(PokerType.bomb);
			}
			//three take one
			else if(map.size() == 2){
				
			}
			
			
			
		}
		
		
		
		
		return pw;
	}
	
	public void test(List<Poker> list){
		
		
		
	}
	
	/**
	 * map<value, count>
	 * map<值, 牌数量>
	 * @param list
	 * @return
	 */
	public Map<Integer, Integer> toMap(List<Poker> list){
		Map<Integer, Integer> map = new HashMap<Integer, Integer>(5);
		for(Poker p : list){
			Integer count = map.get(p.getValue());
			if(null == count)
				count = 0;
			
			map.put(p.getValue(), ++count);
		}
		return map;
	}
	
	/**
	 * 1
	 * 
	 * @param type
	 * @return
	 */
	private boolean matchType(String ...types){
		for(String type : types){
			
			
			
			
		}
		return false;
	}
	
	public static void main(String[] args) {
		List<Poker> list = new ArrayList<Poker>();
		
		list.add(new Poker(ColourType.heitao, 3));
		list.add(new Poker(ColourType.fangzhuan, 3));
		list.add(new Poker(ColourType.hongxin, 3));
		list.add(new Poker(ColourType.hongxin, 6));
		
		Map<Integer, Integer> map = new PlayManager().toMap(list);
		
		for(Iterator<Entry<Integer, Integer>> it = map.entrySet().iterator(); it.hasNext(); ){
			Entry<Integer, Integer> entry = it.next();
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
		
	}
}
