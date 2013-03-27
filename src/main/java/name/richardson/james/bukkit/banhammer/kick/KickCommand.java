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

import java.util.ArrayList;
import java.util.List;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.PlayerListener;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.formatters.StringFormatter;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ConsoleCommand
public class KickCommand extends AbstractCommand {

  /** A instance of the Bukkit server. */
  private final Server server;

  /** The player who is going to be kicked */
  private Player player;

  /** The reason to give to the kicked player */
  private String reason;

  public KickCommand(final BanHammer plugin) {
    super(plugin);
    this.server = plugin.getServer();
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    if (this.player.isOnline()) {
      this.player.kickPlayer(this.getLocalisation().getMessage(PlayerListener.class, "kick-message", this.reason));
      server.broadcast(this.getLocalisation().getMessage(this, "kick-broadcast", player.getName(), sender.getName()), "banhammer.notify");
      server.broadcast(this.getLocalisation().getMessage(this, "reason", reason), "banhammer.notify");
      this.getLogger().info(this.getLocalisation().getMessage(this, "kicked", player.getName(), sender.getName()));
    }
    this.player = null;
  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getLocalisation().getMessage(BanHammer.class, "must-specify-player"), null);
    }

    this.player = this.matchPlayer(arguments[0]);
    if (this.player == null) {
      throw new CommandArgumentException(this.getLocalisation().getMessage(BanHammer.class, "must-specify-player"), null);
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
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments) {
    List<String> list = new ArrayList<String>();
    if (arguments.length <= 1) {
      for (Player player : this.server.getOnlinePlayers()) {
        if (arguments.length < 1) {
          list.add(player.getName());
        } else if (player.getName().startsWith(arguments[0])) {
          list.add(player.getName());
        }
      }
    }
    return list;
  }

}
