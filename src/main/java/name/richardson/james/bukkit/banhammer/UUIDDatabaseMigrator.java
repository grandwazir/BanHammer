package name.richardson.james.bukkit.banhammer;

import java.util.*;
import java.util.concurrent.Callable;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.persistence.database.DatabaseLoader;
import name.richardson.james.bukkit.utilities.persistence.database.SimpleDatabaseMigrator;

import name.richardson.james.bukkit.banhammer.ban.*;

public class UUIDDatabaseMigrator extends SimpleDatabaseMigrator {

	private List<OldBanRecord> bans;
	private List<OldPlayerRecord> players;

	public UUIDDatabaseMigrator(final DatabaseLoader oldDatabaseLoader, final DatabaseLoader newDatabaseLoader) {
		super(oldDatabaseLoader, newDatabaseLoader);
	}

	/**
	 * This method is called after the old database has been loaded but before the new one has been created. <p/> You should implement this method to get any
	 * records out of the previous database before it is dropped. It is essential that you store all the records you want in an Collection as once past this stage
	 * you can not make any queries on the old database.
	 *
	 * @param database the old database
	 * @since 7.0.0
	 */
	@Override
	public void beforeUpgrade(final EbeanServer database) {
		this.bans = database.find(OldBanRecord.class).findList();
		this.players = database.find(OldPlayerRecord.class).findList();
	}

	/**
	 * This method is called after the new database has been loaded. <p/> You should implement this method to insert any records you want from the old database.
	 *
	 * @param database the old database
	 * @since 7.0.0
	 */
	@Override
	public void afterUpgrade(final EbeanServer database) {
		List<String> names = new ArrayList<String>();
		for (OldPlayerRecord player : this.players) {
			names.add(player.getName());
		}
		final Map<String, UUID> uuids = getUUIDS(names);
		PlayerRecordManager playerRecordManager = new PlayerRecordManager(database);
		for (OldBanRecord ban : this.bans) {
			final PlayerRecordManager.BannedPlayerBuilder bannedPlayerBuilder = playerRecordManager.getBannedPlayerBuilder();
			UUID playerUUID = uuids.get(ban.getPlayer().getName());
			UUID creatorUUID = uuids.get(ban.getCreator().getName());
			bannedPlayerBuilder.setCreator(creatorUUID).setPlayer(playerUUID).setExpiresAt(ban.getExpiresAt()).setCreatedAt(ban.getCreatedAt()).save();
		}
		this.bans.clear();
		this.players.clear();
	}

	private Map<String,UUID> getUUIDS(List<String> names) {
		try {
			UUIDFetcher uuidFetcher = new UUIDFetcher(names);
			final Map<String,UUID> uuids = uuidFetcher.call();
			return uuids;
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyMap();
		}
	}
}
