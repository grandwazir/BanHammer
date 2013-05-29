/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * ImportCommand.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.management;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.formatters.StringFormatter;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundles;
import name.richardson.james.bukkit.utilities.logging.Logger;

@ConsoleCommand
public class ImportCommand extends AbstractCommand {

	private final ChoiceFormatter formatter;

	/** A reference to the BanHammer API. */
	private final BanHandler handler;

	/** The reason which will be set for all imported bans */
	private String reason;

	/** A instance of the Bukkit server. */
	private final Server server;

	private final Logger logger = new Logger(this);

	private CommandSender sender;

	public ImportCommand(final BanHammer plugin) {
		super(ResourceBundles.MESSAGES);
		this.handler = plugin.getHandler();
		this.server = plugin.getServer();
		this.formatter = new ChoiceFormatter(ResourceBundles.MESSAGES);
		this.formatter.setLimits(0, 1, 2);
		this.formatter.setFormats(this.getMessage("banhammer.no-bans"), this.getMessage("banhammer.one-ban"), this.getMessage("banhammer.many-bans"));
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		final int totalBans = this.server.getBannedPlayers().size();
		if (arguments.isEmpty()) {
			this.reason = this.getMessage("importcommand.default-reason");
		} else {
			StringFormatter.combineString(arguments, " ");
		}
		this.sender = sender;
		final int totalImported = this.importBans();
		// send outcome to the player
		this.formatter.setMessage("importcommand.bans-imported");
		this.formatter.setArguments(totalImported);
		sender.sendMessage(this.formatter.getMessage());
		if (totalImported != totalBans) {
			this.formatter.setMessage("importcommand.bans-failed");
			this.formatter.setArguments(totalBans - totalImported);
			sender.sendMessage(this.formatter.getMessage());
		}
		this.sender = null;
	}

	private int importBans() {
		int total = 0;
		for (final OfflinePlayer player : this.server.getBannedPlayers()) {
			if (this.handler.banPlayer(player.getName(), this.sender.getName(), this.reason, 0, false)) {
				// this removes the entry from banned-players.txt to ensure we are not
				// banning twice.
				player.setBanned(false);
				total = total + 1;
			} else {
				this.logger.log(Level.WARNING, "importcommand.unable-to-import", player.getName());
			}
		}
		return total;
	}

}
