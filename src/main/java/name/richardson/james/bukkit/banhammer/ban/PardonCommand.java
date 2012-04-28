/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * PardonCommand.java is part of BanHammer.
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
import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.PluginCommand;
import name.richardson.james.bukkit.utilities.internals.Logger;

public class PardonCommand extends PluginCommand {

  /** The logger for this class . */
  private final static Logger logger = new Logger(PardonCommand.class);
  
  /** A reference to the BanHammer API. */
  private final BanHandler handler;
  
  /** A instance of the Bukkit server. */
  private final Server server;
  
  /** The player who is going to be pardoned */
  private OfflinePlayer player;

  public PardonCommand(final BanHammer plugin) {
    super(plugin);
    handler = plugin.getHandler(PardonCommand.class);
    this.plugin = plugin;
    this.server = plugin.getServer();
    this.registerPermissions();
  }
  
  private void registerPermissions() {
    final String prefix = plugin.getDescription().getName().toLowerCase() + ".";
    final String wildcardDescription = String.format(plugin.getMessage("wildcard-permission-description"), this.getName());
    // create the wildcard permission
    Permission wildcard = new Permission(prefix + this.getName() + ".*", wildcardDescription, PermissionDefault.OP);
    wildcard.addParent(plugin.getRootPermission(), true);
    this.addPermission(wildcard);
    // create the base permission
    Permission base = new Permission(prefix + this.getName(), plugin.getMessage("pardoncommand-permission-description"), PermissionDefault.OP);
    base.addParent(wildcard, true);
    this.addPermission(base);
    // add ability to pardon your own bans
    Permission own = new Permission(prefix + this.getName() + "." + plugin.getMessage("pardoncommand-own-permission-name"), plugin.getMessage("pardoncommand-own-permission-name"), PermissionDefault.OP);
    own.addParent(base, true);
    this.addPermission(own);
    // add ability to pardon the bans of others
    Permission others = new Permission(prefix + this.getName() + "." + plugin.getMessage("pardoncommand-others-permission-name"), plugin.getMessage("pardoncommand-others-permission-name"), PermissionDefault.OP);
    others.addParent(base, true);
    this.addPermission(others);
  }

  public void execute(CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final BanRecord record = handler.getPlayerBan(player.getName());

    if (record != null) {
      
      if (sender.hasPermission(this.getPermission(3)) && !record.getCreatedBy().equalsIgnoreCase(sender.getName())) {
        handler.pardonPlayer(player.getName(), sender.getName(), true);
        player.setBanned(false);
        sender.sendMessage(this.getSimpleFormattedMessage("pardoncommand-response-message", player.getName()));
        server.broadcast(this.getFormattedBroadcastMessage(sender.getName()), "banhammer.notify");
        logger.info(this.getFormattedSummaryMessage(sender.getName()));
        return;
      } else if (!record.getCreatedBy().equalsIgnoreCase(sender.getName())) {
        throw new CommandPermissionException(this.getMessage("pardoncommand-cannot-pardon-others-bans"), this.getPermission(3));
      }
      
      if (sender.hasPermission(this.getPermission(2)) && record.getCreatedBy().equalsIgnoreCase(sender.getName())) {
        handler.pardonPlayer(player.getName(), sender.getName(), true);
        player.setBanned(false);
        logger.info(this.getFormattedSummaryMessage(sender.getName()));
        return;
      } else if (record.getCreatedBy().equalsIgnoreCase(sender.getName())) {
        throw new CommandPermissionException(this.getMessage("pardoncommand-cannot-pardon-own-bans"), this.getPermission(3));
      }
  
    } else {
      sender.sendMessage(this.getSimpleFormattedMessage("player-is-not-banned", player.getName()));
    }
    
  }

  public void parseArguments(String[] arguments, CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getMessage("must-specify-a-player"), this.getMessage("name-autocompletion"));
    } else {
      this.player = matchPlayer(arguments[0]);
    }
    
  }
  
  private String getFormattedSummaryMessage(String name) {
    final Object[] arguments = { player.getName(), name };
    return this.getSimpleFormattedMessage("pardoncommand-summary-result", arguments);
  }

  private String getFormattedBroadcastMessage(String name) {
    final Object[] arguments = {player.getName(), name };
    return this.getSimpleFormattedMessage("pardoncommand-broadcast-message", arguments);
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
