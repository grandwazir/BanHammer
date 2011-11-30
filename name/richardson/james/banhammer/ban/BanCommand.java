/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * BanCommand.java is part of BanHammer.
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.Command;
import name.richardson.james.banhammer.util.BanHammerTime;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class BanCommand extends Command {

  private final BanHandler banHandler;

  public BanCommand(final BanHammer plugin) {
    super(plugin);
    this.name = BanHammer.getMessage("ban-command-name");
    this.description = BanHammer.getMessage("ban-command-description");
    this.usage = BanHammer.getMessage("ban-command-usage");
    this.permission = "banhammer." + this.name;
    registerPermission(this.permission, this.description, PermissionDefault.OP);
    this.banHandler = plugin.getHandler();
  }

  @Override
  public void execute(final CommandSender sender, final Map<String, String> arguments) {
    long expiryTime = 0;
    final Player player = this.getPlayer(arguments.get("playerName"));
    final String playerName = player != null ? player.getName() : arguments.get("playerName");
    final String senderName = this.getSenderName(sender);
    final String reason = arguments.get("reason");
    
    if (arguments.containsKey("time")) {
      expiryTime = BanHammerTime.parseTime(arguments.get("time"));
    }
    
    if (expiryTime > BanHammerTime.parseTime("1w") && sender.hasPermission("banhammer.ban.temporary")) {
      sender.sendMessage(ChatColor.RED + String.format(BanHammer.getMessage("ban-too-long")));
      return;
    }
    
    if (!this.banHandler.banPlayer(playerName, senderName, reason, expiryTime, true)) {
      sender.sendMessage(ChatColor.RED + String.format(BanHammer.getMessage("player-already-banned"), playerName));
    } else {
      sender.sendMessage(ChatColor.RED + String.format(BanHammer.getMessage("player-banned"), playerName));
    }
    
  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) {
    Map<String, String> m = new HashMap<String, String>();

    try {
      for (String argument : arguments) {
        if (argument.startsWith("t:")) {
          m.put("time", argument);
          arguments.remove(argument);
          break;
        }
      }

      m.put("playerName", arguments.remove(0));
      m.put("reason", this.combineString(arguments, " "));
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException();
    }

    return m;
  }

}
