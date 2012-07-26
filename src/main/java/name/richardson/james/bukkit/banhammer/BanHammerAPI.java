/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * BanHammerAPI.java is part of BanHammer.
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

import java.util.List;

import name.richardson.james.bukkit.banhammer.migration.OldBanRecord;

public interface BanHammerAPI {

  /**
   * Ban a player from the server and prevent them from logging in. If the
   * player is online when they are banned they will be kicked.
   * 
   * @param playerName
   *          The name player that is to be banned
   * @param senderName
   *          The name of the person doing the banning. If called by a plugin
   *          without user intervention this should be the name of the plugin.
   * @param reason
   *          The reason why the player has been banned.
   * @param expiryTime
   *          A Long representing how many milliseconds the player should be
   *          banned for. For permanent bans you should specify 0.
   * @param notify
   *          if true, players are not notified of the ban.
   * @return true if the ban has been successfully created. Will return false if
   *         the player is already banned.
   */
  public boolean banPlayer(String playerName, String senderName, String reason, Long banLength, boolean notify);

  /**
   * Get the details of any active ban associated with a player. This will only
   * return details of an active ban, one that is preventing them from logging
   * into the server. Previous bans will not be returned.
   * 
   * @param player
   *          - The name of the player to check (search is case insensitive).
   * @return a CachedBan with the details of the active ban or null if no ban
   *         exists.
   */
  public OldBanRecord getPlayerBan(String playerName);

  /**
   * Get the details of all bans associated with a specific player. This will
   * provide a CachedBan providing access to all the related data, but no
   * ability to change or modify the actual record.
   * 
   * @param player
   *          The name of the player to check (search is case insensitive).
   * @return a list with the details of all bans associated with the player. If
   *         no bans are on record, the list will be empty.
   */
  public List<OldBanRecord> getPlayerBans(String playerName);

  /**
   * Check to see if a player is currently banned. This only checks to see if
   * the player has an active ban, one that is preventing them from logging into
   * the server. Previous bans are not taken into account.
   * 
   * @param player
   *          The name of the player to check.
   * @return true if the player is currently banned, false if they are not.
   */
  public boolean isPlayerBanned(String playerName);

  /**
   * Pardon a player (remove any active bans) and allow them to login once more.
   * 
   * @param player
   *          - The player that is to be pardoned.
   * @param senderName
   *          - The name of the person doing the pardoning. If called by a
   *          plugin without user intervention this should be the name of the
   *          plugin.
   * @param notify
   *          - if true, players are not notified of the ban.
   * @return true if the ban has been successfully repealed. Will return false
   *         if the player was not banned in the first place.
   */
  public boolean pardonPlayer(String playerName, String senderName, Boolean notify);

  /**
   * Remove a ban from the database
   * 
   * @param ban
   *          - The CachedBan to convert into a BanRecord and remove.
   */
  public boolean removePlayerBan(OldBanRecord ban);

  /**
   * Remove bans from the database
   * 
   * @param ban
   *          - A Set of CachedBans to convert into a BanRecords and remove.
   * @return the number of bans removed.
   */
  public int removePlayerBans(List<OldBanRecord> bans);

}
