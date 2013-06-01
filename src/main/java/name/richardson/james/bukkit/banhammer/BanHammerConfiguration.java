/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * BanHammerConfiguration.java is part of BanHammer.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

import name.richardson.james.bukkit.utilities.configuration.SimplePluginConfiguration;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;
import name.richardson.james.bukkit.utilities.logging.PluginLogger;

public class BanHammerConfiguration extends SimplePluginConfiguration {

	/** The configured ban limits. */
	private final Map<String, Long> limits = new LinkedHashMap<String, Long>();
	private final Logger logger = PluginLogger.getLogger(this.getClass());

	public BanHammerConfiguration(final File file, final InputStream defaults) throws IOException {
		super(file, defaults);
		this.setBanLimits();
	}

	/**
	 * Gets the ban limits.
	 * 
	 * @return the ban limits
	 */
	public Map<String, Long> getBanLimits() {
		return Collections.unmodifiableMap(this.limits);
	}

	public List<String> getImmunePlayers() {
		final List<String> list = this.getConfiguration().getStringList("immune-players");
		return (list != null) ? list : new ArrayList<String>();
	}

	public long getUndoTime() {
		return TimeFormatter.parseTime(this.getConfiguration().getString("undo-time", "1m"));
	}

	/**
	 * Checks if is alias should be enabled.
	 * 
	 * @return true, if is alias is enabled
	 */
	public boolean isAliasEnabled() {
		return this.getConfiguration().getBoolean("alias-plugin.enabled");
	}

	/**
	 * Read and sets the ban limits.
	 */
	private void setBanLimits() {
		this.limits.clear();
		final ConfigurationSection section = this.getConfiguration().getConfigurationSection("ban-limits");
		for (final String key : section.getKeys(false)) {
			try {
				final String name = key;
				final Long length = TimeFormatter.parseTime(section.getString(key));
				this.limits.put(name, length);
			} catch (final NumberFormatException e) {
				this.logger.log(Level.WARNING, "banhammer.limit-invalid", key);
			}
		}
	}

}
