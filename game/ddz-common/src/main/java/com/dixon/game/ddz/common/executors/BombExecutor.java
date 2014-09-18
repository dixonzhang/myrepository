package com.dixon.game.ddz.common.executors;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dixon.game.ddz.common.bean.Poker;
import com.dixon.game.ddz.common.bean.PokerWraper;
import com.dixon.game.ddz.common.enu.PokerType;
import com.dixon.game.ddz.common.exceptions.WrongPokerTypeException;

/**
 * 炸弹，非王炸
 * @author dixon
 *
 */
public class BombExecutor implements Executor{
	public PokerWraper execute(Map<Integer, Integer> map, List<Poker> pokerList, PokerWraper pw) throws WrongPokerTypeException{
		//王炸
		if(map.size() == 2){
			int index = 0;
			for(Iterator<Integer> it = map.keySet().iterator(); it.hasNext(); ){
				int value = it.next();
				if(16 == value || 17 == value)
					index++;
			}
			//是王炸
			if(2 == index){
				pw.setPokerLeaderValue(17);
				pw.setPokerType(PokerType.bomb);
				return pw;
			}
			else
				throw new WrongPokerTypeException("wrong poker type");
		}
		
		//普通炸弹
		pw.setPokerLeaderValue(map.keySet().iterator().next());
		pw.setPokerType(PokerType.bomb);
		return pw;
	}
}
