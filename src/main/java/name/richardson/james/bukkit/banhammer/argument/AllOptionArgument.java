package name.richardson.james.bukkit.banhammer.argument;

import name.richardson.james.bukkit.utilities.command.argument.*;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;

public class AllOptionArgument {

	public static final Messages MESSAGES = MessagesFactory.getMessages();

	public static BooleanMarshaller getInstance() {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(MESSAGES.allOptionArgumentID(), MESSAGES.allOptionArgumentName(), MESSAGES.allOptionArgumentDescription());
		final Argument argument = new SwitchArgument(metadata);
		return new SimpleBooleanMarshaller(argument);
	}

}
