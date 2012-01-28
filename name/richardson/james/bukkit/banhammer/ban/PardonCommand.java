/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * PardonCommand.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with BanHammer.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.ban;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.BanHandler;
import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.banhammer.management.ImportCommand;
import name.richardson.james.bukkit.util.command.CommandArgumentException;
import name.richardson.james.bukkit.util.command.CommandPermissionException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class PardonCommand extends PlayerCommand {

  public static final String NAME = "pardon";
  public static final String DESCRIPTION = "Pardon a player";
  public static final String PERMISSION_DESCRIPTION = "Allow users to pardon a player";
  public static final String USAGE = "<name>";

  public static final Permission PERMISSION = new Permission("banhammer.pardon", ImportCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private final BanHandler handler;
  private final BanHammer plugin;

  public PardonCommand(final BanHammer plugin) {
    super(plugin, PardonCommand.NAME, PardonCommand.DESCRIPTION, PardonCommand.USAGE, PardonCommand.PERMISSION_DESCRIPTION, PardonCommand.PERMISSION);
    this.handler = plugin.getHandler(PardonCommand.class);
    this.plugin = plugin;
    this.registerAdditionalPermissions();
  }

  private void registerAdditionalPermissions() {
    final Permission wildcard = new Permission(PardonCommand.PERMISSION.getName() + ".*", "Allow a user to pardon all bans.", PermissionDefault.OP);
    this.plugin.addPermission(wildcard, true);
    String permissionNode = PardonCommand.PERMISSION.getName() + "." + "own";
    String description = "Allow users to only pardon bans made by themselves.";
    Permission permission = new Permission(permissionNode, description, PermissionDefault.OP);
    permission.addParent(wildcard, true);
    this.plugin.addPermission(permission, false);
    permissionNode = PardonCommand.PERMISSION.getName() + "." + "all";
    description = "Allow users to pardon all bans regardless of who issued it";
    permission = new Permission(permissionNode, description, PermissionDefault.OP);
    permission.addParent(wildcard, true);
    this.plugin.addPermission(permission, false);
  }

  @Override
  public void execute(final CommandSender sender, Map<String, Object> arguments) throws CommandPermissionException {
    final String playerName = (String) arguments.get("playerName");
    final String senderName = sender.getName();
    final BanRecord record = handler.getPlayerBan(playerName);
    
    if (record != null) {
      if (record.getCreatedBy().equalsIgnoreCase(senderName) || sender.hasPermission(PardonCommand.PERMISSION.getName() + "." + "all")) {
        this.handler.pardonPlayer(playerName, senderName, true);
        sender.sendMessage(String.format(ChatColor.GREEN + "%s has been pardoned.", playerName));
      } else {
        throw new CommandPermissionException(senderName, plugin.getServer().getPluginManager().getPermission(PardonCommand.PERMISSION.getName() + "." + "all"));
      }
    } else {
      sender.sendMessage(String.format(ChatColor.YELLOW + "%s is not banned.", playerName));
    }
  }

  @Override
  public Map<String, Object> parseArguments(List<String> arguments) throws CommandArgumentException {
    Map<String, Object> m = new HashMap<String, Object>();

    try {
      m.put("playerName", arguments.get(0));
    } catch (IndexOutOfBoundsException e) {
      throw new CommandArgumentException("You must specify a valid player name", "You need to type the whole name.");
    }

    return m;
  }

}
