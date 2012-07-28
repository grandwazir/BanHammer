/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * HistoryCommand.java is part of BanHammer.
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
import name.richardson.james.bukkit.banhammer.BanHandler;
import name.richardson.james.bukkit.banhammer.api.BanSummary;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.command.PluginCommand;

@ConsoleCommand
public class HistoryCommand extends PluginCommand {

  /** Reference to the BanHammer API */
  private final BanHandler handler;

  /** Reference to the BanHammer plugin */
  private final BanHammer plugin;

  /** A instance of the Bukkit server. */
  private final Server server;

  /** The player whos history we are going to check */
  private OfflinePlayer player;

  public HistoryCommand(final BanHammer plugin) {
    super(plugin);
    this.handler = plugin.getHandler(HistoryCommand.class);
    this.plugin = plugin;
    this.server = plugin.getServer();
    this.registerPermissions();
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final List<BanRecord> bans = this.handler.getPlayerBans(this.player.getName());

    if (sender.hasPermission(this.getPermission(3)) && !this.player.getName().equalsIgnoreCase(sender.getName())) {
      this.displayHistory(bans, sender);
      return;
    } else if (!this.player.getName().equalsIgnoreCase(sender.getName())) {
      throw new CommandPermissionException(this.getMessage("cannot-view-others-history"), this.getPermission(3));
    }

    if (sender.hasPermission(this.getPermission(2)) && this.player.getName().equalsIgnoreCase(sender.getName())) {
      this.displayHistory(bans, sender);
      return;
    } else if (this.player.getName().equalsIgnoreCase(sender.getName())) {
      throw new CommandPermissionException(this.getMessage("cannot-view-own-history"), this.getPermission(3));
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

  private void displayHistory(final List<BanRecord> bans, final CommandSender sender) {
    sender.sendMessage(this.getFormattedMessageHeader(bans.size(), this.player.getName()));
    for (final BanRecord ban : bans) {
      final BanSummary summary = new BanSummary(this.plugin, ban);
      sender.sendMessage(summary.getSelfHeader());
      sender.sendMessage(summary.getReason());
      sender.sendMessage(summary.getLength());
      if (ban.getType() == BanRecord.Type.TEMPORARY) {
        sender.sendMessage(summary.getExpiresAt());
      }
    }
  }

  private String getFormattedMessageHeader(final int size, final String name) {
    final Object[] arguments = { size, name };
    final double[] limits = { 0, 1, 2 };
    final String[] formats = { this.getMessage("no-ban").toLowerCase(), this.getMessage("one-ban").toLowerCase(), this.getMessage("many-bans") };
    return this.getChoiceFormattedMessage("historycommand-header", arguments, formats, limits);
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
    final String wildcardDescription = String.format(this.plugin.getMessage("plugincommand.wildcard-permission-description"), this.getName());
    // create the wildcard permission
    final Permission wildcard = new Permission(prefix + this.getName() + ".*", wildcardDescription, PermissionDefault.OP);
    wildcard.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(wildcard);
    // create the base permission
    final Permission base = new Permission(prefix + this.getName(), this.getMessage("permission-description"), PermissionDefault.TRUE);
    base.addParent(wildcard, true);
    this.addPermission(base);
    // add ability to view your own ban history
    final Permission own = new Permission(prefix + this.getName() + "." + this.getMessage("own-permission-name"), this.getMessage("own-permission-description"), PermissionDefault.TRUE);
    own.addParent(base, true);
    this.addPermission(own);
    // add ability to view the ban history of others
    final Permission others = new Permission(prefix + this.getName() + "." + this.getMessage("others-permission-name"), this.getMessage("others-permission-description"), PermissionDefault.OP);
    others.addParent(base, true);
    this.addPermission(others);
  }

}
