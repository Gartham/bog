package com.gartham.discord.bots.furry.commands;

import java.math.BigInteger;
import java.util.Collections;

import com.gartham.discord.bots.furry.Bog;

import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class BalanceCommand extends AbstractSlashCommand {

	private final Bog bog;

	public BalanceCommand(Bog bog) {
		super(Commands.slash("balance", "Shows your balance (or the balance of another user).")
				.addOption(OptionType.USER, "user", "The user to get the balance of.", false));
		this.bog = bog;
	}

	@Override
	protected void handle(SlashCommandInteractionEvent e) {
		var target = e.getOption("user", e.getUser(), OptionMapping::getAsUser);
		e.reply(target.getAsMention() + " has " + Utilities.format(
				bog.hasUserData(target.getId()) ? bog.getUserData(target.getId()).getBalance().get() : BigInteger.ZERO)
				+ ".").allowedMentions(Collections.emptyList()).queue();
	}

}
