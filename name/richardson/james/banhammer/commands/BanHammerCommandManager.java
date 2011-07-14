package name.richardson.james.banhammer.commands;

import java.util.Arrays;
import java.util.List;

import name.richardson.james.banhammer.BanHammer;
import name.richardson.james.banhammer.exceptions.NoMatchingPlayer;
import name.richardson.james.banhammer.exceptions.NotEnoughArguments;
import name.richardson.james.banhammer.exceptions.PlayerAlreadyBanned;
import name.richardson.james.banhammer.exceptions.PlayerNotAuthorised;
import name.richardson.james.banhammer.persistant.BanRecord;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanHammerCommandManager implements CommandExecutor {
	static final List<String> commands = Arrays.asList("kick", "pardon", "ban", "tempban");
	static final List<String> subCommands = Arrays.asList("check", "history", "purge");
	
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
			}
		} catch (NoMatchingPlayer e) {
			sender.sendMessage(ChatColor.RED + BanHammer.messages.getString("noMatchingPlayer"));
		} catch (PlayerNotAuthorised e) {
			sender.sendMessage(ChatColor.RED + BanHammer.messages.getString("notAuthorised"));
		} catch (NotEnoughArguments e ) {
			sender.sendMessage(ChatColor.RED + BanHammer.messages.getString("notEnoughArguments"));
			sender.sendMessage(ChatColor.YELLOW + e.getUsage());
		} catch (PlayerAlreadyBanned e ) {
			sender.sendMessage(ChatColor.RED + BanHammer.messages.getString("playerNotAlreadyBanned"));
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
			player = BanHammer.getInstance().matchPlayerExactly(args[0]);
			playerName = player.getDisplayName();
			reason = combineString(1, args, " ");
		}
		
		if (BanHammer.cache.contains(playerName)) {
			throw new PlayerAlreadyBanned();
		} else {
			BanRecord.create(playerName, senderName, new Long(0), System.currentTimeMillis(), reason);
			BanHammer.cache.add(playerName);
			if (player != null) player.kickPlayer(reason);
			return true;
		}
	}
	
	private boolean kickPlayer(CommandSender sender, String[] args) throws NoMatchingPlayer, NotEnoughArguments {
		if (args.length < 2) throw new NotEnoughArguments("kick", "/kick [name] [reason]");
		String playerName = args[0];
		String reason = combineString(1, args, " ");
		Player player = BanHammer.getInstance().matchPlayer(playerName);

		player.kickPlayer(String.format(BanHammer.messages.getString("kickedMessage"), reason));
		return true;
	}
	
	public boolean tempBanPlayer(CommandSender sender, String[] args) throws NotEnoughArguments, NoMatchingPlayer, PlayerAlreadyBanned {
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
			player = BanHammer.getInstance().matchPlayerExactly(args[0]);
			playerName = player.getDisplayName();
			reason = combineString(3, args, " ");
			banTime = parseTimeSpec(args[1], args[2]);
		}
	
		if (BanHammer.cache.contains(playerName)) {
			throw new PlayerAlreadyBanned();
		} else {
			BanRecord.create(playerName, senderName, banTime, System.currentTimeMillis(), reason);
			BanHammer.cache.add(playerName);
			if (player != null) player.kickPlayer(reason);
			return true;
		}
		
	}
	
	public boolean pardonPlayer(CommandSender sender, String[] args) throws NotEnoughArguments {
		if (args.length < 1) throw new NotEnoughArguments("pardon", "/pardon [name]");
		
		if (BanHammer.cache.contains(args[0])) {
			BanHammer.cache.remove(args[0]);
			BanRecord.findFirst(args[0]).destroy();
			sender.sendMessage(String.format(BanHammer.messages.getString("playerPardoned"), args[0]));
		} else {
			sender.sendMessage(String.format(BanHammer.messages.getString("unableToPardonPlayer"), args[0]));
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
	
	 private long parseTimeSpec(String time, String unit) {
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
			 return sec*100;
		 } catch (NumberFormatException ex) {
			 return 0;
		 }
	}
	


	
}
