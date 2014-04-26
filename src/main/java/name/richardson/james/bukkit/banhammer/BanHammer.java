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
import java.util.logging.Level;
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

import name.richardson.james.bukkit.alias.Alias;
import name.richardson.james.bukkit.alias.persistence.PlayerNameRecordManager;
import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.ban.event.AliasBannedPlayerListener;
import name.richardson.james.bukkit.banhammer.ban.event.NormalBannedPlayerListener;
import name.richardson.james.bukkit.banhammer.ban.event.PlayerNotifier;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.PLUGIN_UNABLE_TO_HOOK_ALIAS;

public final class BanHammer extends JavaPlugin {

	public static final String NOTIFY_PERMISSION_NAME = "banhammer.notify";
	public static final int PROJECT_ID = 31269;

	private static final String CONFIG_NAME = "config.yml";
	private static final String DATABASE_CONFIG_NAME = "database.yml";

	private final Logger logger = PluginLoggerFactory.getLogger(BanHammer.class);

	private BanRecordManager banRecordManager;
	private PluginConfiguration configuration;
	private EbeanServer database;
	private PlayerRecordManager playerRecordManager;

	public BanRecordManager getBanRecordManager() {
		return banRecordManager;
	}

	private void setBanRecordManager(BanRecordManager banRecordManager) {
		this.banRecordManager = banRecordManager;
	}

	public EbeanServer getDatabase() {
		return database;
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		final List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(BanRecord.class);
		classes.add(PlayerRecord.class);
		return classes;
	}

	public PlayerRecordManager getPlayerRecordManager() {
		return playerRecordManager;
	}

	private void setPlayerRecordManager(PlayerRecordManager playerRecordManager) {
		this.playerRecordManager = playerRecordManager;
	}

	@Override
	public void onEnable() {
		try {
			this.loadConfiguration();
			this.loadDatabase();
			this.loadManagers();
			this.registerCommands();
			this.registerListeners();
			this.setupMetrics();
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

	/**
	 * Hook the alias plugin and load a handler if it exists.
	 */
	private void hookAlias() {
		final Alias plugin = (Alias) this.getServer().getPluginManager().getPlugin("Alias");
		if (plugin == null) {
			logger.log(Level.WARNING, PLUGIN_UNABLE_TO_HOOK_ALIAS.asMessage());
		} else {
			logger.log(Level.FINE, "Using {0}.", plugin.getDescription().getFullName());
			PlayerNameRecordManager playerNameRecordManager = plugin.getPlayerNameRecordManager();
			new AliasBannedPlayerListener(this, this.getServer().getPluginManager(), this.getPlayerRecordManager(), playerNameRecordManager);
		}
	}

	private void loadConfiguration()
	throws IOException {
		PrefixedLogger.setPrefix(this.getName());
		final File file = new File(this.getDataFolder().getPath() + File.separatorChar + CONFIG_NAME);
		final InputStream defaults = this.getResource(CONFIG_NAME);
		this.configuration = new PluginConfiguration(file, defaults);
		this.logger.setLevel(configuration.getLogLevel());
		if (configuration.isAliasEnabled()) hookAlias();
	}

	private void loadDatabase()
	throws IOException {
		ServerConfig serverConfig = new ServerConfig();
		getServer().configureDbConfig(serverConfig);
		serverConfig.setClasses(Arrays.asList(BanRecord.class, PlayerRecord.class));
		serverConfig.setName(this.getName());
		final File file = new File(this.getDataFolder().getPath() + File.separatorChar + DATABASE_CONFIG_NAME);
		final InputStream defaults = this.getResource(DATABASE_CONFIG_NAME);
		final DatabaseConfiguration configuration = new SimpleDatabaseConfiguration(file, defaults, serverConfig, this.getName());
		final DatabaseLoader loader = DatabaseLoaderFactory.getDatabaseLoader(configuration);
		loader.initalise();
		this.database = loader.getEbeanServer();
	}

	private void loadManagers() {
		this.setPlayerRecordManager(new PlayerRecordManager(this.getDatabase()));
		this.setBanRecordManager(new BanRecordManager(this.getDatabase()));
	}

	private void registerCommands() {
		Set<Command> commands = new HashSet<Command>();
		AbstractCommand command = new AuditCommand(getPlayerRecordManager(), getBanRecordManager());
		commands.add(command);
		command = new BanCommand(this.getServer(), this.getServer().getPluginManager(), getPlayerRecordManager(), configuration.getBanLimits(), configuration.getImmunePlayers());
		commands.add(command);
		getCommand("ban").setExecutor(new FallthroughCommandInvoker(this, this.getServer().getScheduler(), command));
		command = new CheckCommand(getPlayerRecordManager());
		commands.add(command);
		command = new HistoryCommand(getPlayerRecordManager());
		commands.add(command);
		command = new ExportCommand(getPlayerRecordManager(), getServer());
		commands.add(command);
		command = new ImportCommand(getPlayerRecordManager(), getServer());
		commands.add(command);
		command = new KickCommand(getServer());
		commands.add(command);
		getCommand("kick").setExecutor(new FallthroughCommandInvoker(this, this.getServer().getScheduler(), command));
		command = new LimitsCommand(configuration.getBanLimits());
		commands.add(command);
		command = new PardonCommand(getServer().getPluginManager(), getBanRecordManager(), getPlayerRecordManager());
		commands.add(command);
		getCommand("pardon").setExecutor(new FallthroughCommandInvoker(this, this.getServer().getScheduler(), command));
		command = new PurgeCommand(getPlayerRecordManager(), getBanRecordManager());
		commands.add(command);
		command = new RecentCommand(getBanRecordManager());
		commands.add(command);
		command = new UndoCommand(getPlayerRecordManager(), getBanRecordManager(), configuration.getUndoTime());
		commands.add(command);
		// create the invoker
		command = new HelpCommand(commands, "bh");
		CommandInvoker invoker = new FallthroughCommandInvoker(this, this.getServer().getScheduler(), command);
		invoker.addCommands(commands);
		// bind invoker to plugin command
		getCommand("bh").setExecutor(invoker);
	}

	private void registerListeners() {
		new NormalBannedPlayerListener(this, this.getServer().getPluginManager(), getServer(), this.getPlayerRecordManager());
		new PlayerNotifier(this, this.getServer().getPluginManager(), getServer());
	}

	private void setupMetrics()
	throws IOException {
		new MetricsListener(this, this.getServer().getPluginManager(), getBanRecordManager());
	}

}
