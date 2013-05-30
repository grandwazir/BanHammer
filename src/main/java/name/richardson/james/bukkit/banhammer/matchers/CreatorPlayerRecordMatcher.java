package name.richardson.james.bukkit.banhammer.matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;

public class CreatorPlayerRecordMatcher extends PlayerRecordMatcher {

	private final static Set<String> names = new TreeSet<String>();

	public static void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		names.add(event.getPlayerName().toLowerCase());
	}

	public static void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
		if (event.getRecord().getCreator().getCreatedBans().size() == 0) {
			names.remove(event.getRecord().getCreator().getName().toLowerCase());
		}
	}

	private static void getCreatorNameList() {
		final List<String> names = new ArrayList<String>();
		final List<PlayerRecord> records = PlayerRecordMatcher.getDatabase().find(PlayerRecord.class).findList();
		for (final PlayerRecord record : records) {
			if (record.getCreatedBans().size() == 0) {
				continue;
			}
			names.add(record.getName().toLowerCase());
		}
		CreatorPlayerRecordMatcher.names.clear();
		CreatorPlayerRecordMatcher.names.addAll(names);
	}

	public CreatorPlayerRecordMatcher() {
		if (CreatorPlayerRecordMatcher.names.isEmpty()) {
			CreatorPlayerRecordMatcher.getCreatorNameList();
		}
	}

	@Override
	public List<String> getMatches(String argument) {
		argument = argument.toLowerCase();
		final List<String> names = new ArrayList<String>();
		for (final String playerName : CreatorPlayerRecordMatcher.names) {
			if (playerName.startsWith(argument)) {
				names.add(playerName);
			}
		}
		return names;
	}

}
