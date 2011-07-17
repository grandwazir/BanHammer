package name.richardson.james.banhammer.commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.exceptions.InvalidTimeUnit;
import name.richardson.james.banhammer.exceptions.NoMatchingPlayer;
import name.richardson.james.banhammer.exceptions.NotEnoughArguments;
import name.richardson.james.banhammer.exceptions.PlayerAlreadyBanned;
import name.richardson.james.banhammer.exceptions.PlayerNotAuthorised;
import name.richardson.james.banhammer.persistant.BanRecord;
import name.richardson.james.banhammer.utilities.BanHammerTime;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanHammerCommandManager implements CommandExecutor {
	static final List<String> commands = Arrays.asList("kick", "pardon", "ban", "tempban");
	static final List<String> subCommands = Arrays.asList("check", "history", "purge", "recent", "reload");
	
	private BanHammer plugin;
	
	public BanHammerCommandManager(BanHammer plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final String command = cmd.getName();
		
		try {
		// Handle root commands
			if (commands.contains(command)) {
				playerHasPermission(sender, "banhammer." + cmd.getName());
				if (command.equalsIgnoreCase("ban")) return banPlayer(sender, args);
				if (command.equalsIgnoreCase("kick")) return kickPlayer(sender, args);
				if (command.equalsIgnoreCase("pardon")) return pardonPlayer(sender, args);
				if (command.equalsIgnoreCase("tempban")) return tempBanPlayer(sender, args);
				
			}
			// Handle sub commands commands
			if (command.equalsIgnoreCase("bh")) {
				if (args.length == 0) return false;
				final String subCommand = args[0];
				if (!subCommands.contains(subCommand)) return false;
				playerHasPermission(sender, "banhammer." + subCommand);
				if (subCommand.equalsIgnoreCase("purge")) return purgePlayerHistory(sender, args);
				if (subCommand.equalsIgnoreCase("check")) return checkPlayer(sender, args);
				if (subCommand.equalsIgnoreCase("history")) return playerHistory(sender, args);
				if (subCommand.equalsIgnoreCase("recent")) return recentBans(sender, args);
				if (subCommand.equalsIgnoreCase("reload")) return reloadCache(sender, args);
			}
		} catch (NoMatchingPlayer e) {
			sender.sendMessage(ChatColor.RED + BanHammer.messages.getString("noMatchingPlayer"));
			sender.sendMessage(ChatColor.YELLOW + BanHammer.messages.getString("offlinePlayerHint"));
		} catch (PlayerNotAuthorised e) {
			sender.sendMessage(ChatColor.RED + BanHammer.messages.getString("notAuthorised"));
		} catch (NotEnoughArguments e ) {
			sender.sendMessage(ChatColor.RED + BanHammer.messages.getString("notEnoughArguments"));
			sender.sendMessage(ChatColor.YELLOW + e.getUsage());
		} catch (PlayerAlreadyBanned e ) {
			sender.sendMessage(String.format(ChatColor.RED + BanHammer.messages.getString("playerAlreadyBanned"), e.getPlayerName()));
		} catch (InvalidTimeUnit e ) {
			sender.sendMessage(String.format(ChatColor.RED + BanHammer.messages.getString("invalidTimeUnit")));
			sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("validTimeUnitHint")));
		} 
		return true;
	}
	
	public boolean banPlayer(CommandSender sender, String[] args) throws NotEnoughArguments, NoMatchingPlayer, PlayerAlreadyBanned {
		if (args.length < 2) throw new NotEnoughArguments("ban", "/ban <-f> [name] [reason]");
		
		String senderName = BanHammer.getInstance().getSenderName(sender);
		Player player = null;
		String playerName;
		String reason;
		
		if (args[0].equalsIgnoreCase("-f")) {
			if (args.length < 3) throw new NotEnoughArguments("ban", "/ban <-f> [name] [reason]");
			playerName = args[1];
			reason = combineString(2, args, " ");
		} else {
			player = BanHammer.getInstance().matchPlayer(args[0]);
			playerName = player.getDisplayName();
			reason = combineString(1, args, " ");
		}
		
		if (BanHammer.cache.contains(playerName)) {
			throw new PlayerAlreadyBanned(playerName);
		} else {
			BanRecord.create(playerName, senderName, new Long(0), System.currentTimeMillis(), reason);
			BanHammer.cache.add(playerName);
			if (player != null) player.kickPlayer(String.format(BanHammer.messages.getString("kickedMessage"), reason));
			BanHammer.log(Level.INFO, String.format(BanHammer.messages.getString("logPlayerBanned"), senderName, playerName));
			plugin.notifyPlayers((String.format(ChatColor.RED + BanHammer.messages.getString("notifyBannedPlayer"), playerName)), sender);
			plugin.notifyPlayers((String.format(ChatColor.YELLOW + BanHammer.messages.getString("notifyReason"), reason)), sender);
			return true;
		}
	}
	
	private boolean kickPlayer(CommandSender sender, String[] args) throws NoMatchingPlayer, NotEnoughArguments {
		if (args.length < 2) throw new NotEnoughArguments("kick", "/kick [name] [reason]");
		String playerName = args[0];
		String reason = combineString(1, args, " ");
		Player player = BanHammer.getInstance().matchPlayer(playerName);

		player.kickPlayer(String.format(BanHammer.messages.getString("kickedMessage"), reason));
		plugin.notifyPlayers((String.format(ChatColor.RED + BanHammer.messages.getString("notifyKickedPlayer"), playerName)), sender);
		plugin.notifyPlayers((String.format(ChatColor.YELLOW + BanHammer.messages.getString("notifyReason"), reason)), sender);
		return true;
	}
	
	public boolean tempBanPlayer(CommandSender sender, String[] args) throws NotEnoughArguments, NoMatchingPlayer, PlayerAlreadyBanned, InvalidTimeUnit {
		if (args.length < 4) throw new NotEnoughArguments("tempban", "/tempban <-f> [name] [time] [unit] [reason]");
		
		String senderName = BanHammer.getInstance().getSenderName(sender);
		Player player = null;
		String playerName;
		String reason;
		Long banTime;
		
		if (args[0].equalsIgnoreCase("-f")) {
			if (args.length < 5) throw new NotEnoughArguments("tempban", "/tempban <-f> [name] [time] [unit] [reason]");
			playerName = args[1];
			reason = combineString(4, args, " ");
			banTime = parseTimeSpec(args[2], args[3]);
		} else {
			player = BanHammer.getInstance().matchPlayer(args[0]);
			playerName = player.getDisplayName();
			reason = combineString(3, args, " ");
			banTime = parseTimeSpec(args[1], args[2]);
		}
		
		if (BanHammer.cache.contains(playerName)) {
			throw new PlayerAlreadyBanned(playerName);
		} else {
			BanRecord.create(playerName, senderName, (banTime + System.currentTimeMillis()), System.currentTimeMillis(), reason);
			BanHammer.cache.add(playerName);
			if (player != null) player.kickPlayer(reason);
			BanHammer.log(Level.INFO, String.format(BanHammer.messages.getString("logPlayerTempBanned"), senderName, playerName, BanHammerTime.millisToLongDHMS(banTime)));
			plugin.notifyPlayers((String.format(ChatColor.RED + BanHammer.messages.getString("notifyTempBannedPlayer"), playerName)), sender);
			plugin.notifyPlayers((String.format(ChatColor.YELLOW + BanHammer.messages.getString("notifyReason"), reason)), sender);
			plugin.notifyPlayers((String.format(ChatColor.YELLOW +BanHammer.messages.getString("notifyTime"), BanHammerTime.millisToLongDHMS(banTime))), sender);
			return true;
		}
		
	}
	
	public boolean pardonPlayer(CommandSender sender, String[] args) throws NotEnoughArguments {
		if (args.length < 1) throw new NotEnoughArguments("pardon", "/pardon [name]");
		String playerName = args[0];
		String senderName = plugin.getSenderName(sender);
		
		if (BanHammer.cache.contains(playerName)) {
			BanHammer.cache.remove(playerName);
			BanRecord.findFirst(playerName).destroy();
			BanHammer.log(Level.INFO, String.format(BanHammer.messages.getString("logPlayerPardoned"), senderName, playerName));
			plugin.notifyPlayers((String.format(ChatColor.GREEN + BanHammer.messages.getString("playerPardoned"), playerName)), sender);
		} else {
			sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("playerNotBanned"), playerName));
		}
		return true;
	}
	
	public boolean purgePlayerHistory(CommandSender sender, String[] args) throws NotEnoughArguments { 
		if (args.length < 2) throw new NotEnoughArguments("bh purge", "/bh purge [name]");
		String playerName = args[1];
		String senderName = plugin.getSenderName(sender);
		
		List<BanRecord> bans = BanRecord.find(playerName);
		if (bans.isEmpty()) {
			sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("noBanHistory"), playerName));
		} else {
			String banTotal = Integer.toString(bans.size());
			BanRecord.destroy(bans);
			BanHammer.log(Level.INFO, String.format(BanHammer.messages.getString("logPlayerPurged"), senderName, playerName));
			sender.sendMessage(String.format(ChatColor.GREEN + BanHammer.messages.getString("notifyPurgedPlayer"), banTotal, playerName));
		}
		return true;
	}
	
	public boolean playerHistory(CommandSender sender, String[] args) throws NotEnoughArguments { 
		if (args.length < 2) throw new NotEnoughArguments("bh history", "/bh history [name]");
		String playerName = args[1];
		
		List<BanRecord> bans = BanRecord.find(playerName);
		if (bans.isEmpty()) {
			sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("noBanHistory"), playerName));
		} else {
			String banTotal = Integer.toString(bans.size());
			sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + BanHammer.messages.getString("banHistorySummary"), playerName, banTotal));	
			for (BanRecord ban : bans) {
				Date createdDate = new Date(ban.getCreatedAt());
				DateFormat dateFormat = new SimpleDateFormat("MMM d");
				String createdAt = dateFormat.format(createdDate);
				sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("banSummary"), ban.getCreatedBy(), createdAt));
				sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("banReason"), ban.getReason()));
				if (ban.getType().equals(BanRecord.type.PERMENANT)) {
					sender.sendMessage(ChatColor.YELLOW + BanHammer.messages.getString("banTimePermenant"));
				} else if  (ban.getType().equals(BanRecord.type.TEMPORARY)) {
					Long banTime = ban.getExpiresAt() - ban.getCreatedAt();
					sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("banTimeTemporary"), BanHammerTime.millisToLongDHMS(banTime)));
				}
			}
		}
		return true;
	}
	
	public boolean recentBans(CommandSender sender, String[] args) throws NotEnoughArguments { 
		if (args.length < 2) throw new NotEnoughArguments("bh recent", "/bh recent [max_to_display]");
		Integer maxRows;
		
		try {
			maxRows = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			maxRows = 3;
		}
		
		List<BanRecord> bans = BanRecord.findRecent(maxRows);
		if (bans.isEmpty()) {
			sender.sendMessage(ChatColor.YELLOW + BanHammer.messages.getString("noRecentBans"));
		} else {
			String banTotal = Integer.toString(bans.size());
			sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + BanHammer.messages.getString("recentBanCount"), banTotal));	
			for (BanRecord ban : bans) {
				Date createdDate = new Date(ban.getCreatedAt());
				DateFormat dateFormat = new SimpleDateFormat("MMM d");
				String createdAt = dateFormat.format(createdDate);
				sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("banSummaryWithName"), ban.getPlayer(), ban.getCreatedBy(), createdAt));
				sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("banReason"), ban.getReason()));
				if (ban.getType().equals(BanRecord.type.PERMENANT)) {
					sender.sendMessage(ChatColor.YELLOW + BanHammer.messages.getString("banTimePermenant"));
				} else if  (ban.getType().equals(BanRecord.type.TEMPORARY)) {
					Long banTime = ban.getExpiresAt() - ban.getCreatedAt();
					sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("banTimeTemporary"), BanHammerTime.millisToLongDHMS(banTime)));
				}
			}
		}
		return true;
	}
	
	public boolean reloadCache(CommandSender sender, String[] args) throws NotEnoughArguments { 
		String senderName = plugin.getSenderName(sender);
		BanHammer.cache.reload();
		String cacheSize = Integer.toString(BanHammer.cache.size());
		
		BanHammer.log(Level.INFO, String.format(BanHammer.messages.getString("logCacheReloaded"), senderName));
		BanHammer.log(Level.INFO, String.format(BanHammer.messages.getString("bansLoaded"), cacheSize));
		sender.sendMessage(String.format(ChatColor.GREEN + BanHammer.messages.getString("notifyCachedReloaded"), cacheSize));
		return true;
	}
	
	
		
	public boolean checkPlayer(CommandSender sender, String[] args) throws NotEnoughArguments { 
		if (args.length < 2) throw new NotEnoughArguments("bh check", "/bh check [name]");
		String playerName = args[1];
		
		if (BanHammer.cache.contains(playerName)) {
			BanRecord ban = BanRecord.findFirst(playerName);
			if (ban.getType().equals(BanRecord.type.PERMENANT)) {
				sender.sendMessage(String.format(ChatColor.RED + BanHammer.messages.getString("notifyBannedPlayer"), playerName));
				sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("notifyReason"), ban.getReason()));
			} else if (ban.getType().equals(BanRecord.type.TEMPORARY)) {
				Date expiryDate = new Date(ban.getExpiresAt());
				DateFormat dateFormat = new SimpleDateFormat("MMM d H:mm a ");
				String expiryDateString = dateFormat.format(expiryDate) + "(" + Calendar.getInstance().getTimeZone().getDisplayName() + ")";
				sender.sendMessage(String.format(ChatColor.RED + BanHammer.messages.getString("notifyTempBannedPlayer"), playerName));
				sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("notifyExpiresOn"), expiryDateString));
			}
		} else {
			sender.sendMessage(String.format(ChatColor.YELLOW + BanHammer.messages.getString("playerNotBanned"), playerName));
		}
		return true;
	}
	
	private boolean playerHasPermission(CommandSender sender, String node) throws NoMatchingPlayer, PlayerNotAuthorised {
		String playerName = plugin.getSenderName(sender);
		
		if (BanHammer.getPermissions() != null) {
			if (playerName.equals("console")) {
				return true;
			} else if (BanHammer.getPermissions().has(plugin.matchPlayerExactly(playerName), node)) {
				return true;
			} 
		} else if (sender.isOp()) return true;
		throw new PlayerNotAuthorised();
	}

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
	
	// Borrows from the excellent KiwiAdmin
	 private long parseTimeSpec(String time, String unit) throws InvalidTimeUnit {
		 long sec;
		 try {
			 sec = Integer.parseInt(time)*60;
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
			 else { 
				 throw new InvalidTimeUnit();
			 }
			 return sec*1000;
		 } catch (NumberFormatException ex) {
			 throw new InvalidTimeUnit();
		 }
	}
	
}
