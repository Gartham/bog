package com.gartham.discord.bots.furry.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class AbstractSlashCommand extends SlashCommand {

	public AbstractSlashCommand(String name, String description) {
		super(name, description);
	}

	public AbstractSlashCommand(SlashCommandData command) {
		super(command);
	}

	@Override
	public final void onEvent(SlashCommandInteractionEvent e) {
		if (e.getName().equals(getName()))
			handle(e);
	}

	protected abstract void handle(SlashCommandInteractionEvent e);

}
