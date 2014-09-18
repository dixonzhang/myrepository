package com.dixon.game.ddz.common.executors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dixon.game.ddz.common.bean.Poker;
import com.dixon.game.ddz.common.bean.PokerWraper;
import com.dixon.game.ddz.common.enu.PokerType;
import com.dixon.game.ddz.common.exceptions.WrongPokerTypeException;

/**
 * 连对
 * @author dixon
 *
 */
public class MultiPairExecutor implements Executor{
	public PokerWraper execute(Map<Integer, Integer> map, List<Poker> pokerList, PokerWraper pw) throws WrongPokerTypeException{
		List<Integer> valueList = new ArrayList<Integer>(map.size());
		for(Iterator<Integer> it = map.keySet().iterator(); it.hasNext(); ){
			valueList.add(it.next());
		}
		
		Collections.sort(valueList);
		
		//最大的牌值不能大于14（即比A还要大）
		if(valueList.get(valueList.size()-1) > 14)
			throw new WrongPokerTypeException("wrong poker type");
		
		//检查是否是顺子
		int valueCount = 0;
		for(Integer value : valueList){
			valueCount += value;
		}
		//等差数列求和 (a1+an)*n/2
		int n = valueList.size();
		int a1 = valueList.get(0);
		int an = valueList.get(n-1);
		
		int targetCount = (a1+an)*n/2;
		
		if(valueCount == targetCount && (an-a1)/(n-1) == 1){
			pw.setPokerLeaderValue(an);
			pw.setPokerType(PokerType.multiPair);
			return pw;
		}
		else
			throw new WrongPokerTypeException("wrong poker type");
		
	}
}
