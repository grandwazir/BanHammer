/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * PurgeCommand.java is part of BanHammer.
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
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class PurgeCommand extends PlayerCommand {
  
  public static final String NAME = "purge";
  public static final String DESCRIPTION = "Purge all bans associated with a player";
  public static final String PERMISSION_DESCRIPTION = "Allow users to purge all bans associated with a player";
  public static final String USAGE = "<name>";

  public static final Permission PERMISSION = new Permission("banhammer.purge", PurgeCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private final BanHandler handler;

  public PurgeCommand(final BanHammer plugin) {
    super(plugin, PurgeCommand.NAME, PurgeCommand.DESCRIPTION, PurgeCommand.USAGE, PurgeCommand.PERMISSION_DESCRIPTION, PurgeCommand.PERMISSION);
    this.handler = plugin.getHandler();
  }

  @Override
  public void execute(final CommandSender sender, Map<String, Object> arguments) {
    String playerName = (String) arguments.get("playerName");
    String senderName = sender.getName();
    
    List<BanRecord> bans = BanRecord.find(playerName);
    if (bans.isEmpty())
      sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.getMessage("ban-history-none"), playerName));
    else {
      String banTotal = Integer.toString(bans.size());
      BanRecord.destroy(bans);
      logger.info(String.format(BanHammer.getMessage("player-bans-purged"), senderName, playerName));
      sender.sendMessage(String.format(ChatColor.GREEN + BanHammer.getMessage("bans-purged"), banTotal, playerName));
    }
  }

  @Override
  public Map<String, Object> parseArguments(List<String> arguments) {
    Map<String, Object> m = new HashMap<String, Object>();

    try {
      m.put("playerName", arguments.get(0));
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException();
    }

    return m;
  }

}
