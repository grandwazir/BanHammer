package name.richardson.james.banhammer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import name.richardson.james.banhammer.cache.CachedList;
import name.richardson.james.banhammer.listeners.BanHammerPlayerListener;
import name.richardson.james.banhammer.persistant.BanRecord;

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

	private static EbeanServer db;
	private static BanHammer instance;

	private final static Locale locale = Locale.getDefault();
	private final static Logger logger = Logger.getLogger("Minecraft");

	static PermissionHandler permissions;
	
	private PluginDescriptionFile desc;

	private final BanHammerPlayerListener playerListener;

	private PluginManager pm;

	public BanHammer() {
		BanHammer.instance = this;
		this.playerListener = new BanHammerPlayerListener();

		if (messages == null) {
			try {
				BanHammer.messages = ResourceBundle.getBundle(
						"name.richardson.james.banhammer.localisation.Messages", locale);
			} catch (MissingResourceException e) {
				BanHammer.messages = ResourceBundle
						.getBundle("name.richardson.james.banhammer.localisation.Messages");
				log(Level.WARNING, String.format(messages
						.getString("noLocalisationFound"), locale.getDisplayLanguage()));
			}
		}
	}
	
	public void onDisable() {
		log(Level.INFO, String.format(messages.getString("pluginDisabled"), desc
				.getName()));
	}

	public void onEnable() {
		desc = getDescription();
		db = getDatabase();
		pm = getServer().getPluginManager();

		setupDatabase();
		cache = new CachedList();

		setupPermissions();

		// Register events
		pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener,
				Event.Priority.Highest, this);

		log(Level.INFO, String.format(messages.getString("pluginEnabled"), desc
				.getFullName()));
	}

	public static EbeanServer getDb() {
		return db;
	}

	public String getName() {
		return desc.getName();
	}

	public String getVersion() {
		return desc.getVersion();
	}
	
	public static void log(Level level, String msg) {
		logger.log(level, "[BanHammer] " + msg);
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

}
