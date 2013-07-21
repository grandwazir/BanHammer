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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import name.richardson.james.bukkit.utilities.permissions.Permissions;
import name.richardson.james.bukkit.utilities.plugin.AbstractDatabasePlugin;
import name.richardson.james.bukkit.utilities.plugin.AbstractPlugin;

import name.richardson.james.bukkit.alias.Alias;
import name.richardson.james.bukkit.alias.AliasHandler;
import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.ban.event.AliasBannedPlayerListener;
import name.richardson.james.bukkit.banhammer.ban.event.BannedPlayerListener;
import name.richardson.james.bukkit.banhammer.ban.event.PlayerNotifier;

@Permissions(permissions = {BanHammer.PLUGIN_PERMISSION_NAME, BanHammer.NOTIFY_PERMISSION_NAME})
public final class BanHammer extends AbstractDatabasePlugin {

	public static final String PLUGIN_PERMISSION_NAME = "banhammer";
	public static final String NOTIFY_PERMISSION_NAME = "banhammer.notify";

	private AliasHandler aliasHandler;
	private BanRecordManager banRecordManager;
	private PluginConfiguration configuration;
	private PlayerRecordManager playerRecordManager;

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

	public BanRecordManager getBanRecordManager() {
		return banRecordManager;
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

	public PlayerRecordManager getPlayerRecordManager() {
		return playerRecordManager;
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
			super.onEnable();
			this.loadConfiguration();
			this.loadManagers();
			this.registerCommands();
			this.registerListeners();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void loadManagers() {
		this.playerRecordManager = new PlayerRecordManager(this.getDatabase());
		this.banRecordManager = new BanRecordManager(this.getDatabase());
	}

	private void loadConfiguration()
	throws IOException {
		final File file = new File(this.getDataFolder().getPath() + File.separatorChar + AbstractPlugin.CONFIG_NAME);
		final InputStream defaults = this.getResource(CONFIG_NAME);
		this.configuration = new PluginConfiguration(file, defaults);
		if (configuration.isAliasEnabled()) hookAlias();
	}

	protected void loadDatabase()
	throws IOException {
		super.loadDatabase();
		this.playerRecordManager = new PlayerRecordManager(this.getDatabase());
		this.banRecordManager = new BanRecordManager(this.getDatabase());
	}

	/**
	 * Hook the alias plugin and load a handler if it exists.
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

	private void registerCommands() {
		//TODO: Reimplement this nicely
	}

	private void registerListeners() {
		if (this.configuration.isAliasEnabled() && (this.aliasHandler != null)) {
			new AliasBannedPlayerListener(this, this.getServer().getPluginManager(), getServer(), this.playerRecordManager, this.aliasHandler);
		} else {
			new BannedPlayerListener(this, this.getServer().getPluginManager(), getServer(), this.playerRecordManager);
		}
		new PlayerNotifier(this, this.getServer().getPluginManager(), this.getServer());
	}

}
