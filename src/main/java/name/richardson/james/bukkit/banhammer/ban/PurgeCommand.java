/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * PurgeCommand.java is part of BanHammer.
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

import javax.persistence.OptimisticLockException;

import com.avaje.ebean.EbeanServer;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;

@ConsoleCommand
public class PurgeCommand extends AbstractCommand {

  /** A instance of the Bukkit server. */
  private final Server server;

  /** The player from whom we are going to purge bans */
  private OfflinePlayer player;

  private EbeanServer database;

  private ChoiceFormatter formatter;

  public PurgeCommand(final BanHammer plugin) {
    super(plugin, false);
    this.database = plugin.getDatabase();
    this.server = plugin.getServer();
    this.formatter = new ChoiceFormatter(this.getLocalisation());
    this.formatter.setLimits(0, 1, 2);
    this.formatter.setMessage(this, "purged");
    this.formatter.setFormats(this.getLocalisation().getMessage(BanHammer.class, "no-bans"), this.getLocalisation().getMessage(BanHammer.class, "one-ban"), this.getLocalisation().getMessage(BanHammer.class, "many-bans"));
  }

  public void execute(final CommandSender sender) throws CommandPermissionException, CommandUsageException {
    final PlayerRecord playerRecord = PlayerRecord.find(database, player.getName());
    int i = 0;
    if (playerRecord != null) {
      i = BanRecord.deleteBans(database, playerRecord.getBans());
    }
    this.formatter.setArguments(i, player.getName());
    sender.sendMessage(this.formatter.getMessage());
    this.getLogger().info(this, "log-purged", player.getName(), sender.getName());
  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getLocalisation().getMessage(BanHammer.class, "must-specify-player"), null);
    } else {
      this.player = this.matchPlayer(arguments[0]);
    }

  }

  private OfflinePlayer matchPlayer(final String name) {
    return this.server.getOfflinePlayer(name);
  }

}
