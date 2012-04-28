/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * HistoryCommand.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.ban;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.BanHandler;
import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.PluginCommand;

public class HistoryCommand extends PluginCommand {

  /** Reference to the BanHammer API */
  private final BanHandler handler;
  
  /** Reference to the BanHammer plugin */
  private final BanHammer plugin;
  
  /** A instance of the Bukkit server. */
  private final Server server;
  
  /** The player whos history we are going to check */
  private OfflinePlayer player;

  public HistoryCommand(final BanHammer plugin) {
    super(plugin);
    handler = plugin.getHandler(HistoryCommand.class);
    this.plugin = plugin;
    this.server = plugin.getServer();
    this.registerPermissions();
  }
  
  private void registerPermissions() {
    final String prefix = plugin.getDescription().getName().toLowerCase() + ".";
    final String wildcardDescription = String.format(plugin.getMessage("wildcard-permission-description"), this.getName());
    // create the wildcard permission
    Permission wildcard = new Permission(prefix + this.getName() + ".*", wildcardDescription, PermissionDefault.OP);
    wildcard.addParent(plugin.getRootPermission(), true);
    this.addPermission(wildcard);
    // create the base permission
    Permission base = new Permission(prefix + this.getName(), plugin.getMessage("historycommand-permission-description"), PermissionDefault.OP);
    base.addParent(wildcard, true);
    this.addPermission(base);
    // add ability to view your own ban history
    Permission own = new Permission(prefix + this.getName() + "." + plugin.getMessage("historycommand-own-permission-name"), plugin.getMessage("pardoncommand-own-permission-name"), PermissionDefault.OP);
    own.addParent(base, true);
    this.addPermission(own);
    // add ability to view the ban history of others
    Permission others = new Permission(prefix + this.getName() + "." + plugin.getMessage("historycommand-others-permission-name"), plugin.getMessage("pardoncommand-others-permission-name"), PermissionDefault.OP);
    others.addParent(base, true);
    this.addPermission(others);
  }

  public void execute(CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    final List<BanRecord> bans = handler.getPlayerBans(player.getName());
    
    if (sender.hasPermission(this.getPermission(3)) && !player.getName().equalsIgnoreCase(sender.getName())) {
      this.displayHistory(bans, sender);
      return;
    } else if (!player.getName().equalsIgnoreCase(sender.getName())) {
      throw new CommandPermissionException(this.getMessage("historycommand-cannot-view-others-history"), this.getPermission(3));
    }
    
    if (sender.hasPermission(this.getPermission(2)) && player.getName().equalsIgnoreCase(sender.getName())) {
      this.displayHistory(bans, sender);
      return;
    } else if (player.getName().equalsIgnoreCase(sender.getName())) {
      throw new CommandPermissionException(this.getMessage("historycommand-cannot-view-own-history"), this.getPermission(3));
    }
    
  }
  
  private void displayHistory(List<BanRecord> bans, CommandSender sender) {
    sender.sendMessage(this.getFormattedMessageHeader(bans.size()));
    for (BanRecord ban : bans) {
      BanSummary summary = new BanSummary(plugin, ban);
      sender.sendMessage(summary.getHeader());
      sender.sendMessage(summary.getReason());
      sender.sendMessage(summary.getLength());
      if (ban.getType() == BanRecord.Type.TEMPORARY) sender.sendMessage(summary.getExpiresAt());
    }
  }
  
  private String getFormattedMessageHeader(int size) {
    final Object[] arguments = { size };
    final double[] limits = { 0, 1, 2 };
    final String[] formats = { this.getMessage("no-ban").toLowerCase(), this.getMessage("only-ban").toLowerCase(), this.getMessage("many-bans") };
    return this.getChoiceFormattedMessage("historycommand-header", arguments, formats, limits);
  }

  public void parseArguments(String[] arguments, CommandSender sender) throws CommandArgumentException {
    if (arguments.length == 0) {
      if (sender instanceof ConsoleCommandSender) throw new CommandArgumentException(this.getMessage("must-specify-a-player"), this.getMessage("name-autocompletion"));
      this.player = (OfflinePlayer) sender;
    } else {
      this.player = matchPlayer(arguments[0]);
    }
    
  }
  
  private OfflinePlayer matchPlayer(final String name) {
    final List<Player> players = this.server.matchPlayer(name);
    if (players.isEmpty()) {
      return server.getOfflinePlayer(name);
    } else {
      return players.get(0);
    }
  }
  
  /**
  @Override
  public void execute(final CommandSender sender, final Map<String, Object> arguments) throws CommandPermissionException {
    final String playerName = arguments.get("playerName") != null ? (String) arguments.get("playerName") : sender.getName();
    if (!playerName.equalsIgnoreCase(sender.getName()) && !sender.hasPermission(HistoryCommand.PERMISSION_OTHER)) {
      throw new CommandPermissionException("You are not allowed to view other players ban history.", HistoryCommand.PERMISSION_OTHER);
    } else {
      final List<BanRecord> bans = handler.getPlayerBans(playerName);

      if (bans.isEmpty()) {
        sender.sendMessage(String.format(ChatColor.YELLOW + "%s has no bans on record.", playerName));
      } else {
        sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + "%s has %d ban(s) on record:", playerName, bans.size()));
        for (final BanRecord ban : bans) {
          sendBanDetail(sender, ban);
        }
      }
    }
  }

  @Override
  public Map<String, Object> parseArguments(final List<String> arguments) throws CommandArgumentException {
    final Map<String, Object> m = new HashMap<String, Object>();

    try {
      m.put("playerName", arguments.get(0));
    } catch (final IndexOutOfBoundsException e) {
      m.put("playerName", null);
    }

    return m;
  }

  protected void sendBanDetail(final CommandSender sender, final BanRecord ban) {
    final Date createdDate = new Date(ban.getCreatedAt());
    final DateFormat dateFormat = new SimpleDateFormat("MMM d");
    final String createdAt = dateFormat.format(createdDate);
    sender.sendMessage(String.format(ChatColor.YELLOW + "Banned by %s on %s", ban.getCreatedBy(), createdAt));
    sender.sendMessage(String.format(ChatColor.YELLOW + "- Reason: %s.", ban.getReason()));
    switch (ban.getType()) {
    case PERMENANT:
      sender.sendMessage(ChatColor.YELLOW + "- Length: Permanent.");
      break;
    case TEMPORARY:
      final Date expiryDate = new Date(ban.getExpiresAt());
      final DateFormat expiryDateFormat = new SimpleDateFormat("MMM d H:mm a ");
      final String expiryDateString = expiryDateFormat.format(expiryDate) + "(" + Calendar.getInstance().getTimeZone().getDisplayName() + ")";
      final Long banTime = ban.getExpiresAt() - ban.getCreatedAt();
      sender.sendMessage(String.format(ChatColor.YELLOW + "- Length: %s", Time.millisToLongDHMS(banTime)));
      sender.sendMessage(String.format(ChatColor.YELLOW + "- Expires on: %s", expiryDateString));
      break;
    }
  }
  
  */

}
