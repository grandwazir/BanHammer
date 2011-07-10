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
	static final List<String> commands = Arrays.asList("kick", "pardon", "ban", "tempban");
	static final List<String> subCommands = Arrays.asList("check", "history", "purge");
	// Banned players
	static ArrayList<String> permenantBans = new ArrayList<String>();
	static Map<String,Long> temporaryBans = new HashMap<String,Long>();
	// Listeners
	private final BanHammerPlayerListener BanHammerPlayerListener = new BanHammerPlayerListener(this);
	private static final boolean broadcastActions = true;

	public void onEnable(){
		info = this.getDescription();
		log.info(String.format("[BanHammer] %s is enabled!", info.getFullName()));
		// Setup Environment
		setupDatabase();
		setupPermissions();
		// Register events
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_LOGIN, BanHammerPlayerListener, Event.Priority.Highest, this);
		// Load banned players
		BanHammerRecord.setup(this);
		for(BanHammerRecord banHammerRecord : BanHammerRecord.findPermenantBans())
			permenantBans.add(banHammerRecord.getPlayer().toLowerCase());
		log.info("[BanHammer] - " + Integer.toString(temporaryBans.size()) + " temporary ban(s) found");
		for(BanHammerRecord banHammerRecord : BanHammerRecord.findTemporaryBans())
			temporaryBans.put(banHammerRecord.getPlayer().toLowerCase(), banHammerRecord.getExpiresAt());
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
			if (command.equalsIgnoreCase("tempban")) return tempBanPlayer(sender, args);
			if (command.equalsIgnoreCase("kick")) return kickPlayer(sender, args);
			if (command.equalsIgnoreCase("pardon")) return pardonPlayer(sender, args);
		}
		// Handle sub commands commands
		if (command.equalsIgnoreCase("bh")) {
			if (args.length == 0) return false;
			final String subCommand = args[0];
			if (!subCommands.contains(subCommand)) return false;
			if (!playerHasPermission(sender, "bh." + subCommand)) return false;
			if (subCommand.equalsIgnoreCase("check")) return checkPlayer(sender, args);
			if (subCommand.equalsIgnoreCase("history")) return getBanHistory(sender, args);
			if (subCommand.equalsIgnoreCase("purge")) return purgeBanHistory(sender, args);
		}
		return false;
	}
	
	private boolean banPlayer(CommandSender sender, String[] args) {
		boolean banOfflinePlayers = false;
		long expiresAt = 0;
		String playerName;
		String senderName = plugin.getName(sender);
		String reason;
		// Check to see we have enough arguments
		if (args.length < 2) return false;
		
		// Create attributes.
		if (args[0].equalsIgnoreCase("-f")) {
			if (args.length < 3) return false;
			banOfflinePlayers = true;
			playerName = args[1];
			reason = combineString(2, args, " ");
		} else {
			playerName = args[0];
			reason = combineString(1, args, " ");
		}
		
		// Check to see if the player is already banned
		if (BanHammerRecord.isBanned(playerName)) {
			sender.sendMessage(ChatColor.RED + playerName + " is already banned");
			return true;
		} 
		
		// Check to see if the player is on the server
		if (!isPlayerOnline(playerName) && !banOfflinePlayers) {
			sender.sendMessage(ChatColor.RED + "No matching player.");
			sender.sendMessage(ChatColor.YELLOW + "To ban offline players use -f"); 
			return true;
		} else if (!banOfflinePlayers) {
			// if they are match the name to the player
			playerName = getServer().matchPlayer(playerName).get(0).getDisplayName();
		}		
		
		
		// Ban the player
		BanHammerRecord.create(playerName, senderName, expiresAt, reason);
		permenantBans.add(playerName.toLowerCase());
		String banNotification = ChatColor.RED + playerName + ChatColor.YELLOW + " has been banned";
		String banReason = ChatColor.YELLOW + "Reason: " + ChatColor.RED + reason;
		// Kick the player (if they are on the server)
		if (isPlayerOnline(playerName))
			getPlayerFromName(playerName).kickPlayer("Banned: " + reason);
		// Notify players
		notifyPlayers(sender, banNotification);
		notifyPlayers(sender, banReason);
		return true;	
	}
	
	private boolean tempBanPlayer(CommandSender sender, String[] args) {
		boolean banOfflinePlayers = false;
		long banTime = 0;
		long expiresAt = 0;
		String playerName;
		String senderName = plugin.getName(sender);
		String reason;
		
		// check we have enough arguments
		if (args.length < 4) return false;
		
		// Create attributes.
		if (args[0].equalsIgnoreCase("-f")) {
			if (args.length < 5) return false;
			banOfflinePlayers = true;
			playerName = args[1];
			banTime = (parseTimeSpec(args[2], args[3]));
			reason = combineString(4, args, " ");
		} else {
			playerName = args[0];
			banTime = (parseTimeSpec(args[1], args[2]));
			reason = combineString(3, args, " ");
		}
		
		expiresAt = banTime + System.currentTimeMillis();
		
		// Check to see if the player is already banned
		if (BanHammerRecord.isBanned(playerName)) {
			sender.sendMessage(ChatColor.RED + playerName + " is already banned");
			return true;
		} 
		
		// Check to see if the player is on the server
		if (!isPlayerOnline(playerName) && !banOfflinePlayers) {
			sender.sendMessage(ChatColor.RED + "No matching player.");
			sender.sendMessage(ChatColor.YELLOW + "To ban offline players use -f"); 
			return true;
		} else if (!banOfflinePlayers) {
			// if they are match the name to the player
			playerName = getServer().matchPlayer(playerName).get(0).getDisplayName();
		}
			
		// Ban the player
		BanHammerRecord.create(playerName, senderName, expiresAt, reason);
		temporaryBans.put(playerName.toLowerCase(), (banTime + System.currentTimeMillis()));
		String banNotification = ChatColor.RED + playerName + ChatColor.YELLOW + " has been temporarily banned";
		String banReason = ChatColor.YELLOW + "Reason: " + ChatColor.RED + reason;
		String banLength = ChatColor.YELLOW + "Time: " + ChatColor.RED + BanHammerTime.millisToLongDHMS(banTime);
		// Kick the player (if they are on the server)
		if (isPlayerOnline(playerName))
			getPlayerFromName(playerName).kickPlayer("Banned: " + reason);
		// Notify players
		notifyPlayers(sender, banNotification);
		notifyPlayers(sender, banReason);
		notifyPlayers(sender, banLength);
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
	
	private boolean checkPlayer(CommandSender sender, String[] args) {

		if (args.length != 2) {
			sender.sendMessage(ChatColor.RED + "No player specified!");
			return true;
		}
		
		// notify the player if they are banned or not
		String playerName =  args[1];
		if (isPlayerBanned(playerName)) {
			sender.sendMessage(ChatColor.RED + playerName + " is currently banned");
			List<BanHammerRecord> bans = BanHammerRecord.find(playerName);
			printBanDetails(sender, bans.get(0));
		} else {
			sender.sendMessage(ChatColor.GREEN + playerName + " is not banned");
		}
		return true;
		
	}
		
	private boolean getBanHistory(CommandSender sender, String[] args) {

		if (args.length != 2) {
			sender.sendMessage(ChatColor.RED + "No player specified!");
			return true;
		}
		
		// get the list of bans and return if we have none.
		String playerName = args[1];
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "Ban history for " + playerName);
		List<BanHammerRecord> bans = BanHammerRecord.find(playerName);
		if (bans.isEmpty()) {
			sender.sendMessage(ChatColor.GREEN + playerName + " has no bans on record");
			return true;
		}
		
		// else print all the bans
		sender.sendMessage(ChatColor.LIGHT_PURPLE + Integer.toString(bans.size()) + " ban(s) on record");
		for(BanHammerRecord ban : bans)
			printBanDetails(sender, ban);
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
		List<BanHammerRecord> banHammerRecords = getDatabase().find(BanHammerRecord.class).where().ieq("player", playerName).between("expires_at", time, "9999999999999").findList();
		if (banHammerRecords.size() > 1)
			log.warning("[BANHAMMER] Expecting to find 1 ban but actually got " + Integer.toString(banHammerRecords.size()));
		if (banHammerRecords.size() >= 1)
			return banHammerRecords.get(0);
		// Check for a permanent ban
		banHammerRecords = getDatabase().find(BanHammerRecord.class).where().ieq("player", playerName).ieq("expires_at", "0").findList();
		if (banHammerRecords.size() > 1)
			log.warning("[BanHammer] Expecting to find 1 ban but actually got " + Integer.toString(banHammerRecords.size()));
		return banHammerRecords.get(0);
	}

	private Player getPlayerFromName(String playerName) {
		List<Player> possiblePlayers = getServer().matchPlayer(playerName);
		return possiblePlayers.get(0);
	}
	
	 boolean isPlayerBanned(String playerName) {
		playerName = playerName.toLowerCase();
		if (permenantBans.contains(playerName))
			return true;
		if (temporaryBans.containsKey(playerName)) {
			if (temporaryBans.get(playerName) > System.currentTimeMillis()) {
				return true;
			} else {
				temporaryBans.remove(playerName);
			}	
		}
		return false;
	}

	private boolean kickPlayer(CommandSender sender, String[] args) {	
		// Check to see we have enough arguments
		if (args.length < 1) return false;
		String playerName = args[0];
		// Check to see if the player is on the server
		if (!isPlayerOnline(playerName)) {
			sender.sendMessage(ChatColor.RED + "No matching player.");
			return true;
		} else {
			// if they are match the name to the player
			playerName = getServer().matchPlayer(playerName).get(0).getDisplayName();
		}
				
		// Prepare to kick player
		Player player = getPlayerFromName(args[0]);
		String senderName = player.getName();
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
		log.info("[BanHammer] " + playerName + " kicked by " + senderName);
		return true;
	}

	private boolean isPlayerOnline(String playerName) {
		List<Player> possiblePlayers = getServer().matchPlayer(playerName);
		if (possiblePlayers.size() == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	private void notifyPlayers(CommandSender sender, String notification) {
		if (broadcastActions) {
			if (getName(sender).equalsIgnoreCase("console"))
				sender.sendMessage(notification);
			getServer().broadcastMessage(notification);
		} else {
			sender.sendMessage(notification);
		}	
	}
	
	
	
	private boolean pardonPlayer(CommandSender sender, String[] args) {
		
		// Check to see we have enough arguments
		if (args.length != 1) return false;
		
		// Create arguments.
		String senderName = plugin.getName(sender);
		String playerName = args[0].toLowerCase();
		
		// Remove the current active ban
		if (isPlayerBanned(playerName)) {
			List<BanHammerRecord> bans = BanHammerRecord.find(playerName);
			removeBan(bans.get(0));
			notifyPlayers(sender, ChatColor.GREEN + playerName + " has been pardoned");
			log.info("[BanHammer] " + senderName + " has pardoned " + playerName);
		} else {
			sender.sendMessage(ChatColor.YELLOW + playerName + " is not banned");
		}
		
		return true;
	}
	
	private boolean purgeBanHistory(CommandSender sender, String[] args) {
		
		// Check to see we have enough arguments
		if (args.length != 2) { sender.sendMessage(ChatColor.RED + "No player specified!"); return true; };
		
		// Create arguments.
		String senderName = plugin.getName(sender);
		String playerName = args[1].toLowerCase();
		
		// Remove all bans
		if (isPlayerBanned(playerName)) {
			List<BanHammerRecord> bans = BanHammerRecord.find(playerName);
			BanHammerRecord.destroy(bans);
			log.info("[BanHammer] " + senderName + " has removed all bans associated with " + playerName);
			sender.sendMessage(ChatColor.GREEN + "All bans associated with " + playerName + " have been deleted");
		} else {
			sender.sendMessage(ChatColor.YELLOW + playerName + " does not have any bans on record");
		}
		
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
	
	private void printBanDetails(CommandSender sender, BanHammerRecord banHammerRecord) {
		DateFormat dateFormat = new SimpleDateFormat("MMM d");
		String createdOn = ChatColor.RED + dateFormat.format(banHammerRecord.getCreatedAt()) + ChatColor.YELLOW;
		String createdBy = ChatColor.RED + banHammerRecord.getCreatedBy() + ChatColor.YELLOW;
		long banLength = banHammerRecord.getExpiresAt() - banHammerRecord.getCreatedAt();
		String banTime = ChatColor.RED + BanHammerTime.millisToLongDHMS(banLength) + ChatColor.YELLOW;
		if (banTime.contains("0 second"))
			banTime = ChatColor.RED + "permanent" + ChatColor.YELLOW;
		sender.sendMessage(ChatColor.YELLOW + "- " + createdOn + " by " + createdBy);
		sender.sendMessage(ChatColor.YELLOW + "-- Reason: " + ChatColor.RED + banHammerRecord.getReason());
		sender.sendMessage(ChatColor.YELLOW + "-- Ban length: " + banTime);
	}
	
	
	private void removeBan(BanHammerRecord record) {
		String playerName = record.getPlayer().toLowerCase();
		if (record.getExpiresAt() == 0) {
			if (permenantBans.contains(playerName)) permenantBans.remove(playerName);
		} else {
			if (temporaryBans.containsKey(playerName)) temporaryBans.remove(playerName);
		}
		record.destroy();
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
			log.warning("[BanHammer] - Permission system not detected, defaulting to OP");
		    return;
		}
		    
		CurrentPermissions = ((Permissions) permissionsPlugin).getHandler();
		log.info("[BanHammer] - Permission system found: "+((Permissions)permissionsPlugin).getDescription().getFullName());
	}

}