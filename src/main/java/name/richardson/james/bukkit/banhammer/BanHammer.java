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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import name.richardson.james.bukkit.utilities.command.CommandManager;
import name.richardson.james.bukkit.utilities.plugin.AbstractPlugin;
import name.richardson.james.bukkit.utilities.plugin.PluginPermissions;

import name.richardson.james.bukkit.alias.Alias;
import name.richardson.james.bukkit.alias.AliasHandler;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.api.SimpleBanHandler;
import name.richardson.james.bukkit.banhammer.ban.*;
import name.richardson.james.bukkit.banhammer.ban.event.PlayerNotifier;
import name.richardson.james.bukkit.banhammer.ban.management.*;
import name.richardson.james.bukkit.banhammer.ban.event.AliasBannedPlayerListener;
import name.richardson.james.bukkit.banhammer.ban.event.BannedPlayerListener;
import name.richardson.james.bukkit.banhammer.kick.KickCommand;
import name.richardson.james.bukkit.banhammer.matchers.BanLimitMatcher;
import name.richardson.james.bukkit.banhammer.matchers.BannedPlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.matchers.CreatorPlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.matchers.PlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.metrics.MetricsListener;
import name.richardson.james.bukkit.banhammer.persistence.*;

@PluginPermissions(permissions = {"banhammer", "banhammer.notify"})
public final class BanHammer extends AbstractPlugin {

	public static final DateFormat LONG_DATE_FORMAT = new SimpleDateFormat("d MMMMM yyyy HH:mm (z)");
	public static final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("d MMM yyyy HH:mm (z)");

	private AliasHandler aliasHandler;
	private BanHammerConfiguration configuration;
	private BanHandler handler;
	private PlayerRecordManager playerRecordManager;
	private BanRecordManager banRecordManager;

	/**
	 * Gets the guardian handler.
	 *
	 * @return the guardian handler
	 */
	public AliasHandler getAliasHandler() {
		return this.aliasHandler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * name.richardson.james.bukkit.utilities.updater.Updatable#getArtifactID()
	 */
	public String getArtifactID() {
		return "ban-hammer";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.plugin.java.JavaPlugin#getDatabaseClasses()
	 */
	@Override
	public List<Class<?>> getDatabaseClasses() {
		final List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(BanRecord.class);
		classes.add(PlayerRecord.class);
		return classes;
	}

	/**
	 * This returns a new handler to allow access to the BanHammer API.
	 */
	public BanHandler getHandler() {
		if (this.handler == null) {
			this.handler = new SimpleBanHandler(playerRecordManager, banRecordManager);
		}
		return this.handler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin#setupPersistence
	 * ()
	 */
	public String getVersion() {
		return this.getDescription().getVersion();
	}

	@Override
	public void onEnable() {
		try {
			this.loadConfiguration();
			this.loadDatabase();
			this.setPermissions();
			this.registerCommands();
			this.registerListeners();
			this.setupMetrics();
			this.updatePlugin();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin#loadConfiguration
	 * ()
	 */
	@Override
	protected void loadConfiguration()
	throws IOException {
		super.loadConfiguration();
		final File file = new File(this.getDataFolder().getAbsolutePath() + File.separatorChar + "config.yml");
		final InputStream defaults = this.getResource("config.yml");
		this.configuration = new BanHammerConfiguration(file, defaults);
		if (this.configuration.isAliasEnabled()) {
			this.hookAlias();
		}
	}

	protected void loadDatabase()
	throws IOException {
		super.loadDatabase();
		this.playerRecordManager = new PlayerRecordManager(this.getDatabase());
		this.banRecordManager = new BanRecordManager(this.getDatabase());
	}

	@Override
	protected void setupMetrics()
	throws IOException {
		if (this.configuration.isCollectingStats()) {
			new MetricsListener(this);
		}
	}

	/**
	 * Hook the guardian plugin and load a handler if it exists.
	 */
	private void hookAlias() {
		final Alias plugin = (Alias) this.getServer().getPluginManager().getPlugin("Alias");
		if (plugin == null) {
			this.getLocalisedLogger().log(Level.WARNING, "unable-to-hook-alias");
		} else {
			this.getLocalisedLogger().log(Level.FINE, "Using {0}.", plugin.getDescription().getFullName());
			this.aliasHandler = plugin.getHandler();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin#registerCommands
	 * ()
	 */
	private void registerCommands() {
		final Set<String> names = new HashSet<String>();
		final Set<String> bannedPlayers = new HashSet<String>();
		final Set<String> banCreaters = new HashSet<String>();
		for (PlayerRecord playerRecord : this.playerRecordManager.list()) {
			names.add(playerRecord.getName().toLowerCase());
			if (playerRecord.isBanned()) {
				bannedPlayers.add(playerRecord.getName().toLowerCase());
			}
			if (playerRecord.getCreatedBans().size() != 0) {
				banCreaters.add(playerRecord.getName().toLowerCase());
			}
		}
		PlayerRecordMatcher.setNameList(names);
		BannedPlayerRecordMatcher.setNameList(bannedPlayers);
		CreatorPlayerRecordMatcher.setNameList(banCreaters);
		BanLimitMatcher.setBanLimits(this.configuration.getBanLimits().keySet());
		final CommandManager commandManager = new CommandManager("bh", this.getDescription());
		final BanCommand banCommand = new BanCommand(this.getHandler(), this.configuration.getBanLimits(), this.configuration.getImmunePlayers(), this.getServer());
		final KickCommand kickCommand = new KickCommand(this);
		final PardonCommand pardonCommand = new PardonCommand(this.playerRecordManager, this.getHandler(), this.getServer());
		// register commands
		commandManager.addCommand(new AuditCommand(this.playerRecordManager, this.banRecordManager));
		commandManager.addCommand(banCommand);
		commandManager.addCommand(new CheckCommand(this.playerRecordManager, this.getServer()));
		commandManager.addCommand(new ExportCommand(this.playerRecordManager, this.getServer()));
		commandManager.addCommand(new HistoryCommand(this.getHandler()));
		commandManager.addCommand(new ImportCommand(this.getHandler(), this.getServer()));
		commandManager.addCommand(kickCommand);
		commandManager.addCommand(new LimitsCommand(this.configuration.getBanLimits()));
		commandManager.addCommand(pardonCommand);
		commandManager.addCommand(new PurgeCommand(this.playerRecordManager, this.banRecordManager));
		commandManager.addCommand(new RecentCommand(this.banRecordManager));
		commandManager.addCommand(new UndoCommand(this.playerRecordManager, this.banRecordManager, this.configuration.getUndoTime()));
		// register commands again as root commands
		this.getCommand("ban").setExecutor(banCommand);
		this.getCommand("kick").setExecutor(kickCommand);
		this.getCommand("pardon").setExecutor(pardonCommand);
	}

	private void registerListeners() {
		if (this.configuration.isAliasEnabled() && (this.aliasHandler != null)) {
			new AliasBannedPlayerListener(this, this.playerRecordManager, this.aliasHandler, this.getHandler());
		} else {
			new BannedPlayerListener(this, this.playerRecordManager);
		}
		new PlayerNotifier(this, this.getServer());
	}

}
