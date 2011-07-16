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
import name.richardson.james.banhammer.commands.BanHammerCommandManager;
import name.richardson.james.banhammer.exceptions.NoMatchingPlayer;
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

	public static CachedList cache;
	public static ResourceBundle messages;
	
	static Map<String, Long> bans = new HashMap<String, Long>();

	private static BanHammerCommandManager commands;
	private static EbeanServer db;
	private static BanHammer instance;

	private final static Locale locale = Locale.getDefault();
	private final static Logger logger = Logger.getLogger("Minecraft");
	private final static Boolean notifyPlayers = true;

	static PermissionHandler permissions;
	
	private PluginDescriptionFile desc;

	private final BanHammerPlayerListener playerListener;

	private PluginManager pm;

	public BanHammer() {
		BanHammer.instance = this;
		BanHammer.commands = new BanHammerCommandManager(this);
		
		this.playerListener = new BanHammerPlayerListener();

		if (messages == null) {
			try {
				BanHammer.messages = ResourceBundle.getBundle("name.richardson.james.banhammer.localisation.Messages", locale);
			} catch (MissingResourceException e) {
				BanHammer.messages = ResourceBundle.getBundle("name.richardson.james.banhammer.localisation.Messages");
				log(Level.WARNING, String.format(messages.getString("noLocalisationFound"), locale.getDisplayLanguage()));
			}
		}
	}
	
	public void onDisable() {
		cache.unload();
		log(Level.INFO, String.format(messages.getString("pluginDisabled"), desc.getName()));
	}

	public void onEnable() {
		desc = getDescription();
		db = getDatabase();
		pm = getServer().getPluginManager();

		setupDatabase();
		setupPermissions();

		// Register events
		pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Event.Priority.Highest, this);
		
		// Register commands
		getCommand("ban").setExecutor(commands);
		getCommand("kick").setExecutor(commands);
		getCommand("pardon").setExecutor(commands);
		getCommand("tempban").setExecutor(commands);
		getCommand("bh").setExecutor(commands);
		
		// Create cache
		cache = new CachedList();
		
		log(Level.INFO, String.format(messages.getString("bansLoaded"), Integer.toString(cache.size())));
		log(Level.INFO, String.format(messages.getString("pluginEnabled"), desc.getFullName()));
	}

	public static EbeanServer getDb() {
		return db;
	}

	public static PermissionHandler getPermissions() {
		return permissions;
	}
	
	public static BanHammer getInstance() {
		return instance;
	}
	
	public String getName() {
		return desc.getName();
	}
	
	public Player matchPlayerExactly(String playerName) throws NoMatchingPlayer {
		for (Player player : getServer().getOnlinePlayers()) {
			if (player.getName().equalsIgnoreCase(playerName))
				return player;
		}
		throw new NoMatchingPlayer();
	}
	
	public Player matchPlayer(String playerName) throws NoMatchingPlayer {
		List<Player> list = getServer().matchPlayer(playerName);
		if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new NoMatchingPlayer();
		}
	}
	
	public String getSenderName(CommandSender sender) {
		if (sender instanceof Player) {
			Player player = (Player)sender;
			String senderName = player.getName();
			return senderName;
		} else {
			return "console";
		}
	}

	public String getVersion() {
		return desc.getVersion();
	}
	
	public static void log(Level level, String msg) {
		logger.log(level, "[BanHammer] " + msg);
	}
	
	public void notifyPlayers(String message, CommandSender sender) {
		if (notifyPlayers) {
			if (getSenderName(sender).equals("console"))
				sender.sendMessage(message);
			getServer().broadcastMessage(message);
		} else {
			sender.sendMessage(message);
		}
	}

	private void setupDatabase() {
		try {
			getDatabase().find(BanRecord.class).findRowCount();
		} catch (PersistenceException ex) {
			log(Level.WARNING, messages.getString("noDatabase"));
			installDDL();
		}
	}

	private void setupPermissions() {
		Plugin plugin = pm.getPlugin("Permissions");

		if (permissions == null && plugin != null) {
			log(Level.INFO, String.format(messages.getString("permissionsFound"),
					plugin.getDescription().getFullName()));
			permissions = ((Permissions) plugin).getHandler();
		} else {
			log(Level.WARNING, messages.getString("permissionsNotFound"));
		}
	}
	
	
	@Override
  public List<Class<?>> getDatabaseClasses() {
	  List<Class<?>> list = new ArrayList<Class<?>>();
	  list.add(BanRecord.class);
	  return list;
  }

}
