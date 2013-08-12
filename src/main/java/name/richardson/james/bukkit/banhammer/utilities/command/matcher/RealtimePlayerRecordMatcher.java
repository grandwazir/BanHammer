package name.richardson.james.bukkit.banhammer.utilities.command.matcher;

import java.util.Collections;
import java.util.Set;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

public class RealtimePlayerRecordMatcher extends PlayerRecordMatcher {

	public RealtimePlayerRecordMatcher(PlayerRecordManager playerRecordManager, PlayerRecordManager.PlayerStatus mode) {
		super(playerRecordManager, mode);
	}

	@Override
	public Set<String> matches(String argument) {
		if (argument.length() < 3) return Collections.emptySet();
		setPlayerNames();
		return super.matches(argument);
	}

}
