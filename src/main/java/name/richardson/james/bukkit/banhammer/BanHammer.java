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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

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
import name.richardson.james.bukkit.banhammer.management.ReloadCommand;
import name.richardson.james.bukkit.util.Logger;
import name.richardson.james.bukkit.util.Plugin;
import name.richardson.james.bukkit.util.command.CommandManager;
import name.richardson.james.bukkit.util.command.PlayerCommand;
import name.richardson.james.bukkit.utilities.plugin.SimplePlugin;

public class BanHammer extends SimplePlugin {

  /**
   * This returns a localised string from the loaded ResourceBundle.
   * 
   * @param key
   *          The key for the desired string.
   * @return The string for the given key.
   */
  public static String getMessage(final String key) {
    return BanHammer.messages.getString(key);
  }

  private long maximumTemporaryBan;
  private static ResourceBundle messages;

  private CommandManager cm;
  private BannedPlayerListener bannedPlayerListener;
  private PluginDescriptionFile desc;
  private PluginManager pm;
  private BanHammerConfiguration configuration;

  private DatabaseHandler database;
  private final HashSet<String> bannedPlayerNames = new HashSet<String>();

  private AliasHandler aliasHandler;

  public AliasHandler getAliasHandler() {
    return aliasHandler;
  }

  protected BanHammerConfiguration getBanHammerConfiguration() {
    return configuration;
  }

  public Map<String, Long> getBanLimits() {
    return configuration.getBanLimits();
  }

  public Set<String> getBannedPlayers() {
    return Collections.unmodifiableSet(bannedPlayerNames);
  }

  @Override
  public List<Class<?>> getDatabaseClasses() {
    final List<Class<?>> list = new ArrayList<Class<?>>();
    list.add(BanRecord.class);
    return list;
  }

  public DatabaseHandler getDatabaseHandler() {
    return database;
  }

  /**
   * This returns a handler to allow access to the BanHammer API.
   * 
   * @return A new BanHandler instance.
   */
  public BanHandler getHandler(final Class<?> parentClass) {
    return new BanHandler(parentClass, this);
  }

  public long getMaximumTemporaryBan() {
    return maximumTemporaryBan;
  }

  protected Set<String> getModifiableBannedPlayers() {
    return bannedPlayerNames;
  }

  private void hookAlias() {
    final Alias plugin = (Alias) pm.getPlugin("Alias");
    if (plugin == null) {
      logger.warning("Unable to hook Alias.");
    } else {
      logger.info("Using " + plugin.getDescription().getFullName() + ".");
      aliasHandler = plugin.getHandler(BanHammer.class);
    }
  }

  private void loadBans() {
    bannedPlayerNames.clear();
    for (final Object record : database.list(BanRecord.class)) {
      final BanRecord ban = (BanRecord) record;
      if (ban.isActive()) {
        bannedPlayerNames.add(ban.getPlayer().toLowerCase());
      }
    }
    logger.info(String.format("%d banned names loaded.", bannedPlayerNames.size()));
  }

  private void loadConfiguration() throws IOException {
    configuration = new BanHammerConfiguration(this);
    if (configuration.isDebugging()) {
      Logger.enableDebugging(getDescription().getName().toLowerCase());
    }
    configuration.setBanLimits();
  }

  public void onDisable() {
    logger.info(String.format("%s is disabled.", desc.getName()));
  }

  public void onEnable() {
    desc = getDescription();
    pm = getServer().getPluginManager();

    try {
      logger.setPrefix("[BanHammer] ");
      loadConfiguration();
      setupDatabase();
      loadBans();
      if (configuration.isAliasEnabled()) {
        hookAlias();
      }
      setPermission();
      registerListeners();
      registerCommands();
    } catch (final IOException exception) {
      logger.severe("Unable to load configuration!");
      exception.printStackTrace();
    } catch (final SQLException exception) {
      exception.printStackTrace();
    } finally {
      if (!getServer().getPluginManager().isPluginEnabled(this)) {
        return;
      }
    }

    logger.info(String.format("%s is enabled.", desc.getFullName()));
  }

  private void registerCommands() {
    cm = new CommandManager(getDescription());
    getCommand("bh").setExecutor(cm);
    final PlayerCommand banCommand = new BanCommand(this);
    final PlayerCommand kickCommand = new KickCommand(this);
    final PlayerCommand pardonCommand = new PardonCommand(this);
    // register commands
    cm.registerCommand("ban", banCommand);
    cm.registerCommand("check", new CheckCommand(this));
    cm.registerCommand("export", new ExportCommand(this));
    cm.registerCommand("history", new HistoryCommand(this));
    cm.registerCommand("import", new ImportCommand(this));
    cm.registerCommand("kick", kickCommand);
    cm.registerCommand("limits", new LimitsCommand(this));
    cm.registerCommand("pardon", pardonCommand);
    cm.registerCommand("purge", new PurgeCommand(this));
    cm.registerCommand("recent", new RecentCommand(this));
    cm.registerCommand("reload", new ReloadCommand(this));
    // register commands again as root commands
    getCommand("ban").setExecutor(banCommand);
    getCommand("kick").setExecutor(kickCommand);
    getCommand("pardon").setExecutor(pardonCommand);
  }

  private void registerListeners() {
    bannedPlayerListener = new BannedPlayerListener(this);
    pm.registerEvents(bannedPlayerListener, this);
  }

  public void reloadBannedPlayers() {
    loadBans();
  }

  public void setMaximumTemporaryBan(final long maximumTemporaryBan) {
    this.maximumTemporaryBan = maximumTemporaryBan;
  }

  private void setupDatabase() throws SQLException {
    try {
      getDatabase().find(BanRecord.class).findRowCount();
    } catch (final PersistenceException ex) {
      logger.warning("No database schema found; making a new one.");
      installDDL();
    }
    database = new DatabaseHandler(getDatabase());
  }

}
