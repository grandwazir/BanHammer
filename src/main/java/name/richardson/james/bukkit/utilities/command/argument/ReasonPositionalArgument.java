package name.richardson.james.bukkit.utilities.command.argument;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerMessages.*;

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
