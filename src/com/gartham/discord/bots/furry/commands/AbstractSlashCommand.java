package com.gartham.discord.bots.furry.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class AbstractSlashCommand implements SlashCommand {
	private final String name;

	public AbstractSlashCommand(String name) {
		this.name = name;
	}

	@Override
	public final void onEvent(SlashCommandInteractionEvent e) {
		if (e.getName().equals(name))
			handle(e);
	}

	protected abstract void handle(SlashCommandInteractionEvent e);

}
