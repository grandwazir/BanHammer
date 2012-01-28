/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * RecentCommand.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with BanHammer.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.ban;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.banhammer.DatabaseHandler;
import name.richardson.james.bukkit.util.Time;
import name.richardson.james.bukkit.util.command.CommandArgumentException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class RecentCommand extends PlayerCommand {

  public static final String NAME = "recent";
  public static final String DESCRIPTION = "Display a list of recent bans";
  public static final String PERMISSION_DESCRIPTION = "Allow users to display a list of recent bans";
  public static final String USAGE = "[limit]";

  public static final Permission PERMISSION = new Permission("banhammer.recent", RecentCommand.PERMISSION_DESCRIPTION, PermissionDefault.OP);
  
  private final DatabaseHandler database;
  
  public RecentCommand(final BanHammer plugin) {
    super(plugin, RecentCommand.NAME, RecentCommand.DESCRIPTION, RecentCommand.USAGE, RecentCommand.PERMISSION_DESCRIPTION, RecentCommand.PERMISSION);
    this.database = plugin.getDatabaseHandler();
  }

  @Override
  public void execute(final CommandSender sender, Map<String, Object> arguments) {
    int maxRows = (Integer) arguments.get("maxRows");
    List<BanRecord> bans = BanRecord.findRecent(database, maxRows);
    if (bans.isEmpty())
      sender.sendMessage(ChatColor.YELLOW + "There are no bans on record.");
    else {
      sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + "Displaying last %d ban(s):", bans.size()));
      for (BanRecord ban : bans) {
        sendBanDetail(sender, ban);
      }
    }
  }

  @Override
  public Map<String, Object> parseArguments(List<String> arguments) throws CommandArgumentException {
    Map<String, Object> m = new HashMap<String, Object>();

    m.put("maxRows", 3);
    
    if (!arguments.isEmpty()) {
      try {
        Integer.parseInt(arguments.get(0));
        m.put("maxRows", arguments.get(0));
      } catch (NumberFormatException exception) {
        throw new CommandArgumentException("You must provide a valid number", "The default amount is 3");
      }
    }
      
    return m;
  }
  
  protected void sendBanDetail(CommandSender sender, BanRecord ban) {
    Date createdDate = new Date(ban.getCreatedAt());
    DateFormat dateFormat = new SimpleDateFormat("MMM d");
    String createdAt = dateFormat.format(createdDate);
    sender.sendMessage(String.format(ChatColor.RED + "%s banned by %s on %s.", ban.getPlayer(), ban.getCreatedBy(), createdAt));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- Reason: %s.", ban.getReason()));
    switch (ban.getType()) {
      case PERMENANT:
        sender.sendMessage(ChatColor.YELLOW + "- Length: Permanent.");
        break;
      case TEMPORARY:
        Date expiryDate = new Date(ban.getExpiresAt());
        DateFormat expiryDateFormat = new SimpleDateFormat("MMM d H:mm a ");
        String expiryDateString = expiryDateFormat.format(expiryDate) + "(" + Calendar.getInstance().getTimeZone().getDisplayName() + ")";
        Long banTime = ban.getExpiresAt() - ban.getCreatedAt();
        sender.sendMessage(String.format(ChatColor.YELLOW + "- Length: %s", Time.millisToLongDHMS(banTime)));
        sender.sendMessage(String.format(ChatColor.YELLOW + "- Expires on: %s", expiryDateString));
        break;
    }
  }

}
