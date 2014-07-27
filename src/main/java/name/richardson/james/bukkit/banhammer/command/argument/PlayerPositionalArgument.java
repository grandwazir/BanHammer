package name.richardson.james.bukkit.banhammer.command.argument;

import org.bukkit.Server;

import name.richardson.james.bukkit.utilities.command.argument.*;
import name.richardson.james.bukkit.utilities.command.argument.suggester.OnlinePlayerSuggester;
import name.richardson.james.bukkit.utilities.command.argument.suggester.Suggester;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;

public class PlayerPositionalArgument {

	public static final Messages MESSAGES = MessagesFactory.getMessages();

	public static PlayerMarshaller getInstance(Server server, int position, boolean required) {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(MESSAGES.playerArgumentID(), MESSAGES.playerArgumentName(), MESSAGES.playerArgumentDescription(), MESSAGES.playerArgumentInvalid());
		Suggester suggester = new OnlinePlayerSuggester(server);
		if (required) {
			Argument argument = new RequiredPositionalArgument(metadata, suggester, position);
			return new RequiredPlayerMarshaller(argument, server);
		} else {
			Argument argument = new PositionalArgument(metadata, suggester, position);
			return new SimplePlayerMarshaller(argument, server);
		}
	}

}
