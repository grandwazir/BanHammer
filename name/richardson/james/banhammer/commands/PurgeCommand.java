
package name.richardson.james.banhammer.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.exceptions.NoMatchingPlayerException;
import name.richardson.james.banhammer.exceptions.NotEnoughArgumentsException;
import name.richardson.james.banhammer.persistant.BanRecord;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PurgeCommand extends Command {

  public PurgeCommand(final BanHammer plugin) {
    super(plugin);
    this.name = "purge";
    this.description = "remove all ban history associated with a player";
    this.usage = "/bh purge [name]";
    this.permission = plugin.getName() + "." + this.name;
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) throws NotEnoughArgumentsException, NoMatchingPlayerException {
    String playerName = arguments.get("playerName");
    String senderName = this.plugin.getSenderName(sender);

    List<BanRecord> bans = BanRecord.find(playerName);
    if (bans.isEmpty())
      sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("noBanHistory"), playerName));
    else {
      String banTotal = Integer.toString(bans.size());
      BanRecord.destroy(bans);
      BanHammer.log(Level.INFO, String.format(BanHammer.messages.getString("logPlayerPurged"), senderName, playerName));
      sender.sendMessage(String.format(ChatColor.GREEN + BanHammer.messages.getString("notifyPurgedPlayer"), banTotal, playerName));
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
