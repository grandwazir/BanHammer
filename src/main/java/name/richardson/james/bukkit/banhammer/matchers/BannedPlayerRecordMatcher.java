package name.richardson.james.bukkit.banhammer.matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;

public class BannedPlayerRecordMatcher extends PlayerRecordMatcher {

	private final static Set<String> names = new TreeSet<String>();

	public static void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		names.add(event.getPlayerName().toLowerCase());
	}

	public static void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
		names.remove(event.getPlayerName().toLowerCase());
	}

	private static void getBannedPlayerNameList() {
		final List<String> names = new ArrayList<String>();
		final List<PlayerRecord> records = PlayerRecordMatcher.getDatabase().find(PlayerRecord.class).findList();
		for (final PlayerRecord record : records) {
			if (record.isBanned()) {
				names.add(record.getName().toLowerCase());
			}
		}
		BannedPlayerRecordMatcher.names.clear();
		BannedPlayerRecordMatcher.names.addAll(names);
	}

	public BannedPlayerRecordMatcher() {
		if (BannedPlayerRecordMatcher.names.isEmpty()) {
			BannedPlayerRecordMatcher.getBannedPlayerNameList();
		}
	}

	@Override
	public List<String> getMatches(String argument) {
		argument = argument.toLowerCase();
		final List<String> names = new ArrayList<String>();
		for (final String playerName : BannedPlayerRecordMatcher.names) {
			if (playerName.startsWith(argument)) {
				names.add(playerName);
			}
		}
		return names;
	}
}
