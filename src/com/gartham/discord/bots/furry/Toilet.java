package com.gartham.discord.bots.furry;

import java.io.IOException;

/**
 * An interface used to represent objects that can be flushed. Flushing flushes
 * changes to some underlying backing store that contains a copy of the object.
 * For example, a program might have its Customer class implement Toilet so that
 * any changes to user data, say account balance or username, can be flushed to
 * a hard drive when needed.
 * 
 * @author Gartham
 * 
 */
public interface Toilet {
	void flush() throws IOException;
}
