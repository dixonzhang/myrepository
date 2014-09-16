package com.dixon.game.ddz.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dixon.game.ddz.bean.Poker;
import com.dixon.game.ddz.enu.ColourType;

/**
 * 洗牌器，得到打乱的54张牌
 * @author dixon
 */
public class DizhuShuffler {
	/**
	 * 洗牌
	 * @return
	 */
	public static List<Poker> suffle() {
		// A 2~10 J Q K joker; 黑桃，红桃，梅花，方砖
		List<Poker> list = new ArrayList<Poker>();

		list.add(new Poker(ColourType.dawang, 0));
		list.add(new Poker(ColourType.xiaowang, 0));

		for (int i = 1; i <= 4; i++) {
			ColourType type = null;
			if(i==1)
				type = ColourType.heitao;
			else if(i==2)
				type = ColourType.hongxin;
			else if(i==3)
				type = ColourType.meihua;
			else
				type = ColourType.fangzhuan;
			
			
			for (int j = 1; j <= 13; j++) {
				Poker p = new Poker(type, j);
				list.add(p);
			}
		}

		Collections.shuffle(list);// 洗牌

		return list;
	}
	
	/**
	 * key 0,1,2 为三个玩家的牌， 3为抢地主的牌
	 * @param list
	 * @return
	 */
	public static Map<Integer, List<Poker>> deal(){
		
		List<Poker> list = suffle();
		
		Map<Integer, List<Poker>> map = new HashMap<Integer, List<Poker>>(4);
		
		for(int i = 0; i < 3; i++){
			map.put(i, list.subList(i*17, (i+1)*17));
		}
		map.put(3, list.subList(17*3, 54));
		
		return map;
	}
	
	public static void main(String[] args) {
		List<Poker> list = suffle();
		
		
		Map<Integer, List<Poker>> map = deal();
		for(Iterator<Entry<Integer, List<Poker>>> it = map.entrySet().iterator(); it.hasNext(); ){
			Entry<Integer, List<Poker>> entry = it.next();
			System.out.println(entry.getKey() + ",,,,,,,,,,");
			for(Poker p : entry.getValue()){
				System.out.println(p.getName());
			}
		}
	}
}
