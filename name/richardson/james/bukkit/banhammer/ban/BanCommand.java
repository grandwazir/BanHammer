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
package name.richardson.james.bukkit.banhammer.ban;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.BanHandler;
import name.richardson.james.bukkit.util.Time;
import name.richardson.james.bukkit.util.command.CommandArgumentException;
import name.richardson.james.bukkit.util.command.CommandPermissionException;
import name.richardson.james.bukkit.util.command.CommandUsageException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class BanCommand extends PlayerCommand {

  public static final String NAME = "ban";
  public static final String DESCRIPTION = "Ban a player";
  public static final String PERMISSION_DESCRIPTION = "Allow users to ban players.";
  public static final String USAGE = "<name> [t:time] [reason]";

  public static final Permission PERMISSION = new Permission("banhammer.ban", BanCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private final BanHammer plugin;
  private final BanHandler banHandler;

  public BanCommand(final BanHammer plugin) {
    super(plugin, BanCommand.NAME, BanCommand.DESCRIPTION, BanCommand.USAGE, BanCommand.PERMISSION_DESCRIPTION, BanCommand.PERMISSION);
    this.plugin = plugin;
    this.banHandler = plugin.getHandler(BanCommand.class);
  }
  
  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final long expiryTime = Time.parseTime((String) arguments.get("time"));
    final String unmatchedPlayerName = (String) (arguments.get("playerName"));
    final Player player = this.plugin.getServer().getPlayer(unmatchedPlayerName);
    final String playerName = player != null ? player.getName() : unmatchedPlayerName;
    final String senderName = sender.getName();
    final String reason = (String) arguments.get("reason");
    
    if (expiryTime == 0 && !sender.hasPermission("banhammer.ban.permanent")) {
      throw new CommandPermissionException("You do not have permission to ban permanently", BanCommand.PERMISSION);
    } else {
      if (!this.banHandler.banPlayer(playerName, senderName, reason, expiryTime, true)) {
        sender.sendMessage(ChatColor.RED + String.format(BanHammer.getMessage("player-already-banned"), playerName));
      } else {
        sender.sendMessage(ChatColor.RED + String.format(BanHammer.getMessage("player-banned"), playerName));
      }
    }
    
    if (!this.banHandler.banPlayer(playerName, senderName, reason, expiryTime, true)) {
      sender.sendMessage(ChatColor.RED + String.format(BanHammer.getMessage("player-already-banned"), playerName));
    } else {
      sender.sendMessage(ChatColor.RED + String.format(BanHammer.getMessage("player-banned"), playerName));
    }
    
  }

  @Override
  public Map<String, Object> parseArguments(List<String> arguments) {
    Map<String, Object> m = new HashMap<String, Object>();

    m.put("reason", "No reason provided");
    m.put("time", "0");
    
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
  
  protected String combineString(List<String> arguments, String seperator) {
    StringBuilder reason = new StringBuilder();
    try {
      for (String argument : arguments) {
        reason.append(argument);
        reason.append(seperator);
      }
      reason.deleteCharAt(reason.length() - seperator.length());
      return reason.toString();
    } catch (StringIndexOutOfBoundsException e) {
      return BanHammer.getMessage("default-reason");
    }
  }



}
