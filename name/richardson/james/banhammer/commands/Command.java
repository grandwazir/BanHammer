
package name.richardson.james.banhammer.commands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.exceptions.InvalidTimeUnitException;
import name.richardson.james.banhammer.exceptions.NoMatchingPlayerException;
import name.richardson.james.banhammer.exceptions.NotEnoughArgumentsException;
import name.richardson.james.banhammer.exceptions.PlayerAlreadyBannedException;
import name.richardson.james.banhammer.exceptions.PlayerNotAuthorisedException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class Command implements CommandExecutor {

  protected String description;
  protected String name;
  protected String[] optionalArgumentKeys;
  protected String permission;
  protected BanHammer plugin;
  protected Integer requiredArgumentCount;
  protected String usage;

  public Command(final BanHammer plugin) {
    super();
    this.plugin = plugin;
  }

  public abstract void execute(CommandSender sender, Map<String, String> arguments) throws InvalidTimeUnitException, NotEnoughArgumentsException,
      PlayerAlreadyBannedException, PlayerNotAuthorisedException, NoMatchingPlayerException;

  public boolean onCommand(final CommandSender sender, final org.bukkit.command.Command command, final String label, final String[] args) {
    try {
      this.authorisePlayer(sender, this.permission);
      LinkedList<String> arguments = new LinkedList<String>();
      arguments.addAll(Arrays.asList(args));
      final Map<String, String> parsedArguments = this.parseArguments(arguments);  
      this.execute(sender, parsedArguments);
    } catch (final PlayerNotAuthorisedException e) {
      sender.sendMessage(ChatColor.RED + this.plugin.getMessage("PlayerNotAuthorisedException"));
    } catch (final NotEnoughArgumentsException e) {
      sender.sendMessage(ChatColor.RED + this.plugin.getMessage("NotEnoughArgumentsException"));
      sender.sendMessage(ChatColor.YELLOW + this.usage);
    } catch (InvalidTimeUnitException e) {
      sender.sendMessage(ChatColor.RED + this.plugin.getMessage("InvalidTimeUnitException"));
      sender.sendMessage(ChatColor.YELLOW + this.plugin.getMessage("ValidTimeUnits"));
    } catch (PlayerAlreadyBannedException e) {
      sender.sendMessage(ChatColor.RED + String.format(this.plugin.getMessage("PlayerAlreadyBannedException"), e.getPlayerName()));
    } catch (NoMatchingPlayerException e) {
      sender.sendMessage(ChatColor.RED + this.plugin.getMessage("NoMatchingPlayerException"));
    }
    return true;
  }

  protected void authorisePlayer(CommandSender sender, String node) throws PlayerNotAuthorisedException {
    node = node.toLowerCase();

    if (sender instanceof ConsoleCommandSender)
      return;
    else {
      final Player player = (Player) sender;
      if (player.hasPermission(node) || player.hasPermission("banhammer.*"))
        return;

      if (this.plugin.externalPermissions != null)
        if (this.plugin.externalPermissions.has(player, node))
          return;
    }

    throw new PlayerNotAuthorisedException();
  }

  protected String combineString(List<String> arguments, String seperator) {
    StringBuilder reason = new StringBuilder();

    try {

      for (String argument : arguments) {
        reason.append(argument);
        reason.append(seperator);
      }

      reason.deleteCharAt(reason.length() - seperator.length());
      return reason.toString();
    } catch (StringIndexOutOfBoundsException e) {
      return "No reason provided";
    }
  }

  protected int getPlayerWeight(String playerName) throws NoMatchingPlayerException {
    final List<String> nodes = Arrays.asList("heavy", "medium", "light");
    String weightNode = null;
    Player player = null;

    if (playerName == "console")
      return 4;
    else try {
      player = this.plugin.matchPlayerExactly(playerName);
      
      for (String key : nodes) {
        String node = "banhammer.weight." + key;
        if (player.hasPermission(node)) {
          weightNode = key;
          break;
        }       
        if (this.plugin.externalPermissions != null) {
          if (this.plugin.externalPermissions.has(player, node)) {
            weightNode = key;
            break;
          }
        }
      }

      if (weightNode != null) {
        if (weightNode.equalsIgnoreCase("heavy"))
          return 3;
        else if (weightNode.equalsIgnoreCase("medium"))
          return 2;
        else if (weightNode.equalsIgnoreCase("light"))
          return 1;
      } else return 0;

    } catch (NoMatchingPlayerException e) {
      BanHammer.log(Level.WARNING, String.format(BanHammer.messages.getString("unableToReferToOfflinePermissions"), playerName));
    }

    return 0;

  }

  protected String getSenderName(final CommandSender sender) {
    if (sender instanceof ConsoleCommandSender)
      return "console";
    else {
      final Player player = (Player) sender;
      return player.getName();
    }
  }

  protected boolean isPlayerValidTarget(String playerName, String targetName) throws NoMatchingPlayerException, PlayerNotAuthorisedException {
    if (playerName.equalsIgnoreCase(targetName))
      throw new PlayerNotAuthorisedException();

    final int playerWeight = this.getPlayerWeight(playerName);
    final int targetWeight = this.getPlayerWeight(targetName);
    if (playerWeight > targetWeight)
      return true;

    throw new PlayerNotAuthorisedException();

  }

  protected abstract Map<String, String> parseArguments(List<String> arguments) throws NotEnoughArgumentsException, InvalidTimeUnitException;

}
