/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * BanHandler.java is part of BanHammer.
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
package name.richardson.james.banhammer.ban;

import java.util.ArrayList;
import java.util.List;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.util.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;


public class BanHandler {

  
  private CachedList cache;
  private Server server;
  
  public BanHandler(Server server) {
    this.server = server;
    this.cache = CachedList.getInstance();
  }

  /**
   * Ban a player from the server and prevent them from logging in. 
   * 
   * If the player is online when they are banned they will be kicked.
   * 
   * @param player The player that is to be banned
   * @param senderName The name of the person doing the banning. If called by a plugin without user intervention this should be the name of the plugin.
   * @param reason The reason why the player has been banned.
   * @param expiryTime A Long representing how many milliseconds the player should be banned for. For permanent bans you should specify 0.
   * @param notify if true, players are not notified of the ban.
   * @return true if the ban has been successfully created. Will return false if the player is already banned.
   */
  public boolean banPlayer(Player player, String senderName, String reason, Long expiryTime, boolean notify) {
    final String playerName = player.getName();
    if (!this.cache.contains(playerName)) {
      BanRecord.create(playerName, senderName, expiryTime + System.currentTimeMillis(), System.currentTimeMillis(), reason);
      this.cache.add(playerName);
      
      if (notify) {
        if (expiryTime.intValue() == 0) {
          server.broadcastMessage(String.format(ChatColor.RED + BanHammer.getMessage("notifyBannedPlayer"), playerName));
        } else {
          server.broadcastMessage(String.format(ChatColor.RED + BanHammer.getMessage("notifyTempBannedPlayer"), playerName));
        }
        server.broadcastMessage(String.format(ChatColor.YELLOW + BanHammer.getMessage("notifyReason"), reason));
      }
      
      if (player.isOnline()) {
        player.kickPlayer(String.format(BanHammer.getMessage("kickedMessage"), reason));
      }

      Logger.info(String.format(BanHammer.getMessage("logPlayerBanned")));
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * Pardon a player and allow them to login once more.
   * 
   * @param player - The player that is to be pardoned.
   * @param senderName - The name of the person doing the pardoning. If called by a plugin without user intervention this should be the name of the plugin.
   * @param notify - if true, players are not notified of the ban.
   * @return true if the ban has been successfully repealed. Will return false if the player was not banned in the first place.
   */
  public boolean pardonPlayer(Player player, String senderName, Boolean notify) {
    final String playerName = player.getName();
    if (this.isPlayerBanned(player)) {
        this.cache.remove(playerName);
        BanRecord.findFirst(playerName).destroy();
        Logger.info(String.format(BanHammer.getMessage("logPlayerPardoned"), senderName, playerName));
        if (notify) {
          server.broadcastMessage(String.format(ChatColor.GREEN + BanHammer.getMessage("playerPardoned")));
        }
        return true;
    } else { 
        return false;
    }
  }

  /**
   * Check to see if a player is currently banned.
   * 
   * This only checks to see if the player has an active ban, one that is preventing them from logging into the server. 
   * Previous bans are not taken into account.
   * 
   * @param player - The player to check.
   * @return true if the player is currently banned, false if they are not.
   */
  public boolean isPlayerBanned(Player player) {
    final String playerName = player.getName();
    if (this.cache.contains(playerName)) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * Get the details of any active bans associated with a player.
   * 
   * This will only return details of an active ban, one that is preventing them from logging into the server. 
   * Previous bans will not be returned.
   * 
   * @param player - The player to check.
   * @return a CachedBan with the details of the active ban or null if no ban exists.
   */
  public CachedBan getPlayerBan(Player player) {
    BanRecord record = BanRecord.findFirst(player.getName());
    if (record != null) {
      return record.toCachedBan();
    } else {
      return null;
    }
  }
  
  /**
   * Get the details of all bans associated with a specific player.
   * 
   * This will provide a CachedBan providing access to all the related data, but no ability to change or modify the actual record.
   * 
   * @param player - The player to lookup.
   * @return a <List>CachedBan with the details of all bans associated with the player. If no bans are on record, the list will be empty.
   */
  public List<CachedBan> getPlayerBans(Player player) {
    List<CachedBan> result = new ArrayList<CachedBan>(); 
    for (BanRecord record : BanRecord.find(player.getName())) {
      result.add(record.toCachedBan());
    }
    return result;
  }
  
}
