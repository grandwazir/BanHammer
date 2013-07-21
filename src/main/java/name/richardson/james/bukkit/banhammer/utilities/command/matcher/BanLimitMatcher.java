package name.richardson.james.bukkit.banhammer.utilities.command.matcher;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import name.richardson.james.bukkit.utilities.command.matcher.Matcher;

public class BanLimitMatcher implements Matcher {

	private final Collection<String> limitNames;
	private final String prefix = "t:";

	public BanLimitMatcher(Collection<String> limitNames) {
		this.limitNames = limitNames;
	}

	@Override
	public Set<String> matches(String argument) {
		TreeSet<String> results = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		argument = argument.toLowerCase(Locale.ENGLISH).replaceAll(prefix, "");
		for (String string : limitNames) {
			if (results.size() == Matcher.MAX_MATCHES) break;
			if (!string.toLowerCase(Locale.ENGLISH).startsWith(argument)) continue;
			results.add(prefix + string);
		}
		return results;
	}

}
