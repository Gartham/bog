package com.gartham.discord.bots.furry;

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

public class BotLauncher {

	public static void main(String[] args) throws LoginException, InterruptedException {
		BotConfiguration config = new BotConfiguration(new CLIParams(args));

		JDA jda = JDABuilder.createDefault(config.getToken(), GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
				.build();

		jda.awaitReady();

		jda.upsertCommand(Commands.slash("beg", "Ask me for cash. I dare you.")).complete();

		jda.addEventListener((EventListener) event -> {
			if (event instanceof SlashCommandInteractionEvent) {
				var e = (SlashCommandInteractionEvent) event;

			}
		});
	}

}
