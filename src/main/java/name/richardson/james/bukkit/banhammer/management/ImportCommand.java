/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * ImportCommand.java is part of BanHammer.
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
package name.richardson.james.bukkit.banhammer.management;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.formatters.StringFormatter;

@ConsoleCommand
public class ImportCommand extends AbstractCommand {

  private final ChoiceFormatter formatter;

  /** A instance of the Bukkit server. */
  private final Server server;

  /** A reference to the BanHammer API. */
  private final BanHandler handler;

  /** The reason which will be set for all imported bans */
  private String reason;

  public ImportCommand(final BanHammer plugin) {
    super(plugin, false);
    this.handler = plugin.getHandler();
    this.server = plugin.getServer();
    this.formatter = new ChoiceFormatter(this.getLocalisation());
    this.formatter.setLimits(0, 1, 2);
    this.formatter.setFormats(this.getLocalisation().getMessage(BanHammer.class, "no-bans"), this.getLocalisation().getMessage(BanHammer.class, "one-ban"), this.getLocalisation().getMessage(BanHammer.class, "many-bans"));
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final int total = this.server.getBannedPlayers().size();
    final long time = 0;
    final String importer = sender.getName();
    int imported = 0;

    for (final OfflinePlayer player : this.server.getBannedPlayers()) {
      if (this.handler.banPlayer(player.getName(), importer, this.reason, time, false)) {
        player.setBanned(false);
        imported = imported + 1;
      } else {
        this.getLogger().warning(this, "unable-to-import", player.getName());
      }
    }

    this.formatter.setMessage(this, "bans-imported");
    this.formatter.setArguments(total);
    sender.sendMessage(this.formatter.getMessage());
    if (imported != total) {
      this.formatter.setMessage(this, "bans-failed");
      this.formatter.setArguments(total - imported);
      sender.sendMessage(this.formatter.getMessage());
    }

  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    this.reason = (arguments.length == 0) ? this.getLocalisation().getMessage(this, "default-reason") : StringFormatter.combineString(arguments, " ");
  }

}
