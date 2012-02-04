/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * ImportCommand.java is part of BanHammer.
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

package name.richardson.james.bukkit.banhammer.management;

import java.util.HashMap;
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
  public static final String USAGE = "[reason]";

  public static final Permission PERMISSION = new Permission("banhammer.import", ImportCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);

  private final BanHandler handler;
  private final Server server;

  public ImportCommand(final BanHammer plugin) {
    super(plugin, ImportCommand.NAME, ImportCommand.DESCRIPTION, ImportCommand.USAGE, ImportCommand.PERMISSION_DESCRIPTION, ImportCommand.PERMISSION);
    this.handler = plugin.getHandler(ImportCommand.class);
    this.server = plugin.getServer();
  }

  @Override
  public void execute(final CommandSender sender, final Map<String, Object> arguments) {
    final int totalBans = this.server.getBannedPlayers().size();
    int imported = 0;
    final long expiryTime = 0;
    final String reason = (String) arguments.get("reason");
    final String senderName = sender.getName();

    for (final OfflinePlayer player : this.server.getBannedPlayers()) {
      if (!this.handler.banPlayer(player.getName(), senderName, reason, expiryTime, false)) {
        this.logger.warning(String.format("Failed to import ban for %s because they are already banned by BanHammer.", player.getName()));
      } else {
        imported++;
      }
      player.setBanned(false);
    }

    this.logger.info(String.format("%s has imported %d bans from banned-players.txt.", sender.getName(), imported));
    sender.sendMessage(String.format(ChatColor.YELLOW + "%d out of %d ban(s) were imported.", imported, totalBans));
  }

  @Override
  public Map<String, Object> parseArguments(final List<String> arguments) {
    final Map<String, Object> m = new HashMap<String, Object>();
    m.put("reason", this.combineString(arguments, " "));
    return m;
  }
  
  protected String combineString(final List<String> arguments, final String seperator) {
    final StringBuilder reason = new StringBuilder();
    try {
      for (final String argument : arguments) {
        reason.append(argument);
        reason.append(seperator);
      }
      reason.deleteCharAt(reason.length() - seperator.length());
      return reason.toString();
    } catch (final StringIndexOutOfBoundsException e) {
      return "No reason provided";
    }
  }

}
