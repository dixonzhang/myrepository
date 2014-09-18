package com.dixon.game.ddz.common.executors;

import java.util.List;
import java.util.Map;

import com.dixon.game.ddz.common.bean.Poker;
import com.dixon.game.ddz.common.bean.PokerWraper;
import com.dixon.game.ddz.common.enu.PokerType;
import com.dixon.game.ddz.common.exceptions.WrongPokerTypeException;

/**
 * 一对
 * @author dixon
 *
 */
public class PairExecutor implements Executor{
	public PokerWraper execute(Map<Integer, Integer> map, List<Poker> pokerList, PokerWraper pw) throws WrongPokerTypeException{
		pw.setPokerLeaderValue(map.keySet().iterator().next());
		pw.setPokerType(PokerType.pair);
		return pw;
	}
}
