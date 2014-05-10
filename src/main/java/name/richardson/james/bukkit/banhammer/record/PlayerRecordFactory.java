package name.richardson.james.bukkit.banhammer.record;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import com.avaje.ebean.EbeanServer;
import org.apache.commons.lang.Validate;

public final class PlayerRecordFactory {

	private PlayerRecordFactory() {}

	/**
	 * Find PlayerRecords that start with the name provided and match a specified status.
	 *
	 * The search is case insensitive.
	 *
	 * @param database the database to use.
	 * @param playerName the name to search for.
	 * @param playerStatus that status to match.
	 * @return an Iterable of PlayerRecords that match.
	 */
	public static Set<PlayerRecord> find(final EbeanServer database, final String playerName, final PlayerRecord.PlayerStatus playerStatus) {
		Set<? extends PlayerRecord> records = database.find(CurrentPlayerRecord.class).where().istartsWith("lastKnownName", playerName).findSet();
		final Iterator<? extends PlayerRecord> iterator = records.iterator();
		switch (playerStatus) {
			case BANNED:
				while (iterator.hasNext()) {
					final PlayerRecord element = iterator.next();
					if (!element.isBanned()) {
						records.remove(element);
					}
				}
			case CREATOR:
				while (iterator.hasNext()) {
					final PlayerRecord element = iterator.next();
					if (element.getCreatedBans().isEmpty()) {
						records.remove(element);
					}
				}
		}
		return new HashSet<PlayerRecord>(records);
	}

	/**
	 * Find a PlayerRecord by providing a UUID.
	 *
	 * @param database the database to use.
	 * @param uuid the UUID to search for.
	 * @return a record or null if no record exists.
	 */
	public static PlayerRecord find(EbeanServer database, UUID uuid) {
		return database.find(CurrentPlayerRecord.class).where().eq("uuid", uuid).findUnique();
	}

	/**
	 * Find a PlayerRecord by providing the name of a player.
	 *
	 * @param database the database to use.
	 * @param playerName the name to search for.
	 * @return a record or null if no record exists.
	 */
	public static PlayerRecord find(EbeanServer database, String playerName) {
		return database.find(CurrentPlayerRecord.class).where().eq("lastKnownName", playerName).findUnique();
	}

	/**
	 * Create a PlayerRecord, if necessary, by providing a player name. If a record already exists that will be returned instead.
	 *
	 * This method is potentially blocking.
	 *
	 * @param database the database to use.
	 * @param playerName the name to use.
	 * @return a PlayerRecord for this player.
	 */
	public static PlayerRecord findOrCreate(EbeanServer database, String playerName) {
		PlayerRecord record = find(database, playerName);
		if (record == null) {
			record = new CurrentPlayerRecord(playerName);
			if (record.getUuid() == null) return null;
			database.save(record);
			record = find(database, playerName);
		}
		Validate.notNull(record);
		return record;
	}

	/**
	 * Create a PlayerRecord, if necessary, by providing a UUID. If a record already exists that will be returned instead.
	 *
	 * @param database the database to use.
	 * @param uuid the UUID to use.
	 * @return a PlayerRecord for this player.
	 */
	public static PlayerRecord findOrCreate(EbeanServer database, UUID uuid) {
		PlayerRecord record = find(database, uuid);
		if (record == null) {
			record = new CurrentPlayerRecord(uuid);
			database.save(record);
			record = find(database, uuid);
		}
		Validate.notNull(record);
		return record;
	}

}
