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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
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
import name.richardson.james.bukkit.banhammer.migration.OldBanRecord;
import name.richardson.james.bukkit.utilities.internals.Logger;
import name.richardson.james.bukkit.utilities.plugin.Localisable;

public class BannedPlayerListener implements Listener, Localisable {

  private final BanHandler handler;
  private final Map<String, CachedBan> bannedPlayers;
  private final AliasHandler aliasHandler;
  private BanHammer plugin;

  private static final Logger logger = new Logger(BannedPlayerListener.class);

  public BannedPlayerListener(final BanHammer plugin) {
    this.handler = plugin.getHandler(BannedPlayerListener.class);
    this.aliasHandler = plugin.getAliasHandler();
    this.bannedPlayers = plugin.getModifiableBannedPlayers();
    this.plugin = plugin;
    logger.setPrefix("[BanHammer] ");
    Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public String getChoiceFormattedMessage(String key, final Object[] arguments, final String[] formats, final double[] limits) {
    key = this.getClass().getSimpleName().toLowerCase() + "." + key;
    return this.plugin.getChoiceFormattedMessage(key, arguments, formats, limits);
  }

  public Locale getLocale() {
    return plugin.getLocale();
  }


  public String getMessage(String key) {
    key = this.getClass().getSimpleName().toLowerCase() + "." + key;
    return this.plugin.getMessage(key);
  }

  public String getSimpleFormattedMessage(String key, final Object argument) {
    final Object[] arguments = { argument };
    return this.getSimpleFormattedMessage(key, arguments);
  }

  public String getSimpleFormattedMessage(String key, final Object[] arguments) {
    key = this.getClass().getSimpleName().toLowerCase() + "." + key;
    return this.plugin.getSimpleFormattedMessage(key, arguments);
  }

  
  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerJoin(final PlayerJoinEvent event) {
    if (this.aliasHandler == null) return;
    final String playerName = event.getPlayer().getName();
    final InetAddress address = event.getPlayer().getAddress().getAddress();
    logger.debug("Checking alias of " + playerName + ".");
    final Set<String> aliases = this.aliasHandler.getPlayersNames(address);
    for (final String alias : aliases) {
      if (this.bannedPlayers.containsKey(alias.toLowerCase())) {
        CachedBan ban = this.bannedPlayers.get(alias.toLowerCase());
        if (ban.isActive()) {
          String reason = getAliasBanReason(ban);
          this.handler.banPlayer(playerName, BanRecord.findById(null, ban.getId()), reason);
          break;
        }
      }
    }
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
    CachedBan ban = new CachedBan(event.getRecord());
    Player player = Bukkit.getServer().getPlayer(event.getPlayerName());
    this.bannedPlayers.put(event.getPlayerName(), ban);
    if (player != null) player.kickPlayer(event.getReason());
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
    this.bannedPlayers.remove(event.getPlayerName());
  }
  
  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerLogin(final PlayerLoginEvent event) {
    final String playerName = event.getPlayer().getName().toLowerCase();
    logger.debug("Checking if " + playerName + " is banned.");
    if (!this.bannedPlayers.containsKey(playerName)) return;
    final CachedBan ban = this.bannedPlayers.get(playerName);
    if (ban.isActive()) {
      String message = null;
      switch (ban.getType()) {
      case TEMPORARY:
        final Date expiryDate = new Date(ban.getExpiresAt());
        final DateFormat dateFormat = new SimpleDateFormat("MMM d H:mm a ");
        final String expiryDateString = dateFormat.format(expiryDate) + "(" + Calendar.getInstance().getTimeZone().getDisplayName() + ")";
        message = this.getSimpleFormattedMessage("temporarily-banned", expiryDateString);
      default:
        message = this.getSimpleFormattedMessage("permenantly-banned", ban.getReason());
      }
      BannedPlayerListener.logger.debug(String.format("Blocked %s from connecting due to an active ban.", playerName));
      event.disallow(PlayerLoginEvent.Result.KICK_BANNED, message);
    } else {
      this.bannedPlayers.remove(playerName);
    }
  }
  
  private String getAliasBanReason(CachedBan ban) {
    // TODO Auto-generated method stub
    return null;
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

}
