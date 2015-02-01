/*
 * Copyright (c) 2015 James Richardson.
 *
 * UpgradeLoader.java is part of ban-hammer.
 *
 * ban-hammer is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ban-hammer is distributed in the hope that it will be useful, but WITHOUTANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along wit ban-hammer. If not, see <http://www.gnu.org/licenses/>.
 */

package name.richardson.james.bukkit.banhammer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import name.richardson.james.bukkit.utilities.persistence.AbstractDatabaseMigrator;
import name.richardson.james.bukkit.utilities.persistence.DatabaseLoader;
import name.richardson.james.bukkit.utilities.persistence.DatabaseMigrator;

import name.richardson.james.bukkit.banhammer.model.legacy.OldBanRecord;
import name.richardson.james.bukkit.banhammer.model.legacy.OldPlayerRecord;

public class UpgradeLoader extends AbstractDatabaseMigrator {

	private static final Logger LOGGER = LogManager.getLogger();
	private final DatabaseLoader oldDatabaseLoader;
	private final DatabaseLoader newDatabaseLoader;

	private List<OldBanRecord> bansToMigrate;
	private List<OldPlayerRecord> playersToMigrate;

	public UpgradeLoader(final DatabaseLoader oldDatabaseLoader, final DatabaseLoader newDatabaseLoader) {
		super(oldDatabaseLoader, newDatabaseLoader);
		this.oldDatabaseLoader = oldDatabaseLoader;
		this.newDatabaseLoader = newDatabaseLoader;
	}

	@Override public void afterUpgrade(final EbeanServer ebeanServer) {
		LOGGER.info("[BanHammer] This may take a while depending on the size of your database.");
		List<String> playerNames = new ArrayList<>();
		for (OldPlayerRecord record: playersToMigrate) {
			playerNames.add(record.getName());
		}
		UUIDFetcher uuidFetcher = new UUIDFetcher(playerNames, true);
		Map<String,UUID> nameUUIDMap = new HashMap<>();
		try {
			nameUUIDMap = uuidFetcher.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Iterator i = nameUUIDMap.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry<String, UUID> pairs = (Map.Entry) i.next();
			PlayerRecord.create(pairs.getValue(), pairs.getKey());
		}
		for (OldBanRecord oldRecord : bansToMigrate) {
			BanRecord.migrate(oldRecord);
		}
		LOGGER.info("[BanHammer] " + ebeanServer.find(PlayerRecord.class).findRowCount() + " players migrated.");
		LOGGER.info("[BanHammer] " + ebeanServer.find(BanRecord.class).findRowCount() + " bans migrated.");
	}

	@Override public void beforeUpgrade(final EbeanServer ebeanServer) {
		LOGGER.warn("[BanHammer] Migrating database...");
		playersToMigrate = ebeanServer.find(OldPlayerRecord.class).findList();
		bansToMigrate = ebeanServer.find(OldBanRecord.class).findList();
	}

}
