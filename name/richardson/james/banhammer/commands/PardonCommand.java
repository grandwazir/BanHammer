
package name.richardson.james.banhammer.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.exceptions.NotEnoughArgumentsException;
import name.richardson.james.banhammer.persistant.BanRecord;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PardonCommand extends Command {

  public PardonCommand(final BanHammer plugin) {
    super(plugin);
    this.name = "pardon";
    this.description = "pardon a player";
    this.usage = "/pardon [name]";
    this.permission = plugin.getName() + "." + this.name;
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) throws NotEnoughArgumentsException {
    String playerName = arguments.get("playerName");
    String senderName = this.plugin.getSenderName(sender);

    if (this.plugin.cache.contains(playerName)) {
      this.plugin.cache.remove(playerName);
      BanRecord.findFirst(playerName).destroy();
      BanHammer.log(Level.INFO, String.format(BanHammer.messages.getString("logPlayerPardoned"), senderName, playerName));
      this.plugin.notifyPlayers((String.format(ChatColor.GREEN + BanHammer.messages.getString("playerPardoned"), playerName)), sender);
    } else sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("playerNotBanned"), playerName));
  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) throws NotEnoughArgumentsException {
    Map<String, String> m = new HashMap<String,String>();

    try {
      m.put("playerName", arguments.get(0));
    } catch (IndexOutOfBoundsException e) {
      throw new NotEnoughArgumentsException();
    }

    return m;
  }

}
