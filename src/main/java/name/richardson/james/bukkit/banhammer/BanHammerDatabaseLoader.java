/*
 * Copyright (c) 2015 James Richardson.
 *
 * BanHammerDatabaseLoader.java is part of ban-hammer.
 *
 * ban-hammer is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ban-hammer is distributed in the hope that it will be useful, but WITHOUTANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along wit ban-hammer. If not, see <http://www.gnu.org/licenses/>.
 */

package name.richardson.james.bukkit.banhammer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import name.richardson.james.bukkit.utilities.persistence.DatabaseLoader;
import name.richardson.james.bukkit.utilities.persistence.DefaultDatabaseLoader;
import name.richardson.james.bukkit.utilities.persistence.configuration.DatabaseConfiguration;

import name.richardson.james.bukkit.banhammer.model.BanHammerDatabase;
import name.richardson.james.bukkit.banhammer.model.BanRecord;
import name.richardson.james.bukkit.banhammer.model.PlayerRecord;
import name.richardson.james.bukkit.banhammer.model.UUIDFetcher;
import name.richardson.james.bukkit.banhammer.model.legacy.OldBanRecord;
import name.richardson.james.bukkit.banhammer.model.legacy.OldPlayerRecord;

public class BanHammerDatabaseLoader extends DefaultDatabaseLoader {

	private static final Logger LOGGER = LogManager.getLogger();

	private List<OldBanRecord> bansToMigrate;
	private List<OldPlayerRecord> playersToMigrate;

	public BanHammerDatabaseLoader(final DatabaseConfiguration configuration) {
		super(configuration);
	}

	@Override protected void afterDatabaseCreate() {
		BanHammerDatabase.initialise(this);
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
		LOGGER.info("[BanHammer] " + getEbeanServer().find(PlayerRecord.class).findRowCount() + " players migrated.");
		LOGGER.info("[BanHammer] " + getEbeanServer().find(BanRecord.class).findRowCount() + " bans migrated.");
	}

	@Override protected void beforeDatabaseDrop() {
		playersToMigrate = getEbeanServer().find(OldPlayerRecord.class).findList();
		bansToMigrate = getEbeanServer().find(OldBanRecord.class).fetch("creator").fetch("player").findList();
	}

}
