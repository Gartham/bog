package com.gartham.discord.bots.furry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;

import org.alixia.javalibrary.JavaTools;
import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONParser;
import org.alixia.javalibrary.parsers.cli.CLIParams;
import org.alixia.javalibrary.streams.CharacterStream;

import com.gartham.utilities.bog.dictionary.parser.DictionaryEntryParser;
import com.gartham.utilities.bog.dictionary.parser.DictionaryEntryParser.Entry;
import com.gartham.utilities.bog.dictionary.parser.DictionaryEntrySplitter;

import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bog {

	public static void main(String[] args) throws LoginException, InterruptedException {
		Bog bog = new Bog(args);
		bog.start();
	}

	private final Map<String, UserData> data = new HashMap<>();

	/**
	 * Loads a user's {@link UserData} object given their ID. Uses the directory
	 * {@link BotConfiguration#getUserDataPath()} in {@link #config}. A new
	 * {@link UserData} is only created if one is not found in {@link #data}.
	 * 
	 * @param userid The id of the user to load the data object of.
	 * @return A {@link UserData} object that can be edited and flushed. Only one of
	 *         these should really be made during the program's life-cycle. The
	 *         program does not yet support reading from the file (nor two separate
	 *         {@link UserData}s representing the same user, since they would not be
	 *         able to synchronize edits made on each other).
	 */
	public synchronized UserData getUserData(String userid) {
		if (data.containsKey(userid))
			return data.get(userid);
		var file = new File(config.getUserDataPath(), userid);

		JSONObject obj = new JSONObject();
		if (file.isFile())
			try (InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
				JSONParser parser = new JSONParser();
				obj = (JSONObject) parser.parse(CharacterStream.from(isr));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassCastException e) {
				System.err.println("Invalid data inside user data file: " + file + ". File ignored. (USER " + userid
						+ " HAS NOT HAD DATA LOADED.)");
			}
		var userData = new UserData() {
			/**
			 * SUID
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void flush() throws IOException {
				try (PrintWriter pw = new PrintWriter(file)) {
					pw.print(toString());
				}
			}
		};
		userData.putAll(obj);
		data.put(userid, userData);
		return userData;
	}

	private final BotConfiguration config;
	private final Random random = new Random();

	public Bog(BotConfiguration config) {
		this.config = config;
	}

	public Bog(String... args) {
		this(new BotConfiguration(new CLIParams(args)));
	}

	public void start() throws LoginException, InterruptedException {
		File dir = new File("rsc/");
		File[] files = dir.listFiles();
		Map<String, List<Entry>> entriesByType = new HashMap<>();
		for (File f : files)
			try {
				DictionaryEntryParser dep = new DictionaryEntryParser(new DictionaryEntrySplitter(
						CharacterStream.from(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))));
				while (dep.peek() != null)
					JavaTools.putIntoListMap(entriesByType, dep.peek().getPos(), dep.next(), ArrayList::new);
			} catch (Exception e) {
				e.printStackTrace();
			}

		JDA jda = JDABuilder.createDefault(config.getToken(), GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
				.build();
		jda.awaitReady();
		jda.updateCommands()
				.addCommands(
						Commands.slash("beg", "Ask me for cash. I dare you. (Something random happens when you ask.)"),
						Commands.slash("word", "Pick the right definition and you'll be rewarded."))
				.complete();

		Map<String, Instant> lastBegUse = new HashMap<>();
		Timer timer = new Timer();

		jda.addEventListener((EventListener) event -> {
			if (event instanceof SlashCommandInteractionEvent) {
				var e = (SlashCommandInteractionEvent) event;
				if (e.getName().equals("beg")) {

					if (lastBegUse.containsKey(e.getUser().getId())) {
						long sec = Duration.between(lastBegUse.get(e.getUser().getId()), Instant.now()).abs()
								.toSeconds();
						if (sec < 45) {
							e.reply("You're running that command too fast! You need to wait " + (45 - sec)
									+ " seconds before reusing it.").complete();
							return;
						}
					}
					lastBegUse.put(e.getUser().getId(), Instant.now());

					var ud = getUserData(e.getUser().getId());
					int amount = random.nextInt(35) + 25;
					ud.getBalance().increase(amount);
					try {
						ud.flush();
					} catch (IOException e1) {
						System.err.println("Failed to flush " + e.getUser().getId());
					}

					var x = switch (random.nextInt(6)) {
					case 0 -> "while parachuting.";
					case 1 -> "at a murder scene.";
					case 2 -> "behind your ear.";
					case 3 -> "in a desk drawer.";
					case 4 -> "under a tree.";
					default -> "in a locker.";
					};

					e.reply("You found " + Utilities.format(amount) + ' ' + x + " You now have "
							+ Utilities.format(ud.getBalance().get()) + '.').complete();
				} else if (e.getName().equals("word")) {
					Entry[] entries = new Entry[3];
					for (int i = 0; i < entries.length; i++) {
						int type = random.nextInt(4);
						var t = entriesByType.get(switch (type) {
						case 0 -> "n.";
						case 1 -> "adv.";
						case 2 -> "a.";
						default -> "v. t.";
						});
						int ind = random.nextInt(t.size());
						while (entries[i] == t.get(ind))
							ind++;
						entries[i] = t.get(ind);
					}

					int ind = random.nextInt(entries.length);
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
										var amt = random.nextInt(200) + 60;
										remove();
										e.reply("You picked right and earned " + Utilities.format(amt) + '.')
												.complete();
										var ud = getUserData(id);
										ud.getBalance().increase(amt);
										try {
											ud.flush();
										} catch (IOException ex) {
											System.err.println("Failed to flush " + e.getUser().getId());
										}
									} else
										e.reply("Wrong. The definition of **" + entry.getWord() + "** is: "
												+ entry.getDefinition()).complete();
									remove();
								}
							}
						}

						private void remove() {
							removed = true;
							repl.editOriginalComponents(ActionRow.of(
									Button.primary("1", Emoji.fromUnicode("U+0031U+FE0FU+20E3")).asDisabled(),
									Button.primary("2", Emoji.fromUnicode("U+0032U+FE0FU+20E3")).asDisabled(),
									Button.primary("3", Emoji.fromUnicode("U+0033 U+FE0F U+20E3")).asDisabled()))
									.complete();
							jda.removeEventListener(this);

						}
					}

					CEL cel = new CEL();
					jda.addEventListener(cel);

					cel.repl = e
							.reply("Pick the right definition for this word: **" + entry.getWord()
									+ "**. You have `45` seconds. \n\n:one: " + entries[0].getDefinition() + "\n:two: "
									+ entries[1].getDefinition() + "\n:three: " + entries[2].getDefinition())
							.addActionRow(Button.primary("1", Emoji.fromUnicode("U+0031U+FE0FU+20E3")),
									Button.primary("2", Emoji.fromUnicode("U+0032U+FE0FU+20E3")),
									Button.primary("3", Emoji.fromUnicode("U+0033 U+FE0F U+20E3")))
							.complete();
					timer.schedule(cel.task, 45000);

				}

			}
		});

	}

	public BotConfiguration getConfig() {
		return config;
	}

}
