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

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import name.richardson.james.bukkit.util.Plugin;
import name.richardson.james.bukkit.util.Time;
import name.richardson.james.bukkit.util.configuration.AbstractConfiguration;

public class BanHammerConfiguration extends AbstractConfiguration {

  private final Map<String, Long> limits = new LinkedHashMap<String, Long>();

  public BanHammerConfiguration(final Plugin plugin) throws IOException {
    super(plugin, "config.yml");
  }

  public Map<String, Long> getBanLimits() {
    return Collections.unmodifiableMap(this.limits);
  }

  public boolean isDebugging() {
    return this.configuration.getBoolean("debugging");
  }

  public void setBanLimits() {
    this.limits.clear();
    this.logger.debug(String.format("Registering ban limits"));
    final ConfigurationSection section = this.configuration.getConfigurationSection("ban-limits");
    for (final String key : section.getKeys(false)) {
      try {
        final String name = key;
        final Long length = Time.parseTime(section.getString(key));
        this.limits.put(name, length);
        this.logger.debug(String.format("Creating new ban limit %s with a maximum time of %s (%d).", name, section.getString(key), length));
      } catch (final NumberFormatException e) {
        this.logger.warning(String.format("Ban limit '%s' specifies an invalid number format.", key));
      }
    }
  }

  @Override
  public void setDefaults() throws IOException {
    this.logger.debug(String.format("Apply default configuration."));
    final org.bukkit.configuration.file.YamlConfiguration defaults = this.getDefaults();
    this.configuration.setDefaults(defaults);
    this.configuration.options().copyDefaults(true);
    // set an example kit if necessary
    if (!this.configuration.isConfigurationSection("ban-limits")) {
      this.logger.debug("Creating example ban limits.");
      this.configuration.createSection("ban-limits");
      final ConfigurationSection section = this.configuration.getConfigurationSection("ban-limits");
      section.set("warning", "1h");
      section.set("short", "1d");
      section.set("medium", "3d");
      section.set("long", "7d");
    }
    this.save();
  }

}
