package name.richardson.james.bukkit.banhammer.utilities.formatters;

import name.richardson.james.bukkit.utilities.formatters.AbstractChoiceFormatter;

public class BanCountChoiceFormatter extends AbstractChoiceFormatter {

	public BanCountChoiceFormatter() {
		setFormats("no-bans", "one-ban", "many-bans");
		setLimits(0,1,2);
	}

}
