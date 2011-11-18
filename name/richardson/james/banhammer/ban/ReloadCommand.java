/*******************************************************************************
 * Copyright (c) 2011 James Richardson.
 * 
 * ReloadCommand.java is part of BanHammer.
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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.Command;
import name.richardson.james.banhammer.exceptions.NotEnoughArgumentsException;
import name.richardson.james.banhammer.util.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends Command {
  
  public ReloadCommand(final BanHammer plugin) {
    super(plugin);
    this.name = "reload";
    this.description = "reload the ban cache";
    this.usage = "/bh reload";
    this.permission = "banhammer." + this.name;
  }

  @Override
  public void execute(final CommandSender sender, Map<String, String> arguments) throws NotEnoughArgumentsException {
    String senderName = this.getSenderName(sender);
    CachedList.getInstance().reload();
    String cacheSize = Integer.toString(CachedList.getInstance().size());

    Logger.info(String.format(BanHammer.getMessage("logCacheReloaded"), senderName));
    Logger.info(String.format(BanHammer.getMessage("bansLoaded"), cacheSize));
    sender.sendMessage(String.format(ChatColor.GREEN + BanHammer.getMessage("notifyCachedReloaded"), cacheSize));
  }

  @Override
  protected Map<String, String> parseArguments(List<String> arguments) throws NotEnoughArgumentsException {
    return Collections.emptyMap();
  }

}
