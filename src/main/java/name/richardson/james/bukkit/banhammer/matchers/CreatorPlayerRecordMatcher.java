package name.richardson.james.bukkit.banhammer.matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;

public class CreatorPlayerRecordMatcher extends PlayerRecordMatcher implements Listener {

	private final static Set<String> names = new TreeSet<String>();

	@EventHandler(priority = EventPriority.MONITOR)
	public static void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		names.remove(event.getPlayerName());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public static void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
		if (event.getRecord().getCreator().getCreatedBans().size() == 0) {
			names.remove(event.getRecord().getCreator().getName());
		}
	}

	private static void getCreatorNameList() {
		final List<String> names = new ArrayList<String>();
		final List<PlayerRecord> records = PlayerRecordMatcher.getDatabase().find(PlayerRecord.class).findList();
		for (final PlayerRecord record : records) {
			if (record.getCreatedBans().size() == 0) {
				continue;
			}
			names.add(record.getName());
		}
		names.clear();
		CreatorPlayerRecordMatcher.names.addAll(names);
	}

	public CreatorPlayerRecordMatcher(final EbeanServer database) {
		super(database);
		if (CreatorPlayerRecordMatcher.names == null) {
			CreatorPlayerRecordMatcher.getCreatorNameList();
		}
	}

	public List<String> getMatches(final String argument) {
		final List<String> names = new ArrayList<String>();
		for (final String playerName : CreatorPlayerRecordMatcher.names) {
			if (playerName.startsWith(argument)) {
				names.add(playerName);
			}
		}
		return names;
	}

}
