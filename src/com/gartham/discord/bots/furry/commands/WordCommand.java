package com.gartham.discord.bots.furry.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.alixia.javalibrary.JavaTools;
import org.alixia.javalibrary.streams.CharacterStream;

import com.gartham.discord.bots.furry.Bog;
import com.gartham.utilities.bog.dictionary.parser.DictionaryEntryParser;
import com.gartham.utilities.bog.dictionary.parser.DictionaryEntryParser.Entry;
import com.gartham.utilities.bog.dictionary.parser.DictionaryEntrySplitter;

import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class WordCommand extends AbstractSlashCommand {

	private final Map<String, List<Entry>> entriesByType = new HashMap<>();
	{
		File dir = new File("rsc/");
		File[] files = dir.listFiles();
		for (File f : files)
			try {
				DictionaryEntryParser dep = new DictionaryEntryParser(new DictionaryEntrySplitter(
						CharacterStream.from(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))));
				while (dep.peek() != null)
					JavaTools.putIntoListMap(entriesByType, dep.peek().getPos(), dep.next(), ArrayList::new);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private final Bog bog;

	public WordCommand(Bog bog) {
		super("word", "Pick the right definition and you'll be rewarded.");
		this.bog = bog;
	}

	@Override
	protected void handle(SlashCommandInteractionEvent e) {
		Entry[] entries = new Entry[3];
		for (int i = 0; i < entries.length; i++) {
			int type = bog.getRandom().nextInt(4);
			var t = entriesByType.get(switch (type) {
			case 0 -> "n.";
			case 1 -> "adv.";
			case 2 -> "a.";
			default -> "v. t.";
			});
			int ind = bog.getRandom().nextInt(t.size());
			while (entries[i] == t.get(ind))
				ind++;
			entries[i] = t.get(ind);
		}

		int ind = bog.getRandom().nextInt(entries.length);
		var entry = entries[ind];

		var id = e.getUser().getId();
		var time = Instant.now();

		class CEL implements EventListener {
			private boolean removed;
			private InteractionHook repl;
			private final TimerTask task = new TimerTask() {

				@Override
				public void run() {
					try {
						remove();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			@Override
			public void onEvent(GenericEvent event) {
				if (Duration.between(Instant.now(), time).toSeconds() >= 45)
					remove();
				else if (event instanceof ButtonInteractionEvent) {
					var e = (ButtonInteractionEvent) event;
					if (e.getUser().getId().equals(id)) {
						if (e.getComponentId().equals(String.valueOf(ind + 1))) {
							var amt = bog.getRandom().nextInt(200) + 60;
							remove();

							var ud = bog.getUserData(id);
							e.reply("You picked right. Here's " + Utilities.format(amt) + ". You now have "
									+ Utilities.format(ud.getBalance().increase(amt)) + '.').complete();
							try {
								ud.flush();
							} catch (IOException ex) {
								System.err.println("Failed to flush " + e.getUser().getId());
							}
						} else
							e.reply("Wrong. The definition of **" + entry.getWord() + "** is: " + entry.getDefinition())
									.complete();
						remove();
					}
				}
			}

			private void disableReply() {
				repl.editOriginalComponents(
						ActionRow.of(Button.primary("1", Emoji.fromUnicode("U+0031U+FE0FU+20E3")).asDisabled(),
								Button.primary("2", Emoji.fromUnicode("U+0032U+FE0FU+20E3")).asDisabled(),
								Button.primary("3", Emoji.fromUnicode("U+0033 U+FE0F U+20E3")).asDisabled()))
						.setContent("Option `" + (ind + 1) + "` was correct!\n" + ":one: **" + entries[0].getWord()
								+ "** - " + entries[0].getDefinition() + "\n:two: **" + entries[1].getWord() + "** - "
								+ entries[1].getDefinition() + "\n:three: **" + entries[2].getWord() + "** - "
								+ entries[2].getDefinition())
						.complete();
			}

			private void remove() {
				removed = true;
				disableReply();
				bog.getJda().removeEventListener(this);

			}
		}

		CEL cel = new CEL();
		bog.getJda().addEventListener(cel);

		cel.repl = e
				.reply("Pick the right definition for this word: **" + entry.getWord()
						+ "**. You have `45` seconds. \n\n:one: " + entries[0].getDefinition() + "\n:two: "
						+ entries[1].getDefinition() + "\n:three: " + entries[2].getDefinition())
				.addActionRow(Button.primary("1", Emoji.fromUnicode("U+0031U+FE0FU+20E3")),
						Button.primary("2", Emoji.fromUnicode("U+0032U+FE0FU+20E3")),
						Button.primary("3", Emoji.fromUnicode("U+0033 U+FE0F U+20E3")))
				.complete();
		bog.getTimer().schedule(cel.task, 45000);

	}

}
