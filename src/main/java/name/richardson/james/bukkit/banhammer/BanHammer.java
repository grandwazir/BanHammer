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

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import name.richardson.james.bukkit.alias.Alias;
import name.richardson.james.bukkit.alias.AliasHandler;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.api.SimpleBanHandler;
import name.richardson.james.bukkit.banhammer.ban.BanCommand;
import name.richardson.james.bukkit.banhammer.ban.CheckCommand;
import name.richardson.james.bukkit.banhammer.ban.HistoryCommand;
import name.richardson.james.bukkit.banhammer.ban.LimitsCommand;
import name.richardson.james.bukkit.banhammer.ban.PardonCommand;
import name.richardson.james.bukkit.banhammer.ban.PurgeCommand;
import name.richardson.james.bukkit.banhammer.ban.RecentCommand;
import name.richardson.james.bukkit.banhammer.ban.UndoCommand;
import name.richardson.james.bukkit.banhammer.kick.KickCommand;
import name.richardson.james.bukkit.banhammer.management.AuditCommand;
import name.richardson.james.bukkit.banhammer.management.ExportCommand;
import name.richardson.james.bukkit.banhammer.management.ImportCommand;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.Command;
import name.richardson.james.bukkit.utilities.command.CommandManager;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.plugin.AbstractPlugin;
import name.richardson.james.bukkit.utilities.plugin.PluginPermissions;

@PluginPermissions(permissions = { "banhammer", "banhammer.notify" })
public final class BanHammer extends AbstractPlugin {

	public static final DateFormat LONG_DATE_FORMAT = new SimpleDateFormat("d MMMMM yyyy HH:mm (z)");

	public static final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("d MMM yyyy HH:mm (z)");

	/** Reference to the Alias API. */
	private AliasHandler aliasHandler;

	/** BanHammer configuration. */
	private BanHammerConfiguration configuration;

	private BanHandler handler;

	/**
	 * Gets the alias handler.
	 * 
	 * @return the alias handler
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
	 * 
	 * @param parentClass
	 *          the class that the handler belongs to
	 * @return A new BanHandler instance.
	 */
	public BanHandler getHandler() {
		if (this.handler == null) {
			this.handler = new SimpleBanHandler(this);
		}
		return this.handler;
	}

	public String getMessage(final String key) {
		String message = this.localisation.getString(key);
		message = ColourFormatter.replace(message);
		return message;
	}

	public String getMessage(final String key, final Object... elements) {
		final MessageFormat formatter = new MessageFormat(this.localisation.getString(key));
		formatter.setLocale(Locale.getDefault());
		String message = formatter.format(elements);
		message = ColourFormatter.replace(message);
		return message;
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
			this.hookAlias();
			this.setPermissions();
			this.registerCommands();
			this.registerCommands();
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
	protected void loadConfiguration() throws IOException {
		super.loadConfiguration();
		this.configuration = new BanHammerConfiguration(this);
		if (this.configuration.isAliasEnabled()) {
			this.hookAlias();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin#registerCommands
	 * ()
	 */
	@Override
	protected void registerCommands() {
		final CommandManager commandManager = new CommandManager("bh");
		final Command banCommand = new BanCommand(this, this.configuration.getBanLimits(), this.configuration.getImmunePlayers());
		final Command kickCommand = new KickCommand(this);
		final Command pardonCommand = new PardonCommand(this);
		// register commands
		commandManager.addCommand(banCommand);
		commandManager.addCommand(new AuditCommand(this));
		commandManager.addCommand(new CheckCommand(this));
		commandManager.addCommand(new ExportCommand(this));
		commandManager.addCommand(new HistoryCommand(this));
		commandManager.addCommand(new ImportCommand(this));
		commandManager.addCommand(kickCommand);
		commandManager.addCommand(new LimitsCommand(this, this.configuration.getBanLimits()));
		commandManager.addCommand(pardonCommand);
		commandManager.addCommand(new PurgeCommand(this));
		commandManager.addCommand(new RecentCommand(this));
		commandManager.addCommand(new UndoCommand(this));
		// register commands again as root commands
		this.getCommand("ban").setExecutor(banCommand);
		this.getCommand("kick").setExecutor(kickCommand);
		this.getCommand("pardon").setExecutor(pardonCommand);
	}

	@Override
	protected void setupMetrics() throws IOException {
		new MetricsListener(this);
	}

	/**
	 * Hook the alias plugin and load a handler if it exists.
	 */
	private void hookAlias() {
		final Alias plugin = (Alias) this.getServer().getPluginManager().getPlugin("Alias");
		if (plugin == null) {
			this.getCustomLogger().log(Level.WARNING, "banhammer.unable-to-hook-alias");
		} else {
			this.getCustomLogger().log(Level.FINE, "banhammer.alias-hooked", plugin.getDescription().getFullName() + ".");
			this.aliasHandler = plugin.getHandler();
		}
	}

	private void registerListeners() {
		new PlayerListener(this);
	}

}
