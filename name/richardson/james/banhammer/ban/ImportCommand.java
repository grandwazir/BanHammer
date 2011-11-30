/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * CheckCommand.java is part of BanHammer.
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
package name.richardson.james.banhammer.ban;

import java.util.List;
import java.util.Map;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.Command;
import name.richardson.james.banhammer.util.Logger;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class ImportCommand extends Command {

  private BanHandler banHandler;

  public ImportCommand(final BanHammer plugin) {
    super(plugin);
    this.name = BanHammer.getMessage("import-command-name");
    this.description = BanHammer.getMessage("import-command-description");
    this.usage = BanHammer.getMessage("import-command-usage");
    this.permission = "banhammer." + this.name;
    registerPermission(this.permission, this.description, PermissionDefault.OP);
    this.banHandler = plugin.getHandler();
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) {
    final int totalBans = this.plugin.getServer().getBannedPlayers().size();
    int imported = this.plugin.getServer().getBannedPlayers().size();
    final long expiryTime = 0;
    final String reason = BanHammer.getMessage("default-import-reason");
    final String senderName = sender.getName();
    
    for (OfflinePlayer player : this.plugin.getServer().getBannedPlayers()) {
      if (!this.banHandler.banPlayer(player.getName(), senderName, reason, expiryTime, false)) {
        Logger.info(String.format(BanHammer.getMessage("failed-import"), player.getName()));
        imported = imported - 1;
      } 
      player.setBanned(false);
    }
    
    Logger.info(String.format(BanHammer.getMessage("ban-import"), this.getSenderName(sender), imported));
    sender.sendMessage(String.format(ChatColor.GREEN + BanHammer.getMessage("bans-imported"), imported, totalBans));
  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) {
    return null;
  }

}
