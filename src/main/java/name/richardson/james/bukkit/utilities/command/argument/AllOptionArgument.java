package name.richardson.james.bukkit.utilities.command.argument;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.*;

public class AllOptionArgument {

	public static BooleanMarshaller getInstance() {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(ARGUMENT_ALL_ID, ARGUMENT_ALL_NAME, ARGUMENT_ALL_DESC);
		final Argument argument = new SwitchArgument(metadata);
		return new BooleanMarshaller(argument);
	}

}
