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

  public BanHammerConfiguration(Plugin plugin) throws IOException {
    super(plugin, "config.yml");
  }
  
  public boolean isDebugging() {
    return this.configuration.getBoolean("debugging");
  }
  
  public void setDefaults() throws IOException {
    logger.debug(String.format("Apply default configuration."));
    final org.bukkit.configuration.file.YamlConfiguration defaults = this.getDefaults();
    this.configuration.setDefaults(defaults);
    this.configuration.options().copyDefaults(true);
    // set an example kit if necessary
    if (!configuration.isConfigurationSection("ban-limits")) {
      logger.debug("Creating example ban limits.");
      configuration.createSection("ban-limits");
      final ConfigurationSection section = configuration.getConfigurationSection("ban-limits");
      section.set("short", "1d");
      section.set("medium", "3d");
      section.set("long", "7d");
    }
    this.save();
  }
  
  public void setBanLimits() {
    limits.clear();
    logger.debug(String.format("Registering ban limits"));
    final ConfigurationSection section = configuration.getConfigurationSection("ban-limits");
    for (String key : section.getKeys(false)) {
      try {
        final String name = key;
        final Long length = Time.parseTime(section.getString(key));
        limits.put(name, length);
        logger.debug(String.format("Creating new ban limit %s with a maximum time of %s (%d).", name, section.getString(key), length));
      } catch (NumberFormatException e) {
        logger.warning(String.format("Ban limit '%s' specifies an invalid number format.", key));
      }
    }
  }
  
  public Map<String, Long> getBanLimits() {
    return Collections.unmodifiableMap(limits);
  }
  
}
