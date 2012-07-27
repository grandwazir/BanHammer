/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * CheckCommand.java is part of BanHammer.
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
package name.richardson.james.bukkit.banhammer.ban;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.command.PluginCommand;

@ConsoleCommand
public class CheckCommand extends PluginCommand {

  /** Reference to the BanHammer API */
  private final BanHandler handler;

  /** The player who we are going to check and see if they are banned */
  private OfflinePlayer player;

  /** A instance of the Bukkit server. */
  private final Server server;

  public CheckCommand(final BanHammer plugin) {
    super(plugin);
    this.handler = plugin.getHandler(CheckCommand.class);
    this.server = plugin.getServer();
    this.registerPermissions();
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final BanRecord ban = handler.getPlayerBan(player.getName());

    if (ban != null) {
      final BanSummary summary = new BanSummary(this.plugin, ban);
      sender.sendMessage(summary.getHeader());
      sender.sendMessage(summary.getReason());
      sender.sendMessage(summary.getLength());
      if (ban.getType() == BanRecord.Type.TEMPORARY) {
        sender.sendMessage(summary.getExpiresAt());
      }
    } else {
      sender.sendMessage(this.getSimpleFormattedMessage("player-is-not-banned", this.player.getName()));
    }

  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      if (sender instanceof ConsoleCommandSender) {
        throw new CommandArgumentException(this.getMessage("must-specify-a-player"), this.getMessage("name-autocompletion"));
      }
      this.player = (OfflinePlayer) sender;
    } else {
      this.player = this.matchPlayer(arguments[0]);
    }

  }

  private OfflinePlayer matchPlayer(final String name) {
    final List<Player> players = this.server.matchPlayer(name);
    if (players.isEmpty()) {
      return this.server.getOfflinePlayer(name);
    } else {
      return players.get(0);
    }
  }

  private void registerPermissions() {
    final String prefix = this.plugin.getDescription().getName().toLowerCase() + ".";
    // create the base permission
    final Permission base = new Permission(prefix + this.getName(), this.getMessage("permission-description"), PermissionDefault.OP);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
  }

}
