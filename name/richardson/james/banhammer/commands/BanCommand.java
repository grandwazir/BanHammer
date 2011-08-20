
package name.richardson.james.banhammer.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.exceptions.InvalidTimeUnitException;
import name.richardson.james.banhammer.exceptions.NoMatchingPlayerException;
import name.richardson.james.banhammer.exceptions.NotEnoughArgumentsException;
import name.richardson.james.banhammer.exceptions.PlayerAlreadyBannedException;
import name.richardson.james.banhammer.exceptions.PlayerNotAuthorisedException;
import name.richardson.james.banhammer.persistant.BanRecord;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand extends Command {

  public BanCommand(final BanHammer plugin) {
    super(plugin);
    this.name = "ban";
    this.description = "ban a player from the server";
    this.usage = "/ban [name] [reason] <t:time>";
    this.permission = plugin.getName() + "." + this.name;
  }

  @Override
  public void execute(final CommandSender sender, final Map<String, String> arguments) throws NotEnoughArgumentsException, NoMatchingPlayerException,
      PlayerNotAuthorisedException, PlayerAlreadyBannedException {
    String senderName = this.plugin.getSenderName(sender);
    String playerName = arguments.get("playerName");
    String reason = arguments.get("reason");
    Player player = null;

    try {
      player = this.plugin.matchPlayer(playerName);
    } catch (NoMatchingPlayerException e) {
      player = null;
    }

    if (this.plugin.cache.contains(playerName))
      throw new PlayerAlreadyBannedException(playerName);
    else {
      this.isPlayerValidTarget(senderName, playerName);
      if (arguments.containsKey("time")) {
        BanRecord.create(playerName, senderName, Long.parseLong(arguments.get("time")) + System.currentTimeMillis(), System.currentTimeMillis(), reason);
        this.plugin.notifyPlayers((String.format(ChatColor.RED + BanHammer.messages.getString("notifyTempBannedPlayer"), playerName)), sender);
      } else {
        BanRecord.create(playerName, senderName, new Long(0), System.currentTimeMillis(), reason);
        this.plugin.notifyPlayers((String.format(ChatColor.RED + BanHammer.messages.getString("notifyBannedPlayer"), playerName)), sender);
      }

      this.plugin.notifyPlayers((String.format(ChatColor.YELLOW + BanHammer.messages.getString("notifyReason"), reason)), sender);
      this.plugin.cache.add(playerName);
      if (player != null)
        player.kickPlayer(String.format(BanHammer.messages.getString("kickedMessage"), reason));
      BanHammer.log(Level.INFO, String.format(BanHammer.messages.getString("logPlayerBanned"), senderName, playerName));

    }

  }

  private String parseTime(String timeString) throws InvalidTimeUnitException {
    long time;

    int months = 0;
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

      if (argument.endsWith("m"))
        months = Integer.parseInt(argument.substring(0, argument.length() - 1));
      else if (argument.endsWith("w"))
        weeks = Integer.parseInt(argument.substring(0, argument.length() - 1));
      else if (argument.endsWith("d"))
        days = Integer.parseInt(argument.substring(0, argument.length() - 1));
      else if (argument.endsWith("h"))
        hours = Integer.parseInt(argument.substring(0, argument.length() - 1));
      else if (argument.endsWith("m"))
        minutes = Integer.parseInt(argument.substring(0, argument.length() - 1));
      else if (argument.endsWith("s"))
        seconds = Integer.parseInt(argument.substring(0, argument.length() - 1));
      else throw new InvalidTimeUnitException();

      result = m.find();
    }

    time = seconds;
    time += minutes * 60;
    time += hours * 3600;
    time += days * 86400;
    time += weeks * 604800;
    // assumes 30 days in a month
    time += months * 2592000;

    // convert to milliseconds
    time = time * 1000;

    if (time == 0) throw new InvalidTimeUnitException();
    
    return Long.toString(time);
  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) throws NotEnoughArgumentsException, InvalidTimeUnitException {
    Map<String, String> m = new HashMap<String,String>();
    BanHammer.log(Level.INFO, arguments.toString());
    
    try {
      
      for (String argument : arguments) {
        if (argument.startsWith("t:")) {
          m.put("time", this.parseTime(argument));
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
