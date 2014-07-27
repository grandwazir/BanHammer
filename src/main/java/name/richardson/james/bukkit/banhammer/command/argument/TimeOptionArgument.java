package name.richardson.james.bukkit.banhammer.command.argument;

import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.ArgumentMetadata;
import name.richardson.james.bukkit.utilities.command.argument.OptionArgument;
import name.richardson.james.bukkit.utilities.command.argument.SimpleArgumentMetadata;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;

public class TimeOptionArgument {

	public static final Messages MESSAGES = MessagesFactory.getMessages();

	public static TimeMarshaller getInstance() {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(MESSAGES.timeArgumentId(), MESSAGES.timeArgumentName(), MESSAGES.timeArgumentDescription());
		Argument argument = new OptionArgument(metadata, null);
		return new TimeMarshaller(argument);
	}

}
