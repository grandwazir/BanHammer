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
package name.richardson.james.banhammer.management;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.ban.CachedList;
import name.richardson.james.banhammer.ban.RecentCommand;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class ReloadCommand extends PlayerCommand {
  
  public static final String NAME = "reload";
  public static final String DESCRIPTION = "Reload the ban cache.";
  public static final String PERMISSION_DESCRIPTION = "Allow users to reload the ban cache";
  public static final String USAGE = "";

  public static final Permission PERMISSION = new Permission("banhammer.reload", RecentCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  public ReloadCommand(final BanHammer plugin) {
    super(plugin, RecentCommand.NAME, RecentCommand.DESCRIPTION, RecentCommand.USAGE, RecentCommand.PERMISSION_DESCRIPTION, RecentCommand.PERMISSION);
  }
  
  @Override
  public void execute(final CommandSender sender, Map<String, Object> arguments) {
    String senderName = sender.getName();
    CachedList.getInstance().reload();
    String cacheSize = Integer.toString(CachedList.getInstance().size());
    logger.info(String.format(BanHammer.getMessage("cache-reloaded"), senderName));
    logger.info(String.format(BanHammer.getMessage("bans-loaded"), cacheSize));
    sender.sendMessage(String.format(ChatColor.GREEN + BanHammer.getMessage("cache-reloaded"), cacheSize));
  }

}
