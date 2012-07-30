/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * BanRecordCache.java is part of BanHammer.
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

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;

public class BanRecordCache {
  
  /** The underlying HashMap. */
  private final HashMap<String, BanRecord> cache = new LinkedHashMap<String, BanRecord>();

  /** The database containing the BanRecords. */
  private final EbeanServer database;

  /**
   * Instantiates a new ban record cache.
   * 
   * @param database the database containing the BanRecords
   */
  public BanRecordCache(final EbeanServer database) {
    this.database = database;
    this.load();
  }

  /**
   * Check if the cache contains a specific player.
   * 
   * This method also validates if the ban is still active before returning.
   * 
   * @param playerName the player name to look for
   * @return true, if successful
   */
  public boolean contains(final String playerName) {
    return this.cache.containsKey(playerName);
  }

  /**
   * Gets details about a banned player.
   * 
   * @param playerName the player name to look for
   * @return the relevant ban record
   */
  public BanRecord get(final String playerName) {
    return cache.get(playerName);
  }

  /**
   * Removes the player from the ban cache.
   * 
   * @param playerName the player name to remove
   */
  public void remove(final String playerName) {
    this.cache.remove(playerName);
  }

  /**
   * Place the player in the ban cache and lookup the relevant ban.
   * 
   * @param playerName the player name to look up
   */
  public void set(final String playerName) {
    this.cache.put(playerName, PlayerRecord.find(this.database, playerName).getActiveBan());
  }

  /**
   * Place the player and in the ban cache with the the ban provided.
   * 
   * @param playerName the player name
   * @param ban the ban
   */
  public void set(final String playerName, final BanRecord ban) {
    this.cache.put(playerName, ban);
  }

  /**
   * The total number of players in the cache.
   * 
   * @return the number of players
   */
  public int size() {
    return this.cache.size();
  }

  /**
   * Load the cache will details of all current active bans.
   */
  private void load() {
    for (final PlayerRecord player : PlayerRecord.list(database)) {
      if (player.isBanned()) {
        this.cache.put(player.getName(), player.getActiveBan());
      }
    }
  }

}
