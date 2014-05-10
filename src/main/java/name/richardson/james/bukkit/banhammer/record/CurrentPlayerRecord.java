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

	/**
	 * Create a PlayerRecord, if necessary, by providing a UUID. If a record already exists that will be returned instead.
	 *
	 * @param database the database to use.
	 * @param uuid the UUID to use.
	 * @return a PlayerRecord for this player.
	 */
	public static CurrentPlayerRecord create(EbeanServer database, UUID uuid) {
		CurrentPlayerRecord record = CurrentPlayerRecord.find(database, uuid);
		if (record == null) {
			record = new CurrentPlayerRecord();
			record.setUuid(uuid);
			String playerName = NameFetcher.getNameOf(uuid);
			record.setLastKnownName(playerName);
			record.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			database.save(record);
			record = CurrentPlayerRecord.find(database, uuid);
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
		UUID uuid = null;
		return create(database, uuid);
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
		Set<PlayerRecord> records = database.find(CurrentPlayerRecord.class).where().istartsWith("lastKnownName", playerName).findSet();
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

	/**
	 * Find a PlayerRecord by providing a UUID.
	 *
	 * @param database the database to use.
	 * @param uuid the UUID to search for.
	 * @return a record or null if no record exists.
	 */
	public static CurrentPlayerRecord find(EbeanServer database, UUID uuid) {
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

	@Override public CurrentBanRecord getActiveBan() {
		for (CurrentBanRecord record : this.getBans()) {
			if (record.getState() == BanRecord.State.NORMAL) return record;
		}
		return null;
	}

	@Override public List<CurrentBanRecord> getBans() {
		return (bans == null) ? Collections.<CurrentBanRecord>emptyList() : bans;
	}

	@Override public List<CurrentBanRecord> getCreatedBans() {
		return (createdBans == null) ? Collections.<CurrentBanRecord>emptyList() : createdBans;
	}

	@Override public long getId() {
		return id;
	}

	@Override public void setId(long id) {
		this.id = id;
	}

	@Override public String getLastKnownName() {
		return lastKnownName;
	}

	@Override public UUID getUuid() {
		return uuid;
	}

	@Override public boolean isBanned() {
		for (CurrentBanRecord record : this.getBans()) {
			if (record.getState() == BanRecord.State.NORMAL) return true;
		}
		return false;
	}

	@Override public void setBans(final List<CurrentBanRecord> bans) {
		this.bans = bans;
	}

	@Override public void setCreatedBans(final List<CurrentBanRecord> createdBans) {
		this.createdBans = createdBans;
	}

	@Override public void setLastKnownName(final String lastKnownName) {
		this.lastKnownName = lastKnownName;
	}

	@Override public void setUuid(final UUID uuid) {
		this.uuid = uuid;
	}

	public void updateName() {
		String playerName = NameFetcher.getNameOf(uuid);
		setLastKnownName(playerName);
	}

}
