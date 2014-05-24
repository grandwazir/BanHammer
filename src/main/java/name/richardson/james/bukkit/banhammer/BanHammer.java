/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * BanHammer.java is part of BanHammer.
 *
 * BanHammer is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.config.ServerConfig;

import name.richardson.james.bukkit.utilities.command.*;
import name.richardson.james.bukkit.utilities.logging.PluginLoggerFactory;
import name.richardson.james.bukkit.utilities.logging.PrefixedLogger;
import name.richardson.james.bukkit.utilities.persistence.configuration.DatabaseConfiguration;
import name.richardson.james.bukkit.utilities.persistence.configuration.SimpleDatabaseConfiguration;
import name.richardson.james.bukkit.utilities.persistence.database.DatabaseLoader;
import name.richardson.james.bukkit.utilities.persistence.database.DatabaseLoaderFactory;
import name.richardson.james.bukkit.utilities.updater.BukkitDevPluginUpdater;
import name.richardson.james.bukkit.utilities.updater.PluginUpdater;

import name.richardson.james.bukkit.banhammer.record.CurrentBanRecord;
import name.richardson.james.bukkit.banhammer.record.CurrentPlayerRecord;
import name.richardson.james.bukkit.banhammer.event.PlayerListener;
import name.richardson.james.bukkit.banhammer.event.PlayerNotifier;
import name.richardson.james.bukkit.banhammer.commands.*;
import name.richardson.james.bukkit.banhammer.record.PlayerRecord;
import name.richardson.james.bukkit.banhammer.record.PlayerRecordFactory;

public final class BanHammer extends JavaPlugin {

	public static final String NOTIFY_PERMISSION_NAME = "banhammer.notify";
	public static final int PROJECT_ID = 31269;

	private static final String CONFIG_NAME = "config.yml";
	private static final String DATABASE_CONFIG_NAME = "database.yml";
	public static final String TABLE_PREFIX = "bh_";

	private final Logger logger = PluginLoggerFactory.getLogger(BanHammer.class);

	private PluginConfiguration configuration;
	private EbeanServer database;

	public EbeanServer getDatabase() {
		return database;
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		final List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(CurrentBanRecord.class);
		classes.add(CurrentPlayerRecord.class);
		return classes;
	}

	@Override
	public void onEnable() {
		try {
			this.loadConfiguration();
			this.loadDatabase();
			this.registerCommands();
			this.registerListeners();
			// TODO: Reimplement this - this.setupMetrics();
			this.updatePlugin();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void updatePlugin() {
		if (!configuration.getAutomaticUpdaterState().equals(PluginUpdater.State.OFF)) {
			PluginUpdater updater = new BukkitDevPluginUpdater(this.getDescription(), configuration.getAutomaticUpdaterBranch(), configuration.getAutomaticUpdaterState(), PROJECT_ID, this.getDataFolder(), Bukkit.getVersion());
			this.getServer().getScheduler().runTaskAsynchronously(this, updater);
			new name.richardson.james.bukkit.utilities.updater.PlayerNotifier(this, this.getServer().getPluginManager(), updater);
		}
	}

	private void loadConfiguration()
	throws IOException {
		PrefixedLogger.setPrefix(this.getName());
		final File file = new File(this.getDataFolder().getPath() + File.separatorChar + CONFIG_NAME);
		final InputStream defaults = this.getResource(CONFIG_NAME);
		this.configuration = new PluginConfiguration(file, defaults);
		this.logger.setLevel(configuration.getLogLevel());
	}

	private void loadDatabase()
	throws IOException {
		ServerConfig serverConfig = new ServerConfig();
		getServer().configureDbConfig(serverConfig);
		serverConfig.setClasses(getDatabaseClasses());
		serverConfig.setName(this.getName());
		final File file = new File(this.getDataFolder().getPath() + File.separatorChar + DATABASE_CONFIG_NAME);
		final InputStream defaults = this.getResource(DATABASE_CONFIG_NAME);
		final DatabaseConfiguration configuration = new SimpleDatabaseConfiguration(file, defaults, serverConfig, this.getName());
		final DatabaseLoader loader = DatabaseLoaderFactory.getDatabaseLoader(configuration);
		loader.initalise();
		this.database = loader.getEbeanServer();
		CurrentPlayerRecord record = new CurrentPlayerRecord();
		record.setLastKnownName("SYSTEM");
		record.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		record.setUuid(null);
		database.save(record);
	}

	private void registerCommands() {
		Set<Command> commands = new HashSet<Command>();
		AbstractCommand command = new AuditCommand(getDatabase());
		commands.add(command);
		command = new BanCommand(this.getServer(), this.getServer().getPluginManager(), database, configuration.getBanLimits(), configuration.getImmunePlayers());
		commands.add(command);
		getCommand("ban").setExecutor(new FallthroughCommandInvoker(this, this.getServer().getScheduler(), command));
		command = new CheckCommand(database);
		commands.add(command);
		command = new HistoryCommand(database);
		commands.add(command);
		command = new ExportCommand(database, getServer());
		commands.add(command);
		command = new ImportCommand(getServer(), database);
		commands.add(command);
		command = new KickCommand(getServer());
		commands.add(command);
		getCommand("kick").setExecutor(new FallthroughCommandInvoker(this, this.getServer().getScheduler(), command));
		command = new LimitsCommand(this.configuration);
		commands.add(command);
		command = new PardonCommand(getServer().getPluginManager(), database);
		commands.add(command);
		getCommand("pardon").setExecutor(new FallthroughCommandInvoker(this, this.getServer().getScheduler(), command));
		command = new PurgeCommand(database);
		commands.add(command);
		command = new RecentCommand(database);
		commands.add(command);
		command = new UndoCommand(database, configuration.getUndoTime());
		commands.add(command);
		// findOrCreate the invoker
		command = new HelpCommand(commands, "bh");
		CommandInvoker invoker = new FallthroughCommandInvoker(this, this.getServer().getScheduler(), command);
		invoker.addCommands(commands);
		// bind invoker to plugin command
		getCommand("bh").setExecutor(invoker);
	}

	private void registerListeners() {
		new PlayerListener(this, this.getServer().getPluginManager(), getServer(), database);
		new PlayerNotifier(this, this.getServer().getPluginManager(), getServer());
	}

}
