/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * BanHammer.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with BanHammer.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.banhammer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.persistence.PersistenceException;

import name.richardson.james.banhammer.ban.BanCommand;
import name.richardson.james.banhammer.ban.BanHandler;
import name.richardson.james.banhammer.ban.BanRecord;
import name.richardson.james.banhammer.ban.CheckCommand;
import name.richardson.james.banhammer.ban.HistoryCommand;
import name.richardson.james.banhammer.ban.PardonCommand;
import name.richardson.james.banhammer.ban.PlayerListener;
import name.richardson.james.banhammer.ban.PurgeCommand;
import name.richardson.james.banhammer.ban.RecentCommand;
import name.richardson.james.banhammer.ban.ReloadCommand;
import name.richardson.james.banhammer.kick.KickCommand;
import name.richardson.james.banhammer.util.Logger;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BanHammerPlugin extends JavaPlugin {

  private static ResourceBundle messages;
  private final static Locale locale = Locale.getDefault();
  private final CommandManager cm;
  
  private PlayerListener playerListener;
  private PluginDescriptionFile desc;
  private PluginManager pm;

  public BanHammerPlugin() {
    this.cm = new CommandManager();
  }

  /**
   * This returns a localised string from the loaded ResourceBundle.
   * 
   * @param key The key for the desired string.
   * @return The string for the given key.
   */
  public static String getMessage(String key) {
    return messages.getString(key);
  }
  
  @Override
  public List<Class<?>> getDatabaseClasses() {
    List<Class<?>> list = new ArrayList<Class<?>>();
    list.add(BanRecord.class);
    return list;
  }

  /**
   * This returns a handler to allow access to the BanHammer API.
   * 
   * @return A new BanHandler instance.
   */
  public BanHandler getHandler() {
    return new BanHandler(this.getServer());
  }

  @Override
  public void onDisable() {
    Logger.info(String.format(messages.getString("plugin-disabled"), this.desc.getName()));
  }

  @Override
  public void onEnable() {
    this.desc = this.getDescription();
    this.pm = this.getServer().getPluginManager();

    try {
      this.setupLocalisation();
      this.setupDatabase();
      this.setupListeners();
      this.setupCommands();
    } catch (Exception e) {
      Logger.severe(e.getMessage());
      e.printStackTrace();
      this.pm.disablePlugin(this);
      return;
    }

    Logger.info(String.format(BanHammerPlugin.getMessage("plugin-enabled"), this.desc.getFullName()));
  }

  private void setupCommands() {
    this.getCommand("ban").setExecutor(new BanCommand(this));
    this.getCommand("kick").setExecutor(new KickCommand(this));
    this.getCommand("pardon").setExecutor(new PardonCommand(this));
    this.getCommand("bh").setExecutor(this.cm);
    this.cm.registerCommand("check", new CheckCommand(this));
    this.cm.registerCommand("history", new HistoryCommand(this));
    this.cm.registerCommand("purge", new PurgeCommand(this));
    this.cm.registerCommand("recent", new RecentCommand(this));
    this.cm.registerCommand("reload", new ReloadCommand(this));
  }

  private void setupDatabase() {
    try {
      this.getDatabase().find(BanRecord.class).findRowCount();
    } catch (PersistenceException ex) {
      Logger.warning(BanHammerPlugin.getMessage("no-database"));
      this.installDDL();
    }
    BanRecord.setDatabase(this.getDatabase());
  }

  private void setupListeners() {
    this.playerListener = new PlayerListener();
    this.pm.registerEvent(Event.Type.PLAYER_LOGIN, this.playerListener, Event.Priority.Highest, this);
  }

  private void setupLocalisation() {
    BanHammerPlugin.messages = ResourceBundle.getBundle("name.richardson.james.banhammer.localisation.Messages", locale);
  }

}
