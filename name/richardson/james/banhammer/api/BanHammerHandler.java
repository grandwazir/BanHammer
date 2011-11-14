package name.richardson.james.banhammer.api;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.ban.BanRecord;


public class BanHammerHandler {
  private BanHammer plugin;
  
  public BanHammerHandler(BanHammer banHammer) {
    this.plugin = banHammer;
  }

  public boolean BanPlayer(String pluginName, Player player, Long time, String reason, boolean notify) {
    if (player instanceof ConsoleCommandSender) {
      BanHammer.log(Level.WARNING, String.format(plugin.getMessage("APIAttemptedToBanConsole"), pluginName)); 
      return false;
    } else if (plugin.cache.contains(player.getName())) {
      BanHammer.log(Level.WARNING, String.format(plugin.getMessage("APIPlayerAlreadyBanned"), pluginName, player.getName())); 
      return false;
    } else if (time < System.currentTimeMillis() && time != 0) {
      BanHammer.log(Level.WARNING, String.format(plugin.getMessage("APIBanExpiryTimeInThePast"), pluginName)); 
      return false;
    } else {
      if (notify) {
        plugin.notifyPlayers((String.format(ChatColor.RED + BanHammer.messages.getString("notifyBannedPlayer"), player.getName())), player);
        plugin.notifyPlayers((String.format(ChatColor.YELLOW + BanHammer.messages.getString("notifyReason"), reason)), player);
      }
      BanRecord.create(player.getName(), pluginName, time, System.currentTimeMillis(), reason);
      plugin.cache.add(player.getName());
      player.kickPlayer(reason);
      BanHammer.log(Level.INFO, String.format(plugin.getMessage("logPlayerBanned"), pluginName, player.getName())); 
      return true;
    }
  }

}
