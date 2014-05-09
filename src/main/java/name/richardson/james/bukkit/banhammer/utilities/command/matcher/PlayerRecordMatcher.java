package name.richardson.james.bukkit.banhammer.utilities.command.matcher;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.entity.Player;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.command.argument.suggester.Suggester;

import name.richardson.james.bukkit.banhammer.ban.OldPlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

public class PlayerRecordMatcher implements Suggester {

	public static int MINIMUM_ARGUMENT_LENGTH = 3;

	private final EbeanServer database;
	private final PlayerRecord.PlayerStatus mode;

	public PlayerRecordMatcher(EbeanServer database, PlayerRecord.PlayerStatus mode) {
		this.database = database;
		this.mode = mode;
	}

	@Override
	public Set<String> suggestValue(String argument) {
		if (argument.length() < MINIMUM_ARGUMENT_LENGTH) return Collections.emptySet();
		TreeSet<String> results = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		argument = argument.toLowerCase(Locale.ENGLISH);
		for (PlayerRecord playerRecord : PlayerRecord.find(database, argument, mode)) {
			if (results.size() == Suggester.MAX_MATCHES) break;
			if (!playerRecord.getLastKnownName().toLowerCase(Locale.ENGLISH).startsWith(argument)) continue;
			results.add(playerRecord.getLastKnownName());
		}
		return results;
	}

}
