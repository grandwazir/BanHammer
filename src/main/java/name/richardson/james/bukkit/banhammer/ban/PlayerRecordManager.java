package name.richardson.james.bukkit.banhammer.ban;

import javax.persistence.PersistenceException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.logging.PrefixedLogger;

public class PlayerRecordManager {

	public enum PlayerStatus {
		ANY,
		BANNED,
		CREATOR
	}

	private final Logger LOGGER = PrefixedLogger.getLogger(PlayerRecordManager.class);

	private final EbeanServer database;

	public PlayerRecordManager(EbeanServer database) {
		if (database == null) throw new IllegalArgumentException();
		this.database = database;
	}

	public int count() {
		return this.database.find(PlayerRecord.class).findRowCount();
	}

	public PlayerRecord create(String playerName) {
		PlayerRecord record = this.find(playerName);
		if (record != null) return record;
		LOGGER.log(Level.FINER, "Creating PlayerRecord for " + playerName);
		record = new PlayerRecord();
		record.setName(playerName);
		this.save(record);
		return this.find(playerName);
	}

	public void delete(PlayerRecord record) {
		LOGGER.log(Level.FINER, "Deleting PlayerRecord for " + record.getName());
		this.database.delete(record);
	}

	public void delete(List<PlayerRecord> records) {
		this.database.delete(records);
	}

	public boolean exists(String playerName) {
		LOGGER.log(Level.FINER, "Checking to see if PlayerRecord exists for " + playerName);
		try {
			return (database.find(PlayerRecord.class).where().ieq("name", playerName).findUnique() != null);
		} catch (PersistenceException e) {
			this.removeDuplicates(playerName);
			return (database.find(PlayerRecord.class).where().ieq("name", playerName).findUnique() != null);
		}
	}

	public PlayerRecord find(String playerName) {
		LOGGER.log(Level.FINER, "Finding PlayerRecord for " + playerName);
		try {
			return database.find(PlayerRecord.class).where().ieq("name", playerName).findUnique();
		} catch (PersistenceException e) {
			this.removeDuplicates(playerName);
			return database.find(PlayerRecord.class).where().ieq("name", playerName).findUnique();
		}
	}

	public List<PlayerRecord> list(String playerName, PlayerStatus status) {
		switch (status) {
			case ANY: {
				return database.find(PlayerRecord.class).where().startsWith("name", playerName).findList();
			} case BANNED: {
				List<PlayerRecord> records = database.find(PlayerRecord.class).where().startsWith("name", playerName).findList();
				ListIterator<PlayerRecord> i = records.listIterator();
				while (i.hasNext()) {
					PlayerRecord record = i.next();
					if (record.isBanned()) continue;
					i.remove();
				}
				return records;
			} case CREATOR: {
				List<PlayerRecord> records = database.find(PlayerRecord.class).where().startsWith("name", playerName).findList();
				ListIterator<PlayerRecord> i = records.listIterator();
				while (i.hasNext()) {
					PlayerRecord record = i.next();
					if (record.isBanned()) continue;
					i.remove();
				}
				return records;
			}
		}
		return Collections.emptyList();
	}

	public List<PlayerRecord> list() {
		LOGGER.log(Level.FINER, "Returning list containing all PlayerRecords.");
		return database.find(PlayerRecord.class).findList();
	}

	public void save(PlayerRecord record) {
		LOGGER.log(Level.FINER, "Saving PlayerRecord for " + record.getName());
		this.database.save(record);
	}

	public void save(List<PlayerRecord> records) {
		this.database.save(records);
	}

	public void update(PlayerRecord record) {
		LOGGER.log(Level.FINER, "Updating PlayerRecord for " + record.getName());
		this.update(record);
	}

	public void update(List<PlayerRecord> records) {
		this.database.update(records);
	}

	/**
	 * Delete duplicate player records.
	 * <p/>
	 * This happened due to a bug introduced around version 2.0. I thought it was
	 * not a major problem but it appears to be causing issues for many players.
	 * This will automatically fix any issues as they are found.
	 *
	 * @param playerName the player name
	 */
	private void removeDuplicates(String playerName) {
		LOGGER.log(Level.WARNING, "duplicate-record-found");
		final List<PlayerRecord> records = database.find(PlayerRecord.class).where().ieq("name", playerName).findList();
		for (final PlayerRecord record : records) {
			if ((record.getCreatedBans().size() == 0) && (record.getBans().size() == 0)) {
				this.delete(record);
			}
		}
	}

	public class BannedPlayerBuilder {

		private final BanRecord record;

		public BannedPlayerBuilder() {
			this.record = new BanRecord();
			this.record.setState(BanRecord.State.NORMAL);
			this.setExpiryTime(0);
		}

		public BannedPlayerBuilder setCreator(String playerName) {
			this.record.setCreator(create(playerName));
			return this;
		}

		public BannedPlayerBuilder setExpiresAt(Timestamp timestamp) {
			this.record.setExpiresAt(timestamp);
			return this;
		}

		public BannedPlayerBuilder setExpiryTime(long time) {
			long now = System.currentTimeMillis();
			this.record.setCreatedAt(new Timestamp(now));
			if (time != 0) this.record.setExpiresAt(new Timestamp(now + time));
			return this;
		}

		public BannedPlayerBuilder setPlayer(String playerName) {
			this.record.setPlayer(create(playerName));
			return this;
		}

		public BannedPlayerBuilder setReason(String reason) {
			this.record.setReason(reason);
			return this;
		}

		public boolean save() {
			BanRecordManager manager = new BanRecordManager(database);
			return manager.save(record);
		}

	}
}
