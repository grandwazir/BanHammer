package name.richardson.james.bukkit.banhammer.utilities.argument;

import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.InvalidArgumentException;
import name.richardson.james.bukkit.utilities.command.argument.StringArgument;

import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;

public abstract class RecordArgument implements Argument {

	private static PlayerRecordManager playerRecordManager;
	private final StringArgument stringArgument;
	private boolean required;

	public static void setPlayerRecordManager(PlayerRecordManager playerRecordManager) {
		RecordArgument.playerRecordManager = playerRecordManager;
	}

	public static PlayerRecordManager getPlayerRecordManager() {
		return RecordArgument.playerRecordManager;
	}

	public RecordArgument() {
		stringArgument = new StringArgument();
		stringArgument.setRequired(true);
		stringArgument.setCaseInsensitive(true);
	}

	public Object getValue()
	throws InvalidArgumentException {
		return this.stringArgument.getValue();
	}

	public void parseValue(Object playerName)
	throws InvalidArgumentException {
		this.stringArgument.parseValue(playerName);
	}

	@Override
	public boolean isRequired() {
		return required;
	}

	@Override
	public void setRequired(boolean b) {
		this.required = b;
	}

}
