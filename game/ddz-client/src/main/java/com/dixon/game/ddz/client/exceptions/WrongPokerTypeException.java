package com.dixon.game.ddz.client.exceptions;

public class WrongPokerTypeException extends Exception {
	private static final long serialVersionUID = -6382075835534378261L;
	
	public WrongPokerTypeException(String msg) {
		super(msg);
	}
}