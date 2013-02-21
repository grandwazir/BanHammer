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

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avaje.ebean.EbeanServer;

@ConsoleCommand
public class CheckCommand extends AbstractCommand {

  /** The player who we are going to check and see if they are banned */
  private OfflinePlayer player;

  /** A instance of the Bukkit server. */
  private final Server server;

  private final EbeanServer database;

  public CheckCommand(final BanHammer plugin) {
    super(plugin, false);
    this.database = plugin.getDatabase();
    this.server = plugin.getServer();
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final PlayerRecord playerRecord = PlayerRecord.find(database, player.getName());
    if ((playerRecord != null) && playerRecord.isBanned()) {
      final BanRecord ban = playerRecord.getActiveBan();
      final BanSummary summary = new BanSummary(this.getLocalisation(), ban);
      sender.sendMessage(summary.getHeader());
      sender.sendMessage(summary.getReason());
      sender.sendMessage(summary.getLength());
      if (ban.getType() == BanRecord.Type.TEMPORARY) {
        sender.sendMessage(summary.getExpiresAt());
      }
    } else {
      sender.sendMessage(this.getLocalisation().getMessage(this, "player-is-not-banned", this.player.getName()));
    }
  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      if (!(sender instanceof Player)) {
        throw new CommandArgumentException(this.getLocalisation().getMessage(BanHammer.class, "must-specify-player"), null);
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

}
