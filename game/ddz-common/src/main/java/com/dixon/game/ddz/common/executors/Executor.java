package com.dixon.game.ddz.common.executors;

import java.util.List;
import java.util.Map;

import com.dixon.game.ddz.common.bean.Poker;
import com.dixon.game.ddz.common.bean.PokerWraper;
import com.dixon.game.ddz.common.exceptions.WrongPokerTypeException;

public interface Executor {
	public PokerWraper	execute(Map<Integer, Integer> map, List<Poker> pokerList, PokerWraper pokerWraper) throws WrongPokerTypeException;
}
