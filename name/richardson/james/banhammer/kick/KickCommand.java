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

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.Command;
import name.richardson.james.banhammer.util.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class KickCommand extends Command {

  public KickCommand(BanHammer plugin) {
    super(plugin);
    this.name = BanHammer.getMessage("kick-command-name");
    this.description = BanHammer.getMessage("kick-command-description");
    this.usage = BanHammer.getMessage("kick-command-usage");
    this.permission = "banhammer." + this.name;
    registerPermission(this.permission, this.description, PermissionDefault.OP);
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) {
    final Player player = this.getPlayer(arguments.get("playerName"));
    final String playerName = arguments.get("playerName");
    final String senderName = this.getSenderName(sender);
    
    if (player != null) {
      player.kickPlayer(String.format(BanHammer.getMessage("player-kicked-notification"), arguments.get("reason")));
      Logger.info(String.format(BanHammer.getMessage("player-kicked"), senderName, playerName));
      this.plugin.getServer().broadcast(String.format(ChatColor.RED + BanHammer.getMessage("broadcast-player-kicked"), playerName), "banhammer.notify");
      this.plugin.getServer().broadcast(String.format(ChatColor.YELLOW + BanHammer.getMessage("broadcast-player-banned-reason"), arguments.get("reason")), "banhammer.notify");
    } else {
      Logger.info(String.format(BanHammer.getMessage("no-player-found"), playerName));
    }
  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) {
    Map<String, String> m = new HashMap<String, String>();
    try {
      m.put("playerName", arguments.remove(0));
      m.put("reason", this.combineString(arguments, " "));
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException();
    }
    return m;
  }

}
