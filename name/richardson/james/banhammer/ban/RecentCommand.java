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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class RecentCommand extends Command {

  public RecentCommand(final BanHammer plugin) {
    super(plugin);
    this.name = BanHammer.getMessage("recent-command-name");
    this.description = BanHammer.getMessage("recent-command-description");
    this.usage = BanHammer.getMessage("recent-command-usage");
    this.permission = "banhammer." + this.name;
    registerPermission(this.permission, this.description, PermissionDefault.OP);
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) {
    int maxRows;

    try {
      maxRows = Integer.parseInt(arguments.get("maxRows"));
    } catch (NumberFormatException e) {
      maxRows = 3;
    }

    List<BanRecord> bans = BanRecord.findRecent(maxRows);
    if (bans.isEmpty())
      sender.sendMessage(ChatColor.YELLOW + BanHammer.getMessage("ban-recent-none"));
    else {
      String banTotal = Integer.toString(bans.size());
      sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + BanHammer.getMessage("ban-recent-summary"), banTotal));
      for (BanRecord ban : bans) {
        sendBanDetail(sender, ban);
      }
    }
  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) {
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
