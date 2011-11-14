
package name.richardson.james.banhammer.kick;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.Command;
import name.richardson.james.banhammer.exceptions.NoMatchingPlayerException;
import name.richardson.james.banhammer.exceptions.NotEnoughArgumentsException;
import name.richardson.james.banhammer.exceptions.PlayerNotAuthorisedException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand extends Command {

  public KickCommand(final BanHammer plugin) {
    super(plugin);
    this.name = "kick";
    this.description = "kick a player from the server";
    this.usage = "/kick [name] <reason>";
    this.permission = plugin.getName() + "." + this.name;
    this.requiredArgumentCount = 2;
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) throws NotEnoughArgumentsException, NoMatchingPlayerException,
      PlayerNotAuthorisedException {
    String senderName = this.plugin.getSenderName(sender);
    Player player = this.plugin.matchPlayer(arguments.get("playerName"));

    this.isPlayerValidTarget(senderName, player.getName());

    player.kickPlayer(String.format(BanHammer.messages.getString("kickedMessage"), arguments.get("reason")));
    BanHammer.log(Level.INFO, String.format(BanHammer.messages.getString("logPlayerKicked"), senderName, player.getName()));
    this.plugin.notifyPlayers((String.format(ChatColor.RED + BanHammer.messages.getString("notifyKickedPlayer"), player.getName())), sender);
    this.plugin.notifyPlayers((String.format(ChatColor.YELLOW + BanHammer.messages.getString("notifyReason"), arguments.get("reason"))), sender);
  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) throws NotEnoughArgumentsException {
    Map<String, String> m = new HashMap<String, String>();

    try {
      m.put("playerName", arguments.remove(0));
      m.put("reason", this.combineString(arguments, " "));
    } catch (IndexOutOfBoundsException e) {
      throw new NotEnoughArgumentsException();
    }

    return m;
  }

}
