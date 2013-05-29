package name.richardson.james.bukkit.banhammer.matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.matchers.Matcher;

public class PlayerRecordMatcher implements Matcher {

	private static EbeanServer database;

	private final static Set<String> names = new TreeSet<String>();

	public static void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		names.add(event.getPlayerName().toLowerCase());
	}

	public static void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
		names.remove(event.getPlayerName().toLowerCase());
	}

	public static void setDatabase(final EbeanServer database) {
		PlayerRecordMatcher.database = database;
	}

	protected static EbeanServer getDatabase() {
		return database;
	}

	private static void getNameList() {
		final List<PlayerRecord> records = PlayerRecord.list(database);
		PlayerRecordMatcher.names.clear();
		for (final PlayerRecord record : records) {
			PlayerRecordMatcher.names.add(record.getName().toLowerCase());
		}
	}

	public PlayerRecordMatcher() {
		if (PlayerRecordMatcher.names.isEmpty()) {
			PlayerRecordMatcher.getNameList();
		}

	}

	public List<String> getMatches(String argument) {
		argument = argument.toLowerCase();
		final List<String> names = new ArrayList<String>();
		for (final String playerName : PlayerRecordMatcher.names) {
			if (playerName.startsWith(argument)) {
				names.add(playerName);
			}
		}
		return names;
	}

}
