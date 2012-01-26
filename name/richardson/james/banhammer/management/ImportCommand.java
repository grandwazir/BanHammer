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
package name.richardson.james.banhammer.management;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.ban.BanHandler;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class ImportCommand extends PlayerCommand {

  public static final String NAME = "import";
  public static final String DESCRIPTION = "Import bans from banned-players.txt";
  public static final String PERMISSION_DESCRIPTION = "Allow users to import bans from banned-players.txt";
  public static final String USAGE = "";

  public static final Permission PERMISSION = new Permission("banhammer.import", ImportCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private final BanHandler banHandler;
  private final Server server;
  
  public ImportCommand(final BanHammer plugin) {
    super(plugin, ImportCommand.NAME, ImportCommand.DESCRIPTION, ImportCommand.USAGE, ImportCommand.PERMISSION_DESCRIPTION, ImportCommand.PERMISSION);
    this.banHandler = plugin.getHandler();
    this.server = plugin.getServer();
  }

  @Override
  public void execute(final CommandSender sender, Map<String, Object> arguments) {
    final int totalBans = server.getBannedPlayers().size();
    int imported = 0;
    final long expiryTime = 0;
    final String reason = BanHammer.getMessage("default-import-reason");
    final String senderName = sender.getName();
    
    for (OfflinePlayer player : server.getBannedPlayers()) {
      if (!this.banHandler.banPlayer(player.getName(), senderName, reason, expiryTime, false)) {
        logger.info(String.format(BanHammer.getMessage("failed-import"), player.getName()));
      } else {
        imported++;
      }
      player.setBanned(false);
    }
    
    logger.info(String.format(BanHammer.getMessage("ban-import"), sender.getName(), imported));
    sender.sendMessage(String.format(ChatColor.GREEN + BanHammer.getMessage("bans-imported"), imported, totalBans));
  }

  @Override
  public Map<String, Object> parseArguments(List<String> arguments) {
    return null;
  }

}
