package name.richardson.james.bukkit.banhammer.utilities.formatters;

import name.richardson.james.bukkit.utilities.formatters.AbstractChoiceFormatter;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.*;

public class BanLimitChoiceFormatter extends AbstractChoiceFormatter {

	public BanLimitChoiceFormatter() {
		setFormats(CHOICE_NO_LIMITS, CHOICE_ONE_LIMIT, CHOICE_MANY_LIMITS);
		setLimits(0,1,2);
	}

}
