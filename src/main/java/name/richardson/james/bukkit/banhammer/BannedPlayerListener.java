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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import name.richardson.james.bukkit.alias.AliasHandler;
import name.richardson.james.bukkit.alias.PlayerNameRecord;
import name.richardson.james.bukkit.util.Logger;

public class BannedPlayerListener implements Listener {

  private final BanHandler handler;
  private final Set<String> bannedPlayers;
  private final AliasHandler aliasHandler;

  private static final Logger logger = new Logger(BannedPlayerListener.class);

  public BannedPlayerListener(final BanHammer plugin) {
    this.handler = plugin.getHandler(BannedPlayerListener.class);
    this.aliasHandler = plugin.getAliasHandler();
    this.bannedPlayers = plugin.getModifiableBannedPlayers();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerLogin(final PlayerLoginEvent event) {
    final String playerName = event.getPlayer().getName();

    if (this.bannedPlayers.contains(playerName.toLowerCase())) {
      String message;
      final BanRecord ban = this.handler.getPlayerBan(playerName);
      if (ban.isActive()) {
        if (ban.getType().equals(BanRecord.Type.PERMENANT)) {
          message = String.format("You have been permanently banned. Reason: %s.", ban.getReason());
        } else {
          final Date expiryDate = new Date(ban.getExpiresAt());
          final DateFormat dateFormat = new SimpleDateFormat("MMM d H:mm a ");
          final String expiryDateString = dateFormat.format(expiryDate) + "(" + Calendar.getInstance().getTimeZone().getDisplayName() + ")";
          message = String.format("You have been banned until %s.", expiryDateString);
        }
        BannedPlayerListener.logger.debug(String.format("Blocked %s from connecting due to an active ban.", playerName));
        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, message);
      } else {
        this.bannedPlayers.remove(playerName.toLowerCase());
      } 
    } else if (this.aliasHandler != null) {
      Set<String> aliases = this.aliasHandler.getPlayersNames(event.getPlayer().getAddress().getAddress());
      for (String alias : aliases) {
        if (this.bannedPlayers.contains(alias)) {
          // get details of previous ban
          BanRecord ban = this.handler.getPlayerBan(alias);
          Long time = System.currentTimeMillis() - ban.getExpiresAt();
          String message = String.format("Alias of %s.", alias);
          if (ban.getExpiresAt() == 0) time = (long) 0;
          event.disallow(PlayerLoginEvent.Result.KICK_BANNED, message);
          this.handler.banPlayer(playerName, "CONSOLE", message, time, true);
          break;
        }
      }
    }
  }
}
