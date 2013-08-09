package name.richardson.james.bukkit.banhammer;

import name.richardson.james.bukkit.utilities.formatters.localisation.LocalisedChoiceFormatter;

public class BanCountChoiceFormatter extends LocalisedChoiceFormatter {

	public BanCountChoiceFormatter() {
		setFormats("no-bans", "one-ban", "many-bans");
		setLimits(0,1,2);
	}

}
