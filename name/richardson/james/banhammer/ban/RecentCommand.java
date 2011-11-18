/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * RecentCommand.java is part of BanHammer.
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.Command;
import name.richardson.james.banhammer.exceptions.NotEnoughArgumentsException;
import name.richardson.james.banhammer.util.BanHammerTime;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RecentCommand extends Command {

  public RecentCommand(final BanHammer plugin) {
    super(plugin);
    this.name = "recent";
    this.description = "shows the most recent bans made on the server";
    this.usage = "/bh recent [maxBans]";
    this.permission = "banhammer." + this.name;
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) throws NotEnoughArgumentsException {
    int maxRows;

    try {
      maxRows = Integer.parseInt(arguments.get("maxRows"));
    } catch (NumberFormatException e) {
      maxRows = 3;
    }

    List<BanRecord> bans = BanRecord.findRecent(maxRows);
    if (bans.isEmpty())
      sender.sendMessage(ChatColor.YELLOW + BanHammer.getMessage("noRecentBans"));
    else {
      String banTotal = Integer.toString(bans.size());
      sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + BanHammer.getMessage("recentBanCount"), banTotal));
      for (BanRecord ban : bans) {
        Date createdDate = new Date(ban.getCreatedAt());
        DateFormat dateFormat = new SimpleDateFormat("MMM d");
        String createdAt = dateFormat.format(createdDate);
        sender
            .sendMessage(String.format(ChatColor.YELLOW + BanHammer.getMessage("banSummaryWithName"), ban.getPlayer(), ban.getCreatedBy(), createdAt));
        sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.getMessage("banReason"), ban.getReason()));
        if (ban.getType().equals(BanRecord.Type.PERMENANT))
          sender.sendMessage(ChatColor.YELLOW + BanHammer.getMessage("banTimePermenant"));
        else if (ban.getType().equals(BanRecord.Type.TEMPORARY)) {
          Long banTime = ban.getExpiresAt() - ban.getCreatedAt();
          sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.getMessage("banTimeTemporary"), BanHammerTime.millisToLongDHMS(banTime)));
        }
      }
    }
  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) throws NotEnoughArgumentsException {
    Map<String, String> m = new HashMap<String, String>();
    arguments.remove(0);

    try {
      m.put("maxRows", arguments.get(0));
    } catch (IndexOutOfBoundsException e) {
      m.put("maxRows", "3");
    }

    return m;
  }

}
