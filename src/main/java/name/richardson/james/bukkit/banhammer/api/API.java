/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * API.java is part of BanHammer.
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
package name.richardson.james.bukkit.banhammer.api;

import java.util.List;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord;

public interface API {

  /**
   * Ban a player from the server.
   *
   * @param playerName the name of the player to ban
   * @param senderName the name of the player/plugin requesting the ban
   * @param reason the reason for the ban
   * @param banLength the length of the ban in milliseconds
   * @param notify if true, broadcast a message to notify players
   * @return true, if successful
   */
  public boolean banPlayer(String playerName, String senderName, String reason, long banLength, boolean notify);
  
  /**
   * Pardon a player and allow them to login to the server again
   *
   * @param playerName the name of the player to pardon
   * @param senderName the name of the player/plugin requesting the pardon
   * @param notify if true, broadcast a message to notify players
   * @return true, if successful
   */
  public boolean pardonPlayer(String playerName, String senderName, boolean notify);
  
  /**
   * Checks if a player is currently banned.
   *
   * @param playerName the name of the player
   * @return true, if is player banned
   */
  public boolean isPlayerBanned(String playerName);
   
  /**
   * Gets all bans made against a player.
   *
   * @param playerName the name of the player
   * @return the player's bans
   */
  public List<BanRecord> getPlayerBans(String playerName);

}
