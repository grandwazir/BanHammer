package name.richardson.james.bukkit.banhammer.matchers;

import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;

public class CreatorPlayerRecordMatcher extends PlayerRecordMatcher {

	public CreatorPlayerRecordMatcher(final EbeanServer database) {
		super(database);
	}

	public List<String> getMatches(final String argument) {
		final List<String> names = new ArrayList<String>();
		final List<PlayerRecord> records = this.getDatabase().find(PlayerRecord.class).where().istartsWith("name", argument).findList();
		for (final PlayerRecord record : records) {
			if (record.getCreatedBans().size() == 0) {
				continue;
			}
			names.add(record.getName());
		}
		return names;
	}

}
