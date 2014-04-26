package name.richardson.james.bukkit.banhammer.utilities.formatters;

import name.richardson.james.bukkit.utilities.formatters.AbstractChoiceFormatter;
import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.*;

public class BanCountChoiceFormatter extends AbstractChoiceFormatter {

	public BanCountChoiceFormatter() {
		setFormats(CHOICE_NO_BANS, CHOICE_ONE_BAN, CHOICE_MANY_BANS);
		setLimits(0,1,2);
	}

}
