package com.dixon.game.ddz.common.enu;

/**
 * 进去桌之后，玩家的状态，客户端以此做出判断该如何处理
 * @author dixon
 *
 */
public enum PlayerType {
	//  desktype is ready
	//需准备
	reading,
	//已准备
	readied,
	
	// desktype is deal
	//叫地主
	call, //其他两家waiting
	
	// desktype is grab
	//抢地主
	grab, //其他两家waiting

	// desktype is play
	//出牌
	first, //其他两家waiting
	//跟牌
	follow, //其他两家waiting
	//等待
	waiting,
	
	// desktype is gameover
	//赢
	winner,
	//输
	loser;
	
}
