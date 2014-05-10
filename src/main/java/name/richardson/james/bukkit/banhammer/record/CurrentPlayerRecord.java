package name.richardson.james.bukkit.banhammer.record;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;
import org.apache.commons.lang.Validate;

import name.richardson.james.bukkit.banhammer.utilities.NameFetcher;

@Entity
@Table(name = "banhammer_players")
public class CurrentPlayerRecord extends SimpleRecord implements PlayerRecord {

	@OneToMany(mappedBy = "player", targetEntity = CurrentBanRecord.class, cascade = {CascadeType.REMOVE})
	private List<CurrentBanRecord> bans;
	@OneToMany(mappedBy = "creator", targetEntity = CurrentBanRecord.class)
	private List<CurrentBanRecord> createdBans;
	@Id
	@NotNull
	private long id;
	@NotNull
	private String lastKnownName;
	private UUID uuid;

	protected CurrentPlayerRecord() {}

	public CurrentPlayerRecord(UUID uuid) {
		final Timestamp now = new Timestamp(System.currentTimeMillis());
		if (uuid != null) {
			final String name = NameFetcher.getNameOf(uuid);
			this.setLastKnownName(name);
		}	else {
			this.setLastKnownName("CONSOLE");
		}
		this.setUuid(uuid);
		this.setCreatedAt(now);
	}

	public CurrentPlayerRecord(String playerName) {
		Validate.notNull(playerName);
		final Timestamp now = new Timestamp(System.currentTimeMillis());
		String name = NameFetcher.getNameOf(uuid);
		this.setLastKnownName(name);
		this.setUuid(uuid);
		this.setCreatedAt(now);
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
		PlayerRecord record = CurrentPlayerRecord.find(database, playerName);
		if (record == null) {
			record = new CurrentPlayerRecord(playerName);
			database.save(record);
			record = CurrentPlayerRecord.find(database, playerName);
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
		PlayerRecord record = CurrentPlayerRecord.find(database, uuid);
		if (record == null) {
			record = new CurrentPlayerRecord(uuid);
			database.save(record);
			record = CurrentPlayerRecord.find(database, uuid);
		}
		Validate.notNull(record);
		return record;
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

	@Override public BanRecord getActiveBan() {
		for (BanRecord record : this.getBans()) {
			if (record.getState() == BanRecord.State.NORMAL) return record;
		}
		return null;
	}

	@Override public List<BanRecord> getBans() {
		List<BanRecord> bans = new ArrayList<BanRecord>();
		if (this.bans != null) bans.addAll(bans);
		return bans;
	}

	@Override public List<BanRecord> getCreatedBans() {
		List<BanRecord> bans = new ArrayList<BanRecord>();
		if (this.createdBans != null) bans.addAll(createdBans);
		return bans;
	}

	@Override public long getId() {
		return id;
	}

	@Override public String getLastKnownName() {
		return lastKnownName;
	}

	@Override public UUID getUuid() {
		return uuid;
	}

	@Override public boolean isBanned() {
		for (BanRecord record : this.getBans()) {
			if (record.getState() == BanRecord.State.NORMAL) return true;
		}
		return false;
	}

	@Override public void setBans(final Set<BanRecord> bans) {
		this.bans.clear();
		for (BanRecord ban : bans) {
			if (ban instanceof CurrentBanRecord) this.bans.add((CurrentBanRecord) ban);
		}
	}

	@Override public void setCreatedBans(final Set<BanRecord> createdBans) {
		this.createdBans.clear();
		for (BanRecord ban : bans) {
			if (ban instanceof CurrentBanRecord) this.createdBans.add((CurrentBanRecord) ban);
		}
	}

	@Override public void setId(long id) {
		this.id = id;
	}

	@Override public void setLastKnownName(final String lastKnownName) {
		this.lastKnownName = lastKnownName;
	}

	@Override public void setUuid(final UUID uuid) {
		this.uuid = uuid;
	}

}
