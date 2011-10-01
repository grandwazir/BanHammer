package name.richardson.james.banhammer.configurations;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.exceptions.UnableToCreateConfigurationException;


public class SettingsConfiguration extends Configuration {

  public SettingsConfiguration(File file, BanHammer plugin) throws UnableToCreateConfigurationException {
    super(file, plugin);
  }

  @Override
  void setDefaults(File file) throws UnableToCreateConfigurationException {
    try {
      file.getParentFile().mkdirs();
      file.createNewFile();
      this.getString("chat-flood-protection", "");
      this.getBoolean("chat-flood-protection.enabled", true);
      this.getString("chat-flood-protection.", "To ask a question just type /oracle ask");
      this.save();
    } catch (final IOException e) {
      throw new UnableToCreateConfigurationException(file.getPath());
    }
  }
  
  

}
