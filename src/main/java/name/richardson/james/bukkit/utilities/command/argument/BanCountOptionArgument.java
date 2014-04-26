package name.richardson.james.bukkit.utilities.command.argument;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.*;

public class BanCountOptionArgument {

	public static IntegerMarshaller getInstance(final int defaultValue) {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(ARGUMENT_BANCOUNT_ID, ARGUMENT_BANCOUNT_NAME, ARGUMENT_BANCOUNT_DESC);
		final OptionArgument argument = new OptionArgument(metadata, null);
		return new IntegerMarshaller(argument, defaultValue);
	}

}
