package name.richardson.james.bukkit.banhammer.utilities.command.matcher;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.event.Listener;

import name.richardson.james.bukkit.utilities.command.matcher.Matcher;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

public class PlayerRecordMatcher implements Matcher, Listener {

	private final PlayerRecordManager.PlayerStatus mode;
	private final Set<String> playerNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
	private final PlayerRecordManager playerRecordManager;

	public PlayerRecordMatcher(PlayerRecordManager playerRecordManager, PlayerRecordManager.PlayerStatus mode) {
		this.playerRecordManager = playerRecordManager;
		this.mode = mode;
	}

	@Override
	public Set<String> matches(String argument) {
		TreeSet<String> results = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		argument = argument.toLowerCase(Locale.ENGLISH);
		for (String string : getPlayerNames()) {
			if (results.size() == Matcher.MAX_MATCHES) break;
			if (!string.toLowerCase(Locale.ENGLISH).startsWith(argument)) continue;
			results.add(string);
		}
		return results;
	}

	protected final Set<String> getPlayerNames() {
		return playerNames;
	}

	private Set<String> setPlayerNames() {
		List<PlayerRecord> playerRecordList = playerRecordManager.list("", mode);
		for (PlayerRecord playerRecord : playerRecordList) {
			playerNames.add(playerRecord.getName());
		}
		return playerNames;
	}

}
