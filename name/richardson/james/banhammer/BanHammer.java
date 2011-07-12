package name.richardson.james.banhammer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import name.richardson.james.banhammer.listeners.BanHammerPlayerListener;
import name.richardson.james.banhammer.persistant.BanHammerRecord;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.avaje.ebean.EbeanServer;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class BanHammer extends JavaPlugin {
	
	private final static Locale locale = Locale.getDefault();
	private final static Logger logger = Logger.getLogger("Minecraft");
	private final static ResourceBundle messages = ResourceBundle.getBundle("localisation", locale);
	
	private PluginManager pm;
    private PluginDescriptionFile desc;
    
    private static EbeanServer db;
	private static BanHammer instance;
    
    private final BanHammerPlayerListener playerListener;
	
    static Map<String,Long> bans = new HashMap<String,Long>();
    static PermissionHandler permissions;
    
    public BanHammer() {
    	BanHammer.instance = this;
    	this.playerListener = new BanHammerPlayerListener();
    }
	

	public void onEnable(){
    	desc = getDescription();
		db = getDatabase();
		
		setupDatabase();
		// setupPermissions();
		
		// Register events
		pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Event.Priority.Highest, this);
		
		log(Level.INFO, String.format(messages.getString("pluginEnabled"), desc.getFullName()));
	}
	
	public void onDisable(){
		log(Level.INFO, String.format(messages.getString("pluginDisabled"), desc.getName()));
	}
	
	public static void log(Level level, String msg) {
        logger.log(level, "[BanHammer] " + msg);
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
	
	private void setupDatabase() {
		try {
            getDatabase().find(BanHammerRecord.class).findRowCount();
        } catch (PersistenceException ex) {
        	installDDL();
        }
	}

}
