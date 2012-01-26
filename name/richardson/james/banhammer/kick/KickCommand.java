/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * KickCommand.java is part of BanHammer.
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
package name.richardson.james.banhammer.kick;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class KickCommand extends PlayerCommand {

  public static final String NAME = "kick";
  public static final String DESCRIPTION = "Kick a player";
  public static final String PERMISSION_DESCRIPTION = "Allow users to kick players";
  public static final String USAGE = "<name>";

  public static final Permission PERMISSION = new Permission("banhammer.kick", KickCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private final Server server;
  
  public KickCommand(BanHammer plugin) {
    super(plugin, KickCommand.NAME, KickCommand.DESCRIPTION, KickCommand.USAGE, KickCommand.PERMISSION_DESCRIPTION, KickCommand.PERMISSION);
    this.server = plugin.getServer();
  }

  @Override
  public void execute(final CommandSender sender, Map<String, Object> arguments) {
    final Player player = this.server.getPlayer((String) arguments.get("playerName"));
    final String playerName = (String) arguments.get("playerName");
    final String senderName = sender.getName();
    
    if (player != null) {
      player.kickPlayer(String.format(BanHammer.getMessage("player-kicked-notification"), arguments.get("reason")));
      logger.info(String.format(BanHammer.getMessage("player-kicked"), senderName, playerName));
      this.server.broadcast(String.format(ChatColor.RED + BanHammer.getMessage("broadcast-player-kicked"), playerName), "banhammer.notify");
      this.server.broadcast(String.format(ChatColor.YELLOW + BanHammer.getMessage("broadcast-player-banned-reason"), arguments.get("reason")), "banhammer.notify");
    } else {
      logger.info(String.format(BanHammer.getMessage("no-player-found"), playerName));
    }
  }

  @Override
  public Map<String, Object> parseArguments(List<String> arguments) {
    Map<String, Object> m = new HashMap<String, Object>();
    try {
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
