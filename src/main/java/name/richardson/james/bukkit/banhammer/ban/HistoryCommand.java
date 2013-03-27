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

import java.util.ArrayList;
import java.util.List;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

@ConsoleCommand
public class HistoryCommand extends AbstractCommand {

  /** Reference to the BanHammer API */
  private final BanHandler handler;

  /** A instance of the Bukkit server. */
  private final Server server;

  /** The player whos history we are going to check */
  private String playerName;

  private final ChoiceFormatter formatter;

  public HistoryCommand(final BanHammer plugin) {
    super(plugin);
    this.handler = plugin.getHandler();
    this.server = plugin.getServer();
    this.formatter = new ChoiceFormatter(this.getLocalisation());
    this.formatter.setLimits(0, 1, 2);
    this.formatter.setMessage(this, "header");
    this.formatter.setFormats(this.getLocalisation().getMessage(BanHammer.class, "no-bans"), this.getLocalisation().getMessage(BanHammer.class, "one-ban"), this.getLocalisation().getMessage(BanHammer.class, "many-bans"));
    this.registerPermissions();
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final List<BanRecord> bans = this.handler.getPlayerBans(this.playerName);

    if (sender.hasPermission(this.getPermissions().get(1)) && !this.playerName.equalsIgnoreCase(sender.getName())) {
      this.displayHistory(bans, sender);
      return;
    } else if (!this.playerName.equalsIgnoreCase(sender.getName())) {
      throw new CommandPermissionException(this.getLocalisation().getMessage(this, "cannot-view-others-history"), this.getPermissions().get(2));
    }

    if (sender.hasPermission(this.getPermissions().get(2)) && this.playerName.equalsIgnoreCase(sender.getName())) {
      this.displayHistory(bans, sender);
      return;
    } else if (this.playerName.equalsIgnoreCase(sender.getName())) {
      throw new CommandPermissionException(this.getLocalisation().getMessage(this, "cannot-view-own-history"), this.getPermissions().get(1));
    }

  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      if (!(sender instanceof Player)) {
        throw new CommandArgumentException(this.getLocalisation().getMessage(BanHammer.class, "must-specify-player"), null);
      }
      this.playerName = sender.getName();
    } else {
      this.playerName = this.matchPlayer(arguments[0]);
    }

  }

  private void displayHistory(final List<BanRecord> bans, final CommandSender sender) {
    this.formatter.setArguments(bans.size(), this.playerName);
    sender.sendMessage(this.formatter.getMessage());
    for (final BanRecord ban : bans) {
      final BanSummary summary = new BanSummary(this.getLocalisation(), ban);
      sender.sendMessage(summary.getSelfHeader());
      sender.sendMessage(summary.getReason());
      sender.sendMessage(summary.getLength());
      if (ban.getType() == BanRecord.Type.TEMPORARY) {
        sender.sendMessage(summary.getExpiresAt());
      }
    }
  }

  private String matchPlayer(final String name) {
    final List<Player> players = this.server.matchPlayer(name);
    if (players.isEmpty()) {
      return name;
    } else {
      return players.get(0).getName();
    }
  }

  private void registerPermissions() {
    // add ability to view your own ban history
    Permission own = this.getPermissionManager().createPermission(this, "own", PermissionDefault.TRUE, this.getPermissions().get(0), true);
    this.addPermission(own);
    // add ability to view the ban history of others
    Permission others = this.getPermissionManager().createPermission(this, "others", PermissionDefault.OP, this.getPermissions().get(0), true);
    this.addPermission(others);
  }

  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments) {
    List<String> list = new ArrayList<String>();
    if (arguments.length <= 1) {
      for (Player player : this.server.getOnlinePlayers()) {
        list.add(player.getName());
      }
    }
    return list;
  }

}
