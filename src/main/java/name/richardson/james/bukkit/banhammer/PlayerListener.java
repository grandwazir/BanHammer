/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * PlayerListener.java is part of BanHammer.
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

import java.net.InetAddress;
import java.util.List;

import com.avaje.ebean.EbeanServer;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.permissions.Permission;

import name.richardson.james.bukkit.alias.AliasHandler;
import name.richardson.james.bukkit.alias.persistence.PlayerNameRecord;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.ban.BanSummary;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.listener.LoggableListener;
import name.richardson.james.bukkit.utilities.localisation.Localisation;

public class PlayerListener extends LoggableListener {

  /**
   * The types of message that can be broadcasted in response to events.
   */
  public enum BroadcastMessageType {

    /** Player has been banned. */
    PLAYER_BANNED,

    /** Player has been pardoned. */
    PLAYER_PARDONED
  }

  /** The BanHammer API. */
  private final BanHandler handler;

  /** The Alias API. */
  private final AliasHandler aliasHandler;

  private Localisation localisation;

  private final Permission permission;

  private EbeanServer database;

  /**
   * Instantiates a new player listener.
   * 
   * @param plugin the plugin
   */
  public PlayerListener(final BanHammer plugin, Permission notify) {
    super(plugin);
    this.aliasHandler = plugin.getAliasHandler();
    this.handler = plugin.getHandler();
    this.permission = notify;
    this.database = plugin.getDatabase();
    this.localisation = plugin.getLocalisation();
  }

  /**
   * When a player is banned, update the cache and inform players.
   * 
   * @param event the event
   */
  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
    final Player player = Bukkit.getServer().getPlayer(event.getPlayerName());
    final BanRecord record = event.getRecord();
    if (player != null) {
      player.kickPlayer(this.localisation.getMessage(this, "ban-kick-message", record.getReason()));
    }
    if (!event.isSilent()) {
      this.broadcast(record, BroadcastMessageType.PLAYER_BANNED);
    }
  }

  /**
   * When a player is kicked, set the leave message.
   * 
   * @param event the event
   */
  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerKicked(final PlayerKickEvent event) {
    event.setReason(this.localisation.getMessage(this, "kick-message", event.getReason()));
    event.setLeaveMessage(this.localisation.getMessage(this, "kick-broadcast", event.getPlayer(), event.getReason()));
  }

  /**
   * When a player logins, check to see if they are banned.
   * 
   * @param event the event
   */
  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlayerLogin(final AsyncPlayerPreLoginEvent event) {
    PlayerRecord record = this.isPlayerBanned(event.getName(), event.getAddress());
    if (record != null) {
      String message = null;
      switch (record.getActiveBan().getType()) {
      case TEMPORARY:
        message = this.localisation.getMessage(this, "temporarily-banned", BanHammer.LONG_DATE_FORMAT.format(record.getActiveBan().getExpiresAt()));
        break;
      default:
        message = this.localisation.getMessage(this, "permenantly-banned", record.getActiveBan().getReason());
      }
      event.disallow(PlayerPreLoginEvent.Result.KICK_BANNED, message);
    }
  }

  /**
   * When a player is pardoned, remove them from the cache and notify players.
   * 
   * @param event the event
   */
  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
    if (!event.isSilent()) this.broadcast(event.getRecord(), BroadcastMessageType.PLAYER_PARDONED);
  }

  /**
   * Broadcast notification message to the server.
   * 
   * @param ban the ban
   * @param type the type
   */
  private void broadcast(final BanRecord ban, final BroadcastMessageType type) {
    final Server server = Bukkit.getServer();
    final Object[] arguments = { ban.getPlayer().getName(), ban.getCreator().getName() };
    switch (type) {
    case PLAYER_BANNED:
      final BanSummary summary = new BanSummary(this.localisation, ban);
      server.broadcast(this.localisation.getMessage(this, "ban-broadcast", arguments), this.permission.getName());
      server.broadcast(summary.getReason(), this.permission.getName());
      server.broadcast(summary.getLength(), this.permission.getName());
      break;
    case PLAYER_PARDONED:
      server.broadcast(this.localisation.getMessage(this, "ban-broadcast", arguments), this.permission.getName());
      break;
    }
  }

  /**
   * Checks if is player banned.
   * 
   * @param player the player
   * @return true, if is player banned
   */
  private PlayerRecord isPlayerBanned(final String playerName, InetAddress address) {
    this.getLogger().debug(this, "checking-for-bans", playerName);
    PlayerRecord record = PlayerRecord.find(database, playerName);
    if (record.isBanned()) {
      return record;
    } else if (this.aliasHandler != null) {
      this.getLogger().debug(this, "checking-for-alias", playerName);
      final List<PlayerNameRecord> aliases = this.aliasHandler.getPlayersNames(address);
      for (final PlayerNameRecord alias : aliases) {
        record = PlayerRecord.find(database, playerName);
        if (record.isBanned()) {
          final String reason = this.localisation.getMessage(this, "alias-ban-reason", record.getName());
          this.handler.banPlayer(playerName, record.getActiveBan(), reason, true);
          return record;
        }
      }
    }
    this.getLogger().debug(this, "player-not-banned", playerName);
    return null;
  }

}
