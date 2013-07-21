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
import java.util.*;

import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;
import name.richardson.james.bukkit.utilities.persistence.configuration.SimplePluginConfiguration;

public class BanHammerConfiguration extends SimplePluginConfiguration {

	private final Map<String, Long> limits = new LinkedHashMap<String, Long>();

	public BanHammerConfiguration(final File file, final InputStream defaults)
	throws IOException {
		super(file, defaults);
	}

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

	public boolean isAliasEnabled() {
		return this.getConfiguration().getBoolean("alias-plugin.enabled");
	}

}
