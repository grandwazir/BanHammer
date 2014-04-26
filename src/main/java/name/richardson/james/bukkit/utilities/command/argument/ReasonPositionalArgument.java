package name.richardson.james.bukkit.utilities.command.argument;

import org.bukkit.Server;

import name.richardson.james.bukkit.utilities.command.argument.suggester.OnlinePlayerSuggester;
import name.richardson.james.bukkit.utilities.command.argument.suggester.Suggester;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.*;

public class ReasonPositionalArgument {

	public static Argument getInstance(int position, boolean required) {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(ARGUMENT_REASON_ID, ARGUMENT_REASON_NAME, ARGUMENT_REASON_DESC, ARGUMENT_REASON_ERROR);
		if (required) {
			return new RequiredJoinedPositionalArgument(metadata, position);
		} else {
			return new JoinedPositionalArgument(metadata, position);
		}
	}

}
