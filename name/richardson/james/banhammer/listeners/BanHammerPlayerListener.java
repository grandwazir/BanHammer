package name.richardson.james.banhammer.listeners;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.persistant.BanRecord;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;

public class BanHammerPlayerListener extends PlayerListener {
	/*
	public void onPlayerLogin(PlayerLoginEvent event) {
		String playerName = event.getPlayer().getDisplayName();
		if (plugin.isPlayerBanned(playerName)) {
			BanHammerRecord banHammerRecord = plugin.getPlayerBan(playerName);
			String message;
			if (banHammerRecord.getExpiresAt() > 0) {
				// Create the message
				Date expiryDate = new Date(banHammerRecord.getExpiresAt());
				DateFormat dateFormat = new SimpleDateFormat("MMM d H:mm a ");
				String expiryDateString = dateFormat.format(expiryDate) + "(" + Calendar.getInstance().getTimeZone().getDisplayName() + ")"; 
				message = "You have been banned until " + expiryDateString;
			} else {
				message = "You have been permanently banned. Reason: " + banHammerRecord.getReason();
			}
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, message);
		}
	}
	*/
}
