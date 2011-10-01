package name.richardson.james.banhammer.configurations;

import java.io.File;
import java.util.logging.Level;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.exceptions.UnableToCreateConfigurationException;


public abstract class Configuration extends org.bukkit.util.config.Configuration {

  protected final BanHammer plugin;
  protected File file;

  public Configuration(File file, BanHammer plugin) throws UnableToCreateConfigurationException {
    super(file);
    this.plugin = plugin;
    this.file = file;
    this.checkExistance();
  }

  abstract void setDefaults(File file) throws UnableToCreateConfigurationException;
  
  protected boolean isEmpty() {
    return this.getAll().isEmpty();
  }
  
  void checkExistance() throws UnableToCreateConfigurationException {
    this.load();
    if (isEmpty()) {
      BanHammer.log(Level.WARNING, String.format(plugin.getMessage("configurationNotFound"), this.getClass().getSimpleName()));
      BanHammer.log(Level.INFO, String.format(plugin.getMessage("configurationCreation"), this.getClass().getSimpleName(), file.getPath()));
      setDefaults(file);
    } else {
      BanHammer.log(Level.INFO, String.format(plugin.getMessage("configurationLoaded"), this.getClass().getSimpleName(), file.getPath()));
    }
  }
  
}
