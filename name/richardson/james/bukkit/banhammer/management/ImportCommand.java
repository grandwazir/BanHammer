/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * CheckCommand.java is part of BanHammer.
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
package name.richardson.james.bukkit.banhammer.management;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.BanHandler;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class ImportCommand extends PlayerCommand {

  public static final String NAME = "import";
  public static final String DESCRIPTION = "Import bans from banned-players.txt";
  public static final String PERMISSION_DESCRIPTION = "Allow users to import bans from banned-players.txt";
  public static final String USAGE = "";

  public static final Permission PERMISSION = new Permission("banhammer.import", ImportCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private final BanHandler handler;
  private final Server server;
  
  public ImportCommand(final BanHammer plugin) {
    super(plugin, ImportCommand.NAME, ImportCommand.DESCRIPTION, ImportCommand.USAGE, ImportCommand.PERMISSION_DESCRIPTION, ImportCommand.PERMISSION);
    this.handler = plugin.getHandler(ImportCommand.class);
    this.server = plugin.getServer();
  }

  @Override
  public void execute(final CommandSender sender, Map<String, Object> arguments) {
    final int totalBans = server.getBannedPlayers().size();
    int imported = 0;
    final long expiryTime = 0;
    final String reason = "Imported from banned-players.txt";
    final String senderName = sender.getName();
    
    for (OfflinePlayer player : server.getBannedPlayers()) {
      if (!this.handler.banPlayer(player.getName(), senderName, reason, expiryTime, false)) {
        logger.warning(String.format("Failed to import ban for %s because they are already banned by BanHammer.", player.getName()));
      } else {
        imported++;
      }
      player.setBanned(false);
    }
    
    logger.info(String.format("%s has imported %d bans from banned-players.txt.", sender.getName(), imported));
    sender.sendMessage(String.format(ChatColor.YELLOW + "%d out of %d ban(s) were imported.", imported, totalBans));
  }

  @Override
  public Map<String, Object> parseArguments(List<String> arguments) {
    return null;
  }

}
