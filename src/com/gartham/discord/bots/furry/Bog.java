package com.gartham.discord.bots.furry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;

import javax.security.auth.login.LoginException;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONParser;
import org.alixia.javalibrary.parsers.cli.CLIParams;
import org.alixia.javalibrary.streams.CharacterStream;

import com.gartham.discord.bots.furry.commands.BalanceCommand;
import com.gartham.discord.bots.furry.commands.BegCommand;
import com.gartham.discord.bots.furry.commands.WordCommand;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
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

	/**
	 * Returns <code>true</code> if a {@link UserData} for the specified user has
	 * already been loaded or if the file for that user data exists.
	 * 
	 * @param userid The ID of the user.
	 * @return <code>true</code> if the {@link UserData} has been loaded or if its
	 *         file exists, <code>false</code> otherwise.
	 */
	public synchronized boolean hasUserData(String userid) {
		return data.containsKey(userid) || new File(config.getUserDataPath(), userid).isFile();
	}

	private final BotConfiguration config;
	private final Random random = new Random();
	private final Timer timer = new Timer();
	private JDA jda;

	public JDA getJda() {
		return jda;
	}

	public Timer getTimer() {
		return timer;
	}

	public Random getRandom() {
		return random;
	}

	public Bog(BotConfiguration config) {
		this.config = config;
	}

	public Bog(String... args) {
		this(new BotConfiguration(new CLIParams(args)));
	}

	public void start() throws LoginException, InterruptedException {
		jda = JDABuilder.createDefault(config.getToken(), GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)).build();
		jda.awaitReady();

		BegCommand bc = new BegCommand(this);
		WordCommand wc = new WordCommand(this);
		BalanceCommand bal = new BalanceCommand(this);

		jda.updateCommands().addCommands(bc.command(), wc.command(), bal.command()).complete();
		jda.addEventListener(bc, wc, bal);
	}

	public BotConfiguration getConfig() {
		return config;
	}

}
