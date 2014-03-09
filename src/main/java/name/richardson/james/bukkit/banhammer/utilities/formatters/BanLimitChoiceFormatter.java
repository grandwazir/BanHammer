package name.richardson.james.bukkit.banhammer.utilities.formatters;

import name.richardson.james.bukkit.utilities.formatters.AbstractChoiceFormatter;

import name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation;

public class BanLimitChoiceFormatter extends AbstractChoiceFormatter {

	public BanLimitChoiceFormatter() {
		setFormats(BanHammerLocalisation.CHOICE_NO_LIMITS, BanHammerLocalisation.CHOICE_ONE_LIMIT, BanHammerLocalisation.CHOICE_MANY_LIMITS);
		setLimits(0,1,2);
	}

}
