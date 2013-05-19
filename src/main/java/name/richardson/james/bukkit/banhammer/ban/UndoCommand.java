/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * UndoCommand.java is part of BanHammer.
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;

@ConsoleCommand
public class UndoCommand extends AbstractCommand {

  private final long undoTime = 60000;
  
  private EbeanServer database;

  private Server server;

  private String playerName;

  public UndoCommand(BanHammer plugin) {
    super(plugin);
    this.database = plugin.getDatabase();
    this.server = plugin.getServer();
    this.registerPermissions();
  }

  public void execute(final CommandSender sender) throws CommandPermissionException, CommandUsageException {
    final PlayerRecord playerRecord = PlayerRecord.find(database, playerName);
    final List<BanRecord> playerBans = playerRecord.getCreatedBans();
    final ListIterator<BanRecord> playerBansIter = playerBans.listIterator(playerBans.size());
    final Timestamp undoTime = new Timestamp(System.currentTimeMillis() - this.undoTime);
    final boolean own = this.getPermissionManager().hasPlayerPermission(sender, this.getPermissions().get(1));
    final boolean others = this.getPermissionManager().hasPlayerPermission(sender, this.getPermissions().get(2));
    final boolean unrestricted = this.getPermissionManager().hasPlayerPermission(sender, this.getPermissions().get(3));

    while(playerBansIter.hasPrevious()) {
      final BanRecord ban = playerBansIter.previous();
      if (own && (ban.getCreator().getName().equalsIgnoreCase(sender.getName()))) {
        if (undoTime.before(ban.getCreatedAt()) || unrestricted) {
          this.removeBan(ban);
          sender.sendMessage(this.getLocalisation().getMessage(this, "success-own", ban.getPlayer().getName()));
          return;
        } else {
          throw new CommandUsageException(this.getLocalisation().getMessage(this, "time-expired"));
        }
      }
      if (others && (!ban.getCreator().getName().equalsIgnoreCase(sender.getName()))) {
        if (undoTime.before(ban.getCreatedAt()) || unrestricted) {
          this.removeBan(ban);
          sender.sendMessage(this.getLocalisation().getMessage(this, "success-others", ban.getPlayer().getName(), ban.getCreator().getName()));      
          return;
        } else {
          throw new CommandUsageException(this.getLocalisation().getMessage(this, "time-expired"));
        }
      }
    }
    
    sender.sendMessage(this.getLocalisation().getMessage(this, "no-ban-to-undo"));
    
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments) {
    List<String> list = new ArrayList<String>();
    Set<String> temp = new TreeSet<String>();
    if (arguments.length <= 1) {
      for (Player player : this.server.getOnlinePlayers()) {
        if (arguments.length < 1) {
          temp.add(player.getName());
        } else if (player.getName().startsWith(arguments[0])) {
          temp.add(player.getName());
        }         
        if (arguments[0].length() >= 3) {
          temp.addAll(BanRecord.getBannedPlayersThatStartWith(database, arguments[0]));
        }
      }
    }
    list.addAll(temp);
    return list;
  }
  
  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      this.playerName = sender.getName();
    } else {
      this.playerName = arguments[0];
    }
  }

  private void registerPermissions() {
    Permission own = this.getPermissionManager().createPermission(this, "own", PermissionDefault.TRUE, this.getPermissions().get(0), true);
    this.addPermission(own);
    Permission others = this.getPermissionManager().createPermission(this, "others", PermissionDefault.OP, this.getPermissions().get(0), true);
    this.addPermission(others);
    Permission unrestricted = this.getPermissionManager().createPermission(this, "unrestricted", PermissionDefault.OP, this.getPermissions().get(0), true);
    this.addPermission(unrestricted);
  }
  
  private void removeBan(BanRecord ban) {
    final Timestamp now = new Timestamp(System.currentTimeMillis());
    if (ban.getExpiresAt() == null || ban.getExpiresAt().after(now)) {
      BanHammerPlayerPardonedEvent event = new BanHammerPlayerPardonedEvent(ban, false);
      Bukkit.getServer().getPluginManager().callEvent(event);
    }
    BanRecord.deleteBan(this.database, ban);
  }
  
}
