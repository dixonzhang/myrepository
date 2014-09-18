package com.dixon.game.ddz.common.executors;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dixon.game.ddz.common.bean.Poker;
import com.dixon.game.ddz.common.bean.PokerWraper;
import com.dixon.game.ddz.common.enu.PokerType;
import com.dixon.game.ddz.common.exceptions.WrongPokerTypeException;

/**
 * 三 or 三带一x
 * @author dixon
 *
 */
public class ThreeExecutor implements Executor{
	public PokerWraper execute(Map<Integer, Integer> map, List<Poker> pokerList, PokerWraper pw) throws WrongPokerTypeException{
		for(Iterator<Entry<Integer, Integer>> it = map.entrySet().iterator(); it.hasNext(); ){
			Entry<Integer, Integer> entry = it.next();
			if(entry.getValue() == 3){
				pw.setPokerLeaderValue(entry.getKey());
				pw.setPokerType(PokerType.three);
			}
			//TODO 是否可以带王？
		}
		return pw;
	}
}
