/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * HistoryCommand.java is part of BanHammer.
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
package name.richardson.james.bukkit.banhammer.ban;

import java.util.List;

import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.matchers.BannedPlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandMatchers;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;

@ConsoleCommand
@CommandPermissions(permissions = { "banhammer.history", "banhammer.history.own", "banhammer.history.others" })
@CommandMatchers(matchers = { BannedPlayerRecordMatcher.class })
public class HistoryCommand extends AbstractCommand {

	/** Reference to the BanHammer API */
	private final BanHandler handler;

	/** The player whos history we are going to check */
	private String playerName;

	private final ChoiceFormatter formatter;

	public HistoryCommand(final BanHammer plugin) {
		super();
		this.handler = plugin.getHandler();
		this.formatter = new ChoiceFormatter();
		this.formatter.setLimits(0, 1, 2);
		this.formatter.setMessage("historycommand.header");
		this.formatter.setFormats(this.getMessage("banhammer.no-bans"), this.getMessage("banhammer.one-ban"), this.getMessage("banhammer.many-bans"));
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		if (arguments.isEmpty()) {
			this.playerName = sender.getName();
		} else {
			this.playerName = arguments.remove(0);
		}

		if (this.hasPermission(sender)) {
			final List<BanRecord> bans = this.handler.getPlayerBans(this.playerName);
			this.displayHistory(bans, sender);
		} else {
			sender.sendMessage(this.getMessage("permission-denied"));
		}

	}

	private void displayHistory(final List<BanRecord> bans, final CommandSender sender) {
		this.formatter.setArguments(bans.size(), this.playerName);
		sender.sendMessage(this.formatter.getMessage());
		for (final BanRecord ban : bans) {
			final BanSummary summary = new BanSummary(ban);
			sender.sendMessage(summary.getSelfHeader());
			sender.sendMessage(summary.getReason());
			sender.sendMessage(summary.getLength());
			if (ban.getType() == BanRecord.Type.TEMPORARY) {
				sender.sendMessage(summary.getExpiresAt());
			}
		}
	}

	private boolean hasPermission(final CommandSender sender) {
		final boolean isSenderTargetingSelf = (this.playerName.equalsIgnoreCase(sender.getName())) ? true : false;
		if (sender.hasPermission("banhammer.history.own") && isSenderTargetingSelf) { return true; }
		if (sender.hasPermission("banhammer.history.others") && !isSenderTargetingSelf) { return true; }
		return false;
	}

}
