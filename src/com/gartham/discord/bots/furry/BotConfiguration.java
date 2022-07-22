package com.gartham.discord.bots.furry;

import java.io.File;

import org.alixia.javalibrary.parsers.cli.CLIParams;

public class BotConfiguration {

	/*
	 * Add properties to configure the bot on startup here. These are parsed from
	 * command line arguments by the bot.
	 * 
	 * A couple example arguments are provided in the class. You can delete them if
	 * you'd like.
	 */

	public BotConfiguration(CLIParams cmdLineArguments) {
		token = cmdLineArguments.readString("", "--token");
		userDataPath = new File(cmdLineArguments.readString("", "--user-data-path", "-udp"));
	}

	private final String token;
	private final File userDataPath;

	public String getToken() {
		return token;
	}

	public File getUserDataPath() {
		return userDataPath;
	}

}
