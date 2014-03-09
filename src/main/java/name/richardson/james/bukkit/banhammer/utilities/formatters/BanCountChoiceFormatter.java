package name.richardson.james.bukkit.banhammer.utilities.formatters;

import name.richardson.james.bukkit.utilities.formatters.AbstractChoiceFormatter;

import name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation;

public class BanCountChoiceFormatter extends AbstractChoiceFormatter {

	public BanCountChoiceFormatter() {
		setFormats(BanHammerLocalisation.CHOICE_NO_BANS, BanHammerLocalisation.CHOICE_ONE_BAN, BanHammerLocalisation.CHOICE_MANY_BANS);
		setLimits(0,1,2);
	}

}
