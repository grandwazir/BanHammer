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
import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanCommand;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class ExportCommand extends PlayerCommand {

  public static final String NAME = "export";
  public static final String DESCRIPTION = "Export bans to banned-players.txt";
  public static final String PERMISSION_DESCRIPTION = "Allow users to export bans to banned-players.txt";
  public static final String USAGE = "";

  public static final Permission PERMISSION = new Permission("banhammer.export", ExportCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private final Server server;
  
  public ExportCommand(final BanHammer plugin) {
    super(plugin, BanCommand.NAME, BanCommand.DESCRIPTION, BanCommand.USAGE, BanCommand.PERMISSION_DESCRIPTION, BanCommand.PERMISSION);
    this.server = plugin.getServer();
  }

  @Override
  public void execute(final CommandSender sender, Map<String, Object> arguments) {
    final List<BanRecord> bans = BanRecord.list();
    for (BanRecord ban : BanRecord.list()) {
      OfflinePlayer player = server.getOfflinePlayer(ban.getPlayer());
      player.setBanned(true);
    }
    logger.info(String.format(BanHammer.getMessage("ban-export"), sender.getName()));
    sender.sendMessage(String.format(ChatColor.GREEN + BanHammer.getMessage("bans-exported"), bans.size()));
  }

  @Override
  public Map<String, Object> parseArguments(List<String> arguments) {
    return null;
  }

}
