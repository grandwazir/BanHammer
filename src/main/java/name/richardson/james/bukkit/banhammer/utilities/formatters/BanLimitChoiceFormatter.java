package name.richardson.james.bukkit.banhammer.utilities.formatters;

import name.richardson.james.bukkit.utilities.formatters.AbstractChoiceFormatter;

/**
 * Created with IntelliJ IDEA. User: james Date: 22/08/13 Time: 17:05 To change this template use File | Settings | File Templates.
 */
public class BanLimitChoiceFormatter extends AbstractChoiceFormatter {

	public BanLimitChoiceFormatter() {
		setFormats("no-limits", "one-limit", "many-limits");
		setLimits(0,1,2);
	}

}
