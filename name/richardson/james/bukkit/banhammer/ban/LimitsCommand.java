/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * LimitsCommand.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with BanHammer.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.ban;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.util.Time;
import name.richardson.james.bukkit.util.command.CommandArgumentException;
import name.richardson.james.bukkit.util.command.CommandPermissionException;
import name.richardson.james.bukkit.util.command.CommandUsageException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class LimitsCommand extends PlayerCommand {

  public static final String NAME = "limits";
  public static final String DESCRIPTION = "Check what ban limits are available to you.";
  public static final String PERMISSION_DESCRIPTION = "Allow users to check what ban limits are available for them to use.";
  public static final String USAGE = "";

  public static final Permission PERMISSION = new Permission("banhammer.limits", LimitsCommand.PERMISSION_DESCRIPTION, PermissionDefault.TRUE);
  
  private final BanHammer plugin;

  public LimitsCommand(final BanHammer plugin) {
    super(plugin, LimitsCommand.NAME, LimitsCommand.DESCRIPTION, LimitsCommand.USAGE, LimitsCommand.PERMISSION_DESCRIPTION, LimitsCommand.PERMISSION);
    this.plugin = plugin;
  }
  

  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    
    sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + "There are currently %d limits configured:", plugin.getBanLimits().size()));
    for (final Entry<String, Long> limit : plugin.getBanLimits().entrySet()) {
      ChatColor colour;
      if (sender.hasPermission("banhammer.ban." + limit.getKey())) {
        colour = ChatColor.GREEN;
      } else {
        colour = ChatColor.RED;
      }
      sender.sendMessage(String.format(colour + "- %s (%s)", limit.getKey(), Time.millisToLongDHMS(limit.getValue())));
    }

  }
  

}
