package name.richardson.james.bukkit.banhammer.matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;

public class    CreatorPlayerRecordMatcher extends PlayerRecordMatcher {

	private final static Set<String> names = new TreeSet<String>();

	public static void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		names.add(event.getPlayerName().toLowerCase());
	}

	public static void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
		if (event.getRecord().getCreator().getCreatedBans().size() == 0) {
			names.remove(event.getRecord().getCreator().getName().toLowerCase());
		}
	}

	public static void setNameList(final Set<String> names) {
		CreatorPlayerRecordMatcher.names.clear();
		CreatorPlayerRecordMatcher.names.addAll(names);
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
