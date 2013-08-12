package name.richardson.james.bukkit.banhammer.utilities.command.matcher;

import org.bukkit.event.Listener;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.ban.event.BanHammerPlayerBannedEvent;

public class CreatorPlayerRecordManager extends PlayerRecordMatcher implements Listener {

	public CreatorPlayerRecordManager(PlayerRecordManager playerRecordManager, PlayerRecordManager.PlayerStatus mode) {
		super(playerRecordManager, mode);
	}

	public void onPlayerBanned(BanHammerPlayerBannedEvent event) {
		this.getPlayerNames().add(event.getPlayerName());
	}

}
