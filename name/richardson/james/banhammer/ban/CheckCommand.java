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
package name.richardson.james.banhammer.ban;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class CheckCommand extends Command {

  private CachedList cache;

  public CheckCommand(final BanHammer plugin) {
    super(plugin);
    this.name = BanHammer.getMessage("check-command-name");
    this.description = BanHammer.getMessage("check-command-description");
    this.usage = BanHammer.getMessage("check-command-usage");
    this.permission = "banhammer." + this.name;
    registerPermission(this.permission, this.description, PermissionDefault.OP);
    this.cache = CachedList.getInstance();
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) {
    String playerName = arguments.get("playerName");
    
    if (this.cache.contains(playerName)) {
      BanRecord ban = BanRecord.findFirst(playerName);
      sender.sendMessage(String.format(ChatColor.RED + BanHammer.getMessage("ban-history-detail"), playerName, ban.getCreatedBy()));
      sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.getMessage("ban-history-reason"), ban.getReason()));
      switch (ban.getType()) {
        case PERMENANT:
          sender.sendMessage(ChatColor.YELLOW + BanHammer.getMessage("ban-history-time-permanent:"));
          break;
        case TEMPORARY:
          Date expiryDate = new Date(ban.getExpiresAt());
          DateFormat dateFormat = new SimpleDateFormat("MMM d H:mm a ");
          String expiryDateString = dateFormat.format(expiryDate) + "(" + Calendar.getInstance().getTimeZone().getDisplayName() + ")";
          sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.getMessage("ban-history-expires-on"), expiryDateString));
          break;
      }
    } else {
      sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.getMessage("player-not-banned"), playerName));
    }

  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) {
    Map<String, String> m = new HashMap<String, String>();
    arguments.remove(0);

    try {
      m.put("playerName", arguments.get(0));
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException();
    }

    return m;
  }

}
