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

import java.util.ArrayList;
import java.util.List;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.avaje.ebean.EbeanServer;

@ConsoleCommand
public class RecentCommand extends AbstractCommand {

  public static final int DEFAULT_LIMIT = 5;

  /** The number of bans to return */
  private int count;

  private EbeanServer database;

  private ChoiceFormatter formatter;

  public RecentCommand(final BanHammer plugin) {
    super(plugin);
    this.database = plugin.getDatabase();
    this.formatter = new ChoiceFormatter(this.getLocalisation());
    this.formatter.setLimits(1, 2);
    this.formatter.setMessage(this, "header");
    this.formatter.setFormats(this.getLocalisation().getMessage(BanHammer.class, "one-ban"), this.getLocalisation().getMessage(BanHammer.class, "many-bans"));
  }

  public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final List<BanRecord> bans = BanRecord.getRecentBans(database, count);

    if (!bans.isEmpty()) {
      this.formatter.setArguments(bans.size());
      sender.sendMessage(this.formatter.getMessage());
      for (final BanRecord ban : bans) {
        final BanSummary summary = new BanSummary(this.getLocalisation(), ban);
        sender.sendMessage(summary.getHeader());
        sender.sendMessage(summary.getReason());
        sender.sendMessage(summary.getLength());
        if (ban.getType() == BanRecord.Type.TEMPORARY) {
          sender.sendMessage(summary.getExpiresAt());
        }
      }
    } else {
      sender.sendMessage(this.getLocalisation().getMessage(this, "no-bans"));
    }
  }

  public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      this.count = DEFAULT_LIMIT;
    } else {
      try {
        this.count = Integer.parseInt(arguments[0]);
      } catch (final NumberFormatException exception) {
        this.count = DEFAULT_LIMIT;
      }
    }
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments) {
    List<String> list = new ArrayList<String>();
    return list;
  }

}
