package name.richardson.james.bukkit.banhammer.argument;

import java.util.Date;

import name.richardson.james.bukkit.utilities.command.argument.AbstractMarshaller;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.time.PreciseTimeFormatter;
import name.richardson.james.bukkit.utilities.time.TimeFormatter;

public class SimpleTimeMarshaller extends AbstractMarshaller implements TimeMarshaller {

	private TimeFormatter formatter = new PreciseTimeFormatter();

	public SimpleTimeMarshaller(final Argument argument) {
		super(argument);
	}

	@Override public Date getDate() {
		return new Date(System.currentTimeMillis() + getTime());
	}

	@Override public long getTime() {
		return formatter.getDurationInMilliseconds(getString());
	}

}
