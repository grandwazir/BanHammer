package name.richardson.james.banhammer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import name.richardson.james.banhammer.BanHammer;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;

public class BanHammerPlayerListener extends PlayerListener {
	private BanHammer plugin;
	
	public BanHammerPlayerListener(BanHammer plugin) {
		this.plugin = plugin;
	}
	
	public void onPlayerLogin(PlayerLoginEvent event) {
		String playerName = event.getPlayer().getDisplayName();
		if (plugin.isPlayerBanned(playerName)) {
			String message = "You have been banned";
			BanHammerRecord ban = plugin.getPlayerBan(playerName);
			if (ban.getExpiresAt() > 0) {
				// Create the message
				Date expiryDate = new Date(ban.getExpiresAt());
				DateFormat dateFormat = new SimpleDateFormat("MMM d H:mm a ");
				String expiryDateString = dateFormat.format(expiryDate) + "(" + Calendar.getInstance().getTimeZone().getDisplayName() + ")"; 
				message = "You have been banned until " + expiryDateString;
			} else {
				message = "You have been permanently banned. Reason: " + ban.getReason();
			}
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, message);
		}
	}
}
