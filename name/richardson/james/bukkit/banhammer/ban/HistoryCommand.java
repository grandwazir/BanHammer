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
package name.richardson.james.bukkit.banhammer.ban;

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

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.BanHandler;
import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.util.Time;
import name.richardson.james.bukkit.util.command.CommandArgumentException;
import name.richardson.james.bukkit.util.command.CommandPermissionException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class HistoryCommand extends PlayerCommand {

  public static final String NAME = "history";
  public static final String DESCRIPTION = "View a player's ban history";
  public static final String PERMISSION_DESCRIPTION = "Allow users to view a player's ban history";
  public static final String USAGE = "[name]";

  public static final Permission PERMISSION = new Permission("banhammer.history", HistoryCommand.PERMISSION_DESCRIPTION, PermissionDefault.TRUE);
  public static final Permission PERMISSION_OTHER = new Permission("banhammer.history.others", "Allow users to check the ban history of others.", PermissionDefault.OP);
  
  private final BanHandler handler;
  private final BanHammer plugin;
  
  public HistoryCommand(final BanHammer plugin) {
    super(plugin, HistoryCommand.NAME, HistoryCommand.DESCRIPTION, HistoryCommand.USAGE, HistoryCommand.PERMISSION_DESCRIPTION, HistoryCommand.PERMISSION);
    this.handler = plugin.getHandler(HistoryCommand.class);
    this.plugin = plugin;
    final Permission wildcard = new Permission(HistoryCommand.PERMISSION.getName() + ".*", "Allow a user to check the ban history of everyone.", PermissionDefault.OP);
    this.plugin.addPermission(wildcard, true);
    PERMISSION_OTHER.addParent(wildcard, true);
    this.plugin.addPermission(PERMISSION_OTHER, false);
  }

  @Override
  public void execute(final CommandSender sender, Map<String, Object> arguments) throws CommandPermissionException {
    final String playerName = arguments.get("playerName") != null ? (String) arguments.get("playerName") : sender.getName();
    if (!playerName.equalsIgnoreCase(sender.getName()) && !sender.hasPermission(PardonCommand.PERMISSION.getName() + "." + "others")) {
      throw new CommandPermissionException("You are not allowed to v/bh kiew other player's ban history.", PERMISSION_OTHER);
    } else {
      final List<BanRecord> bans = handler.getPlayerBans(playerName);
      
      if (bans.isEmpty()) {
        sender.sendMessage(String.format(ChatColor.YELLOW + "%s has no bans on record.", playerName));
      } else {
        sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + "%s has %d ban(s) on record:", playerName, bans.size()));
        for (BanRecord ban : bans) {
          sendBanDetail(sender, ban);
        }
      }
    }
  }

  @Override
  public Map<String, Object> parseArguments(List<String> arguments) throws CommandArgumentException {
    Map<String, Object> m = new HashMap<String, Object>();

    try {
      m.put("playerName", arguments.get(0));
    } catch (IndexOutOfBoundsException e) {
      m.put("playerName", null);
    }

    return m;
  }

  protected void sendBanDetail(CommandSender sender, BanRecord ban) {
    Date createdDate = new Date(ban.getCreatedAt());
    DateFormat dateFormat = new SimpleDateFormat("MMM d");
    String createdAt = dateFormat.format(createdDate);
    sender.sendMessage(String.format(ChatColor.YELLOW + "Banned by %s on %s", ban.getCreatedBy(), createdAt));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- Reason: %s.", ban.getReason()));
    switch (ban.getType()) {
      case PERMENANT:
        sender.sendMessage(ChatColor.YELLOW + "- Length: Permanent.");
        break;
      case TEMPORARY:
        Date expiryDate = new Date(ban.getExpiresAt());
        DateFormat expiryDateFormat = new SimpleDateFormat("MMM d H:mm a ");
        String expiryDateString = expiryDateFormat.format(expiryDate) + "(" + Calendar.getInstance().getTimeZone().getDisplayName() + ")";
        Long banTime = ban.getExpiresAt() - ban.getCreatedAt();
        sender.sendMessage(String.format(ChatColor.YELLOW + "- Length: %s", Time.millisToLongDHMS(banTime)));
        sender.sendMessage(String.format(ChatColor.YELLOW + "- Expires on: %s", expiryDateString));
        break;
    }
  }
  
}
