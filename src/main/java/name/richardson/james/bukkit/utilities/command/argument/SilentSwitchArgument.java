package name.richardson.james.bukkit.utilities.command.argument;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.*;

public class SilentSwitchArgument extends BooleanMarshaller {

	public static SilentSwitchArgument getInstance() {
		ArgumentMetadata metadata = new SimpleArgumentMetadata(ARGUMENT_SILENT_ID, ARGUMENT_SILENT_NAME, ARGUMENT_SILENT_DESC);
		return new SilentSwitchArgument(metadata);
	}

	private SilentSwitchArgument(final ArgumentMetadata metadata) {
		super(new SwitchArgument(metadata));
	}

}
