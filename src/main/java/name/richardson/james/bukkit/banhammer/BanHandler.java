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

import name.richardson.james.bukkit.banhammer.ban.BanSummary;
import name.richardson.james.bukkit.utilities.internals.Handler;
import name.richardson.james.bukkit.utilities.plugin.Localisable;

public class BanHandler extends Handler implements BanHammerAPI, Localisable {

  private final Set<String> bannedPlayers;
  private final DatabaseHandler database;
  private final Server server;
  private final BanHammer plugin;

  public BanHandler(final Class<?> parentClass, final BanHammer plugin) {
    super(parentClass);
    this.database = plugin.getDatabaseHandler();
    this.bannedPlayers = plugin.getModifiableBannedPlayers();
    this.server = plugin.getServer();
    this.plugin = plugin;
    logger.setPrefix("[BanHammer] ");
  }

  public boolean banPlayer(final String playerName, final String senderName, final String reason, final Long banLength, final boolean notify) {
    if (!this.isPlayerBanned(playerName)) {
      final BanRecord ban = new BanRecord();
      final long now = System.currentTimeMillis();
      ban.setCreatedAt(now);
      ban.setPlayer(playerName);
      ban.setCreatedBy(senderName);
      ban.setReason(reason);

      if (banLength != 0) {
        ban.setExpiresAt(now + banLength);
      } else {
        ban.setExpiresAt(0);
      }

      this.database.save(ban);
      this.bannedPlayers.add(playerName.toLowerCase());

      final Player player = this.server.getPlayerExact(playerName);
      if (player != null) {
        player.kickPlayer(reason);
      }

      if (notify) {
        BanSummary summary = new BanSummary(plugin, ban);
        this.notifyPlayers(this.getBanBroadcastMessage(senderName, playerName));
        if (banLength == 0) {
          this.notifyPlayers(summary.getReason());
        } else {
          this.notifyPlayers(summary.getReason());
          this.notifyPlayers(summary.getLength());
        }
      }

      logger.info(this.getBanSummaryMessage(senderName, playerName, reason));

      return true;
    } else {
      return false;
    }
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

  public BanRecord getPlayerBan(final String playerName) {
    return BanRecord.findFirstByName(this.database, playerName);
  }

  public List<BanRecord> getPlayerBans(final String playerName) {
    return BanRecord.findByName(this.database, playerName);
  }

  public String getSimpleFormattedMessage(final String key, final Object argument) {
    final Object[] arguments = { argument };
    return this.plugin.getSimpleFormattedMessage(key, arguments);
  }

  public String getSimpleFormattedMessage(final String key, final Object[] arguments) {
    return this.plugin.getSimpleFormattedMessage(key, arguments);
  }

  public boolean isPlayerBanned(final String playerName) {
    final BanRecord record = BanRecord.findFirstByName(this.database, playerName);
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
      this.database.delete(BanRecord.findFirstByName(this.database, playerName));
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
