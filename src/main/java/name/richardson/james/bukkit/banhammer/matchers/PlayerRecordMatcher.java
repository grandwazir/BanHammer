package name.richardson.james.bukkit.banhammer.matchers;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.matchers.Matcher;

public abstract class PlayerRecordMatcher implements Matcher {

	private static EbeanServer database;

	protected static EbeanServer getDatabase() {
		return database;
	}

	public PlayerRecordMatcher(final EbeanServer database) {
		PlayerRecordMatcher.database = database;
	}

}
