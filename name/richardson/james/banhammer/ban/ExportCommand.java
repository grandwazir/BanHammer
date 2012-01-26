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

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.Command;
import name.richardson.james.banhammer.util.Logger;

public class ExportCommand extends Command {

  public ExportCommand(final BanHammer plugin) {
    super(plugin);
    this.name = BanHammer.getMessage("export-command-name");
    this.description = BanHammer.getMessage("export-command-description");
    this.usage = BanHammer.getMessage("export-command-usage");
    this.permission = "banhammer." + this.name;
    registerPermission(this.permission, this.description, PermissionDefault.OP);
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) {
    final List<BanRecord> bans = BanRecord.list();
    for (BanRecord ban : BanRecord.list()) {
      OfflinePlayer player = this.getOfflinePlayer(ban.getPlayer());
      player.setBanned(true);
    }
    Logger.info(String.format(BanHammer.getMessage("ban-export"), this.getSenderName(sender)));
    sender.sendMessage(String.format(ChatColor.GREEN + BanHammer.getMessage("bans-exported"), bans.size()));
  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) {
    return null;
  }

}
