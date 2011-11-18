/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * BanCommand.java is part of BanHammer.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.Command;
import name.richardson.james.banhammer.exceptions.InvalidTimeUnitException;
import name.richardson.james.banhammer.exceptions.NoMatchingPlayerException;
import name.richardson.james.banhammer.exceptions.NotEnoughArgumentsException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand extends Command {

  private final BanHandler banHandler;

  public BanCommand(final BanHammer plugin) {
    super(plugin);
    this.name = "ban";
    this.description = "ban a player from the server";
    this.usage = "/ban [name] [reason] <t:time>";
    this.permission = "banhammer." + this.name;
    this.banHandler = plugin.getHandler();
  }

  @Override
  public void execute(final CommandSender sender, final Map<String, String> arguments) throws NotEnoughArgumentsException, InvalidTimeUnitException {
    final String senderName = this.getSenderName(sender);
    String playerName;
    final String reason = arguments.get("reason");
    Long expiryTime = (long) 0;
    
    try {
      Player player = this.getPlayer(arguments.get("playerName"));
      playerName = player.getName();
    } catch (NoMatchingPlayerException e) {
      playerName = arguments.get("playerName");
    }
    
    if (arguments.containsKey("time")) {
      expiryTime = parseTime(arguments.get("time"));
    }
    
    if (!this.banHandler.banPlayer(playerName, senderName, reason, expiryTime, true)) {
      sender.sendMessage(ChatColor.RED + String.format(BanHammer.getMessage("PlayerAlreadyBannedException"), playerName));
    }
    
  }

  private Long parseTime(String timeString) throws InvalidTimeUnitException {
    long time;

    int weeks = 0;
    int days = 0;
    int hours = 0;
    int minutes = 0;
    int seconds = 0;

    Pattern p = Pattern.compile("\\d+[a-z]{1}");
    Matcher m = p.matcher(timeString);
    boolean result = m.find();

    while (result) {
      String argument = m.group();

      if (argument.endsWith("w"))
        weeks = Integer.parseInt(argument.substring(0, argument.length() - 1));
      else if (argument.endsWith("d"))
        days = Integer.parseInt(argument.substring(0, argument.length() - 1));
      else if (argument.endsWith("h"))
        hours = Integer.parseInt(argument.substring(0, argument.length() - 1));
      else if (argument.endsWith("m"))
        minutes = Integer.parseInt(argument.substring(0, argument.length() - 1));
      else if (argument.endsWith("s"))
        seconds = Integer.parseInt(argument.substring(0, argument.length() - 1));
      else throw new NumberFormatException();

      result = m.find();
    }

    time = seconds;
    time += minutes * 60;
    time += hours * 3600;
    time += days * 86400;
    time += weeks * 604800;

    // convert to milliseconds
    time = time * 1000;

    if (time == 0)
      throw new InvalidTimeUnitException();

    return time;
  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) throws NotEnoughArgumentsException, InvalidTimeUnitException {
    Map<String, String> m = new HashMap<String, String>();

    try {
      for (String argument : arguments) {
        if (argument.startsWith("t:")) {
          m.put("time", argument);
          arguments.remove(argument);
          break;
        }
      }

      m.put("playerName", arguments.remove(0));
      m.put("reason", this.combineString(arguments, " "));
    } catch (IndexOutOfBoundsException e) {
      throw new NotEnoughArgumentsException();
    }

    return m;
  }

}
