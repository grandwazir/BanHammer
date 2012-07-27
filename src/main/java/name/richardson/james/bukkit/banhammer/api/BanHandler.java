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

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.internals.Handler;
import name.richardson.james.bukkit.utilities.persistence.SQLStorage;

public class BanHandler extends Handler implements API {

  private final SQLStorage database;

  public BanHandler(final Class<?> parentClass, final BanHammer plugin) {
    super(parentClass);
    this.database = plugin.getSQLStorage();
    logger.setPrefix(plugin.getLoggerPrefix());
  }

  public boolean banPlayer(final String playerName, final BanRecord record, final String reason) {
    if (!this.isPlayerBanned(playerName)) {
      /** Get the various database records required */
      final PlayerRecord playerRecord = PlayerRecord.find(this.database, playerName);
      final PlayerRecord creatorRecord = record.getCreater();
      final BanRecord banRecord = new BanRecord();

      /** Set the specifics of the ban */
      banRecord.setCreatedAt(record.getCreatedAt());
      banRecord.setPlayer(playerRecord);
      banRecord.setCreator(creatorRecord);
      banRecord.setReason(reason);
      banRecord.setExpiresAt(record.getExpiresAt());
      banRecord.setState(record.getState());

      /** Save records */
      final Object[] records = { playerRecord, creatorRecord, banRecord };
      this.database.save(records);
      /** Fire event */
      final BanHammerPlayerBannedEvent event = new BanHammerPlayerBannedEvent(banRecord, true);
      Bukkit.getServer().getPluginManager().callEvent(event);
      return true;
    } else {
      return false;
    }
  }

  public boolean banPlayer(final String playerName, final String senderName, final String reason, final Long banLength, final boolean notify) {
    if (!this.isPlayerBanned(playerName)) {
      /** Get the various database records required */
      final PlayerRecord playerRecord = PlayerRecord.find(this.database, playerName);
      final PlayerRecord creatorRecord = PlayerRecord.find(this.database, senderName);
      final BanRecord banRecord = new BanRecord();
      final Timestamp now = new Timestamp(System.currentTimeMillis());

      /** Set the specifics of the ban */
      banRecord.setCreatedAt(now);
      banRecord.setPlayer(playerRecord);
      banRecord.setCreator(playerRecord);
      banRecord.setReason(reason);
      if (banLength != 0) {
        banRecord.setExpiresAt(new Timestamp(System.currentTimeMillis() + banLength));
      }

      /** Save records */
      final Object[] records = { playerRecord, creatorRecord, banRecord };
      this.database.save(records);
      /** Fire event */
      final BanHammerPlayerBannedEvent event = new BanHammerPlayerBannedEvent(banRecord, notify);
      Bukkit.getServer().getPluginManager().callEvent(event);
      return true;
    } else {
      /** Player is already banned return false. */
      return false;
    }
  }

  public List<BanRecord> getPlayerBans(final String playerName) {
    PlayerRecord record = null;
    if (PlayerRecord.exists(this.database, playerName)) {
      record = PlayerRecord.find(this.database, playerName);
    }
    return (record == null) ? new LinkedList<BanRecord>() : record.getBans();
  }

  public boolean isPlayerBanned(final String playerName) {
    PlayerRecord record = null;
    if (PlayerRecord.exists(this.database, playerName)) {
      record = PlayerRecord.find(this.database, playerName);
    }
    return (record == null) ? false : record.isBanned();
  }

  public boolean pardonPlayer(final String playerName, final String senderName, final Boolean notify) {
    if (this.isPlayerBanned(playerName)) {
      final BanRecord ban = PlayerRecord.find(this.database, playerName).getActiveBan();
      ban.setState(BanRecord.State.PARDONED);
      /** Save records */
      this.database.save(ban);
      /** Fire event */
      final BanHammerPlayerPardonedEvent event = new BanHammerPlayerPardonedEvent(ban, notify);
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
