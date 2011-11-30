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
package name.richardson.james.banhammer.ban;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.richardson.james.banhammer.BanHammerPlugin;
import name.richardson.james.banhammer.Command;
import name.richardson.james.banhammer.exceptions.NotEnoughArgumentsException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CheckCommand extends Command {

  private CachedList cache;

  public CheckCommand(final BanHammerPlugin plugin) {
    super(plugin);
    this.name = "check";
    this.description = "check to see if a player is banned or not";
    this.usage = "/bh check [name]";
    this.permission = "banhammer." + this.name;
    this.cache = CachedList.getInstance();
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) throws NotEnoughArgumentsException {
    String playerName = arguments.get("playerName");

    if (this.cache.contains(playerName)) {
      BanRecord ban = BanRecord.findFirst(playerName);
      if (ban.getType().equals(BanRecord.Type.PERMENANT)) {
        sender.sendMessage(String.format(ChatColor.RED + BanHammerPlugin.getMessage("notifyBannedPlayer"), playerName));
        sender.sendMessage(String.format(ChatColor.YELLOW + BanHammerPlugin.getMessage("notifyReason"), ban.getReason()));
      } else if (ban.getType().equals(BanRecord.Type.TEMPORARY)) {
        Date expiryDate = new Date(ban.getExpiresAt());
        DateFormat dateFormat = new SimpleDateFormat("MMM d H:mm a ");
        String expiryDateString = dateFormat.format(expiryDate) + "(" + Calendar.getInstance().getTimeZone().getDisplayName() + ")";
        sender.sendMessage(String.format(ChatColor.RED + BanHammerPlugin.getMessage("notifyTempBannedPlayer"), playerName));
        sender.sendMessage(String.format(ChatColor.YELLOW + BanHammerPlugin.getMessage("notifyExpiresOn"), expiryDateString));
      }
    } else sender.sendMessage(String.format(ChatColor.YELLOW + BanHammerPlugin.getMessage("playerNotBanned"), playerName));

  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) throws NotEnoughArgumentsException {
    Map<String, String> m = new HashMap<String, String>();
    arguments.remove(0);

    try {
      m.put("playerName", arguments.get(0));
    } catch (IndexOutOfBoundsException e) {
      throw new NotEnoughArgumentsException();
    }

    return m;
  }

}
