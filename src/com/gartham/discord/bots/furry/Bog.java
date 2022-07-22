package com.gartham.discord.bots.furry;

import java.io.File;
import java.util.Random;

import javax.security.auth.login.LoginException;

import org.alixia.javalibrary.parsers.cli.CLIParams;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bog {

	public static void main(String[] args) throws LoginException, InterruptedException {
		Bog bog = new Bog(args);
		bog.start();
	}

	/**
	 * Loads a user's {@link UserData} object given their ID. Uses the directory
	 * {@link BotConfiguration#getUserDataPath()} in {@link #config}.
	 * 
	 * @param userid
	 * @return
	 */
	public UserData load(String userid) {
		var file = new File(config.getUserDataPath(), userid);
		return new UserData(file);
	}

	private final BotConfiguration config;
	private final Random random = new Random();

	public Bog(BotConfiguration config) {
		this.config = config;
	}

	public Bog(String... args) {
		this(new BotConfiguration(new CLIParams(args)));
	}

	public void start() throws LoginException, InterruptedException {
		JDA jda = JDABuilder.createDefault(config.getToken(), GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
				.build();
		jda.awaitReady();
		jda.upsertCommand(
				Commands.slash("beg", "Ask me for cash. I dare you. (Something random happens when you ask.)"))
				.complete();

		jda.addEventListener((EventListener) event -> {
			if (event instanceof SlashCommandInteractionEvent) {
				var e = (SlashCommandInteractionEvent) event;

			}
		});
	}

	public BotConfiguration getConfig() {
		return config;
	}

}
