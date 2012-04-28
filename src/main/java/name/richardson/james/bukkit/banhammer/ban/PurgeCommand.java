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

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.BanHandler;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.PluginCommand;
import name.richardson.james.bukkit.utilities.internals.Logger;

public class PurgeCommand extends PluginCommand {

  /** The logger for this class . */
  private final static Logger logger = new Logger(PurgeCommand.class);
  
  /** A reference to the BanHammer API. */
  private final BanHandler handler;
  
  /** A instance of the Bukkit server. */
  private final Server server;

  /** The player from whom we are going to purge bans */
  private OfflinePlayer player;

  public PurgeCommand(final BanHammer plugin) {
    super(plugin);
    handler = plugin.getHandler(PurgeCommand.class);
    server = plugin.getServer();
    this.registerPermissions();
  }

  
  private void registerPermissions() {
    final String prefix = this.plugin.getDescription().getName().toLowerCase() + ".";
    // create the base permission
    final Permission base = new Permission(prefix + this.getName(), this.getMessage("purgecommand-permission-description"), PermissionDefault.OP);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
  }


  public void execute(CommandSender sender) throws CommandPermissionException, CommandUsageException {
    final int i = handler.removePlayerBans(handler.getPlayerBans(player.getName()));
    sender.sendMessage(this.getFormattedResponseMessage(i));
    logger.info(this.getFormattedSummaryMessage(i, sender.getName()));
  }
  

  private String getFormattedSummaryMessage(int total, String name) {
    final Object[] arguments = { total, player.getName(), name };
    final double[] limits = { 0, 1, 2 };
    final String[] formats = { this.getMessage("no-bans").toLowerCase(), this.getMessage("one-ban").toLowerCase(), this.getMessage("many-bans").toLowerCase() };
    return this.getChoiceFormattedMessage("purgecommand-summary-result", arguments, formats, limits);
  }

  private String getFormattedResponseMessage(int total) {
    final Object[] arguments = { total, player.getName() };
    final double[] limits = { 0, 1, 2 };
    final String[] formats = { this.getMessage("no-bans").toLowerCase(), this.getMessage("one-ban").toLowerCase(), this.getMessage("many-bans").toLowerCase() };
    return this.getChoiceFormattedMessage("purgecommand-response-message", arguments, formats, limits);
  }

  public void parseArguments(String[] arguments, CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getMessage("must-specify-a-player"), this.getMessage("name-autocompletion"));
    } else {
      this.player = matchPlayer(arguments[0]);
    }
    
  }
  
  private OfflinePlayer matchPlayer(final String name) {
    final List<Player> players = this.server.matchPlayer(name);
    if (players.isEmpty()) {
      return server.getOfflinePlayer(name);
    } else {
      return players.get(0);
    }
  }

}
