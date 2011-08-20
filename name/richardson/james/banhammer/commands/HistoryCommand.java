
package name.richardson.james.banhammer.commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.exceptions.NotEnoughArgumentsException;
import name.richardson.james.banhammer.persistant.BanRecord;
import name.richardson.james.banhammer.utilities.BanHammerTime;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HistoryCommand extends Command {

  public HistoryCommand(final BanHammer plugin) {
    super(plugin);
    this.name = "history";
    this.description = "show all the bans associated with a player";
    this.usage = "/bh history [name]";
    this.permission = plugin.getName() + "." + this.name;
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) throws NotEnoughArgumentsException {
    String playerName = arguments.get("playerName");

    List<BanRecord> bans = BanRecord.find(playerName);
    if (bans.isEmpty())
      sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("noBanHistory"), playerName));
    else {
      String banTotal = Integer.toString(bans.size());
      sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + BanHammer.messages.getString("banHistorySummary"), playerName, banTotal));
      for (BanRecord ban : bans) {
        Date createdDate = new Date(ban.getCreatedAt());
        DateFormat dateFormat = new SimpleDateFormat("MMM d");
        String createdAt = dateFormat.format(createdDate);
        sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("banSummary"), ban.getCreatedBy(), createdAt));
        sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("banReason"), ban.getReason()));
        if (ban.getType().equals(BanRecord.type.PERMENANT))
          sender.sendMessage(ChatColor.YELLOW + BanHammer.messages.getString("banTimePermenant"));
        else if (ban.getType().equals(BanRecord.type.TEMPORARY)) {
          Long banTime = ban.getExpiresAt() - ban.getCreatedAt();
          sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("banTimeTemporary"), BanHammerTime.millisToLongDHMS(banTime)));
        }
      }
    }
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
