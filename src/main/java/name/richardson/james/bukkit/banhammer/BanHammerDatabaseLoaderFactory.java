/*
 * Copyright (c) 2015 James Richardson.
 *
 * DatabaseLoaderFactory.java is part of ban-hammer.
 *
 * ban-hammer is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ban-hammer is distributed in the hope that it will be useful, but WITHOUTANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along wit ban-hammer. If not, see <http://www.gnu.org/licenses/>.
 */

package name.richardson.james.bukkit.banhammer;

import name.richardson.james.bukkit.utilities.persistence.DatabaseLoader;
import name.richardson.james.bukkit.utilities.persistence.DefaultDatabaseLoader;
import name.richardson.james.bukkit.utilities.persistence.EntityPersistenceController;
import name.richardson.james.bukkit.utilities.persistence.SQLiteDatabaseLoader;
import name.richardson.james.bukkit.utilities.persistence.configuration.DatabaseConfiguration;

/**
 * Create and return a suitable database loader depending on the database name.richardson.james.bukkit.utilities.persistence.configuration provided. This is used to abstract away the implementation requirement
 * to have a different database loader for SQLite due to a bug in the schema generation when using the Ebean versions shipped with Bukkit.
 */
public class BanHammerDatabaseLoaderFactory {

	/**
	 * Returns a database loader configured with the provided database name.richardson.james.bukkit.utilities.persistence.configuration.
	 *
	 * @param configuration the name.richardson.james.bukkit.utilities.persistence.configuration to use for the database loader
	 * @return the database loader
	 */
	public static DatabaseLoader getDatabaseLoader(DatabaseConfiguration configuration) {
		configuration.getServerConfig().add(new EntityPersistenceController());
		if (configuration.getDataSourceConfig().getDriver().contains("sqlite")) {
			return new BanHammerSQLiteDatabaseLoader(configuration);
		} else {
			return new BanHammerDatabaseLoader(configuration);
		}
	}

}
