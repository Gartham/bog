package com.gartham.discord.bots.furry.commands;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class SlashCommand implements EventListener {

	private final String name, description;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public SlashCommand(String name, String description) {
		this.name = name;
		this.description = description;
	}

	@Override
	public void onEvent(GenericEvent event) {
		if (event instanceof SlashCommandInteractionEvent)
			onEvent((SlashCommandInteractionEvent) event);
	}

	public abstract void onEvent(SlashCommandInteractionEvent e);

	public SlashCommandData makeCommand() {
		return Commands.slash(name, description);
	}
	
}
