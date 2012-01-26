/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * HistoryCommand.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with BanHammer.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.banhammer.ban;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.bukkit.util.Time;
import name.richardson.james.bukkit.util.command.CommandArgumentException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class HistoryCommand extends PlayerCommand {

  public static final String NAME = "history";
  public static final String DESCRIPTION = "View a player's ban history";
  public static final String PERMISSION_DESCRIPTION = "Allow users to view a player's ban history";
  public static final String USAGE = "<name>";

  public static final Permission PERMISSION = new Permission("banhammer.history", HistoryCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private final BanHandler banHandler;
  
  public HistoryCommand(final BanHammer plugin) {
    super(plugin, HistoryCommand.NAME, HistoryCommand.DESCRIPTION, HistoryCommand.USAGE, HistoryCommand.PERMISSION_DESCRIPTION, HistoryCommand.PERMISSION);
    this.banHandler = plugin.getHandler();
  }

  @Override
  public void execute(final CommandSender sender, Map<String, Object> arguments) {
    final String playerName = (String) arguments.get("playerName");
    final List<CachedBan> bans = banHandler.getPlayerBans(playerName);
    
    if (bans.isEmpty()) {
      sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.getMessage("ban-history-none"), playerName));
    } else {
      sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + BanHammer.getMessage("ban-history-summary"), playerName, bans.size()));
      for (CachedBan ban : bans) {
        sendBanDetail(sender, ban);
      }
    }
  }

  @Override
  public Map<String, Object> parseArguments(List<String> arguments) throws CommandArgumentException {
    Map<String, Object> m = new HashMap<String, Object>();

    try {
      m.put("playerName", arguments.get(0));
    } catch (IndexOutOfBoundsException e) {
      throw new CommandArgumentException("You must provide a player name to check", "You must type the name exactly");
    }

    return m;
  }

  protected void sendBanDetail(CommandSender sender, CachedBan ban) {
    Date createdDate = new Date(ban.getCreatedAt());
    DateFormat dateFormat = new SimpleDateFormat("MMM d");
    String createdAt = dateFormat.format(createdDate);
    sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.getMessage("ban-history-detail"), ban.getCreatedBy(), createdAt));
    sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.getMessage("ban-history-reason"), ban.getReason()));
    switch (ban.getType()) {
      case PERMENANT:
        sender.sendMessage(ChatColor.YELLOW + BanHammer.getMessage("ban-history-time-permanent"));
        break;
      case TEMPORARY:
        Date expiryDate = new Date(ban.getExpiresAt());
        DateFormat expiryDateFormat = new SimpleDateFormat("MMM d H:mm a ");
        String expiryDateString = expiryDateFormat.format(expiryDate) + "(" + Calendar.getInstance().getTimeZone().getDisplayName() + ")";
        Long banTime = ban.getExpiresAt() - ban.getCreatedAt();
        sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.getMessage("ban-history-time-temporary"), Time.millisToLongDHMS(banTime)));
        sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.getMessage("ban-history-expires-on"), expiryDateString));
        break;
    }
  }
  
}
