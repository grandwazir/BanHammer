/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * RecentCommand.java is part of BanHammer.
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

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.banhammer.DatabaseHandler;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.PluginCommand;

public class RecentCommand extends PluginCommand {

  public static final int DEFAULT_LIMIT = 5;

  /** A reference to the database handler */
  private final DatabaseHandler database;

  /** The number of bans to return */
  private int count;

  public RecentCommand(final BanHammer plugin) {
    super(plugin);
    this.database = plugin.getDatabaseHandler();
    this.registerPermissions();
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final List<BanRecord> bans = BanRecord.findRecent(this.database, this.count);
    if (bans.size() != 0) {
      sender.sendMessage(this.getFormattedMessageHeader(bans.size()));
      for (final BanRecord ban : bans) {
        final BanSummary summary = new BanSummary(this.plugin, ban);
        sender.sendMessage(summary.getHeader());
        sender.sendMessage(summary.getReason());
        sender.sendMessage(summary.getLength());
        if (ban.getType() == BanRecord.Type.TEMPORARY) {
          sender.sendMessage(summary.getExpiresAt());
        }
      }
    } else {
      sender.sendMessage(this.getMessage("recentcommand-no-bans"));
    }
  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      this.count = DEFAULT_LIMIT;
    } else {
      try {
        this.count = Integer.parseInt(arguments[0]);
      } catch (final NumberFormatException exception) {
        throw new CommandArgumentException(this.getMessage("must-specify-valid-number"), this.getSimpleFormattedMessage("recentcommand-default", RecentCommand.DEFAULT_LIMIT));
      }
    }
  }

  private String getFormattedMessageHeader(final int size) {
    final Object[] arguments = { size };
    final double[] limits = { 1, 2 };
    final String[] formats = { this.getMessage("only-ban"), this.getMessage("many-bans") };
    return this.getChoiceFormattedMessage("recentcommand-header", arguments, formats, limits);
  }

  private void registerPermissions() {
    final String prefix = this.plugin.getDescription().getName().toLowerCase() + ".";
    // create the base permission
    final Permission base = new Permission(prefix + this.getName(), this.getMessage("recentcommand-permission-description"), PermissionDefault.OP);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
  }

}
