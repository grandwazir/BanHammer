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

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.formatters.StringFormatter;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

@ConsoleCommand
public class BanCommand extends AbstractCommand {

  /** Reference to the BanHammer API. */
  private final BanHandler handler;

  /** A instance of the Bukkit server. */
  private final Server server;

  /** The name of the player who we are going to ban. */
  private OfflinePlayer player;

  /** How long in milliseconds to ban the player for. */
  private long time;

  /** The reason given for the player's ban. */
  private String reason;

  /** The ban limis. */
  private Map<String, Long> limits;

  private final List<String> immunePlayers;
  
  /** The wildcard permission needed for banning permeantly **/
  private final Permission wildcardPermission;

  /**
   * Instantiates a new BanCommand.
   * 
   * @param plugin the plugin that this command belongs to
   * @param limits the registered ban limits to use
   */
  public BanCommand(final BanHammer plugin, Map<String, Long> limits, List<String> immunePlayers) {
    super(plugin);
    this.immunePlayers = immunePlayers;
    this.limits = limits;
    this.registerLimitPermissions();
    this.server = plugin.getServer();
    this.handler = plugin.getHandler();
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.utilities.command.Command#execute(org.bukkit
   * .command.CommandSender)
   */
  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    if (this.immunePlayers.contains(this.player.getName()) || this.player.isOp()) {
      if (!this.getPermissionManager().hasPlayerPermission(sender, this.getPermissionManager().getRootPermission().getName())) {
      throw new CommandPermissionException(
          this.getLocalisation().getMessage(this, "player-immune"), 
          this.wildcardPermission);
      }  
    }
    if (this.isBanLengthAuthorised(sender, this.time)) {
      if (!this.handler.banPlayer(this.player.getName(), sender.getName(), this.reason, this.time, true)) {
        sender.sendMessage(this.getLocalisation().getMessage(this, "player-already-banned", this.player.getName()));
      } else {
        sender.sendMessage(this.getLocalisation().getMessage(this, "player-banned", this.player.getName()));
      }
    } else {
      throw new CommandPermissionException(this.getLocalisation().getMessage(this, "ban-time-too-long"), this.wildcardPermission);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.utilities.command.Command#parseArguments(java
   * .lang.String[], org.bukkit.command.CommandSender)
   */
  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    final LinkedList<String> args = new LinkedList<String>();
    args.addAll(Arrays.asList(arguments));

    if (args.size() == 0) {
      throw new CommandArgumentException(this.getLocalisation().getMessage(BanHammer.class, "must-specify-player"), null);
    } else {
      this.player = this.matchPlayer(args.remove(0));
    }

    if ((args.size() != 0) && args.get(0).startsWith("t:")) {
      final String time = args.remove(0).replaceAll("t:", "");
      if (limits.containsKey(time)) {
        this.time = limits.get(time);
      } else {
        this.time = TimeFormatter.parseTime(time);
      }
    } else {
      this.time = 0;
    }
    if (args.isEmpty()) {
      throw new CommandArgumentException(this.getLocalisation().getMessage(this, "must-specify-a-reason"), this.getLocalisation().getMessage(this, "reason-hint"));
    } else {
      this.reason = StringFormatter.combineString(args, " ");
    }
  }

  /**
   * Checks if is ban length authorised.
   * 
   * @param sender the sender
   * @param banLength the ban length
   * @return true, if is ban length authorised
   */
  private boolean isBanLengthAuthorised(final CommandSender sender, final long banLength) {
    if (sender instanceof ConsoleCommandSender) return true;
    if (this.getPermissionManager().hasPlayerPermission(sender, this.wildcardPermission)) return true;
    if ((banLength == 0) && !this.getPermissionManager().hasPlayerPermission(sender, this.wildcardPermission)) {
      return false;
    } else {
      for (final Entry<String, Long> limit : limits.entrySet()) {
        if (this.getPermissionManager().hasPlayerPermission(sender, this.getRootPermission().getName().replace("*", "") + "." + limit.getKey()) && (banLength <= limit.getValue())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Match a String with an OfflinePlayer.
   * 
   * @param name the name to match
   * @return the offline player
   */
  private OfflinePlayer matchPlayer(final String name) {
    final List<Player> players = this.server.matchPlayer(name);
    if (players.isEmpty()) {
      return this.server.getOfflinePlayer(name);
    } else {
      return players.get(0);
    }
  }
  
  private void registerLimitPermissions() {
    final Permission parent = this.getPermissions().get(0);
    if (!limits.isEmpty()) {
      for (final Entry<String, Long> limit : limits.entrySet()) {
        final Permission permission = new Permission(parent.getName() + "." + limit.getKey(), this.getLocalisation().getMessage(this, "permission-limit-description", TimeFormatter.millisToLongDHMS(limit.getValue())), PermissionDefault.OP);
        permission.addParent(parent, true);
        this.getPermissionManager().addPermission(permission);
        this.addPermission(permission);
      }
    }
  }

}
