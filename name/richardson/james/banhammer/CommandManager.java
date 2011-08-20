
package name.richardson.james.banhammer;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandManager implements CommandExecutor {

  private final HashMap<String, CommandExecutor> commands = new HashMap<String, CommandExecutor>();

  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (args.length != 0)
      if (this.commands.containsKey(args[0])) {
        this.commands.get(args[0]).onCommand(sender, command, label, args);
        return true;
      }
    sender.sendMessage(ChatColor.RED + "Invalid command!");
    sender.sendMessage(ChatColor.YELLOW + "/bh [check|history|purge|recent|reload]");
    return true;
  }

  public void registerCommand(final String command, final CommandExecutor executor) {
    this.commands.put(command, executor);
  }

}
