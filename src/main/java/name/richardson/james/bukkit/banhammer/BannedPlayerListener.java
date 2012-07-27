/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * BannedPlayerListener.java is part of BanHammer.
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import name.richardson.james.bukkit.alias.AliasHandler;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.ban.BanSummary;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.utilities.internals.Logger;
import name.richardson.james.bukkit.utilities.plugin.Localisable;

public class BannedPlayerListener implements Listener, Localisable {

  public enum BroadcastMessageType {
    PLAYER_BANNED,
    PLAYER_PARDONED
  }

  public static final String NOTIFY_PERMISSION = "banhammer.notify";

  /** The logger for this class */
  private static final Logger logger = new Logger(BannedPlayerListener.class);

  /** The BanHammer API */
  private final BanHandler handler;

  /** The Alias API */
  private final AliasHandler aliasHandler;

  /** A cache of currently banned players and their active bans */
  private final BannedPlayersCache cache;

  /** The BanHammer plugin */
  private final BanHammer plugin;

  public BannedPlayerListener(final BanHammer plugin) {
    this.aliasHandler = plugin.getAliasHandler();
    this.handler = plugin.getHandler(BannedPlayerListener.class);
    this.cache = new BannedPlayersCache(this.plugin.getSQLStorage());
    this.plugin = plugin;
    logger.setPrefix("[BanHammer] ");
    Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public String getChoiceFormattedMessage(String key, final Object[] arguments, final String[] formats, final double[] limits) {
    key = this.getClass().getSimpleName().toLowerCase() + "." + key;
    return this.plugin.getChoiceFormattedMessage(key, arguments, formats, limits);
  }

  public Locale getLocale() {
    return this.plugin.getLocale();
  }

  public String getMessage(String key) {
    key = this.getClass().getSimpleName().toLowerCase() + "." + key;
    return this.plugin.getMessage(key);
  }

  public String getSimpleFormattedMessage(final String key, final Object argument) {
    final Object[] arguments = { argument };
    return this.getSimpleFormattedMessage(key, arguments);
  }

  public String getSimpleFormattedMessage(String key, final Object[] arguments) {
    key = this.getClass().getSimpleName().toLowerCase() + "." + key;
    return this.plugin.getSimpleFormattedMessage(key, arguments);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
    final Player player = Bukkit.getServer().getPlayer(event.getPlayerName());
    final BanRecord record = event.getRecord();
    this.cache.set(event.getPlayerName(), record);
    if (player != null) {
      player.kickPlayer(record.getReason());
    }
    if (!event.isSilent()) {
      this.broadcast(record, BroadcastMessageType.PLAYER_BANNED);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerJoin(final PlayerJoinEvent event) {
    if (this.aliasHandler == null) {
      return;
    }
    final String playerName = event.getPlayer().getName();
    final InetAddress address = event.getPlayer().getAddress().getAddress();
    final Set<String> aliases = this.aliasHandler.getPlayersNames(address);
    logger.debug("Checking alias of " + playerName + ".");
    for (final String alias : aliases) {
      if (this.cache.contains(alias.toLowerCase())) {
        final BanRecord ban = this.cache.get(alias.toLowerCase());
        final String reason = this.getSimpleFormattedMessage("alias-ban-reason", ban.getPlayer().getName());
        this.handler.banPlayer(playerName, ban, reason);
        break;
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerLogin(final PlayerLoginEvent event) {
    final String playerName = event.getPlayer().getName().toLowerCase();
    logger.debug("Checking if " + playerName + " is banned.");
    if (!this.cache.contains(playerName)) {
      return;
    }
    final BanRecord ban = this.cache.get(playerName);
    String message = null;
    switch (ban.getType()) {
    case TEMPORARY:
      message = this.getSimpleFormattedMessage("temporarily-banned", BanHammer.DATE_FORMAT.format(ban.getExpiresAt()));
    default:
      message = this.getSimpleFormattedMessage("permenantly-banned", ban.getReason());
    }
    BannedPlayerListener.logger.debug(String.format("Blocked %s from connecting due to an active ban.", playerName));
    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, message);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
    this.cache.remove(event.getPlayerName());
    if (!event.isSilent()) {
      this.broadcast(event.getRecord(), BroadcastMessageType.PLAYER_PARDONED);
    }
  }

  private void broadcast(final BanRecord ban, final BroadcastMessageType type) {
    final Server server = Bukkit.getServer();
    final Object[] arguments = { ban.getPlayer().getName(), ban.getCreater().getName() };
    switch (type) {
    case PLAYER_BANNED:
      final BanSummary summary = new BanSummary(this.plugin, ban);
      server.broadcast(this.getSimpleFormattedMessage("ban-broadcast-message", arguments), NOTIFY_PERMISSION);
      server.broadcast(summary.getReason(), NOTIFY_PERMISSION);
      server.broadcast(summary.getLength(), NOTIFY_PERMISSION);
    case PLAYER_PARDONED:
      server.broadcast(this.getSimpleFormattedMessage("pardon-broadcast-message", arguments), NOTIFY_PERMISSION);
    }
  }

}
