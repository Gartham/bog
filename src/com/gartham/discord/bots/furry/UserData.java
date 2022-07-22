package com.gartham.discord.bots.furry;

import java.io.IOException;

public abstract class UserData extends DataObject implements Toilet {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	private final BigIntegerEntry balance = new BigIntegerEntry("balance");

	public BigIntegerEntry getBalance() {
		return balance;
	}

}
