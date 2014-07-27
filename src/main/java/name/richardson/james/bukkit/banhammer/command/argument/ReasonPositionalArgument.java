package name.richardson.james.bukkit.banhammer.command.argument;

import name.richardson.james.bukkit.utilities.command.argument.*;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;

public class ReasonPositionalArgument {

	public static final Messages MESSAGES = MessagesFactory.getMessages();

	public static Argument getInstance(int position, boolean required) {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(MESSAGES.reasonArgumentId(), MESSAGES.reasonArgumentName(), MESSAGES.reasonArgumentDescription(), MESSAGES.reasonArgumentInvalid());
		if (required) {
			return new RequiredJoinedPositionalArgument(metadata, position);
		} else {
			return new JoinedPositionalArgument(metadata, position);
		}
	}

}
