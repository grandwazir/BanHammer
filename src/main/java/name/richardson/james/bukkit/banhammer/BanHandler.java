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
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import name.richardson.james.bukkit.util.Handler;
import name.richardson.james.bukkit.util.Time;

public class BanHandler extends Handler implements BanHammerAPI {

  private final Set<String> bannedPlayers;
  private final DatabaseHandler database;
  private final Server server;

  public BanHandler(final Class<?> parentClass, final BanHammer plugin) {
    super(parentClass);
    database = plugin.getDatabaseHandler();
    bannedPlayers = plugin.getModifiableBannedPlayers();
    server = plugin.getServer();
  }

  public boolean banPlayer(final String playerName, final String senderName, final String reason, final Long banLength, final boolean notify) {
    if (!isPlayerBanned(playerName)) {
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

      database.save(ban);
      bannedPlayers.add(playerName.toLowerCase());
      final Player player = server.getPlayerExact(playerName);
      if (player != null) {
        player.kickPlayer(reason);
      }

      if (notify) {
        if (banLength == 0) {
          notifyPlayers(ChatColor.RED + playerName + " has been permanently banned.");
          notifyPlayers(ChatColor.YELLOW + "- Reason: " + reason);
        } else {
          notifyPlayers(ChatColor.RED + playerName + " has been banned.");
          notifyPlayers(ChatColor.YELLOW + "- Reason: " + reason + ".");
          notifyPlayers(ChatColor.YELLOW + "- Length: " + Time.millisToLongDHMS(banLength) + ".");
        }
      }

      Handler.logger.info(String.format("%s has been banned by %s.", playerName, senderName));

      return true;
    } else {
      return false;
    }
  }

  public BanRecord getPlayerBan(final String playerName) {
    return BanRecord.findFirstByName(database, playerName);
  }

  public List<BanRecord> getPlayerBans(final String playerName) {
    return BanRecord.findByName(database, playerName);
  }

  public boolean isPlayerBanned(final String playerName) {
    final BanRecord record = BanRecord.findFirstByName(database, playerName);
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

  private void notifyPlayers(final String message) {
    server.broadcast(message, "banhammer.notify");
  }

  public boolean pardonPlayer(final String playerName, final String senderName, final Boolean notify) {
    if (isPlayerBanned(playerName)) {
      database.delete(BanRecord.findFirstByName(database, playerName));
      bannedPlayers.remove(playerName.toLowerCase());
      if (notify) {
        notifyPlayers(ChatColor.GREEN + playerName + " has been pardoned by " + senderName + ".");
      }
      Handler.logger.info(String.format("%s has been pardoned by %s.", playerName, senderName));
      return true;
    } else {
      return false;
    }
  }

  public boolean removePlayerBan(final BanRecord ban) {
    database.delete(ban);
    Handler.logger.debug(String.format("Removed a ban belonging to %s.", ban.getPlayer()));
    return true;
  }

  public int removePlayerBans(final List<BanRecord> bans) {
    final int i = database.delete(bans);
    Handler.logger.debug(String.format("Removed %d ban(s).", i));
    return i;
  }

}
