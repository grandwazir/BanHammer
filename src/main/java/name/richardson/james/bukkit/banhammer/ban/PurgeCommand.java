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

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.command.PluginCommand;
import name.richardson.james.bukkit.utilities.internals.Logger;

@ConsoleCommand
public class PurgeCommand extends PluginCommand {

  /** The logger for this class . */
  private final static Logger logger = new Logger(PurgeCommand.class);

  /** A instance of the Bukkit server. */
  private final Server server;

  /** The player from whom we are going to purge bans */
  private OfflinePlayer player;

  public PurgeCommand(final BanHammer plugin) {
    super(plugin);
    this.server = plugin.getServer();
    this.registerPermissions();
  }

  public void execute(final CommandSender sender) throws CommandPermissionException, CommandUsageException {
    final PlayerRecord playerRecord = PlayerRecord.find(plugin.getDatabase(), player.getName());
    int i = 0;
    if (playerRecord != null) {
      i = playerRecord.getBans().size();
      playerRecord.getBans().clear();
      plugin.getDatabase().save(playerRecord);
    }
    sender.sendMessage(this.getFormattedResponseMessage(i));
    logger.info(this.getFormattedSummaryMessage(i, sender.getName()));
  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getMessage("must-specify-a-player"), this.getMessage("name-autocompletion"));
    } else {
      this.player = this.matchPlayer(arguments[0]);
    }

  }

  private String getFormattedResponseMessage(final int total) {
    final Object[] arguments = { total, this.player.getName() };
    final double[] limits = { 0, 1, 2 };
    final String[] formats = { this.getMessage("no-bans").toLowerCase(), this.getMessage("one-ban").toLowerCase(), this.getMessage("many-bans").toLowerCase() };
    return this.getChoiceFormattedMessage("response-message", arguments, formats, limits);
  }

  private String getFormattedSummaryMessage(final int total, final String name) {
    final Object[] arguments = { total, this.player.getName(), name };
    final double[] limits = { 0, 1, 2 };
    final String[] formats = { this.getMessage("no-bans").toLowerCase(), this.getMessage("one-ban").toLowerCase(), this.getMessage("many-bans").toLowerCase() };
    return this.getChoiceFormattedMessage("summary-result", arguments, formats, limits);
  }

  private OfflinePlayer matchPlayer(final String name) {
    return this.server.getOfflinePlayer(name);
  }

  private void registerPermissions() {
    final String prefix = this.plugin.getDescription().getName().toLowerCase() + ".";
    // create the base permission
    final Permission base = new Permission(prefix + this.getName(), this.getMessage("permission-description"), PermissionDefault.OP);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
  }

}
