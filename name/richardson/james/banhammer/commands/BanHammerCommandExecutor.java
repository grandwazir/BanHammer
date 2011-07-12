package name.richardson.james.banhammer.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import name.richardson.james.banhammer.BanHammer;

public class BanHammerCommandExecutor implements CommandExecutor {
    private BanHammer plugin;

    public BanHammerCommandExecutor(BanHammer plugin) {
        this.plugin = plugin;
    }

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		/*
		final String command = cmd.getName();
		// Handle root commands
		if (commands.contains(command)) {
			if (!playerHasPermission(sender, "banhammer." + cmd.getName())) return true;
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
			if (!playerHasPermission(sender, "banhammer." + subCommand)) return false;
			if (subCommand.equalsIgnoreCase("check")) return checkPlayer(sender, args);
			if (subCommand.equalsIgnoreCase("history")) return getBanHistory(sender, args);
			if (subCommand.equalsIgnoreCase("purge")) return purgeBanHistory(sender, args);
		*/
		return false;
	}

}
