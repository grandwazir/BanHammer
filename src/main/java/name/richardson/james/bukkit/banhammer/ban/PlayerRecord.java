package name.richardson.james.bukkit.banhammer.ban;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

import com.avaje.ebean.EbeanServer;
import org.apache.commons.lang.Validate;

import name.richardson.james.bukkit.banhammer.utilities.UUIDFetcher;

public class PlayerRecord extends Record {

	private static final Map<String, UUID> UUIDS = new ConcurrentSkipListMap<String, UUID>(String.CASE_INSENSITIVE_ORDER);

	@OneToMany(mappedBy = "player", targetEntity = BanRecord.class, cascade = { CascadeType.REMOVE })
	private List<BanRecord> bans;

	@OneToMany(mappedBy = "creator", targetEntity = BanRecord.class)
	private List<BanRecord> createdBans;

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
		UUID uuid = getUUIDOf(playerName);
		return create(database, uuid);
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
		UUID uuid = getUUIDOf(playerName);
		return find(database, uuid);
	}

	private static UUID getUUIDOf(final String playerName) {
		if (!UUIDS.containsKey(playerName)) {
			try {
				UUID uuid = UUIDFetcher.getUUIDOf(playerName);
				UUIDS.put(playerName, uuid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return UUIDS.get(playerName);
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
	 * @param records the records to save.
	 * @return the number of records saved successfully.
	 */
	public static int save(EbeanServer database, Collection<PlayerRecord> records) {
		return database.save(records);
	}

	public List<BanRecord> getBans() {
		return (bans == null) ? Collections.<BanRecord>emptyList() : bans;
	}

	public List<BanRecord> getCreatedBans() {
		return (createdBans == null) ? Collections.<BanRecord>emptyList() : createdBans;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setBans(final List<BanRecord> bans) {
		this.bans = bans;
	}

	public void setCreatedBans(final List<BanRecord> createdBans) {
		this.createdBans = createdBans;
	}

	public void setUuid(final UUID uuid) {
		this.uuid = uuid;
	}

}
