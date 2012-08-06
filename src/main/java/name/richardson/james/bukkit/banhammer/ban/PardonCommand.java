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

import com.avaje.ebean.EbeanServer;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;

@ConsoleCommand
public class PardonCommand extends AbstractCommand {

  private Permission own;
  
  private Permission others;
  
  /** A reference to the BanHammer API. */
  private final BanHandler handler;

  /** A instance of the Bukkit server. */
  private final Server server;

  /** The player who is going to be pardoned */
  private OfflinePlayer player;

  private EbeanServer database;

  public PardonCommand(final BanHammer plugin) {
    super(plugin, true);
    this.database = plugin.getDatabase();
    this.handler = plugin.getHandler();
    this.server = plugin.getServer();
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {

    if (handler.isPlayerBanned(player.getName())) {
      final BanRecord banRecord = PlayerRecord.find(database, player.getName()).getActiveBan();
      
      if (sender.hasPermission(others) && !banRecord.getCreator().getName().equalsIgnoreCase(sender.getName())) {
        this.handler.pardonPlayer(this.player.getName(), sender.getName(), true);
        this.player.setBanned(false);
        sender.sendMessage(this.getLocalisation().getMessage(this, "pardoned", this.player.getName()));
        return;
      } else if (!banRecord.getCreator().getName().equalsIgnoreCase(sender.getName())) {
        throw new CommandPermissionException(this.getLocalisation().getMessage(this, "cannot-pardon-others-bans"), others);
      }

      if (sender.hasPermission(own) && banRecord.getCreator().getName().equalsIgnoreCase(sender.getName())) {
        this.handler.pardonPlayer(this.player.getName(), sender.getName(), true);
        this.player.setBanned(false);
        sender.sendMessage(this.getLocalisation().getMessage(this, "pardoned", this.player.getName()));
        return;
      } else if (banRecord.getCreator().getName().equalsIgnoreCase(sender.getName())) {
        throw new CommandPermissionException(this.getLocalisation().getMessage(this, "cannot-pardon-own-bans"), own);
      }

    } else {
      sender.sendMessage(this.getLocalisation().getMessage(this, "player-is-not-banned", this.player.getName()));
    }

  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getLocalisation().getMessage(this, "must-specify-a-player"), null);
    } else {
      this.player = this.matchPlayer(arguments[0]);
    }
  }

  private OfflinePlayer matchPlayer(final String name) {
    return this.server.getOfflinePlayer(name);
  }

  protected void registerPermissions(boolean wildcard) {
    super.registerPermissions(wildcard);
    final String prefix = this.getPermissionManager().getRootPermission().getName().replace("*", "");
    own = new Permission(prefix + this.getName() + "." + this.getLocalisation().getMessage(this, "own-permission-name"), this.getLocalisation().getMessage(this, "own-permission-description"), PermissionDefault.OP);
    own.addParent(this.getRootPermission(), true);
    this.getPermissionManager().addPermission(own, false);
    // add ability to pardon the bans of others
    others = new Permission(prefix + this.getName() + "." + this.getLocalisation().getMessage(this, "others-permission-name"), this.getLocalisation().getMessage(this, "others-permission-description"), PermissionDefault.OP);
    others.addParent(this.getRootPermission(), true);
    this.getPermissionManager().addPermission(others, false);
  }

}
