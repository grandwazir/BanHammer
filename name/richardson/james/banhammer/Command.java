/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * Command.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with BanHammer.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.banhammer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import name.richardson.james.banhammer.exceptions.InvalidTimeUnitException;
import name.richardson.james.banhammer.exceptions.NotEnoughArgumentsException;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class Command implements CommandExecutor {

  protected static final String CONSOLE_NAME = "Console";
  
  protected String description;
  protected String name;
  protected String[] optionalArgumentKeys;
  protected String permission;
  protected BanHammerPlugin plugin;
  protected Integer requiredArgumentCount;
  protected String usage;

  public Command(BanHammerPlugin plugin) {
    this.plugin = plugin;
  }
  
  public abstract void execute(CommandSender sender, Map<String, String> arguments) throws InvalidTimeUnitException, NotEnoughArgumentsException;

  @Override
  public boolean onCommand(final CommandSender sender, final org.bukkit.command.Command command, final String label, final String[] args) {
    if (!this.authorisePlayer(sender)) {
      sender.sendMessage(ChatColor.RED + BanHammerPlugin.getMessage("PlayerNotAuthorisedException"));
      return true;
    }
    
    try {
      LinkedList<String> arguments = new LinkedList<String>();
      arguments.addAll(Arrays.asList(args));
      final Map<String, String> parsedArguments = this.parseArguments(arguments);  
      this.execute(sender, parsedArguments);
    } catch (final NotEnoughArgumentsException e) {
      sender.sendMessage(ChatColor.RED + BanHammerPlugin.getMessage("NotEnoughArgumentsException"));
      sender.sendMessage(ChatColor.YELLOW + this.usage);
    } catch (InvalidTimeUnitException e) {
      sender.sendMessage(ChatColor.RED + BanHammerPlugin.getMessage("InvalidTimeUnitException"));
      sender.sendMessage(ChatColor.YELLOW + BanHammerPlugin.getMessage("ValidTimeUnits"));
    }
    return true;
  }

  /**
   * Check to see if a player has permission to use this command.
   * 
   * A console user is permitted to use all commands by default.
   * 
   * @param sender The player/console that is attempting to use the command
   * @return true if the player has permission; false otherwise.
   */
  protected boolean authorisePlayer(CommandSender sender) {
    if (sender instanceof ConsoleCommandSender) {
      return true;
    } else if (sender instanceof Player) {
      final Player player = (Player) sender;
      if (player.hasPermission(this.permission) || player.hasPermission("banhammer.*")) {
        return true;
      }
    } 
    return false;
  }

  /**
   * Broadcast a message to all players
   * 
   * @param message A string that you want every player to receive.
   */
  protected void broadcastMessage(String message) {
    plugin.getServer().broadcastMessage(message);
  }
  
  /**
   * Combine an array of strings together into one string. 
   * 
   * Trailing separators are removed.
   * 
   * @param arguments A list of strings to append together
   * @param seperator A string that will appear between each appended string.
   * @return result The resulting string or "No reason specified" if an exception is raised.
   */
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
  
  /**
   * Attempt to match an Player by name. 
   * 
   * This method will attempt to match players that are currently
   * online. Failing that and if offline is true, it will return an
   * OfflinePlayer for the specified name. 
   * 
   * @param playerName The name to match.
   * @param offline If true and no match was found, return an OfflinePlayer for the same name.
   * @return result Player that matches that name.
   * @throws NoMatchingPlayerException - If the number of matches is not equal to 1.
   */
  protected Player getPlayer(String playerName) {
    List<Player> playerList = this.plugin.getServer().matchPlayer(playerName);
    if (playerList.size() == 1) {
      return playerList.get(0);
    }
    return null;
  }
  
  protected OfflinePlayer getOfflinePlayer(String playerName) {
    return this.plugin.getServer().getOfflinePlayer(playerName);
  }
  
  /**
   * Get the name of a CommandSender.
   * 
   * By default a CommandSender which is not a Player has no name. In this case
   * the method will return the value of consoleName.
   * 
   * @param sender The CommandSender that you wish to resolve the name of.
   * @return name Return the name of the Player or "Console" if no name available.
   */
  protected String getSenderName(CommandSender sender) {
    if (sender instanceof ConsoleCommandSender) {
      return Command.CONSOLE_NAME;
    } else {
      final Player player = (Player) sender;
      return player.getName();
    }
  }    

  protected abstract Map<String, String> parseArguments(List<String> arguments) throws NotEnoughArgumentsException, InvalidTimeUnitException;

}
