/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * KickCommand.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.kick;

import java.util.List;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.formatters.StringFormatter;

@ConsoleCommand
public class KickCommand extends AbstractCommand {

  /** A instance of the Bukkit server. */
  private final Server server;

  /** The player who is going to be kicked */
  private Player player;

  /** The reason to give to the kicked player */
  private String reason;

  public KickCommand(final BanHammer plugin) {
    super(plugin, false);
    this.server = plugin.getServer();
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    if (this.player.isOnline()) {
      this.player.kickPlayer(this.reason);
      this.getLogger().info(this, "kicked", player.getName(), sender.getName());
    }
    this.player = null;
  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getLocalisation().getMessage(this, "must-specify-player"), null);
    }
    
    this.player = this.matchPlayer(arguments[0]);
    if (this.player == null) {
      throw new CommandArgumentException(this.getLocalisation().getMessage(this, "must-specify-player"), null);
    }
    
    if (arguments.length > 1) {
      final String[] elements = new String[arguments.length - 1];
      System.arraycopy(arguments, 1, elements, 0, arguments.length - 1);
      this.reason = StringFormatter.combineString(elements, " ");
    } else {
      this.reason = this.getLocalisation().getMessage(this, "default-reason");
    }
  }

  private Player matchPlayer(final String name) {
    final List<Player> players = this.server.matchPlayer(name);
    if (players.isEmpty()) {
      return null;
    }
    return players.get(0);
  }

}
