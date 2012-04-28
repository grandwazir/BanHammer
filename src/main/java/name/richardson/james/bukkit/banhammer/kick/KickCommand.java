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
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.command.PluginCommand;
import name.richardson.james.bukkit.utilities.formatters.StringFormatter;
import name.richardson.james.bukkit.utilities.internals.Logger;

@ConsoleCommand
public class KickCommand extends PluginCommand {

  /** The logger for this class . */
  private final static Logger logger = new Logger(KickCommand.class);

  /** A instance of the Bukkit server. */
  private final Server server;

  /** The player who is going to be kicked */
  private Player player;

  /** The reason to give to the kicked player */
  private String reason;

  public KickCommand(final BanHammer plugin) {
    super(plugin);
    logger.setPrefix("[" + plugin.getName() + "]");
    this.server = plugin.getServer();
    this.registerPermissions();
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    if (this.player.isOnline()) {
      this.player.kickPlayer(this.getSimpleFormattedMessage("kicked-notification", this.reason));
      logger.info(this.getFormattedSummaryMessage(sender.getName()));
      this.server.broadcast(this.getSimpleFormattedMessage("kickcommand-player-kicked", this.player.getName()), "banhammer.notify");
      this.server.broadcast(this.getSimpleFormattedMessage("kickcommand-player-kicked-reason", this.reason), "banhammer.notify");
    }
  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getMessage("must-specify-a-player"), this.getMessage("name-autocompletion"));
    }
    this.player = this.matchPlayer(arguments[0]);
    if (this.player == null) {
      throw new CommandArgumentException(this.getMessage("must-specify-a-player"), this.getMessage("name-autocompletion"));
    }
    if (arguments.length > 1) {
      final String[] elements = new String[arguments.length - 1];
      System.arraycopy(arguments, 1, elements, 0, arguments.length - 1);
      this.reason = StringFormatter.combineString(elements, " ");
    } else {
      this.reason = this.getMessage("kickcommand-default-reason");
    }
  }

  private String getFormattedSummaryMessage(final String sender) {
    final Object[] arguments = { this.player.getName(), sender, this.reason };
    return this.getSimpleFormattedMessage("kickcommand-summary-result", arguments);
  }

  private Player matchPlayer(final String name) {
    final List<Player> players = this.server.matchPlayer(name);
    if (players.isEmpty()) {
      return null;
    }
    return players.get(0);
  }

  private void registerPermissions() {
    final String prefix = this.plugin.getDescription().getName().toLowerCase() + ".";
    // create the base permission
    final Permission base = new Permission(prefix + this.getName(), this.getMessage("kickcommand-permission-description"), PermissionDefault.OP);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
  }

}
