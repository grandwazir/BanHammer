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

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import name.richardson.james.bukkit.banhammer.BanRecord.State;
import name.richardson.james.bukkit.banhammer.BanRecord.Type;
import name.richardson.james.bukkit.banhammer.ban.BanSummary;
import name.richardson.james.bukkit.banhammer.migration.OldBanRecord;
import name.richardson.james.bukkit.utilities.internals.Handler;
import name.richardson.james.bukkit.utilities.persistence.SQLStorage;
import name.richardson.james.bukkit.utilities.plugin.Localisable;

public class BanHandler extends Handler implements BanHammerAPI, Localisable {

  private final Set<String> bannedPlayers;
  private final SQLStorage database;
  private final Server server;
  private final BanHammer plugin;

  public BanHandler(final Class<?> parentClass, final BanHammer plugin) {
    super(parentClass);
    this.database = plugin.getSQLStorage();
    this.bannedPlayers = plugin.getModifiableBannedPlayers();
    this.server = plugin.getServer();
    this.plugin = plugin;
    logger.setPrefix(plugin.getLoggerPrefix());
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
      banRecord.setState(State.ACTIVE);
      final Type banType = (banLength != 0) ? Type.TEMPORARY : Type.PERMENANT;
      banRecord.setType(banType);
      
      /** Save records */
      Object[] records = {playerRecord, creatorRecord, banRecord};
      this.database.save(records);
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

  public String getChoiceFormattedMessage(final String key, final Object[] arguments, final String[] formats, final double[] limits) {
    return this.plugin.getChoiceFormattedMessage(key, arguments, formats, limits);
  }

  public Locale getLocale() {
    return this.plugin.getLocale();
  }

  public String getMessage(final String key) {
    return this.plugin.getMessage(key);
  }

  public OldBanRecord getPlayerBan(final String playerName) {
    return OldBanRecord.findFirstByName(this.database, playerName);
  }

  public List<OldBanRecord> getPlayerBans(final String playerName) {
    return OldBanRecord.findByName(this.database, playerName);
  }

  public String getSimpleFormattedMessage(final String key, final Object argument) {
    final Object[] arguments = { argument };
    return this.plugin.getSimpleFormattedMessage(key, arguments);
  }

  public String getSimpleFormattedMessage(final String key, final Object[] arguments) {
    return this.plugin.getSimpleFormattedMessage(key, arguments);
  }

  public boolean isPlayerBanned(final String playerName) {
    final OldBanRecord record = OldBanRecord.findFirstByName(this.database, playerName);
    if (record != null) {
      if (record.isActive()) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public boolean pardonPlayer(final String playerName, final String senderName, final Boolean notify) {
    if (this.isPlayerBanned(playerName)) {
      this.database.delete(OldBanRecord.findFirstByName(this.database, playerName));
      this.bannedPlayers.remove(playerName.toLowerCase());
      if (notify) {
        this.notifyPlayers(this.getPardonBroadcastMessage(senderName, playerName));
      }
      Handler.logger.info(this.getPardonLogMessage(senderName, playerName));
      return true;
    } else {
      return false;
    }
  }

  public boolean removePlayerBan(final OldBanRecord ban) {
    this.database.delete(ban);
    Handler.logger.debug(String.format("Removed a ban belonging to %s.", ban.getPlayer()));
    return true;
  }

  public int removePlayerBans(final List<OldBanRecord> bans) {
    final int i = this.database.delete(bans);
    Handler.logger.debug(String.format("Removed %d ban(s).", i));
    return i;
  }

  private String getBanBroadcastMessage(String senderName, String playerName) {
    final Object[] arguments = { playerName, senderName };
    return this.getSimpleFormattedMessage("bancommand-broadcast-message", arguments);
  }

  private String getBanSummaryMessage(String senderName, String playerName, String reason) {
    final Object[] arguments = { playerName, senderName, reason };
    return this.getSimpleFormattedMessage("bancommand-summary-message", arguments);
  }

  private String getPardonBroadcastMessage(String senderName, String playerName) {
    final Object[] arguments = { playerName, senderName };
    return this.getSimpleFormattedMessage("pardoncommand-broadcast-message", arguments);
  }

  private String getPardonLogMessage(String senderName, String playerName) {
    final Object[] arguments = { playerName, senderName };
    return this.getSimpleFormattedMessage("pardoncommand-summary-result", arguments);
  }

  private void notifyPlayers(final String message) {
    for (Player player : this.server.getOnlinePlayers()) {
      if (player.hasPermission("banhammer.notify")) player.sendMessage(message);
    }
  }

}
