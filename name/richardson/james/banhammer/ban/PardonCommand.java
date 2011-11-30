/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * PardonCommand.java is part of BanHammer.
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

import name.richardson.james.banhammer.BanHammerPlugin;
import name.richardson.james.banhammer.Command;
import name.richardson.james.banhammer.exceptions.NotEnoughArgumentsException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PardonCommand extends Command {

  private BanHandler banHandler;

  public PardonCommand(final BanHammerPlugin plugin) {
    super(plugin);
    this.name = "pardon";
    this.description = "pardon a player";
    this.usage = "/pardon [name]";
    this.permission = "banhammer." + this.name;
    this.banHandler = plugin.getHandler();
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) throws NotEnoughArgumentsException {
    String senderName = this.getSenderName(sender);
    
    if (!this.banHandler.pardonPlayer(arguments.get("playerName"), senderName, true)) {
      sender.sendMessage(String.format(ChatColor.YELLOW + BanHammerPlugin.getMessage("playerNotBanned"), arguments.get("playerName")));
    }
    
  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) throws NotEnoughArgumentsException {
    Map<String, String> m = new HashMap<String, String>();

    try {
      m.put("playerName", arguments.get(0));
    } catch (IndexOutOfBoundsException e) {
      throw new NotEnoughArgumentsException();
    }

    return m;
  }

}
