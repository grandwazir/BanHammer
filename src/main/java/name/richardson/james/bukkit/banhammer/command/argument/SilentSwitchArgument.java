package name.richardson.james.bukkit.banhammer.command.argument;

import name.richardson.james.bukkit.utilities.command.argument.ArgumentMetadata;
import name.richardson.james.bukkit.utilities.command.argument.BooleanMarshaller;
import name.richardson.james.bukkit.utilities.command.argument.SimpleArgumentMetadata;
import name.richardson.james.bukkit.utilities.command.argument.SwitchArgument;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;

public class SilentSwitchArgument extends BooleanMarshaller {

	public static final Messages MESSAGES = MessagesFactory.getMessages();

	public static SilentSwitchArgument getInstance() {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(MESSAGES.silentArgumentId(), MESSAGES.silentArgumentName(), MESSAGES.silentArgumentDescription());
		return new SilentSwitchArgument(metadata);
	}

	private SilentSwitchArgument(final ArgumentMetadata metadata) {
		super(new SwitchArgument(metadata));
	}

}
