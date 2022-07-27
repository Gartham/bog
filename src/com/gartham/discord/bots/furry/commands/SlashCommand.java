package com.gartham.discord.bots.furry.commands;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class SlashCommand implements EventListener {

	private final SlashCommandData command;

	public String getName() {
		return command.getName();
	}

	public String getDescription() {
		return command.getDescription();
	}

	public SlashCommand(String name, String description) {
		command = Commands.slash(name, description);
	}

	public SlashCommand(SlashCommandData command) {
		this.command = command;
	}

	@Override
	public void onEvent(GenericEvent event) {
		if (event instanceof SlashCommandInteractionEvent)
			onEvent((SlashCommandInteractionEvent) event);
	}

	public abstract void onEvent(SlashCommandInteractionEvent e);

	public SlashCommandData command() {
		return command;
	}

}
