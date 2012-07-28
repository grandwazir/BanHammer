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
import java.util.Locale;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

import name.richardson.james.bukkit.alias.AliasHandler;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.api.BanSummary;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.utilities.internals.Logger;
import name.richardson.james.bukkit.utilities.localisation.Localisable;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving player events.
 * The class that is interested in processing a player
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addPlayerListener<code> method. When
 * the player event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see PlayerEvent
 */
public class PlayerListener implements Listener, Localisable {

  /**
   * The types of message that can be broadcasted in response to events.
   */
  public enum BroadcastMessageType {

    /** Player has been banned. */
    PLAYER_BANNED,

    /** Player has been pardoned. */
    PLAYER_PARDONED
  }

  /** The Constant NOTIFY_PERMISSION. */
  public static final String NOTIFY_PERMISSION = "banhammer.notify";

  /** The logger for this class. */
  private final Logger logger = new Logger(this.getClass());

  /** The BanHammer API. */
  private final BanHandler handler;

  /** The Alias API. */
  private final AliasHandler aliasHandler;

  /** A cache of currently banned players and their active bans. */
  private final BanRecordCache cache;

  /** The BanHammer plugin. */
  private final BanHammer plugin;

  /**
   * Instantiates a new player listener.
   * 
   * @param plugin the plugin
   */
  public PlayerListener(final BanHammer plugin) {
    this.plugin = plugin;
    this.aliasHandler = plugin.getAliasHandler();
    this.handler = plugin.getHandler(PlayerListener.class);
    this.cache = new BanRecordCache(plugin.getDatabase());
    this.logger.setPrefix(plugin.getLoggerPrefix());
    Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
  }

  /*
   * (non-Javadoc)
   * @see name.richardson.james.bukkit.utilities.plugin.Localisable#
   * getChoiceFormattedMessage(java.lang.String, java.lang.Object[],
   * java.lang.String[], double[])
   */
  public String getChoiceFormattedMessage(String key, final Object[] arguments, final String[] formats, final double[] limits) {
    key = this.getClass().getSimpleName().toLowerCase() + "." + key;
    return this.plugin.getChoiceFormattedMessage(key, arguments, formats, limits);
  }

  /*
   * (non-Javadoc)
   * @see name.richardson.james.bukkit.utilities.plugin.Localisable#getLocale()
   */
  public Locale getLocale() {
    return this.plugin.getLocale();
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.utilities.plugin.Localisable#getMessage(java
   * .lang.String)
   */
  public String getMessage(String key) {
    key = this.getClass().getSimpleName().toLowerCase() + "." + key;
    return this.plugin.getMessage(key);
  }

  /*
   * (non-Javadoc)
   * @see name.richardson.james.bukkit.utilities.plugin.Localisable#
   * getSimpleFormattedMessage(java.lang.String, java.lang.Object)
   */
  public String getSimpleFormattedMessage(final String key, final Object argument) {
    final Object[] arguments = { argument };
    return this.getSimpleFormattedMessage(key, arguments);
  }

  /*
   * (non-Javadoc)
   * @see name.richardson.james.bukkit.utilities.plugin.Localisable#
   * getSimpleFormattedMessage(java.lang.String, java.lang.Object[])
   */
  public String getSimpleFormattedMessage(String key, final Object[] arguments) {
    key = this.getClass().getSimpleName().toLowerCase() + "." + key;
    return this.plugin.getSimpleFormattedMessage(key, arguments);
  }

  /**
   * When a player is banned, update the cache and inform players.
   * 
   * @param event the event
   */
  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
    logger.debug("BanHammerPlayerBannedEvent received");
    final Player player = Bukkit.getServer().getPlayer(event.getPlayerName());
    final BanRecord record = event.getRecord();
    this.cache.set(event.getPlayerName(), record);
    if (player != null) {
      final String message = this.getSimpleFormattedMessage("banned", record.getReason());
      player.kickPlayer(message);
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
  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlayerKicked(final PlayerKickEvent event) {
    logger.debug("PlayerKickEvent received");
    final Object[] arguments = { event.getPlayer(), event.getReason() };
    event.setLeaveMessage(this.getSimpleFormattedMessage("kick-broadcast", arguments));
  }

  /**
   * When a player logins, check to see if they are banned.
   * 
   * @param event the event
   */
  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerLogin(final PlayerPreLoginEvent event) {
    if (this.isPlayerBanned(event.getName(), event.getAddress())) {
      final BanRecord ban = this.cache.get(event.getName());
      String message = null;
      switch (ban.getType()) {
      case TEMPORARY:
        message = this.getSimpleFormattedMessage("temporarily-banned", BanHammer.DATE_FORMAT.format(ban.getExpiresAt()));
        break;
      default:
        message = this.getSimpleFormattedMessage("permenantly-banned", ban.getReason());
      }
      this.logger.debug(String.format("Blocked %s from connecting due to an active ban.", event.getName()));
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
    logger.debug("BanHammerPlayerPardonedEvent received");
    this.cache.remove(event.getPlayerName());
    if (!event.isSilent()) {
      this.broadcast(event.getRecord(), BroadcastMessageType.PLAYER_PARDONED);
    }
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
      final BanSummary summary = new BanSummary(this.plugin, ban);
      server.broadcast(this.getSimpleFormattedMessage("ban-broadcast", arguments), NOTIFY_PERMISSION);
      server.broadcast(summary.getReason(), NOTIFY_PERMISSION);
      server.broadcast(summary.getLength(), NOTIFY_PERMISSION);
      break;
    case PLAYER_PARDONED:
      server.broadcast(this.getSimpleFormattedMessage("pardon-broadcast", arguments), NOTIFY_PERMISSION);
      break;
    }
  }

  /**
   * Checks if is player banned.
   * 
   * @param player the player
   * @return true, if is player banned
   */
  private boolean isPlayerBanned(final String playerName, InetAddress address) {
    this.logger.debug("Checking if " + playerName + " is banned.");
    if (this.cache.contains(playerName)) {
      return true;
    }
    // check if the player has an active alias
    if (this.aliasHandler != null) {
      this.logger.debug("Checking for alias of " + playerName + ".");
      final Set<String> aliases = this.aliasHandler.getPlayersNames(address);
      for (final String alias : aliases) {
        // if the player has a banned alias, ban this player.
        if (this.cache.contains(alias.toLowerCase())) {
          final BanRecord ban = this.cache.get(alias.toLowerCase());
          final String reason = this.getSimpleFormattedMessage("alias-ban-reason", ban.getPlayer().getName());
          this.handler.banPlayer(playerName, ban, reason, true);
          return true;
        }
      }
    }
    this.logger.debug(playerName + " is not banned.");
    return false;
  }

}
