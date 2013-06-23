package name.richardson.james.bukkit.banhammer.persistence;

import com.avaje.ebean.EbeanServer;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import name.richardson.james.bukkit.utilities.localisation.PluginResourceBundle;
import name.richardson.james.bukkit.utilities.logging.PrefixedLogger;

public class PlayerRecordManager {

	private static final Logger LOGGER = PrefixedLogger.getLogger(PlayerRecordManager.class);
	private static final ResourceBundle RESOURCE_BUNDLE = PluginResourceBundle.getBundle(PlayerRecordManager.class);

	private final EbeanServer database;

	public PlayerRecordManager(EbeanServer database) {
		if (database == null) throw new IllegalArgumentException();
		this.database = database;
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

	public PlayerRecord find(String playerName) {
		LOGGER.log(Level.FINER, "Finding PlayerRecord for " + playerName);
		try {
			return database.find(PlayerRecord.class).where().ieq("name", playerName).findUnique();
		} catch (PersistenceException e) {
			this.removeDuplicates(playerName);
			return database.find(PlayerRecord.class).where().ieq("name", playerName).findUnique();
		}
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

	public List<PlayerRecord> list() {
		LOGGER.log(Level.FINER, "Returning list containing all PlayerRecords.");
		return database.find(PlayerRecord.class).findList();
	}

	/**
	 * Delete duplicate player records.
	 *
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

	public void delete(PlayerRecord record) {
		LOGGER.log(Level.FINER, "Deleting PlayerRecord for " + record.getName());
		this.database.delete(record);
	}

	public void delete(List<PlayerRecord> records) {
		this.database.delete(records);
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
}
