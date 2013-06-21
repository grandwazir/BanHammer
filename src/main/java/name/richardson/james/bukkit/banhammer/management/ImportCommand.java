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
import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.StringFormatter;
import name.richardson.james.bukkit.utilities.logging.LocalisedLogger;

@CommandPermissions(permissions = {"banhammer.import"})
public class ImportCommand extends AbstractCommand {

	private static final Logger logger = LocalisedLogger.getLogger(ImportCommand.class);

	private final BanHandler banHandler;
	private final ChoiceFormatter formatter;
	private final Server server;

	private String reason;
	private CommandSender sender;

	public ImportCommand(final BanHandler banHandler, final Server server) {
		this.banHandler = banHandler;
		this.server = server;
		this.formatter = new ChoiceFormatter(this.getClass());
		this.formatter.setLimits(0, 1, 2);
		this.formatter.setFormats("no-bans", "one-ban", "many-bans");
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		this.sender = sender;
		final int totalBans = this.server.getBannedPlayers().size();

		if (arguments.isEmpty()) {
			this.reason = this.getLocalisation().getString("default-import-reason");
		} else {
			StringFormatter.combineString(arguments, " ");
		}

		final int totalImported = this.importBans();
		// send outcome to the player
		this.formatter.setLocalisedMessage(ColourFormatter.header(this.getLocalisation().getString("bans-imported")));
		this.formatter.setArguments(totalImported);
		sender.sendMessage(this.formatter.getMessage());

		if (totalImported != totalBans) {
			this.formatter.setLocalisedMessage(ColourFormatter.header(this.getLocalisation().getString("bans-failed")));
			this.formatter.setArguments(totalBans - totalImported);
			sender.sendMessage(this.formatter.getMessage());
		}

		this.sender = null;
	}

	private int importBans() {
		int total = 0;
		for (final OfflinePlayer player : this.server.getBannedPlayers()) {
			if (this.banHandler.banPlayer(player.getName(), this.sender.getName(), this.reason, null, false)) {
				// this removes the entry from banned-players.txt to ensure we are not banning twice.
				player.setBanned(false);
				total = total + 1;
			} else {
				this.logger.log(Level.WARNING, "unable-to-import", player.getName());
			}
		}
		return total;
	}

}
