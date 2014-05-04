package name.richardson.james.bukkit.banhammer.ban;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import com.avaje.ebean.EbeanServer;
import org.apache.commons.lang.Validate;

public class PlayerRecord extends Record {

	private UUID uuid;

	/**
	 * Create a PlayerRecord, if necessary, by providing a UUID. If a record already exists that will be returned instead.
	 *
	 * @param database the database to use.
	 * @param uuid the UUID to use.
	 * @return a PlayerRecord for this player.
	 */
	public static PlayerRecord create(EbeanServer database, UUID uuid) {
		PlayerRecord record = PlayerRecord.find(database, uuid);
		if (record == null) {
			record = new PlayerRecord();
			record.setUuid(uuid);
			database.save(record);
			record = PlayerRecord.find(database, uuid);
		}
		Validate.notNull(record);
		return record;
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
	public static PlayerRecord create(EbeanServer database, String playerName) {
		// TODO: Requires implementation of UUIDFetcher.
		return null;
	}

	/**
	 * Find a PlayerRecord by providing a UUID.
	 *
	 * @param database the database to use.
	 * @param uuid the UUID to search for.
	 * @return a record or null if no record exists.
	 */
	public static PlayerRecord find(EbeanServer database, UUID uuid) {
		return database.find(PlayerRecord.class).where().eq("uuid", uuid).findUnique();
	}

	/**
	 * Find a PlayerRecord by providing the name of a player.
	 *
	 * This method is potentially blocking.
	 *
	 * @param database the database to use.
	 * @param playerName the name to search for.
	 * @return a record or null if no record exists.
	 */
	public static PlayerRecord find(EbeanServer database, String playerName) {
		// TODO: Requires implementation of UUIDFetcher.
		return null;
	}

	/**
	 * Save a PlayerRecord
	 *
	 * @param database the database to use.
	 * @param record the record to save.
	 * @return true if the record was saved, false otherwise.
	 */
	public static boolean save(EbeanServer database, PlayerRecord record) {
		int count = database.save(Arrays.asList(record));
		return count != 0;
	}

	/**
	 * Save a collection of PlayerRecords
	 *
	 * @param database the database to use.
	 * @param record the records to save.
	 * @return the number of records saved successfully.
	 */
	public static int save(EbeanServer database, Collection<PlayerRecord> records) {
		return database.save(records);
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(final UUID uuid) {
		this.uuid = uuid;
	}

}
