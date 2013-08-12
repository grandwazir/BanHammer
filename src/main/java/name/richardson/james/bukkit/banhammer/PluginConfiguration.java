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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;
import name.richardson.james.bukkit.utilities.logging.PrefixedLogger;
import name.richardson.james.bukkit.utilities.persistence.configuration.SimplePluginConfiguration;

public class PluginConfiguration extends SimplePluginConfiguration {

	private final Map<String, Long> limits = new LinkedHashMap<String, Long>();
	private final Logger logger = PrefixedLogger.getLogger(this.getClass());

	public PluginConfiguration(final File file, final InputStream defaults)
	throws IOException {
		super(file, defaults);
		logger.config(toString());
	}

	public Map<String, Long> getBanLimits() {
		this.limits.clear();
		final ConfigurationSection section = this.getConfiguration().getConfigurationSection("limits");
		for (final String key : section.getKeys(false)) {
			final String name = key;
			final Long length = TimeFormatter.parseTime(section.getString(key));
			if (length != 0) {
				this.limits.put(name, length);
			} else {
				this.logger.log(Level.WARNING, "banhammer.limit-invalid", key);
			}
		}
		return this.limits;
	}

	public Set<String> getImmunePlayers() {
		final Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		set.addAll(this.getConfiguration().getStringList("immune-players"));
		return set;
	}

	public long getUndoTime() {
		return TimeFormatter.parseTime(this.getConfiguration().getString("undo-time", "1m"));
	}

	public boolean isAliasEnabled() {
		return this.getConfiguration().getBoolean("alias-plugin.enabled");
	}

	@Override
	public String toString() {
		return "PluginConfiguration{" +
		"limits=" + limits +
		"} " + super.toString();
	}

}
