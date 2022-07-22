package com.gartham.discord.bots.furry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.security.auth.login.LoginException;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONParser;
import org.alixia.javalibrary.parsers.cli.CLIParams;
import org.alixia.javalibrary.streams.CharacterStream;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
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
		JDA jda = JDABuilder.createDefault(config.getToken(), GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
				.build();
		jda.awaitReady();
		jda.upsertCommand(
				Commands.slash("beg", "Ask me for cash. I dare you. (Something random happens when you ask.)"))
				.complete();

		jda.addEventListener((EventListener) event -> {
			if (event instanceof SlashCommandInteractionEvent) {
				var e = (SlashCommandInteractionEvent) event;
				var ud = getUserData(e.getUser().getId());
			}
		});
	}

	public BotConfiguration getConfig() {
		return config;
	}

}