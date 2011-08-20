
package name.richardson.james.banhammer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import name.richardson.james.banhammer.cache.CachedList;
import name.richardson.james.banhammer.commands.BanCommand;
import name.richardson.james.banhammer.commands.CheckCommand;
import name.richardson.james.banhammer.commands.HistoryCommand;
import name.richardson.james.banhammer.commands.KickCommand;
import name.richardson.james.banhammer.commands.PardonCommand;
import name.richardson.james.banhammer.commands.PurgeCommand;
import name.richardson.james.banhammer.commands.RecentCommand;
import name.richardson.james.banhammer.commands.ReloadCommand;
import name.richardson.james.banhammer.exceptions.NoMatchingPlayerException;
import name.richardson.james.banhammer.listeners.BanHammerPlayerListener;
import name.richardson.james.banhammer.persistant.BanRecord;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class BanHammer extends JavaPlugin {

  public static ResourceBundle messages;
  private static EbeanServer db;

  private static BanHammer instance;
  private final static Locale locale = Locale.getDefault();

  private final static Logger logger = Logger.getLogger("Minecraft");
  private final static Boolean notifyPlayers = true;
  static Map<String, Long> bans = new HashMap<String, Long>();

  public CachedList cache;
  public PermissionHandler externalPermissions;

  private final CommandManager cm;
  private PluginDescriptionFile desc;

  private final BanHammerPlayerListener playerListener;
  private PluginManager pm;

  public BanHammer() {
    BanHammer.instance = this;
    this.cm = new CommandManager();

    this.playerListener = new BanHammerPlayerListener(this);

    if (messages == null)
      try {
        BanHammer.messages = ResourceBundle.getBundle("name.richardson.james.banhammer.localisation.Messages", locale);
      } catch (MissingResourceException e) {
        BanHammer.messages = ResourceBundle.getBundle("name.richardson.james.banhammer.localisation.Messages");
        log(Level.WARNING, String.format(messages.getString("noLocalisationFound"), locale.getDisplayLanguage()));
      }
  }

  public static EbeanServer getDb() {
    return db;
  }

  public static BanHammer getInstance() {
    return instance;
  }

  public static void log(Level level, String msg) {
    logger.log(level, "[BanHammer] " + msg);
  }

  @Override
  public List<Class<?>> getDatabaseClasses() {
    List<Class<?>> list = new ArrayList<Class<?>>();
    list.add(BanRecord.class);
    return list;
  }

  public String getMessage(String key) {
    return messages.getString(key);
  }

  public String getName() {
    return this.desc.getName();
  }

  public String getSenderName(CommandSender sender) {
    if (sender instanceof Player) {
      Player player = (Player) sender;
      String senderName = player.getName();
      return senderName;
    } else return "console";
  }

  public String getVersion() {
    return this.desc.getVersion();
  }

  public Player matchPlayer(String playerName) throws NoMatchingPlayerException {
    List<Player> list = this.getServer().matchPlayer(playerName);
    if (list.size() == 1)
      return list.get(0);
    else throw new NoMatchingPlayerException();
  }

  public Player matchPlayerExactly(String playerName) throws NoMatchingPlayerException {
    for (Player player : this.getServer().getOnlinePlayers())
      if (player.getName().equalsIgnoreCase(playerName))
        return player;
    throw new NoMatchingPlayerException();
  }

  public void notifyPlayers(String message, CommandSender sender) {
    if (notifyPlayers) {
      if (this.getSenderName(sender).equals("console"))
        sender.sendMessage(message);
      this.getServer().broadcastMessage(message);
    } else sender.sendMessage(message);
  }

  public void onDisable() {
    this.cache.unload();
    log(Level.INFO, String.format(messages.getString("pluginDisabled"), this.desc.getName()));
  }

  public void onEnable() {
    this.desc = this.getDescription();
    db = this.getDatabase();
    this.pm = this.getServer().getPluginManager();

    // Setup environment
    this.setupDatabase();
    this.connectPermissions();

    // Register events
    this.pm.registerEvent(Event.Type.PLAYER_LOGIN, this.playerListener, Event.Priority.Highest, this);

    // Register commands
    this.getCommand("ban").setExecutor(new BanCommand(this));
    this.getCommand("kick").setExecutor(new KickCommand(this));
    this.getCommand("pardon").setExecutor(new PardonCommand(this));
    this.getCommand("bh").setExecutor(this.cm);
    this.cm.registerCommand("check", new CheckCommand(this));
    this.cm.registerCommand("history", new HistoryCommand(this));
    this.cm.registerCommand("purge", new PurgeCommand(this));
    this.cm.registerCommand("recent", new RecentCommand(this));
    this.cm.registerCommand("reload", new ReloadCommand(this));

    // Create cache
    this.cache = new CachedList();

    log(Level.INFO, String.format(messages.getString("bansLoaded"), Integer.toString(this.cache.size())));
    log(Level.INFO, String.format(messages.getString("pluginEnabled"), this.desc.getFullName()));
  }

  private void connectPermissions() {
    final Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
    if (permissionsPlugin != null) {
      this.externalPermissions = ((Permissions) permissionsPlugin).getHandler();
      log(Level.INFO, String.format(messages.getString("usingPermissionsAPI"), ((Permissions) permissionsPlugin).getDescription().getFullName()));
      log(Level.WARNING, messages.getString("permissionsAPIDeprecated"));
    }
  }

  private void setupDatabase() {
    try {
      this.getDatabase().find(BanRecord.class).findRowCount();
    } catch (PersistenceException ex) {
      log(Level.WARNING, messages.getString("noDatabase"));
      this.installDDL();
    }
  }

}
