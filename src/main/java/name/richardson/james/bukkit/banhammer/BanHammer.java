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
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import com.avaje.ebean.EbeanServer;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.alias.Alias;
import name.richardson.james.bukkit.alias.AliasHandler;
import name.richardson.james.bukkit.banhammer.ban.BanCommand;
import name.richardson.james.bukkit.banhammer.ban.CheckCommand;
import name.richardson.james.bukkit.banhammer.ban.HistoryCommand;
import name.richardson.james.bukkit.banhammer.ban.LimitsCommand;
import name.richardson.james.bukkit.banhammer.ban.PardonCommand;
import name.richardson.james.bukkit.banhammer.ban.PurgeCommand;
import name.richardson.james.bukkit.banhammer.ban.RecentCommand;
import name.richardson.james.bukkit.banhammer.kick.KickCommand;
import name.richardson.james.bukkit.banhammer.management.ExportCommand;
import name.richardson.james.bukkit.banhammer.management.ImportCommand;
import name.richardson.james.bukkit.banhammer.migration.MigratedSQLStorage;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.OldBanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.CommandManager;
import name.richardson.james.bukkit.utilities.command.PluginCommand;
import name.richardson.james.bukkit.utilities.persistence.SQLStorage;
import name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin;

public class BanHammer extends SkeletonPlugin {

  /**
   * The date format used by BanHammer.
   * This is used across the plugin for representing dates to the user, for
   * example in replies to commands or notifications of ban lengths.
   */
  public static final DateFormat LONG_DATE_FORMAT = new SimpleDateFormat("d MMMMM yyyy HH:mm (z)");

  public static final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("d MMM yyyy HH:mm (z)");
  
  /** Reference to the Alias API. */
  private AliasHandler aliasHandler;

  /** BanHammer configuration. */
  private BanHammerConfiguration configuration;

  /** The storage for this plugin. */
  private SQLStorage database;

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
   * @see
   * name.richardson.james.bukkit.utilities.updater.Updatable#getArtifactID()
   */
  public String getArtifactID() {
    return "ban-hammer";
  }

  /*
   * (non-Javadoc)
   * @see org.bukkit.plugin.java.JavaPlugin#getDatabase()
   */
  @Override
  public EbeanServer getDatabase() {
    return this.database.getEbeanServer();
  }

  /*
   * (non-Javadoc)
   * @see org.bukkit.plugin.java.JavaPlugin#getDatabaseClasses()
   */
  @Override
  public List<Class<?>> getDatabaseClasses() {
    final List<Class<?>> classes = new LinkedList<Class<?>>();
    classes.add(BanRecord.class);
    classes.add(PlayerRecord.class);
    classes.add(OldBanRecord.class);
    return classes;
  }

  /**
   * This returns a new handler to allow access to the BanHammer API.
   * 
   * @param parentClass the class that the handler belongs to
   * @return A new BanHandler instance.
   */
  public BanHandler getHandler(final Class<?> parentClass) {
    return new BanHandler(parentClass, this);
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin#loadConfiguration
   * ()
   */
  @Override
  protected void loadConfiguration() throws IOException {
    this.configuration = new BanHammerConfiguration(this);
    if (this.configuration.isAliasEnabled()) {
      this.hookAlias();
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin#registerCommands
   * ()
   */
  @Override
  protected void registerCommands() {
    final CommandManager commandManager = new CommandManager(this);
    this.getCommand("bh").setExecutor(commandManager);
    final PluginCommand banCommand = new BanCommand(this, this.configuration.getBanLimits());
    final PluginCommand kickCommand = new KickCommand(this);
    final PluginCommand pardonCommand = new PardonCommand(this);
    // register commands
    commandManager.addCommand(banCommand);
    commandManager.addCommand(new CheckCommand(this));
    commandManager.addCommand(new ExportCommand(this));
    commandManager.addCommand(new HistoryCommand(this));
    commandManager.addCommand(new ImportCommand(this));
    commandManager.addCommand(kickCommand);
    commandManager.addCommand(new LimitsCommand(this, this.configuration.getBanLimits()));
    commandManager.addCommand(pardonCommand);
    commandManager.addCommand(new PurgeCommand(this));
    commandManager.addCommand(new RecentCommand(this));
    // register commands again as root commands
    this.getCommand("ban").setExecutor(banCommand);
    this.getCommand("kick").setExecutor(kickCommand);
    this.getCommand("pardon").setExecutor(pardonCommand);
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin#registerEvents
   * ()
   */
  @Override
  protected void registerEvents() {
    new PlayerListener(this);
  }

  /*
   * (non-Javadoc)
   * @see name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin#
   * registerPermissions()
   */
  @Override
  protected void registerPermissions() {
    // register notify permission
    final String prefix = this.getDescription().getName().toLowerCase() + ".";
    final Permission notify = new Permission(prefix + this.getMessage("banhammer.notify-permission-name"), this.getMessage("banhammer.notify-permission-description"), PermissionDefault.TRUE);
    notify.addParent(this.getRootPermission(), true);
    this.addPermission(notify);
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin#setupMetrics()
   */
  @Override
  protected void setupMetrics() throws IOException {
    new MetricsListener(this);
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin#setupPersistence
   * ()
   */
  @Override
  protected void setupPersistence() throws SQLException {
    this.database = new MigratedSQLStorage(this);
  }

  /**
   * Hook the alias plugin and load a handler if it exists.
   */
  private void hookAlias() {
    final Alias plugin = (Alias) this.getServer().getPluginManager().getPlugin("Alias");
    if (plugin == null) {
      this.logger.warning(this.getMessage("banhammer.unable-to-hook-alias"));
    } else {
      this.logger.info("Using " + plugin.getDescription().getFullName() + ".");
      this.aliasHandler = plugin.getHandler(BanHammer.class);
    }
  }

}
