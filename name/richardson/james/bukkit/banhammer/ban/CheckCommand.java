/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * CheckCommand.java is part of BanHammer.
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
import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.util.Time;
import name.richardson.james.bukkit.util.command.CommandArgumentException;
import name.richardson.james.bukkit.util.command.CommandPermissionException;
import name.richardson.james.bukkit.util.command.CommandUsageException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class CheckCommand extends PlayerCommand {

  public static final String NAME = "check";
  public static final String DESCRIPTION = "Check if a player is banned";
  public static final String PERMISSION_DESCRIPTION = "Allow users to check if a player is banned.";
  public static final String USAGE = "<name>";

  public static final Permission PERMISSION = new Permission("banhammer.check", CheckCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private final BanHandler banHandler;

  public CheckCommand(final BanHammer plugin) {
    super(plugin, BanCommand.NAME, BanCommand.DESCRIPTION, BanCommand.USAGE, BanCommand.PERMISSION_DESCRIPTION, BanCommand.PERMISSION);
    this.banHandler = plugin.getHandler();
  }

  @Override
  public Map<String, Object> parseArguments(List<String> arguments) {
    Map<String, Object> m = new HashMap<String, Object>();

    try {
      m.put("playerName", arguments.get(0));
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException();
    }

    return m;
  }

  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    String playerName = (String) arguments.get("playerName");
    
    if (banHandler.isPlayerBanned(playerName)) {
      BanRecord ban = BanRecord.findFirst(playerName);
      sender.sendMessage(String.format(ChatColor.RED + BanHammer.getMessage("player-banned"), playerName));
      sendBanDetail(sender, ban);
    } else {
      sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.getMessage("player-not-banned"), playerName));
    }
    
  }
  
  protected void sendBanDetail(CommandSender sender, BanRecord ban) {
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
