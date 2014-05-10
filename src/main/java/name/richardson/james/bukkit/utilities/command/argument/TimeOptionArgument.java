package name.richardson.james.bukkit.utilities.command.argument;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerMessages.*;

public class TimeOptionArgument {

	public static TimeMarshaller getInstance() {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(ARGUMENT_TIME_ID, ARGUMENT_TIME_NAME, ARGUMENT_TIME_DESC);
		Argument argument = new OptionArgument(metadata, null);
		return new TimeMarshaller(argument);
	}


}
