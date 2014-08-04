/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 BanHammerDatabase.java is part of BanHammer.

 BanHammer is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option) any
 later version.

 BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Server;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.config.ServerConfig;

import name.richardson.james.bukkit.utilities.persistence.DatabaseLoader;
import name.richardson.james.bukkit.utilities.persistence.DatabaseLoaderFactory;
import name.richardson.james.bukkit.utilities.persistence.configuration.DatabaseConfiguration;
import name.richardson.james.bukkit.utilities.persistence.configuration.SimpleDatabaseConfiguration;

public final class BanHammerDatabase {

	private static final String DATABASE_NAME = "BanHammer";
	private static EbeanServer database;

	private BanHammerDatabase() {}

	public static DatabaseConfiguration configure(Server server, File file, InputStream defaults)
	throws IOException {
		ServerConfig serverConfig = new ServerConfig();
		server.configureDbConfig(serverConfig);
		serverConfig.setClasses(getDatabaseClasses());
		serverConfig.setName(DATABASE_NAME);
		return new SimpleDatabaseConfiguration(file, defaults, DATABASE_NAME, serverConfig);
	}

	protected static EbeanServer getDatabase() {
		if (database == null) throw new IllegalStateException("Database has not yet been initialised!");
		return database;
	}

	public static List<Class<?>> getDatabaseClasses() {
		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(BanRecord.class);
		classes.add(PlayerRecord.class);
		classes.add(CommentRecord.class);
		return classes;
	}

	public static void initialise(DatabaseConfiguration configuration) {
		DatabaseLoader loader = DatabaseLoaderFactory.getDatabaseLoader(configuration);
		loader.initalise();
		database = loader.getEbeanServer();
		PlayerRecord.create(new UUID(0, 0), "CONSOLE");
	}

}
