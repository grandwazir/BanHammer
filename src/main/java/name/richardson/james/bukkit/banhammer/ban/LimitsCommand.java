/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * LimitsCommand.java is part of BanHammer.
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

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.command.PluginCommand;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;

@ConsoleCommand
public class LimitsCommand extends PluginCommand {

  private final BanHammer plugin;

  public LimitsCommand(final BanHammer plugin) {
    super(plugin);
    this.plugin = plugin;
    this.registerPermissions();
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    sender.sendMessage(this.getFormattedMessageHeader(this.plugin.getBanLimits().size()));
    for (final Entry<String, Long> limit : this.plugin.getBanLimits().entrySet()) {
      ChatColor colour;
      if (sender.hasPermission("banhammer.ban." + limit.getKey())) {
        colour = ChatColor.GREEN;
      } else {
        colour = ChatColor.RED;
      }
      final Object[] arguments = { limit.getKey(), TimeFormatter.millisToLongDHMS(limit.getValue()) };
      sender.sendMessage(colour + this.getSimpleFormattedMessage("detail", arguments));
    }
  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    return;
  }

  private String getFormattedMessageHeader(final int size) {
    final Object[] arguments = { size };
    final double[] limits = { 0, 1, 2 };
    final String[] formats = { this.getMessage("no-limits"), this.getMessage("one-limit"), this.getMessage("many-limits") };
    return this.getChoiceFormattedMessage("header", arguments, formats, limits);
  }

  private void registerPermissions() {
    final String prefix = this.plugin.getDescription().getName().toLowerCase() + ".";
    // create the base permission
    final Permission base = new Permission(prefix + this.getName(), this.getMessage("permission-description"), PermissionDefault.TRUE);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
  }

}
