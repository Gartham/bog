package com.gartham.discord.bots.furry.commands;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.gartham.discord.bots.furry.Bog;

import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class BegCommand extends AbstractSlashCommand {

	private final Bog bog;

	public BegCommand(Bog bog) {
		super("beg");
		this.bog = bog;
	}

	private final Map<String, Instant> lastBegUse = new HashMap<>();

	@Override
	protected void handle(SlashCommandInteractionEvent e) {

		if (lastBegUse.containsKey(e.getUser().getId())) {
			long sec = Duration.between(lastBegUse.get(e.getUser().getId()), Instant.now()).abs().toSeconds();
			if (sec < 45) {
				e.reply("You're running that command too fast! You need to wait " + (45 - sec)
						+ " seconds before reusing it.").complete();
				return;
			}
		}
		lastBegUse.put(e.getUser().getId(), Instant.now());

		var ud = bog.getUserData(e.getUser().getId());
		int amount = bog.getRandom().nextInt(35) + 25;
		ud.getBalance().increase(amount);
		try {
			ud.flush();
		} catch (IOException e1) {
			System.err.println("Failed to flush " + e.getUser().getId());
		}

		var x = switch (bog.getRandom().nextInt(6)) {
		case 0 -> "while parachuting.";
		case 1 -> "at a murder scene.";
		case 2 -> "behind your ear.";
		case 3 -> "in a desk drawer.";
		case 4 -> "under a tree.";
		default -> "in a locker.";
		};

		e.reply("You found " + Utilities.format(amount) + ' ' + x + " You now have "
				+ Utilities.format(ud.getBalance().get()) + '.').complete();

	}

}
