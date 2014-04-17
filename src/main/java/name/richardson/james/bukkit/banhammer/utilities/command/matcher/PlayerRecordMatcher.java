package name.richardson.james.bukkit.banhammer.utilities.command.matcher;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import name.richardson.james.bukkit.utilities.command.argument.suggester.Suggester;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

public class PlayerRecordMatcher implements Suggester {

	public static int MINIMUM_ARGUMENT_LENGTH = 3;

	private final PlayerRecordManager.PlayerStatus mode;
	private final PlayerRecordManager playerRecordManager;

	public PlayerRecordMatcher(PlayerRecordManager playerRecordManager, PlayerRecordManager.PlayerStatus mode) {
		this.playerRecordManager = playerRecordManager;
		this.mode = mode;
	}

	@Override
	public Set<String> suggestValue(String argument) {
		if (argument.length() < MINIMUM_ARGUMENT_LENGTH) return Collections.emptySet();
		TreeSet<String> results = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		argument = argument.toLowerCase(Locale.ENGLISH);
		for (PlayerRecord playerRecord : playerRecordManager.list(argument, mode)) {
			if (results.size() == Suggester.MAX_MATCHES) break;
			if (!playerRecord.getName().toLowerCase(Locale.ENGLISH).startsWith(argument)) continue;
			results.add(playerRecord.getName());
		}
		return results;
	}

}
