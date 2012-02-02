/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * ExportCommand.java is part of BanHammer.
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

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.banhammer.DatabaseHandler;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class ExportCommand extends PlayerCommand {

  public static final String NAME = "export";
  public static final String DESCRIPTION = "Export bans to banned-players.txt";
  public static final String PERMISSION_DESCRIPTION = "Allow users to export bans to banned-players.txt";
  public static final String USAGE = "";

  public static final Permission PERMISSION = new Permission("banhammer.export", ExportCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);

  private final Server server;
  private final DatabaseHandler database;

  public ExportCommand(final BanHammer plugin) {
    super(plugin, ExportCommand.NAME, ExportCommand.DESCRIPTION, ExportCommand.USAGE, ExportCommand.PERMISSION_DESCRIPTION, ExportCommand.PERMISSION);
    this.server = plugin.getServer();
    this.database = plugin.getDatabaseHandler();
  }

  @Override
  public void execute(final CommandSender sender, final Map<String, Object> arguments) {
    int exported = 0;
    for (final Object record : this.database.list(BanRecord.class)) {
      final BanRecord ban = (BanRecord) record;
      final OfflinePlayer player = this.server.getOfflinePlayer(ban.getPlayer());
      player.setBanned(true);
      exported++;
    }
    this.logger.info(String.format("%s has exported all bans to banned-players.txt", sender.getName()));
    sender.sendMessage(String.format(ChatColor.GREEN + "%d ban(s) exported.", exported));
  }

}
