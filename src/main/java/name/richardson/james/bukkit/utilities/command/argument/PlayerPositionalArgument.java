package name.richardson.james.bukkit.utilities.command.argument;

import org.bukkit.Server;

import name.richardson.james.bukkit.utilities.command.argument.suggester.OnlinePlayerSuggester;
import name.richardson.james.bukkit.utilities.command.argument.suggester.Suggester;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.*;

public class PlayerPositionalArgument {

	public static PlayerMarshaller getInstance(Server server, int position, boolean required) {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(ARGUMENT_PLAYER_ID, ARGUMENT_PLAYER_NAME, ARGUMENT_PLAYER_DESC, ARGUMENT_PLAYER_ERROR);
		Suggester suggester = new OnlinePlayerSuggester(server);
		if (required) {
			Argument argument = new RequiredPositionalArgument(metadata, suggester, position);
			return new RequiredPlayerMarshaller(argument, server);
		} else {
			Argument argument = new PositionalArgument(metadata, suggester, position);
			return new PlayerMarshaller(argument, server);
		}
	}

}
