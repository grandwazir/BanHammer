package name.richardson.james.banhammer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import name.richardson.james.banhammer.BanHammerPlayerListener;
import name.richardson.james.banhammer.BanHammerRecord;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class BanHammerPlugin extends JavaPlugin {
	
	final BanHammerPlugin plugin = this;
	static Logger log = Logger.getLogger("Minecraft");
	static PermissionHandler CurrentPermissions = null;
	static PluginDescriptionFile info = null;
	
	// Command lists
	static final List<String> commands = Arrays.asList("kick", "pardon", "ban");
	static final List<String> subCommands = Arrays.asList("check", "history");
	
	// Banned players
	static ArrayList<String> permenantBans = new ArrayList<String>();
	static Map<String,Long> temporaryBans = new HashMap<String,Long>();
	
	// Listeners
	private final BanHammerPlayerListener PlayerListener = new BanHammerPlayerListener(this);
	private static final boolean broadcastActions = true;

	public void onEnable(){
		info = this.getDescription();
		log.info(String.format("[BanHammer] %s is enabled!", info.getFullName()));
		
		// Setup Environment
		setupDatabase();
		setupPermissions();
		
		// Register events
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_LOGIN, PlayerListener, Event.Priority.Highest, this);
		
		// Load banned players
		BanHammerRecord.setup(this);
		for(BanHammerRecord ban : BanHammerRecord.findPermenantBans())
			permenantBans.add(ban.getPlayer());
		log.info("[BanHammer] - " + Integer.toString(temporaryBans.size()) + " temporary ban(s) found");
		for(BanHammerRecord ban : BanHammerRecord.findTemporaryBans())
			temporaryBans.put(ban.getPlayer(), ban.getExpiresAt());
		log.info("[BanHammer] - " + Integer.toString(permenantBans.size()) + " permenant ban(s) found");
	}
	
	public void onDisable(){
		log.info(String.format("[BanHammer] %s is disabled!", info.getName()));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		final String command = cmd.getName();
		
		// Handle root commands
		if (commands.contains(command)) {
			if (!playerHasPermission(sender, "bh." + cmd.getName())) return true;
			if (command.equalsIgnoreCase("ban")) return banPlayer(sender, args);
			if (command.equalsIgnoreCase("kick")) return kickPlayer(sender, args);
			if (command.equalsIgnoreCase("pardon")) return pardonPlayer(sender, args);
		}
		
		// Handle sub commands commands
		if (command.equalsIgnoreCase("bh")) {
			if (args.length == 0) return false;
			final String subCommand = args[0];
			if (!subCommands.contains(command)) return false;
			if (!playerHasPermission(sender, "bh." + subCommand)) return true;
			// if (command.equalsIgnoreCase("check")) return checkPlayer(sender, args);
			if (command.equalsIgnoreCase("history")) return getBanHistory(sender, args);
		}
		return false;
	}
	
	private boolean banPlayer(CommandSender sender, String[] args) {
			
		// Check to see we have enough arguments
		if (args.length < 4) return false;
		
		// Create arguments.
		String senderName = plugin.getName(sender);
		String commandOptions = args[1];
		String playerName = args[2];
		String reason = "No reason provided";
		long banTime = 0;
		
		// Check to see if the player is already banned
		if (BanHammerRecord.isBanned(playerName)) {
			sender.sendMessage(ChatColor.RED + playerName + " is already banned");
			return true;
		} 
		
		// Check to see if the player is on the server
		if (!MatchPlayer(playerName) && !commandOptions.contains("f")) {
			sender.sendMessage(ChatColor.RED + "No matching player.");
			sender.sendMessage(ChatColor.YELLOW + "To ban offline players use -f"); 
			return true;
		} 
		
		// Prepare the right sort of ban
		if (commandOptions.contains("p")) {
				reason = combineString(3, args, " ");
		} else if (commandOptions.contains("t")) {
			try {
				banTime = (parseTimeSpec(args[2], args[3]) + System.currentTimeMillis());
				if (args.length >= 6)
					reason = combineString(5, args, " ");
			} catch (ArrayIndexOutOfBoundsException e) {
				sender.sendMessage(ChatColor.RED + "No time provided!");
				sender.sendMessage(ChatColor.YELLOW + "/ban -t [name] [time] [unit] <reason>");
				return true;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You did not specific a ban type!");
			sender.sendMessage(ChatColor.YELLOW + "Choose -p for permenant, or -t for temporary.");
			return true;
		}
		
		// Create the ban
		BanHammerRecord ban = new BanHammerRecord();
		ban.setPlayer(playerName);
		ban.setCreatedBy(senderName);
		ban.setCreatedAt(System.currentTimeMillis());
		ban.setExpiresAt(banTime);
		ban.setReason(reason);
			
		// Ban the player
		BanHammerRecord.create(playerName, senderName, banTime, reason);
		if (banTime == 0) permenantBans.add(playerName);
		else temporaryBans.put(playerName, banTime);
		String banNotification = ChatColor.RED + playerName + ChatColor.YELLOW + " has been banned";
		String banReason = ChatColor.YELLOW + "Reason: " + ChatColor.RED + reason;
		
		// Kick the player (if they are on the server)
		if (MatchPlayer(playerName))
			getPlayerFromName(playerName).kickPlayer("Banned: " + reason);
		
		// Notify players
		notifyPlayers(sender, banNotification);
		notifyPlayers(sender, banReason);
		return true;	
	}
	
	
	
	
	// Borrowed from KiwiAdmin
	private String combineString(int startIndex, String[] args, String seperator) {
		try {
			StringBuilder reason = new StringBuilder();
			for (int i = startIndex; i < args.length; i++) {
				reason.append(args[i]);
				reason.append(seperator);
			}
			reason.deleteCharAt(reason.length() - seperator.length());
			return reason.toString();
		} catch (StringIndexOutOfBoundsException e) {
			return "No reason provided";
		}
	}

	List<BanHammerRecord> getAllPlayerBans(String playerName) {
		@SuppressWarnings("unused")
		List<BanHammerRecord> bans;
		return bans = getDatabase().find(BanHammerRecord.class).where().ieq("player", playerName).findList();
	}

	private boolean getBanHistory(CommandSender sender, String[] args) {
		
		// Check permissions
		if (!this.playerHasPermission(sender, "banhammer.history")) return true;
		
		if (args.length < 2) return false;
		
		// Create arguments.
		String commandOptions = args[0];
		String playerName = args[1];
		
		if (this.isPlayerBanned(playerName)) {
			sender.sendMessage(ChatColor.RED + playerName + " currently banned");
		} else {
			sender.sendMessage(ChatColor.GREEN + playerName + " is not banned");
		}
		
		// List bans
		if (commandOptions.contains("a") && this.isPlayerBanned(playerName)) {
			BanHammerRecord ban = this.getPlayerBan(playerName);
			printBanDetails(sender, ban);
		} else if (commandOptions.contains("A")) {
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "Previous bans:");
			for(BanHammerRecord ban : this.getAllPlayerBans(playerName))
				printBanDetails(sender, ban);
		}
		
		return true;
	}

	@Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(BanHammerRecord.class);
        return list;
    }
	
	public String getName(CommandSender sender) {
		 if (sender instanceof Player) {
			 Player player = (Player)sender;
		     String senderName = player.getName();
		     return senderName;
	     } else {
	        return "console";
	     }
	 }

	BanHammerRecord getPlayerBan(String playerName) {
		long time = System.currentTimeMillis();
		// Check for a temporary ban
		List<BanHammerRecord> bans = getDatabase().find(BanHammerRecord.class).where().ieq("player", playerName).between("expires_at", time, "9999999999999").findList();
		if (bans.size() > 1)
			log.warning("[BANHAMMER] Expecting to find 1 ban but actually got " + Integer.toString(bans.size()));
		if (bans.size() >= 1)
			return bans.get(0);
		// Check for a permanent ban
		bans = getDatabase().find(BanHammerRecord.class).where().ieq("player", playerName).ieq("expires_at", "0").findList();
		if (bans.size() > 1)
			log.warning("[BANHAMMER] Expecting to find 1 ban but actually got " + Integer.toString(bans.size()));
		return bans.get(0);
	}

	private Player getPlayerFromName(String playerName) {
		List<Player> possiblePlayers = getServer().matchPlayer(playerName);
		return possiblePlayers.get(0);
	}
	
	 boolean isPlayerBanned(String PlayerName) {
		if (permenantBans.contains(PlayerName))
			return true;
		if (temporaryBans.containsKey(PlayerName)) {
			if (temporaryBans.get(PlayerName) > System.currentTimeMillis()) {
				return true;
			} else {
				temporaryBans.remove(PlayerName);
			}	
		}
		return false;
	}

	private boolean kickPlayer(CommandSender sender, String[] args) {
		
		// Check permissions
		if (!this.playerHasPermission(sender, "banhammer.kick")) return true;
		
		// Check to see we have enough arguments
		if (args.length < 1) return false;
		String playerName = args[0];
		// Check to see if the player is on the server
		if (!MatchPlayer(playerName)) {
			sender.sendMessage(ChatColor.RED + "No matching player.");
			return true;
		}
				
		// Prepare to kick player
		Player player = getPlayerFromName(args[0]);
		String senderName = plugin.getName(sender);
		
		// Create kick reason
		String reason = "No reason provided.";
		if (args.length > 1)
			reason = combineString(1, args, " ");
		
		// Kick player
		player.kickPlayer("Kicked: " + reason);
		String kickNotification = ChatColor.RED + playerName + ChatColor.YELLOW + " has been kicked";
		String kickReason = ChatColor.YELLOW + "Reason: " + ChatColor.RED + reason;
		
		// Notify players
		notifyPlayers(sender, kickNotification);
		notifyPlayers(sender, kickReason);
		
		// Log in Console
		log.info("[BANHAMMER] " + playerName + " kicked by " + senderName);
		return true;
	}

	private boolean MatchPlayer(String playerName) {
		List<Player> possiblePlayers = getServer().matchPlayer(playerName);
		if (possiblePlayers.size() == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	private void notifyPlayers(CommandSender sender, String notification) {
		if (broadcastActions) {
			getServer().broadcastMessage(notification);
		} else {
			sender.sendMessage(notification);
		}
		
	}
	
	
	
	private boolean pardonPlayer(CommandSender sender, String[] args) {
		
		// Check permissions
		if (!this.playerHasPermission(sender, "banhammer.pardon")) return true;
		
		// Check to see we have enough arguments
		if (args.length < 2) return false;
		
		// Create arguments.
		String senderName = plugin.getName(sender);
		String commandOptions = args[0];
		String playerName = args[1];
		
		// Remove Bans
		if (commandOptions.contains("a")) {
			if (permenantBans.contains(playerName)) {
				BanHammerRecord ban = this.getPlayerBan(playerName);
				removeBan(ban);
			}
		} else if (commandOptions.contains("A")) {
			log.info("[BANHAMMER] Removing all bans for " + playerName);
			List<BanHammerRecord> bans = this.getAllPlayerBans(playerName);
			for (BanHammerRecord record : bans)
				removeBan(record);
		} else {
			sender.sendMessage(ChatColor.RED + "You did not specific what bans to pardon");
			sender.sendMessage(ChatColor.YELLOW + "Choose -a for active bans or -A for all.");
			return true;
		}
		
		// Notify players
		notifyPlayers(sender, ChatColor.GREEN + playerName + " has been pardoned");
		
		// Log to console
		log.info("[BANHAMMER] " + senderName + " has pardoned " + playerName);
		
		return true;
	}
	
	private boolean playerHasPermission(CommandSender sender, String node) {
		String playerName = this.getName(sender);
		if (CurrentPermissions != null) {
			// skip the check if the user is the console
			if (playerName.equals("console")) return true;
			if (CurrentPermissions.has(this.getPlayerFromName(playerName), node))
				return true;
			sender.sendMessage(ChatColor.RED + " You do not have permission to do that.");	
		} else if (sender.isOp()) {
			return true;
		}
		return false;
	}
	
    private long parseTimeSpec(String time, String unit) throws ArrayIndexOutOfBoundsException {
		long sec;
		try {
			sec = Integer.parseInt(time)*60;
		} catch (NumberFormatException ex) {
			return 0;
		}
		if (unit.startsWith("hour"))
			sec *= 60;
		else if (unit.startsWith("day"))
			sec *= (60*24);
		else if (unit.startsWith("week"))
			sec *= (7*60*24);
		else if (unit.startsWith("month"))
			sec *= (30*60*24);
		else if (unit.startsWith("min"))
			sec *= 1;
		else if (unit.startsWith("sec"))
			sec /= 60;
		else 
			throw new ArrayIndexOutOfBoundsException();
		return sec*1000;
	}
	
	private void printBanDetails(CommandSender sender, BanHammerRecord ban) {
		DateFormat dateFormat = new SimpleDateFormat("MMM d");
		String createdOn = ChatColor.RED + dateFormat.format(ban.getCreatedAt()) + ChatColor.YELLOW;
		String createdBy = ChatColor.RED + ban.getCreatedBy() + ChatColor.YELLOW;
		long banLength = ban.getExpiresAt() - ban.getCreatedAt();
		String banTime = ChatColor.RED + BanHammerTime.millisToLongDHMS(banLength) + ChatColor.YELLOW;
		if (banTime.contains("0 second"))
			banTime = ChatColor.RED + "permanent" + ChatColor.YELLOW;
		sender.sendMessage(ChatColor.YELLOW + "- " + createdOn + " by " + createdBy);
		sender.sendMessage(ChatColor.YELLOW + "-- Ban length: " + banTime);
		sender.sendMessage(ChatColor.YELLOW + "-- Reason: " + ChatColor.RED + ban.getReason());
	}
	
	private void removeBan(BanHammerRecord record) {
		String playerName = record.getPlayer();
		if (record.getExpiresAt() == 0) {
			if (permenantBans.contains(playerName)) permenantBans.remove(playerName);
		} else {
			if (temporaryBans.containsKey(playerName)) temporaryBans.remove(playerName);
		}
		getDatabase().delete(record);
	}
	
	private void setupDatabase() {
		try {
            getDatabase().find(BanHammerRecord.class).findRowCount();
        } catch (PersistenceException ex) {
        	installDDL();
        }
	}
	
    private void setupPermissions() {
		if (CurrentPermissions != null) {
			return;
		}
		    
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
		    
		if (permissionsPlugin == null) {
			log.warning("[BANHAMMER] - Permission system not detected, defaulting to OP");
		    return;
		}
		    
		CurrentPermissions = ((Permissions) permissionsPlugin).getHandler();
		log.info("[BANHAMMER] - Found and will use plugin "+((Permissions)permissionsPlugin).getDescription().getFullName());
	}

}