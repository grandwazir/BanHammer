package name.richardson.james.bukkit.banhammer.ban;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;
import org.apache.commons.lang.Validate;

import name.richardson.james.bukkit.banhammer.utilities.NameFetcher;
import name.richardson.james.bukkit.banhammer.utilities.UUIDFetcher;

public class PlayerRecord extends Record {

	public enum PlayerStatus {
		ANY,
		BANNED,
		CREATOR
	}

	private static final Map<String, UUID> UUIDS = new ConcurrentSkipListMap<String, UUID>(String.CASE_INSENSITIVE_ORDER);

	@OneToMany(mappedBy = "player", targetEntity = BanRecord.class, cascade = { CascadeType.REMOVE })
	private List<BanRecord> bans;

	@OneToMany(mappedBy = "creator", targetEntity = BanRecord.class)
	private List<BanRecord> createdBans;

	@NotNull
	private String lastKnownName;

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
			String playerName = NameFetcher.getNameOf(uuid);
			record.setLastKnownName(playerName);
			database.save(record);
			record = PlayerRecord.find(database, uuid);
		}
		Validate.notNull(record);
		return record;
	}

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
	public static Set<PlayerRecord> find(final EbeanServer database, final String playerName, final PlayerStatus playerStatus) {
		Set<PlayerRecord> records = database.find(PlayerRecord.class).where().istartsWith("lastKnownName", playerName).findSet();
		final Iterator<PlayerRecord> iterator = records.iterator();
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
		return records;
	}

	public void updateName() {
		String playerName = NameFetcher.getNameOf(uuid);
		setLastKnownName(playerName);
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
	 * @param database the database to use.
	 * @param playerName the name to search for.
	 * @return a record or null if no record exists.
	 */
	public static PlayerRecord find(EbeanServer database, String playerName) {
		return database.find(PlayerRecord.class).where().eq("lastKnownName", playerName).findUnique();
	}

	private static String getNameOf(final UUID uuid) {
		if (!UUIDS.containsValue(uuid)) {
			try {
				String playerName = NameFetcher.getNameOf(uuid);
				UUIDS.put(playerName, uuid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String playerName = null;
		for (Map.Entry<String, UUID> entry : UUIDS.entrySet()) {
			if (entry.getValue() == uuid) {
				playerName = entry.getKey();
				break;
			}
		}
		return playerName;
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

	public String getLastKnownName() {
		return lastKnownName;
	}

	public String getCurrentName() {
		return PlayerRecord.getNameOf(uuid);
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

	public void setLastKnownName(final String lastKnownName) {
		this.lastKnownName = lastKnownName;
	}

	public void setUuid(final UUID uuid) {
		this.uuid = uuid;
	}

	public BanRecord getActiveBan() {
		for (BanRecord record : this.getBans()) {
			if (record.getState() == BanRecord.State.NORMAL) return record;
		}
		return null;
	}

	public boolean isBanned() {
		for (BanRecord record : this.getBans()) {
			if (record.getState() == BanRecord.State.NORMAL) return true;
		}
		return false;
	}

}
