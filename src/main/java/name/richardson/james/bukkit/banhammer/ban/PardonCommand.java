/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * PardonCommand.java is part of BanHammer.
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

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.BanHandler;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.command.PluginCommand;

@ConsoleCommand
public class PardonCommand extends PluginCommand {

  /** A reference to the BanHammer API. */
  private final BanHandler handler;

  /** A instance of the Bukkit server. */
  private final Server server;

  /** The player who is going to be pardoned */
  private OfflinePlayer player;

  public PardonCommand(final BanHammer plugin) {
    super(plugin);
    this.handler = plugin.getHandler(PardonCommand.class);
    this.plugin = plugin;
    this.server = plugin.getServer();
    this.registerPermissions();
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final BanRecord ban = this.handler.getPlayerBan(this.player.getName());

    if (ban != null) {

      if (sender.hasPermission(this.getPermission(3)) && !ban.getCreator().getName().equalsIgnoreCase(sender.getName())) {
        this.handler.pardonPlayer(this.player.getName(), sender.getName(), true);
        this.player.setBanned(false);
        sender.sendMessage(this.getSimpleFormattedMessage("response-message", this.player.getName()));
        return;
      } else if (!ban.getCreator().getName().equalsIgnoreCase(sender.getName())) {
        throw new CommandPermissionException(this.getMessage("cannot-pardon-others-bans"), this.getPermission(3));
      }

      if (sender.hasPermission(this.getPermission(2)) && ban.getCreator().getName().equalsIgnoreCase(sender.getName())) {
        this.handler.pardonPlayer(this.player.getName(), sender.getName(), true);
        this.player.setBanned(false);
        sender.sendMessage(this.getSimpleFormattedMessage("response-message", this.player.getName()));
        return;
      } else if (ban.getCreator().getName().equalsIgnoreCase(sender.getName())) {
        throw new CommandPermissionException(this.getMessage("cannot-pardon-own-bans"), this.getPermission(3));
      }

    } else {
      sender.sendMessage(this.getSimpleFormattedMessage("player-is-not-banned", this.player.getName()));
    }

  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getMessage("must-specify-a-player"), this.getMessage("name-autocompletion"));
    } else {
      this.player = this.matchPlayer(arguments[0]);
    }
  }

  private OfflinePlayer matchPlayer(final String name) {
    return this.server.getOfflinePlayer(name);
  }

  private void registerPermissions() {
    final String prefix = this.plugin.getDescription().getName().toLowerCase() + ".";
    final String wildcardDescription = String.format(this.plugin.getMessage("plugincommand.wildcard-permission-description"), this.getName());
    // create the wildcard permission
    final Permission wildcard = new Permission(prefix + this.getName() + ".*", wildcardDescription, PermissionDefault.OP);
    wildcard.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(wildcard);
    // create the base permission
    final Permission base = new Permission(prefix + this.getName(), this.getMessage("permission-description"), PermissionDefault.OP);
    base.addParent(wildcard, true);
    this.addPermission(base);
    // add ability to pardon your own bans
    final Permission own = new Permission(prefix + this.getName() + "." + this.getMessage("own-permission-name"), this.getMessage("own-permission-description"), PermissionDefault.OP);
    own.addParent(base, true);
    this.addPermission(own);
    // add ability to pardon the bans of others
    final Permission others = new Permission(prefix + this.getName() + "." + this.getMessage("others-permission-name"), this.getMessage("others-permission-description"), PermissionDefault.OP);
    others.addParent(base, true);
    this.addPermission(others);
  }

}
