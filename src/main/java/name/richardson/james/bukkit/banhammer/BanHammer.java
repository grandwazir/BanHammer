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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

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
import name.richardson.james.bukkit.banhammer.migration.OldBanRecord;
import name.richardson.james.bukkit.utilities.command.CommandManager;
import name.richardson.james.bukkit.utilities.command.PluginCommand;
import name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin;

public class BanHammer extends SkeletonPlugin {

  private AliasHandler aliasHandler;

  private BannedPlayerListener bannedPlayerListener;

  private BanHammerConfiguration configuration;

  private final HashSet<String> bannedPlayerNames = new HashSet<String>();

  private DatabaseHandler database;

  private CommandManager commandManager;

  public AliasHandler getAliasHandler() {
    return this.aliasHandler;
  }

  public String getArtifactID() {
    return "ban-hammer";
  }

  public BanHammerConfiguration getBanHammerConfiguration() {
    return this.configuration;
  }

  public Map<String, Long> getBanLimits() {
    logger.debug(configuration.getBanLimits().toString());
    return this.configuration.getBanLimits();
  }

  public Set<String> getBannedPlayers() {
    return Collections.unmodifiableSet(this.bannedPlayerNames);
  }

  @Override
  public List<Class<?>> getDatabaseClasses() {
    return DatabaseHandler.getDatabaseClasses();
  }

  public DatabaseHandler getDatabaseHandler() {
    return this.database;
  }

  public String getGroupID() {
    return "name.richardson.james.bukkit";
  }

  /**
   * This returns a handler to allow access to the BanHammer API.
   * 
   * @return A new BanHandler instance.
   */
  public BanHandler getHandler(final Class<?> parentClass) {
    return new BanHandler(parentClass, this);
  }

  public void reloadBannedPlayers() {
    this.loadBans();
  }

  private String getFormattedBanCount(final int count) {
    final Object[] arguments = { count };
    final double[] limits = { 0, 1, 2 };
    final String[] formats = { this.getMessage("no-bans"), this.getMessage("one-ban"), this.getMessage("many-bans") };
    return this.getChoiceFormattedMessage("bans-loaded", arguments, formats, limits);
  }

  private void hookAlias() {
    final Alias plugin = (Alias) this.getServer().getPluginManager().getPlugin("Alias");
    if (plugin == null) {
      this.logger.warning("Unable to hook Alias.");
    } else {
      this.logger.info("Using " + plugin.getDescription().getFullName() + ".");
      this.aliasHandler = plugin.getHandler(BanHammer.class);
    }
  }

  private void loadBans() {
    this.bannedPlayerNames.clear();
    for (final Object record : this.database.list(OldBanRecord.class)) {
      final OldBanRecord ban = (OldBanRecord) record;
      if (ban.isActive()) {
        this.bannedPlayerNames.add(ban.getPlayer().toLowerCase());
      }
    }
    this.logger.info(this.getFormattedBanCount(this.database.count(OldBanRecord.class)));
  }

  protected Set<String> getModifiableBannedPlayers() {
    return this.bannedPlayerNames;
  }

  protected void loadConfiguration() throws IOException {
    this.configuration = new BanHammerConfiguration(this);
    if (this.configuration.isAliasEnabled()) {
      this.hookAlias();
    }
  }

  protected void registerCommands() {
    this.commandManager = new CommandManager(this);
    this.getCommand("bh").setExecutor(this.commandManager);
    final PluginCommand banCommand = new BanCommand(this);
    final PluginCommand kickCommand = new KickCommand(this);
    final PluginCommand pardonCommand = new PardonCommand(this);
    // register commands
    this.commandManager.addCommand(banCommand);
    this.commandManager.addCommand(new CheckCommand(this));
    this.commandManager.addCommand(new ExportCommand(this));
    this.commandManager.addCommand(new HistoryCommand(this));
    this.commandManager.addCommand(new ImportCommand(this));
    this.commandManager.addCommand(kickCommand);
    this.commandManager.addCommand(new LimitsCommand(this));
    this.commandManager.addCommand(pardonCommand);
    this.commandManager.addCommand(new PurgeCommand(this));
    this.commandManager.addCommand(new RecentCommand(this));
    this.commandManager.addCommand(new ReloadCommand(this));
    // register commands again as root commands
    this.getCommand("ban").setExecutor(banCommand);
    this.getCommand("kick").setExecutor(kickCommand);
    this.getCommand("pardon").setExecutor(pardonCommand);
  }

  protected void registerEvents() {
    this.bannedPlayerListener = new BannedPlayerListener(this);
    this.getServer().getPluginManager().registerEvents(this.bannedPlayerListener, this);
  }

  protected void setupPersistence() throws SQLException {
    try {
      this.getDatabase().find(OldBanRecord.class).findRowCount();
    } catch (final PersistenceException ex) {
      this.logger.warning(this.getMessage("no-database"));
      this.installDDL();
    }
    this.database = new DatabaseHandler(this.getDatabase());
    this.loadBans();
  }

}
