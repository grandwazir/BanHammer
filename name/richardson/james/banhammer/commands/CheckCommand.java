
package name.richardson.james.banhammer.commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.exceptions.NotEnoughArgumentsException;
import name.richardson.james.banhammer.persistant.BanRecord;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CheckCommand extends Command {

  public CheckCommand(final BanHammer plugin) {
    super(plugin);
    this.name = "check";
    this.description = "check to see if a player is banned or not";
    this.usage = "/bh check [name]";
    this.permission = plugin.getName() + "." + this.name;
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) throws NotEnoughArgumentsException {
    String playerName = arguments.get("playerName");

    if (this.plugin.cache.contains(playerName)) {
      BanRecord ban = BanRecord.findFirst(playerName);
      if (ban.getType().equals(BanRecord.type.PERMENANT)) {
        sender.sendMessage(String.format(ChatColor.RED + BanHammer.messages.getString("notifyBannedPlayer"), playerName));
        sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("notifyReason"), ban.getReason()));
      } else if (ban.getType().equals(BanRecord.type.TEMPORARY)) {
        Date expiryDate = new Date(ban.getExpiresAt());
        DateFormat dateFormat = new SimpleDateFormat("MMM d H:mm a ");
        String expiryDateString = dateFormat.format(expiryDate) + "(" + Calendar.getInstance().getTimeZone().getDisplayName() + ")";
        sender.sendMessage(String.format(ChatColor.RED + BanHammer.messages.getString("notifyTempBannedPlayer"), playerName));
        sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("notifyExpiresOn"), expiryDateString));
      }
    } else sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("playerNotBanned"), playerName));

  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) throws NotEnoughArgumentsException {
    Map<String, String> m = new HashMap<String,String>();
    arguments.remove(0);

    try {
      m.put("playerName", arguments.get(0));
    } catch (IndexOutOfBoundsException e) {
      throw new NotEnoughArgumentsException();
    }

    return m;
  }

}
