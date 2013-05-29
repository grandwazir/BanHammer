/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * UndoCommand.java is part of BanHammer.
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

import java.sql.Timestamp;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.matchers.CreatorPlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandMatchers;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;

@CommandPermissions(permissions = { "banhammer.undo", "banhammer.undo.own", "banhammer.undo.others" })
@CommandMatchers(matchers = { CreatorPlayerRecordMatcher.class })
public class UndoCommand extends AbstractCommand {

	private final EbeanServer database;

	private String playerName;

	private final long undoTime;

	public UndoCommand(final EbeanServer database, final long undoTime) {
		super();
		this.undoTime = undoTime;
		this.database = database;
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		if (arguments.isEmpty()) {
			this.playerName = sender.getName();
		} else {
			this.playerName = arguments.get(0);
		}
		final PlayerRecord playerRecord = PlayerRecord.find(this.database, this.playerName);
		final List<BanRecord> playerBans = playerRecord.getCreatedBans();
		if (playerBans.isEmpty()) {
			sender.sendMessage(this.getMessage("undocommand.no-ban-to-undo"));
		} else {
			// get the last ban in the list which always the most recent ban
			final BanRecord ban = playerBans.get(playerBans.size() - 1);
			if (this.hasPermission(sender, ban.getCreatedAt())) {
				this.database.delete(ban);
				Bukkit.getPluginManager().callEvent(new BanHammerPlayerPardonedEvent(ban, false));
				sender.sendMessage(this.getMessage("undocommand.success-own", ban.getPlayer().getName()));
			} else {
				sender.sendMessage(this.getMessage("permission-denied"));
				if (!this.withinTimeLimit(sender, ban.getCreatedAt())) {
					sender.sendMessage(this.getMessage("undocommand.time-expired"));
				}
			}
		}
	}

	public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
		if (arguments.length == 0) {
			this.playerName = sender.getName();
		} else {
			this.playerName = arguments[0];
		}
		final PlayerRecord playerRecord = PlayerRecord.find(this.database, this.playerName);
		final List<BanRecord> playerBans = playerRecord.getCreatedBans();
		if (playerBans.isEmpty()) {
			sender.sendMessage(this.getMessage("undocommand.no-ban-to-undo"));
		}
	}

	private boolean hasPermission(final CommandSender sender, final Timestamp time) {
		final boolean isSenderTargetingSelf = (this.playerName.equalsIgnoreCase(sender.getName())) ? true : false;
		final boolean withinTimeLimit = this.withinTimeLimit(sender, time);
		if (sender.hasPermission("banhammer.undo.own") && withinTimeLimit && isSenderTargetingSelf) { return true; }
		if (sender.hasPermission("banhammer.audit.others") && withinTimeLimit && !isSenderTargetingSelf) { return true; }
		return false;
	}

	private boolean withinTimeLimit(final CommandSender sender, final Timestamp then) {
		if (sender.hasPermission("banhammer.undo")) { return true; }
		if ((System.currentTimeMillis() - then.getTime()) <= this.undoTime) { return true; }
		return false;
	}
}
