
package name.richardson.james.banhammer.administration;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.Command;
import name.richardson.james.banhammer.exceptions.NotEnoughArgumentsException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends Command {

  public ReloadCommand(final BanHammer plugin) {
    super(plugin);
    this.name = "reload";
    this.description = "reload the ban cache";
    this.usage = "/bh reload";
    this.permission = plugin.getName() + "." + this.name;
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) throws NotEnoughArgumentsException {
    String senderName = this.plugin.getSenderName(sender);
    this.plugin.cache.reload();
    String cacheSize = Integer.toString(this.plugin.cache.size());

    BanHammer.log(Level.INFO, String.format(BanHammer.messages.getString("logCacheReloaded"), senderName));
    BanHammer.log(Level.INFO, String.format(BanHammer.messages.getString("bansLoaded"), cacheSize));
    sender.sendMessage(String.format(ChatColor.GREEN + BanHammer.messages.getString("notifyCachedReloaded"), cacheSize));
  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) throws NotEnoughArgumentsException {
    return Collections.emptyMap();
  }

}
