
package name.richardson.james.banhammer.listeners;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.cache.CachedBan;
import name.richardson.james.banhammer.persistant.BanRecord;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;

public class BanHammerPlayerListener extends PlayerListener {

  final BanHammer plugin;

  public BanHammerPlayerListener(final BanHammer plugin) {
    this.plugin = plugin;
  }

  @Override
  public void onPlayerLogin(PlayerLoginEvent event) {
    String playerName = event.getPlayer().getDisplayName().toLowerCase();
    String message;

    if (this.plugin.cache.contains(playerName)) {
      CachedBan ban = this.plugin.cache.get(playerName);
      if (ban.isActive()) {
        if (ban.getType().equals(BanRecord.type.PERMENANT))
          message = String.format(BanHammer.messages.getString("disallowLoginPermanently"), ban.getReason());
        else {
          Date expiryDate = new Date(ban.getExpiresAt());
          DateFormat dateFormat = new SimpleDateFormat("MMM d H:mm a ");
          String expiryDateString = dateFormat.format(expiryDate) + "(" + Calendar.getInstance().getTimeZone().getDisplayName() + ")";
          message = String.format(BanHammer.messages.getString("disallowLoginTemporarily"), expiryDateString);
        }
        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, message);
      } else this.plugin.cache.remove(playerName);
    }
  }
}
