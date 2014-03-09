/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * PluginConfiguration.java is part of BanHammer.
 *
 * BanHammer is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.configuration.ConfigurationSection;

import name.richardson.james.bukkit.utilities.formatters.time.PreciseDurationTimeFormatter;
import name.richardson.james.bukkit.utilities.formatters.time.TimeFormatter;
import name.richardson.james.bukkit.utilities.persistence.configuration.SimplePluginConfiguration;

public final class PluginConfiguration extends SimplePluginConfiguration {

	private static final String IMMUNE_PLAYERS_KEY = "immune-players";
	private static final String UNDO_TIME_KEY = "undo-time";
	private static final String ALIAS_PLUGIN_ENABLED_KEY = "alias-plugin.enabled";
	private static final String LIMITS_KEY = "ban-limits";

	private final Map<String, Long> limits = new LinkedHashMap<String, Long>();
	private final TimeFormatter timeFormatter = new PreciseDurationTimeFormatter();

	public PluginConfiguration(final File file, final InputStream defaults)
	throws IOException {
		super(file, defaults);
	}

	public Map<String, Long> getBanLimits() {
		this.limits.clear();
		final ConfigurationSection section = this.getConfiguration().getConfigurationSection(LIMITS_KEY);
		for (final String key : section.getKeys(false)) {
			final Long length = timeFormatter.getDurationInMilliseconds(section.getString(key));
			if (length != 0) {
				this.limits.put(key, length);
			}
		}
		return this.limits;
	}

	public Set<String> getImmunePlayers() {
		final Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		set.addAll(this.getConfiguration().getStringList(IMMUNE_PLAYERS_KEY));
		return set;
	}

	public long getUndoTime() {
		return timeFormatter.getDurationInMilliseconds(this.getConfiguration().getString(UNDO_TIME_KEY, "1m"));
	}

	public boolean isAliasEnabled() {
		return this.getConfiguration().getBoolean(ALIAS_PLUGIN_ENABLED_KEY);
	}

	@Override
	public String toString() {
		return "PluginConfiguration{" +
		"limits=" + limits +
		"} " + super.toString();
	}

}
