/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * ExportCommand.java is part of BanHammer.
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
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.OldBanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.command.PluginCommand;
import name.richardson.james.bukkit.utilities.internals.Logger;
import name.richardson.james.bukkit.utilities.persistence.SQLStorage;

@ConsoleCommand
public class ExportCommand extends PluginCommand {

  /** The logger for this class . */
  private final static Logger logger = new Logger(ExportCommand.class);

  /** A instance of the Bukkit server. */
  private final Server server;

  /** The database handler for this plugin. */
  private final SQLStorage database;

  public ExportCommand(final BanHammer plugin) {
    super(plugin);
    logger.setPrefix("[BanHammer] ");
    this.server = plugin.getServer();
    this.database = plugin.getSQLStorage();
    this.registerPermissions();
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.utilities.command.Command#execute(org.bukkit
   * .command.CommandSender)
   */
  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    int exported = 0;
    for (final Object record : this.database.list(PlayerRecord.class)) {
      final PlayerRecord playerRecord = (PlayerRecord) record;
      if (playerRecord.isBanned()) {
        final OfflinePlayer player = this.server.getOfflinePlayer(playerRecord.getName());
        player.setBanned(true);
        exported++;
      }
    }
    logger.info(this.getFormattedLogMessage(sender.getName(), exported));
    sender.sendMessage(this.getFormattedResponseMessage(sender.getName(), exported));
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.utilities.command.Command#parseArguments(java
   * .lang.String[], org.bukkit.command.CommandSender)
   */
  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    return;
  }

  /**
   * Gets the formatted result message for use in logging in the server log.
   * 
   * @param name the name of the CommandSender
   * @param bans the number of bans which were exported
   * @return the formatted log message
   */
  private String getFormattedLogMessage(final String name, final int bans) {
    final Object[] arguments = { bans, name };
    final double[] limits = { 0, 1, 2 };
    final String[] formats = { this.getMessage("no-bans"), this.getMessage("one-ban"), this.getMessage("many-bans") };
    return this.getChoiceFormattedMessage("summary-result", arguments, formats, limits);
  }

  /**
   * Gets the formatted response message for use in replying to the
   * CommandSender.
   * 
   * @param name the name of the CommandSender
   * @param bans the number of bans which were exported
   * @return the formatted response message
   */
  private String getFormattedResponseMessage(final String name, final int bans) {
    final Object[] arguments = { bans, name };
    final double[] limits = { 0, 1, 2 };
    final String[] formats = { this.getMessage("no-bans"), this.getMessage("one-ban"), this.getMessage("many-bans") };
    return this.getChoiceFormattedMessage("response-message", arguments, formats, limits);
  }

  private void registerPermissions() {
    final String prefix = this.plugin.getDescription().getName().toLowerCase() + ".";
    // create the base permission
    final Permission base = new Permission(prefix + this.getName(), this.getMessage("exportcommand-permission-description"), PermissionDefault.OP);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
  }

}
