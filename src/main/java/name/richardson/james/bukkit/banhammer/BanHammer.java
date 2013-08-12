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

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.Command;
import name.richardson.james.bukkit.utilities.command.HelpCommand;
import name.richardson.james.bukkit.utilities.command.invoker.CommandInvoker;
import name.richardson.james.bukkit.utilities.command.invoker.FallthroughCommandInvoker;
import name.richardson.james.bukkit.utilities.command.matcher.OnlinePlayerMatcher;
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
import name.richardson.james.bukkit.banhammer.utilities.command.matcher.*;

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

	private void setAliasHandler(AliasHandler aliasHandler) {
		this.aliasHandler = aliasHandler;
	}

	public BanRecordManager getBanRecordManager() {
		return banRecordManager;
	}

	private void setBanRecordManager(BanRecordManager banRecordManager) {
		this.banRecordManager = banRecordManager;
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
			super.onEnable();
			this.loadConfiguration();
			this.loadManagers();
			this.registerCommands();
			this.registerListeners();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	protected void loadDatabase()
	throws IOException {
		super.loadDatabase();
		this.setPlayerRecordManager(new PlayerRecordManager(this.getDatabase()));
		this.setBanRecordManager(new BanRecordManager(this.getDatabase()));
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
			this.setAliasHandler(plugin.getHandler());
		}
	}

	private void loadConfiguration()
	throws IOException {
		final File file = new File(this.getDataFolder().getPath() + File.separatorChar + AbstractPlugin.CONFIG_NAME);
		final InputStream defaults = this.getResource(CONFIG_NAME);
		this.configuration = new PluginConfiguration(file, defaults);
		if (configuration.isAliasEnabled()) hookAlias();
	}

	private void loadManagers() {
		this.setPlayerRecordManager(new PlayerRecordManager(this.getDatabase()));
		this.setBanRecordManager(new BanRecordManager(this.getDatabase()));
	}

	private void registerCommands() {
		// create argument matchers
		PlayerRecordMatcher playerMatcher = new PlayerRecordMatcher(getPlayerRecordManager(), PlayerRecordManager.PlayerStatus.ANY);
		PlayerRecordMatcher bannedPlayerMatcher = new PlayerRecordMatcher(getPlayerRecordManager(), PlayerRecordManager.PlayerStatus.BANNED);
		PlayerRecordMatcher creatorPlayerMatcher = new PlayerRecordMatcher(getPlayerRecordManager(), PlayerRecordManager.PlayerStatus.CREATOR);
		OnlinePlayerMatcher onlinePlayerMatcher = new OnlinePlayerMatcher(getServer());
		BanLimitMatcher banLimitMatcher = new BanLimitMatcher(configuration.getBanLimits().keySet());
		// create the commands
		Set<Command> commands = new HashSet<Command>();
		AbstractCommand command = new AuditCommand(getPermissionManager(), getPlayerRecordManager(), getBanRecordManager());
		command.addMatcher(creatorPlayerMatcher);
		commands.add(command);
		command = new BanCommand(getPermissionManager(), this.getServer().getPluginManager(), getPlayerRecordManager(), configuration.getBanLimits(), configuration.getImmunePlayers());
		command.addMatcher(onlinePlayerMatcher);
		command.addMatcher(banLimitMatcher);
		commands.add(command);
		getCommand("ban").setExecutor(new FallthroughCommandInvoker(command));
		command = new CheckCommand(getPermissionManager(), getPlayerRecordManager());
		command.addMatcher(bannedPlayerMatcher);
		commands.add(command);
		command = new ExportCommand(getPermissionManager(), getPlayerRecordManager(), getServer());
		commands.add(command);
		command = new ImportCommand(getPermissionManager(), getPlayerRecordManager(), getServer());
		commands.add(command);
		command = new KickCommand(getPermissionManager(), getServer());
		command.addMatcher(onlinePlayerMatcher);
		commands.add(command);
		getCommand("kick").setExecutor(new FallthroughCommandInvoker(command));
		command = new LimitsCommand(getPermissionManager(), configuration.getBanLimits());
		commands.add(command);
		command = new PardonCommand(getPermissionManager(), getServer().getPluginManager(), getBanRecordManager(), getPlayerRecordManager());
		command.addMatcher(bannedPlayerMatcher);
		commands.add(command);
		getCommand("pardon").setExecutor(new FallthroughCommandInvoker(command));
		command = new PurgeCommand(getPermissionManager(), getServer().getPluginManager(), getPlayerRecordManager(), getBanRecordManager());
		command.addMatcher(playerMatcher);
		command = new RecentCommand(getPermissionManager(), getBanRecordManager());
		commands.add(command);
		command = new UndoCommand(getPermissionManager(), getPlayerRecordManager(), getBanRecordManager(), configuration.getUndoTime());
		command.addMatcher(playerMatcher);
		commands.add(command);
		// create the invoker
		command = new HelpCommand(getPermissionManager(), "bh", getDescription(), commands);
		CommandInvoker invoker = new FallthroughCommandInvoker(command);
		invoker.addCommands(commands);
		// bind invoker to plugin command
		getCommand("bh").setExecutor(invoker);
	}

	private void registerListeners() {
		if (this.configuration.isAliasEnabled() && (this.getAliasHandler() != null)) {
			new AliasBannedPlayerListener(this, this.getServer().getPluginManager(), getServer(), this.getPlayerRecordManager(), this.getAliasHandler());
		} else {
			new BannedPlayerListener(this, this.getServer().getPluginManager(), getServer(), this.getPlayerRecordManager());
		}
		new PlayerNotifier(this, this.getServer().getPluginManager(), this.getServer());
	}

}
