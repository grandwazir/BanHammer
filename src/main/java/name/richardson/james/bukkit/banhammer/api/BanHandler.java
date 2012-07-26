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
package name.richardson.james.bukkit.banhammer.api;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.banhammer.PlayerRecord;
import name.richardson.james.bukkit.banhammer.BanRecord.State;
import name.richardson.james.bukkit.banhammer.BanRecord.Type;
import name.richardson.james.bukkit.banhammer.migration.OldBanRecord;
import name.richardson.james.bukkit.utilities.internals.Handler;
import name.richardson.james.bukkit.utilities.persistence.SQLStorage;

public class BanHandler extends Handler implements BanHammerAPI {

  private final SQLStorage database;

  public BanHandler(final Class<?> parentClass, final BanHammer plugin) {
    super(parentClass);
    this.database = plugin.getSQLStorage();
    logger.setPrefix(plugin.getLoggerPrefix());
  }
  
  public boolean banPlayer(final String playerName, BanRecord record, String reason) {
    if (!this.isPlayerBanned(playerName)) {
      /** Get the various database records required */
      final PlayerRecord playerRecord = this.getPlayerRecord(playerName, true);
      final PlayerRecord creatorRecord = record.getCreatedBy();
      final BanRecord banRecord = new BanRecord();
      
      /** Set the specifics of the ban */
      banRecord.setCreatedAt(record.getCreatedAt());
      banRecord.setPlayer(playerRecord);
      banRecord.setCreator(creatorRecord);
      banRecord.setReason(reason);
      banRecord.setExpiresAt(record.getExpiresAt());
      banRecord.setState(record.getState());
      banRecord.setType(record.getType());
      
      /** Save records */
      Object[] records = {playerRecord, creatorRecord, banRecord};
      this.database.save(records);
      /** Fire event */
      BanHammerPlayerBannedEvent event = new BanHammerPlayerBannedEvent(banRecord);
      Bukkit.getServer().getPluginManager().callEvent(event);
      return true;   
    } else {
      return false;
    }
  }
  
  public void migrateRecord(final OldBanRecord record) {
    /** Get the various database records required */
    final PlayerRecord playerRecord = this.getPlayerRecord(record.getPlayer(), true);
    final PlayerRecord creatorRecord = this.getPlayerRecord(record.getCreatedBy(), true);
    final BanRecord banRecord = new BanRecord();
    
    /** Set the specifics of the ban */
    banRecord.setCreatedAt(record.getCreatedAt());
    banRecord.setPlayer(playerRecord);
    banRecord.setCreator(creatorRecord);
    banRecord.setReason(record.getReason());
    banRecord.setExpiresAt(record.getExpiresAt());
    banRecord.setState(BanRecord.State.NORMAL);
    final Type banType = (record.getExpiresAt() != 0) ? Type.TEMPORARY : Type.PERMENANT;
    banRecord.setType(banType);
    
    /** Save records */
    Object[] records = {playerRecord, creatorRecord, banRecord};
    this.database.save(records);
  }

  public boolean banPlayer(final String playerName, final String senderName, final String reason, final Long banLength, final boolean notify) {
    if (!this.isPlayerBanned(playerName)) {
      /** Get the various database records required */
      final PlayerRecord playerRecord = this.getPlayerRecord(playerName, true);
      final PlayerRecord creatorRecord = this.getPlayerRecord(senderName, true);
      final BanRecord banRecord = new BanRecord();
      final long now = System.currentTimeMillis();
      
      /** Set the specifics of the ban */
      banRecord.setCreatedAt(now);
      banRecord.setPlayer(playerRecord);
      banRecord.setCreator(playerRecord);
      banRecord.setReason(reason);
      final long expiresAt = (banLength != 0) ? now + banLength : 0;
      banRecord.setExpiresAt(expiresAt);
      banRecord.setState(State.NORMAL);
      final Type banType = (banLength != 0) ? Type.TEMPORARY : Type.PERMENANT;
      banRecord.setType(banType);
      
      /** Save records */
      Object[] records = {playerRecord, creatorRecord, banRecord};
      this.database.save(records);
      /** Fire event */
      BanHammerPlayerBannedEvent event = new BanHammerPlayerBannedEvent(banRecord);
      Bukkit.getServer().getPluginManager().callEvent(event);
      return true;   
    } else {
      /** Player is already banned return false. */
      return false;
    }
  }
  
  private PlayerRecord getPlayerRecord(String playerName, boolean create) {
    PlayerRecord record = PlayerRecord.findByName(database, playerName);
    if (record != null && create) {
      record = new PlayerRecord();
      record.setPlayerName(playerName);
      database.save(record);
    }
    return record;
  }

  public List<BanRecord> getPlayerBans(final String playerName) {
    PlayerRecord record = this.getPlayerRecord(playerName, false);
    return (record == null) ? new LinkedList<BanRecord>() : record.getBans();
  }

  public boolean isPlayerBanned(final String playerName) {
    final PlayerRecord record = this.getPlayerRecord(playerName, false);
    return (record == null) ? false : record.isBanned();
  }

  public boolean pardonPlayer(final String playerName, final String senderName, final Boolean notify) {
    if (this.isPlayerBanned(playerName)) {
      final PlayerRecord playerRecord = this.getPlayerRecord(playerName, false);
      Type banType = null;
      
      /** Convert all active bans into pardoned bans */
      for (BanRecord ban : playerRecord.getBans()) {
        banType = ban.getType();
        ban.setState(State.PARDONED);
      }
      
      /** Save records */
      this.database.save(playerRecord.getBans());
      /** Fire event */
      BanHammerPlayerPardonedEvent event = new BanHammerPlayerPardonedEvent(playerName, banType);
      Bukkit.getServer().getPluginManager().callEvent(event);
      return true;     
    } else {
      return false;
    }
  }

  public boolean removePlayerBan(final BanRecord ban) {
    this.database.delete(ban);
    Handler.logger.debug(String.format("Removed a ban belonging to %s.", ban.getPlayer()));
    return true;
  }

  public int removePlayerBans(final List<BanRecord> bans) {
    final int i = this.database.delete(bans);
    Handler.logger.debug(String.format("Removed %d ban(s).", i));
    return i;
  }

}
