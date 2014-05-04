package name.richardson.james.bukkit.banhammer.ban;

import javax.persistence.PersistenceException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.logging.PluginLoggerFactory;

public class PlayerRecordManager {

	public enum PlayerStatus {
		ANY,
		BANNED,
		CREATOR
	}
	private final EbeanServer database;
	private final Logger logger = PluginLoggerFactory.getLogger(PlayerRecordManager.class);

	public PlayerRecordManager(EbeanServer database) {
		if (database == null) throw new IllegalArgumentException();
		this.database = database;
	}

	public int count() {
		return this.database.find(OldPlayerRecord.class).findRowCount();
	}

	public OldPlayerRecord create(String playerName) {
		OldPlayerRecord record = this.find(playerName);
		if (record != null) return record;
		logger.log(Level.FINER, "Creating OldPlayerRecord for " + playerName);
		record = new OldPlayerRecord();
		record.setName(playerName);
		this.save(record);
		return this.find(playerName);
	}

	public void delete(OldPlayerRecord record) {
		this.delete(Arrays.asList(record));
	}

	public void delete(List<OldPlayerRecord> records) {
		logger.log(Level.FINER, "Deleting PlayerRecords: " + records);
		this.database.delete(records);
	}

	public boolean exists(String playerName) {
		logger.log(Level.FINER, "Checking to see if OldPlayerRecord exists for " + playerName);
		return find(playerName) != null;
	}

	public OldPlayerRecord find(String playerName) {
		logger.log(Level.FINER, "Finding OldPlayerRecord for " + playerName);
		try {
			return database.find(OldPlayerRecord.class).where().ieq("name", playerName).findUnique();
		} catch (PersistenceException e) {
			this.removeDuplicates(playerName);
			return database.find(OldPlayerRecord.class).where().ieq("name", playerName).findUnique();
		}
	}

	public BannedPlayerBuilder getBannedPlayerBuilder() {
		return new BannedPlayerBuilder();
	}

	public List<OldPlayerRecord> list(String playerName, PlayerStatus status) {
		switch (status) {
			case BANNED: {
				List<OldPlayerRecord> records = database.find(OldPlayerRecord.class).where().istartsWith("name", playerName).findList();
				ListIterator<OldPlayerRecord> i = records.listIterator();
				while (i.hasNext()) {
					OldPlayerRecord record = i.next();
					if (record.isBanned()) continue;
					i.remove();
				}
				return records;
			} case CREATOR: {
				List<OldPlayerRecord> records = database.find(OldPlayerRecord.class).where().istartsWith("name", playerName).findList();
				ListIterator<OldPlayerRecord> i = records.listIterator();
				while (i.hasNext()) {
					OldPlayerRecord record = i.next();
					if (record.getCreatedBans().size() > 0) continue;
					i.remove();
				}
				return records;
			} default: {
				return database.find(OldPlayerRecord.class).where().istartsWith("name", playerName).findList();
			}
		}
	}

	public List<OldPlayerRecord> list() {
		logger.log(Level.FINER, "Returning list containing all PlayerRecords.");
		return database.find(OldPlayerRecord.class).findList();
	}

	public void save(OldPlayerRecord record) {
		this.save(Arrays.asList(record));
	}

	public void save(List<OldPlayerRecord> records) {
		logger.log(Level.FINER, "Saving PlayerRecords: " + records);
		this.database.save(records);
	}

	protected EbeanServer getDatabase() {
		return database;
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
		logger.log(Level.WARNING, "duplicate-record-found");
		final List<OldPlayerRecord> records = database.find(OldPlayerRecord.class).where().ieq("name", playerName).findList();
		for (final OldPlayerRecord record : records) {
			if ((record.getCreatedBans().size() == 0) && (record.getBans().size() == 0)) {
				this.delete(record);
			}
		}
	}

	public class BannedPlayerBuilder {

		private final OldBanRecord record;

		private BannedPlayerBuilder() {
			this.record = new OldBanRecord();
			this.record.setState(OldBanRecord.State.NORMAL);
			this.setExpiryTime(0);
		}

		public OldBanRecord getRecord() {
			return record;
		}

		public boolean save() {
			BanRecordManager manager = new BanRecordManager(PlayerRecordManager.this.getDatabase());
			return manager.save(record);
		}

		public BannedPlayerBuilder setCreator(String playerName) {
			this.record.setCreator(create(playerName));
			return this;
		}

		public BannedPlayerBuilder setExpiresAt(Timestamp timestamp) {
			long now = System.currentTimeMillis();
			this.record.setCreatedAt(new Timestamp(now));
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

	}
}
