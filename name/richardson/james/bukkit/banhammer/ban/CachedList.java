/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * CachedList.java is part of BanHammer.
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
package name.richardson.james.bukkit.banhammer.ban;

import java.util.HashMap;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.util.Logger;


class CachedList {

  private HashMap<String, BanRecord.Type> list = new HashMap<String, BanRecord.Type>();
  private HashMap<String, String> permenant = new HashMap<String, String>();
  private HashMap<String, Long> temporary = new HashMap<String, Long>();
  private static CachedList instance;
  
  CachedList() {
    this.load();
    CachedList.instance = this;
  }
  
  static CachedList getInstance() {
    return instance;
  }

  public void reload() {
    this.unload();
    this.load();
  }

  public void remove(String playerName) {
    playerName = playerName.toLowerCase();
    this.list.remove(playerName);
    this.permenant.remove(playerName);
    this.temporary.remove(playerName);
  }

  public int size() {
    return this.list.size();
  }

  private void unload() {
    this.permenant.clear();
    this.temporary.clear();
  }

  void add(String playerName) {
    playerName = playerName.toLowerCase();
    BanRecord ban = BanRecord.findFirst(playerName);
    this.list.put(playerName, ban.getType());
    if (ban.getType().equals(BanRecord.Type.PERMENANT)) {
      this.permenant.put(playerName, ban.getReason());
    } else if (ban.getType().equals(BanRecord.Type.TEMPORARY)) {
      this.temporary.put(playerName, ban.getExpiresAt());
    }
  }

  boolean contains(String playerName) {
    playerName = playerName.toLowerCase();
    if (this.list.containsKey(playerName)) {
      return true;
    } else { 
      return false;
    }
  }

  CachedBan get(String playerName) {
    playerName = playerName.toLowerCase();
    BanRecord.Type type = this.list.get(playerName);
    if (type.equals(BanRecord.Type.PERMENANT)) {
      if (!this.permenant.containsKey(playerName)) {
        this.add(playerName);
      }
      return new CachedBan((long) 0, playerName, this.permenant.get(playerName), playerName, (long) 0);
    } else {
      if (!this.temporary.containsKey(playerName)) {
        this.add(playerName);
      }
      return new CachedBan(this.temporary.get(playerName), playerName, null, null, (long) 0);
    }
  }

  void load() {
    for (BanRecord ban : BanRecord.list()) {
      if (ban.isActive()) {
        this.list.put(ban.getPlayer().toLowerCase(), ban.getType());
      }
    }
    Logger.info(String.format(BanHammer.getMessage("bans-loaded"), this.size()));
  }

}
