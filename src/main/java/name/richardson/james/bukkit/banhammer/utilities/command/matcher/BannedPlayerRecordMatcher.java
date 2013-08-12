package name.richardson.james.bukkit.banhammer.utilities.command.matcher;

import org.bukkit.event.Listener;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.ban.event.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.ban.event.BanHammerPlayerPardonedEvent;

public class BannedPlayerRecordMatcher extends PlayerRecordMatcher implements Listener {

	public BannedPlayerRecordMatcher(PlayerRecordManager playerRecordManager, PlayerRecordManager.PlayerStatus mode) {
		super(playerRecordManager, mode);
	}

	public void onPlayerBanned(BanHammerPlayerBannedEvent event) {
		this.getPlayerNames().add(event.getPlayerName());
	}

	public void onPlayerPardoned(BanHammerPlayerPardonedEvent event) {
		this.getPlayerNames().remove(event.getPlayerName());
	}

}
