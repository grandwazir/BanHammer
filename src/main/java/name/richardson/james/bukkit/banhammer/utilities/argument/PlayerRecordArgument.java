package name.richardson.james.bukkit.banhammer.utilities.argument;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import name.richardson.james.bukkit.utilities.command.argument.InvalidArgumentException;

import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;

public class PlayerRecordArgument extends RecordArgument {

	private final Set<String> playerNames = new TreeSet<String>();
	private PlayerRecordManager.PlayerStatus playerStatus;

	public PlayerRecordArgument() {
		playerStatus = PlayerRecordManager.PlayerStatus.ANY;
	}

	@Override
	public PlayerRecord getValue()
	throws InvalidArgumentException {
		if (isRequired()) {
			return getPlayerRecordManager().create((String) super.getValue());
		} else {
			return getPlayerRecordManager().find((String) super.getValue());
		}
	}

	@Override
	public void parseValue(Object value)
	throws InvalidArgumentException {
		super.parseValue(value);
	}

	@Override
	public Set<String> getMatches(String playerName) {
		playerNames.clear();
		List<PlayerRecord> records = getPlayerRecordManager().list(playerName, playerStatus);
		for (PlayerRecord record : records) {
			playerNames.add(record.getName());
		}
		return playerNames;
	}
	
	public void setPlayerStatus(PlayerRecordManager.PlayerStatus status) {
		this.playerStatus = status;	
	}

}
