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
import java.util.List;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.banhammer.api.API;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.internals.Handler;

public class BanHandler extends Handler implements API {

  /* The database used by this handler */
  private final EbeanServer database;

  /**
   * Instantiates a new ban handler.
   * 
   * @param parentClass the parent class
   * @param plugin the plugin
   */
  public BanHandler(final Class<?> parentClass, final BanHammer plugin) {
    super(parentClass);
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
    final PlayerRecord player = this.getPlayerRecord(playerName);
    if (player.isBanned()) return false;
    final PlayerRecord creator = this.getPlayerRecord(senderName);
    final BanRecord ban = new BanRecord();
    final long now = System.currentTimeMillis();
    ban.setPlayer(player);
    ban.setCreator(creator);
    ban.setReason(reason);
    ban.setCreatedAt(new Timestamp(now));
    if (banLength != 0) ban.setExpiresAt(new Timestamp(now + banLength));
    Object[] records = { player, creator, ban };
    database.save(Arrays.asList(records));
    return true;
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.banhammer.api.API#getPlayerBans(java.lang.
   * String)
   */
  public List<BanRecord> getPlayerBans(String playerName) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.banhammer.api.API#isPlayerBanned(java.lang
   * .String)
   */
  public boolean isPlayerBanned(String playerName) {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.banhammer.api.API#pardonPlayer(java.lang.String
   * , java.lang.String, boolean)
   */
  public boolean pardonPlayer(String playerName, String senderName, boolean notify) {
    // TODO Auto-generated method stub
    return false;
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
    final PlayerRecord player = this.getPlayerRecord(playerName);
    if (player.isBanned()) return false;
    final PlayerRecord creator = sourceBan.getCreator();
    final BanRecord ban = new BanRecord();
    ban.setPlayer(player);
    ban.setCreator(creator);
    ban.setReason(reason);
    ban.setCreatedAt(sourceBan.getCreatedAt());
    if (sourceBan.getExpiresAt().getTime() != 0) ban.setExpiresAt(sourceBan.getExpiresAt());
    Object[] records = { player, creator, ban };
    database.save(Arrays.asList(records));
    return true;
  }

  /**
   * Gets the database record for a player.
   * 
   * @param playerName the player name
   * @return the player record
   */
  private PlayerRecord getPlayerRecord(String playerName) {
    if (!PlayerRecord.exists(database, playerName)) {
      PlayerRecord playerRecord = new PlayerRecord();
      playerRecord.setName(playerName);
      database.save(playerRecord);
      return PlayerRecord.find(database, playerName);
    } else {
      return PlayerRecord.find(database, playerName);
    }
  }

}
