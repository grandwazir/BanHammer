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
    this.database = plugin.getDatabaseHandler();
    this.bannedPlayers = plugin.getModifiableBannedPlayers();
    this.server = plugin.getServer();
  }

  @Override
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
        if (banLength == 0) {
          this.notifyPlayers(ChatColor.RED + playerName + " has been permanently banned.");
          this.notifyPlayers(ChatColor.YELLOW + "- Reason: " + reason);
        } else {
          this.notifyPlayers(ChatColor.RED + playerName + " has been banned.");
          this.notifyPlayers(ChatColor.YELLOW + "- Reason: " + reason + ".");
          this.notifyPlayers(ChatColor.YELLOW + "- Length: " + Time.millisToLongDHMS(banLength) + ".");
        }
      }

      Handler.logger.info(String.format("%s has been banned by %s.", playerName, senderName));

      return true;
    } else
      return false;
  }

  @Override
  public BanRecord getPlayerBan(final String playerName) {
    return BanRecord.findFirstByName(this.database, playerName);
  }

  @Override
  public List<BanRecord> getPlayerBans(final String playerName) {
    return BanRecord.findByName(this.database, playerName);
  }

  @Override
  public boolean isPlayerBanned(final String playerName) {
    final BanRecord record = BanRecord.findFirstByName(this.database, playerName);
    if (record != null) {
      if (record.isActive())
        return true;
      else
        return false;
    } else
      return false;
  }

  @Override
  public boolean pardonPlayer(final String playerName, final String senderName, final Boolean notify) {
    if (this.isPlayerBanned(playerName)) {
      this.database.delete(BanRecord.findFirstByName(this.database, playerName));
      this.bannedPlayers.remove(playerName.toLowerCase());
      if (notify) {
        this.notifyPlayers(ChatColor.GREEN + playerName + " has been pardoned by " + senderName + ".");
      }
      Handler.logger.info(String.format("%s has been pardoned by %s.", playerName, senderName));
      return true;
    } else
      return false;
  }

  @Override
  public boolean removePlayerBan(final BanRecord ban) {
    this.database.delete(ban);
    Handler.logger.debug(String.format("Removed a ban belonging to %s.", ban.getPlayer()));
    return true;
  }

  @Override
  public int removePlayerBans(final List<BanRecord> bans) {
    final int i = this.database.delete(bans);
    Handler.logger.debug(String.format("Removed %d ban(s).", i));
    return i;
  }

  private void notifyPlayers(final String message) {
    this.server.broadcast(message, "banhammer.notify");
  }

}
