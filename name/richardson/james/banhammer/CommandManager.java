/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * CommandManager.java is part of BanHammer.
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
package name.richardson.james.banhammer;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandManager implements CommandExecutor {

  private final HashMap<String, CommandExecutor> commands = new HashMap<String, CommandExecutor>();

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (args.length != 0)
      if (this.commands.containsKey(args[0])) {
        this.commands.get(args[0]).onCommand(sender, command, label, args);
        return true;
      }
    sender.sendMessage(ChatColor.RED + BanHammer.getMessage("invalid-command"));
    sender.sendMessage(ChatColor.YELLOW + BanHammer.getMessage("invalid-command-hint"));
    sender.sendMessage(ChatColor.YELLOW + BanHammer.getMessage("invalid-command-hint2"));
    return true;
  }

  void registerCommand(final String command, final CommandExecutor executor) {
    this.commands.put(command, executor);
  }

}
