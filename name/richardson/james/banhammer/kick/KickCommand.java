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
import name.richardson.james.banhammer.exceptions.NoMatchingPlayerException;
import name.richardson.james.banhammer.exceptions.NotEnoughArgumentsException;
import name.richardson.james.banhammer.util.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand extends Command {

  public KickCommand(BanHammer plugin) {
    super(plugin);
    this.name = "kick";
    this.description = "kick a player from the server";
    this.usage = "/kick [name] <reason>";
    this.permission = "banhammer." + this.name;
    this.requiredArgumentCount = 2;
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) throws NotEnoughArgumentsException, NoMatchingPlayerException {
    String senderName = this.getSenderName(sender);
    Player player = this.getPlayer(arguments.get("playerName"));
    player.kickPlayer(String.format(BanHammer.getMessage("kickedMessage"), arguments.get("reason")));
    Logger.info(String.format(BanHammer.getMessage("logPlayerKicked"), senderName, player.getName()));
    this.broadcastMessage(String.format(ChatColor.RED + BanHammer.getMessage("notifyKickedPlayer"), player.getName()));
    this.broadcastMessage(String.format(ChatColor.YELLOW + BanHammer.getMessage("notifyReason"), arguments.get("reason")));
  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) throws NotEnoughArgumentsException {
    Map<String, String> m = new HashMap<String, String>();
    try {
      m.put("playerName", arguments.remove(0));
      m.put("reason", this.combineString(arguments, " "));
    } catch (IndexOutOfBoundsException e) {
      throw new NotEnoughArgumentsException();
    }
    return m;
  }

}
