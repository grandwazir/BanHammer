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
package name.richardson.james.bukkit.banhammer;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import name.richardson.james.bukkit.util.Handler;


public class BanHandler extends Handler implements BanHammerAPI {

  private final Set<String> bannedPlayers;
  private final DatabaseHandler database;
  
  public BanHandler(Class<?> parentClass, BanHammer plugin) {
    super(parentClass);
    this.database = plugin.getDatabaseHandler();
    this.bannedPlayers = plugin.getBannedPlayers();
  }

  @Override
  public boolean banPlayer(String playerName, String senderName, String reason, Long banLength, boolean notify) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public BanRecord getPlayerBan(String playerName) {
    return BanRecord.findFirstByName(playerName);
  }

  @Override
  public List<BanRecord> getPlayerBans(String playerName) {
    return BanRecord.findByName(playerName);
  }

  @Override
  public boolean isPlayerBanned(String playerName) {
    return this.bannedPlayers.contains(playerName.toLowerCase());
  }

  @Override
  public boolean pardonPlayer(String playerName, String senderName, Boolean notify) {
    if (this.isPlayerBanned(playerName)) {
      this.database.delete(BanRecord.findFirstByName(playerName));
      this.bannedPlayers.remove(playerName.toLowerCase());
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean removePlayerBan(CachedBan ban) {
    this.database.delete(BanRecord.findCachedBan(ban));
  }

  @Override
  public int removePlayerBans(List<CachedBan> bans) {
    this.database.delete(BanRecord.findCachedBans(bans));
  }

  
}