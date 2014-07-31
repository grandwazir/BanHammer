package name.richardson.james.bukkit.banhammer.player;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import name.richardson.james.bukkit.utilities.command.argument.suggester.Suggester;

public class PlayerRecordMatcher implements Suggester {

	public static int MINIMUM_ARGUMENT_LENGTH = 3;
	private final PlayerRecord.Status mode;

	public PlayerRecordMatcher(PlayerRecord.Status mode) {
		this.mode = mode;
	}

	@Override
	public Set<String> suggestValue(String argument) {
		if (argument.length() < MINIMUM_ARGUMENT_LENGTH) return Collections.emptySet();
		TreeSet<String> results = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		argument = argument.toLowerCase(Locale.ENGLISH);
		for (PlayerRecord playerRecord : PlayerRecord.find(mode, argument)) {
			if (results.size() == Suggester.MAX_MATCHES) break;
			if (!playerRecord.getName().toLowerCase(Locale.ENGLISH).startsWith(argument)) continue;
			results.add(playerRecord.getName());
		}
		return results;
	}

}
