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
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.BanHandler;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.command.PluginCommand;
import name.richardson.james.bukkit.utilities.formatters.StringFormatter;
import name.richardson.james.bukkit.utilities.internals.Logger;

@ConsoleCommand
public class ImportCommand extends PluginCommand {

  /** The logger for this class . */
  private final static Logger logger = new Logger(ImportCommand.class);

  /** A instance of the Bukkit server. */
  private final Server server;

  /** A reference to the BanHammer API. */
  private final BanHandler handler;

  /** The reason which will be set for all imported bans */
  private String reason;

  public ImportCommand(final BanHammer plugin) {
    super(plugin);
    logger.setPrefix("[" + plugin.getName() + "] ");
    this.handler = plugin.getHandler(ImportCommand.class);
    this.server = plugin.getServer();
    this.registerPermissions();
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final int total = this.server.getBannedPlayers().size();
    int imported = 0;
    final long time = 0;
    final String name = sender.getName();

    // Import and ban all players
    for (final OfflinePlayer player : this.server.getBannedPlayers()) {
      if (this.handler.banPlayer(player.getName(), name, this.reason, time, false)) {
        player.setBanned(false);
        imported = imported + 1;
      } else {
        logger.warning(this.getSimpleFormattedMessage("importcommand-player-already-banned", player.getName()));
      }
    }

    logger.info(this.getFormattedLogMessage(name, imported, total));
    if (imported != total) {
      sender.sendMessage(this.getFormattedFailedImportMessage(total - imported));
    }
    sender.sendMessage(this.getFormattedResponseMessage(imported));

  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    this.reason = (arguments.length == 0) ? this.getMessage("importcommand-default-reason") : StringFormatter.combineString(arguments, " ");
  }

  private String getFormattedFailedImportMessage(final int imported) {
    final Object[] arguments = { imported };
    final double[] limits = { 0, 1, 2 };
    final String[] formats = { this.getMessage("no-bans"), this.getMessage("one-ban"), this.getMessage("many-bans") };
    return this.getChoiceFormattedMessage("importcommand-response-failed-imports", arguments, formats, limits);
  }

  private String getFormattedLogMessage(final String name, final int imported, final int total) {
    final Object[] arguments = { imported, total, name };
    final double[] limits = { 0, 1, 2 };
    final String[] formats = { this.getMessage("no-bans").toLowerCase(), this.getMessage("one-ban").toLowerCase(), this.getMessage("many-bans").toLowerCase() };
    return this.getChoiceFormattedMessage("importcommand-summary-result", arguments, formats, limits);
  }

  private String getFormattedResponseMessage(final int imported) {
    final Object[] arguments = { imported };
    final double[] limits = { 0, 1, 2 };
    final String[] formats = { this.getMessage("no-bans"), this.getMessage("one-ban"), this.getMessage("many-bans") };
    return this.getChoiceFormattedMessage("importcommand-response-message", arguments, formats, limits);
  }

  private void registerPermissions() {
    final String prefix = this.plugin.getDescription().getName().toLowerCase() + ".";
    // create the base permission
    final Permission base = new Permission(prefix + this.getName(), this.getMessage("importcommand-permission-description"), PermissionDefault.OP);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
  }

}
