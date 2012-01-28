/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * ReloadCommand.java is part of BanHammer.
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

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.ban.RecentCommand;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class ReloadCommand extends PlayerCommand {
  
  public static final String NAME = "reload";
  public static final String DESCRIPTION = "Reload the ban cache.";
  public static final String PERMISSION_DESCRIPTION = "Allow users to reload the ban cache";
  public static final String USAGE = "";

  public static final Permission PERMISSION = new Permission("banhammer.reload", RecentCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private final BanHammer plugin;
  
  public ReloadCommand(final BanHammer plugin) {
    super(plugin, ReloadCommand.NAME, ReloadCommand.DESCRIPTION, ReloadCommand.USAGE, ReloadCommand.PERMISSION_DESCRIPTION, RecentCommand.PERMISSION);
    this.plugin = plugin;
  }
  
  @Override
  public void execute(final CommandSender sender, Map<String, Object> arguments) {
    String senderName = sender.getName();
    this.plugin.reloadBannedPlayers();
    logger.info(String.format("%s has refreshed the banned player list", senderName));
    sender.sendMessage(String.format(ChatColor.GREEN + "Loaded %d banned name(s) into memory.", this.plugin.getBannedPlayers().size()));
  }

}
