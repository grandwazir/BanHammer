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
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.config.ServerConfig;

import name.richardson.james.bukkit.utilities.command.Command;
import name.richardson.james.bukkit.utilities.command.CommandInvoker;
import name.richardson.james.bukkit.utilities.command.RootCommandInvoker;
import name.richardson.james.bukkit.utilities.command.SimpleCommandInvoker;
import name.richardson.james.bukkit.utilities.persistence.DatabaseLoader;
import name.richardson.james.bukkit.utilities.persistence.DatabaseLoaderFactory;
import name.richardson.james.bukkit.utilities.persistence.configuration.DatabaseConfiguration;
import name.richardson.james.bukkit.utilities.persistence.configuration.SimpleDatabaseConfiguration;
import name.richardson.james.bukkit.utilities.updater.BukkitDevPluginUpdater;
import name.richardson.james.bukkit.utilities.updater.PluginUpdater;

import name.richardson.james.bukkit.banhammer.ban.*;
import name.richardson.james.bukkit.banhammer.player.*;

public class BanHammer extends JavaPlugin {

	public static final String NOTIFY_PERMISSION_NAME = "banhammer.notify";
	public static final int PROJECT_ID = 31269;
	private static final String CONFIG_NAME = "config.yml";
	private static final String DATABASE_CONFIG_NAME = "database.yml";
	private static EbeanServer database;
	private PluginConfiguration configuration;

	@Override
	public List<Class<?>> getDatabaseClasses() {
		final List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(BanRecord.class);
		classes.add(PlayerRecord.class);
		classes.add(CommentRecord.class);
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

	private void loadConfiguration()
	throws IOException {
		final File file = new File(this.getDataFolder().getPath() + File.separatorChar + CONFIG_NAME);
		final InputStream defaults = this.getResource(CONFIG_NAME);
		this.configuration = new PluginConfiguration(file, defaults);
	}

	private void loadDatabase()
	throws IOException {
		ServerConfig serverConfig = new ServerConfig();
		getServer().configureDbConfig(serverConfig);
		serverConfig.setClasses(getDatabaseClasses());
		serverConfig.setName(this.getName());
		serverConfig.setRegister(true);
		final File file = new File(this.getDataFolder().getPath() + File.separatorChar + DATABASE_CONFIG_NAME);
		final InputStream defaults = this.getResource(DATABASE_CONFIG_NAME);
		final DatabaseConfiguration configuration = new SimpleDatabaseConfiguration(file, defaults, getName(), serverConfig);
		final DatabaseLoader loader = DatabaseLoaderFactory.getDatabaseLoader(configuration);
		loader.initalise();
		database = loader.getEbeanServer();
		CommentRecord.setRecordDatabase(database);
		PlayerRecord.setRecordDatabase(database);
		BanRecord.setRecordDatabase(database);
		PlayerRecord.create(new UUID(0, 0), "CONSOLE");
	}

	@Override public EbeanServer getDatabase() {
		return database;
	}

	private void registerCommands() {
		Set<Command> commands = new HashSet<Command>();
		Command command = new AuditCommand(this, getServer().getScheduler());
		commands.add(command);
		command = new BanCommand(this, getServer().getScheduler(), configuration, getServer());
		commands.add(command);
		getCommand("ban").setExecutor(new SimpleCommandInvoker(this, this.getServer().getScheduler(), command));
		command = new CheckCommand(this, getServer().getScheduler());
		commands.add(command);
		command = new CommentCommand(this, getServer().getScheduler(), getServer());
		commands.add(command);
		command = new HistoryCommand(this, getServer().getScheduler());
		commands.add(command);
		command = new ExportCommand(this, getServer().getScheduler(), getServer());
		commands.add(command);
		command = new ImportCommand(this, getServer().getScheduler(), getServer());
		commands.add(command);
		command = new KickCommand(this, getServer().getScheduler(), getServer());
		commands.add(command);
		getCommand("kick").setExecutor(new SimpleCommandInvoker(this, this.getServer().getScheduler(), command));
		command = new LimitsCommand(this, getServer().getScheduler(), configuration);
		commands.add(command);
		command = new PardonCommand(this, getServer().getScheduler());
		commands.add(command);
		getCommand("pardon").setExecutor(new SimpleCommandInvoker(this, this.getServer().getScheduler(), command));
		command = new PurgeCommand(this, getServer().getScheduler(), getDatabase());
		commands.add(command);
		command = new RecentCommand(this, getServer().getScheduler());
		commands.add(command);
		command = new UndoCommand(this, getServer().getScheduler(), configuration);
		commands.add(command);
		CommandInvoker invoker = new RootCommandInvoker(this, this.getServer().getScheduler(), commands, "/bh");
		getCommand("bh").setExecutor(invoker);
	}

	private void registerListeners() {
		new PlayerListener(this, this.getServer().getPluginManager(), getServer(), database);
		new PlayerNotifier(this, this.getServer().getPluginManager(), getServer());
	}

	private void updatePlugin() {
		if (!configuration.getAutomaticUpdaterState().equals(PluginUpdater.State.OFF)) {
			PluginUpdater updater = new BukkitDevPluginUpdater(this.getDescription(), configuration.getAutomaticUpdaterBranch(), configuration.getAutomaticUpdaterState(), PROJECT_ID, this.getDataFolder(), Bukkit.getVersion());
			this.getServer().getScheduler().runTaskAsynchronously(this, updater);
			new name.richardson.james.bukkit.utilities.updater.PlayerNotifier(this, this.getServer().getPluginManager(), updater);
		}
	}

}
