/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * BanHandler.java is part of BanHammer.
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

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.avaje.ebean.EbeanServer;

import org.bukkit.Bukkit;

import name.richardson.james.bukkit.banhammer.api.API;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.internals.Handler;
import name.richardson.james.bukkit.utilities.internals.Logger;

public class BanHandler extends Handler implements API {

  /* Logger for this class */
  private static final Logger logger = new Logger(BanHandler.class);
  
  /* The database used by this handler */
  private final EbeanServer database;

  private final BanHammer plugin;

  /**
   * Instantiates a new ban handler.
   * 
   * @param parentClass the parent class
   * @param plugin the plugin
   */
  public BanHandler(final Class<?> parentClass, final BanHammer plugin) {
    super(parentClass);
    this.plugin = plugin;
    this.database = plugin.getDatabase();
    logger.setPrefix(plugin.getLoggerPrefix());
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.banhammer.api.API#banPlayer(java.lang.String,
   * java.lang.String, java.lang.String, long, boolean)
   */
  public boolean banPlayer(String playerName, String senderName, String reason, long banLength, boolean notify) {
    final PlayerRecord player = PlayerRecord.find(database, playerName);
    if (player.isBanned()) return false;
    final PlayerRecord creator = PlayerRecord.find(database, playerName);
    final BanRecord ban = new BanRecord();
    final long now = System.currentTimeMillis();
    ban.setPlayer(player);
    ban.setCreator(creator);
    ban.setReason(reason);
    ban.setState(BanRecord.State.NORMAL);
    ban.setCreatedAt(new Timestamp(now));
    if (banLength != 0) ban.setExpiresAt(new Timestamp(now + banLength));
    database.save(ban);
    BanHammerPlayerBannedEvent event = new BanHammerPlayerBannedEvent(ban, !notify);
    Bukkit.getServer().getPluginManager().callEvent(event);
    Object[] arguments = { playerName, senderName, reason};
    logger.info(plugin.getSimpleFormattedMessage(this.getClass().getSimpleName().toLowerCase() + ".log-banned-player", arguments));
    return true;
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.banhammer.api.API#getPlayerBans(java.lang.
   * String)
   */
  public List<BanRecord> getPlayerBans(String playerName) {
    final PlayerRecord playerRecord = PlayerRecord.find(database, playerName);
    return (playerRecord == null) ? new LinkedList<BanRecord>() : playerRecord.getBans();
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.banhammer.api.API#isPlayerBanned(java.lang
   * .String)
   */
  public boolean isPlayerBanned(String playerName) {
    final PlayerRecord playerRecord = PlayerRecord.find(database, playerName);
    return (playerRecord == null) ? false : playerRecord.isBanned();
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.banhammer.api.API#pardonPlayer(java.lang.String
   * , java.lang.String, boolean)
   */
  public boolean pardonPlayer(String playerName, String senderName, boolean notify) {
    final PlayerRecord playerRecord = PlayerRecord.find(database, playerName);
    if (playerRecord != null && playerRecord.isBanned()) {
      final BanRecord banRecord = playerRecord.getActiveBan();
      BanHammerPlayerPardonedEvent event = new BanHammerPlayerPardonedEvent(banRecord, !notify);
      database.delete(playerRecord.getActiveBan());
      Bukkit.getServer().getPluginManager().callEvent(event);
      Object[] arguments = { playerName, senderName};
      logger.info(plugin.getSimpleFormattedMessage(this.getClass().getSimpleName().toLowerCase() + ".log-pardoned-player", arguments));
      return true;
    } else {
      return false;
    }
    
  }

  /**
   * Ban a player using another ban as the template.
   * 
   * @param playerName the name of the player to ban
   * @param sourceBan the ban to use as a template
   * @param reason the reason for the ban
   * @param notify if true, broadcast a message to notify players
   * @return true, if successful
   */
  protected boolean banPlayer(String playerName, BanRecord sourceBan, String reason, boolean notify) {
    final PlayerRecord player = PlayerRecord.find(database, playerName);
    if (player.isBanned()) return false;
    final PlayerRecord creator = sourceBan.getCreator();
    final BanRecord ban = new BanRecord();
    ban.setPlayer(player);
    ban.setCreator(creator);
    ban.setReason(reason);
    ban.setState(BanRecord.State.NORMAL);
    ban.setCreatedAt(sourceBan.getCreatedAt());
    if (sourceBan.getExpiresAt().getTime() != 0) ban.setExpiresAt(sourceBan.getExpiresAt());
    database.save(ban);
    Object[] arguments = { playerName, sourceBan.getCreator().getName(), reason};
    logger.info(plugin.getSimpleFormattedMessage(this.getClass().getSimpleName().toLowerCase() + ".log-banned-player", arguments));
    return true;
  }

}
