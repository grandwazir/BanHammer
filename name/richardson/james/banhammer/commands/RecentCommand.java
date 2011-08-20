
package name.richardson.james.banhammer.commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

public class RecentCommand extends Command {

  public RecentCommand(final BanHammer plugin) {
    super(plugin);
    this.name = "recent";
    this.description = "shows the most recent bans made on the server";
    this.usage = "/bh recent [maxBans]";
    this.permission = plugin.getName() + "." + this.name;
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) throws NotEnoughArgumentsException {
    int maxRows;

    try {
      maxRows = Integer.parseInt(arguments.get("maxRows"));
    } catch (NumberFormatException e) {
      maxRows = 3;
    }

    List<BanRecord> bans = BanRecord.findRecent(maxRows);
    if (bans.isEmpty())
      sender.sendMessage(ChatColor.YELLOW + BanHammer.messages.getString("noRecentBans"));
    else {
      String banTotal = Integer.toString(bans.size());
      sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + BanHammer.messages.getString("recentBanCount"), banTotal));
      for (BanRecord ban : bans) {
        Date createdDate = new Date(ban.getCreatedAt());
        DateFormat dateFormat = new SimpleDateFormat("MMM d");
        String createdAt = dateFormat.format(createdDate);
        sender
            .sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("banSummaryWithName"), ban.getPlayer(), ban.getCreatedBy(), createdAt));
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
    Map<String, String> m = new HashMap<String, String>();
    arguments.remove(0);

    try {
      m.put("maxRows", arguments.get(0));
    } catch (IndexOutOfBoundsException e) {
      m.put("maxRows", "3");
    }

    return m;
  }

}
