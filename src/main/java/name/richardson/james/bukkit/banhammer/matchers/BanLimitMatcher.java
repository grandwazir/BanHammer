package name.richardson.james.bukkit.banhammer.matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import name.richardson.james.bukkit.utilities.matchers.Matcher;

public class BanLimitMatcher implements Matcher {

	private static Set<String> limits;

	public static void setBanLimits(final Set<String> limits) {
		BanLimitMatcher.limits = limits;
	}

	public List<String> getMatches(String argument) {
		argument = argument.toLowerCase();
		argument = argument.replaceAll("t:", "");
		final List<String> matches = new ArrayList<String>();
		for (final String limitName : BanLimitMatcher.limits) {
			if (limitName.startsWith(argument)) {
				matches.add("t:" + limitName);
			}
		}
		return matches;
	}

}
