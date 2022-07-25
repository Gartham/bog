package com.gartham.discord.bots.furry.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class AbstractSlashCommand extends SlashCommand {

	public AbstractSlashCommand(String name, String description) {
		super(name, description);
	}

	@Override
	public final void onEvent(SlashCommandInteractionEvent e) {
		if (e.getName().equals(getName()))
			handle(e);
	}

	protected abstract void handle(SlashCommandInteractionEvent e);

}
