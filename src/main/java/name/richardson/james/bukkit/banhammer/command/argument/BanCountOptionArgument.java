package name.richardson.james.bukkit.banhammer.command.argument;

import name.richardson.james.bukkit.utilities.command.argument.ArgumentMetadata;
import name.richardson.james.bukkit.utilities.command.argument.IntegerMarshaller;
import name.richardson.james.bukkit.utilities.command.argument.OptionArgument;
import name.richardson.james.bukkit.utilities.command.argument.SimpleArgumentMetadata;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;

public class BanCountOptionArgument {

	public static final Messages MESSAGES = MessagesFactory.getMessages();

	public static IntegerMarshaller getInstance(final int defaultValue) {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(MESSAGES.banCountArgumentId(), MESSAGES.banCountArgumentName(), MESSAGES.banCountArgumentDescription());
		final OptionArgument argument = new OptionArgument(metadata, null);
		return new IntegerMarshaller(argument, defaultValue);
	}

}
