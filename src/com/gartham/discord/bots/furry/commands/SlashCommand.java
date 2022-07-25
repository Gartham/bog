package com.gartham.discord.bots.furry.commands;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public interface SlashCommand extends EventListener {
	@Override
	default void onEvent(GenericEvent event) {
		if (event instanceof SlashCommandInteractionEvent)
			onEvent((SlashCommandInteractionEvent) event);
	}

	void onEvent(SlashCommandInteractionEvent e);
}
