/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * BanCommand.java is part of BanHammer.
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.command.PluginCommand;
import name.richardson.james.bukkit.utilities.formatters.StringFormatter;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;

@ConsoleCommand
public class BanCommand extends PluginCommand {

  /** Reference to the BanHammer plugin */
  private final BanHammer plugin;

  /** Reference to the BanHammer API */
  private final BanHandler handler;

  /** A instance of the Bukkit server. */
  private final Server server;

  /** The name of the player who we are going to ban */
  private OfflinePlayer player;

  /** How long in milliseconds to ban the player for */
  private long time;

  /** The reason given for the player's ban */
  private String reason;

  public BanCommand(final BanHammer plugin) {
    super(plugin);
    this.plugin = plugin;
    this.server = plugin.getServer();
    this.handler = plugin.getHandler(BanCommand.class);
    this.registerPermissions();
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {

    if (this.isBanLengthAuthorised(sender, this.time)) {
      if (!this.handler.banPlayer(this.player.getName(), sender.getName(), this.reason, this.time, true)) {
        sender.sendMessage(this.getSimpleFormattedMessage("player-already-banned", this.player.getName()));
      } else {
        sender.sendMessage(this.getSimpleFormattedMessage("player-banned", this.player.getName()));
      }
    } else {
      throw new CommandPermissionException(this.getMessage("ban-time-too-long"), this.getPermission(0));
    }
  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    final LinkedList<String> args = new LinkedList<String>();
    args.addAll(Arrays.asList(arguments));

    if (args.size() == 0) {
      throw new CommandArgumentException(this.getMessage("must-specify-a-player"), this.getMessage("name-autocompletion"));
    } else {
      this.player = this.matchPlayer(args.remove(0));
    }

    if ((args.size() != 0) && args.get(0).startsWith("t:")) {
      final String time = args.remove(0).replaceAll("t:", "");
      if (this.plugin.getBanLimits().containsKey(time)) {
        this.time = this.plugin.getBanLimits().get(time);
      } else {
        this.time = TimeFormatter.parseTime(time);
      }
    } else {
      this.time = 0;
    }

    if (args.isEmpty()) {
      throw new CommandArgumentException(this.getMessage("must-specify-a-reason"), this.getMessage("reason-hint"));
    } else {
      this.reason = StringFormatter.combineString(args, " ");
    }
  }

  private boolean isBanLengthAuthorised(final CommandSender sender, final long banLength) {
    if (sender instanceof CommandSender) {
      return true;
    }
    if ((banLength == 0) && !sender.hasPermission(this.getPermission(0))) {
      return false;
    } else {
      for (final Entry<String, Long> limit : this.plugin.getBanLimits().entrySet()) {
        if (sender.hasPermission(this.getPermission(1).getName() + "." + limit.getKey()) && (banLength <= limit.getValue())) {
          return true;
        }
      }
    }
    return false;
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
    final String wildcardDescription = String.format(this.plugin.getMessage("wildcard-permission-description"), this.getName());
    // create the wildcard permission
    final Permission wildcard = new Permission(prefix + this.getName() + ".*", wildcardDescription, PermissionDefault.OP);
    wildcard.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(wildcard);
    // create the base permission
    final Permission base = new Permission(prefix + this.getName(), this.getMessage("permission-description"), PermissionDefault.OP);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
    // create permissions for individual ban limits
    final Map<String, Long> limits = this.plugin.getBanLimits();
    if (!limits.isEmpty()) {
      for (final Entry<String, Long> limit : limits.entrySet()) {
        final Permission permission = new Permission(base.getName() + "." + limit.getKey(), this.getSimpleFormattedMessage("permission-limit-description", TimeFormatter.millisToLongDHMS(limit.getValue())), PermissionDefault.OP);
        permission.addParent(wildcard, true);
        this.addPermission(permission);
      }
    }
  }

}
