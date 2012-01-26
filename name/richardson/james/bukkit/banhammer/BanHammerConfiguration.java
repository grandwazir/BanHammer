package name.richardson.james.bukkit.banhammer;

import java.io.IOException;

import name.richardson.james.bukkit.util.Plugin;
import name.richardson.james.bukkit.util.configuration.AbstractConfiguration;


public class BanHammerConfiguration extends AbstractConfiguration {

  public BanHammerConfiguration(Plugin plugin) throws IOException {
    super(plugin, "config.yml");
  }
  
  public boolean isDebugging() {
    return this.configuration.getBoolean("debugging");
  }

}
