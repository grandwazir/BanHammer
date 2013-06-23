package name.richardson.james.bukkit.banhammer.matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import name.richardson.james.bukkit.banhammer.ban.event.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.ban.event.BanHammerPlayerPardonedEvent;

import name.richardson.james.bukkit.utilities.matchers.Matcher;

public class PlayerRecordMatcher implements Matcher {

	private final static Set<String> names = new TreeSet<String>();

	public static void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		names.add(event.getPlayerName().toLowerCase());
	}

	public static void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
		names.remove(event.getPlayerName().toLowerCase());
	}

	public static void setNameList(final Set<String> names) {
		PlayerRecordMatcher.names.clear();
		PlayerRecordMatcher.names.addAll(names);
	}

	public List<String> getMatches(String argument) {
		argument = argument.toLowerCase();
		final List<String> names = new ArrayList<String>();
		// this is here to prevent large database sets disconnecting clients
		// up to around 1000 names appears to be ok at once.
		if (argument.length() != 0) {
			for (final String playerName : PlayerRecordMatcher.names) {
				if (playerName.startsWith(argument)) {
					names.add(playerName);
				}
			}
		}
		return names;
	}

}
