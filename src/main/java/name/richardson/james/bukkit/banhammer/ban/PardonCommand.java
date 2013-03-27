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

import java.util.ArrayList;
import java.util.List;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.avaje.ebean.EbeanServer;

@ConsoleCommand
public class PardonCommand extends AbstractCommand {

  /** A reference to the BanHammer API. */
  private final BanHandler handler;

  /** A instance of the Bukkit server. */
  private final Server server;

  /** The player who is going to be pardoned */
  private OfflinePlayer player;

  private EbeanServer database;

  public PardonCommand(final BanHammer plugin) {
    super(plugin);
    this.database = plugin.getDatabase();
    this.handler = plugin.getHandler();
    this.server = plugin.getServer();
    this.registerPermissions();
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {

    if (handler.isPlayerBanned(player.getName())) {
      final BanRecord banRecord = PlayerRecord.find(database, player.getName()).getActiveBan();

      if (sender.hasPermission(this.getPermissions().get(2)) && !banRecord.getCreator().getName().equalsIgnoreCase(sender.getName())) {
        this.handler.pardonPlayer(this.player.getName(), sender.getName(), true);
        this.player.setBanned(false);
        sender.sendMessage(this.getLocalisation().getMessage(this, "pardoned", this.player.getName()));
        return;
      } else if (!banRecord.getCreator().getName().equalsIgnoreCase(sender.getName())) {
        throw new CommandPermissionException(this.getLocalisation().getMessage(this, "cannot-pardon-others-bans"), this.getPermissions().get(2));
      }

      if (sender.hasPermission(this.getPermissions().get(1)) && banRecord.getCreator().getName().equalsIgnoreCase(sender.getName())) {
        this.handler.pardonPlayer(this.player.getName(), sender.getName(), true);
        this.player.setBanned(false);
        sender.sendMessage(this.getLocalisation().getMessage(this, "pardoned", this.player.getName()));
        return;
      } else if (banRecord.getCreator().getName().equalsIgnoreCase(sender.getName())) {
        throw new CommandPermissionException(this.getLocalisation().getMessage(this, "cannot-pardon-own-bans"), this.getPermissions().get(1));
      }

    } else {
      sender.sendMessage(this.getLocalisation().getMessage(this, "player-is-not-banned", this.player.getName()));
    }

  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      throw new CommandArgumentException(this.getLocalisation().getMessage(BanHammer.class, "must-specify-a-player"), null);
    } else {
      this.player = this.matchPlayer(arguments[0]);
    }
  }

  private OfflinePlayer matchPlayer(final String name) {
    return this.server.getOfflinePlayer(name);
  }

  private void registerPermissions() {
    Permission own = this.getPermissionManager().createPermission(this, "own", PermissionDefault.OP, this.getPermissions().get(0), true);
    this.addPermission(own);
    Permission others = this.getPermissionManager().createPermission(this, "others", PermissionDefault.OP, this.getPermissions().get(0), true);
    this.addPermission(others);
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments) {
    List<String> list = new ArrayList<String>();
    if (arguments.length <= 1) {
      if (arguments[0].length() >= 3) {
        list.addAll(BanRecord.getBannedPlayersThatStartWith(database, arguments[0]));
      }
    }
    return list;
  }

}
