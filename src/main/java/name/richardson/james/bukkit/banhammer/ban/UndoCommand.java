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

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandMatchers;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.localisation.LocalisedCommandSender;

import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.matchers.CreatorPlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.BanRecordManager;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;

@CommandPermissions(permissions = {"banhammer.undo", "banhammer.undo.own", "banhammer.undo.others"})
@CommandMatchers(matchers = {CreatorPlayerRecordMatcher.class})
public class UndoCommand extends AbstractCommand {

	private final BanRecordManager banRecordManager;
	private final PlayerRecordManager playerRecordManager;
	private final long undoTime;

	private String playerName;

	public UndoCommand(final PlayerRecordManager playerRecordManager, final BanRecordManager banRecordManager, final long undoTime) {
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
		this.undoTime = undoTime;
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		LocalisedCommandSender localisedCommandSender = new LocalisedCommandSender(sender, this.getLocalisation());
		if (arguments.isEmpty()) {
			this.playerName = sender.getName();
		} else {
			this.playerName = arguments.get(0);
		}

		if (this.playerRecordManager.exists(this.playerName)) {
			final PlayerRecord playerRecord = playerRecordManager.find(this.playerName);
			final List<BanRecord> playerBans = playerRecord.getCreatedBans();
			if (playerBans.isEmpty()) {
				localisedCommandSender.warning("no-ban-to-undo");
			} else {
				final BanRecord ban = playerBans.get(playerBans.size() - 1);
				if (this.hasPermission(sender, ban.getCreatedAt())) {
					this.banRecordManager.delete(ban);
					Bukkit.getPluginManager().callEvent(new BanHammerPlayerPardonedEvent(ban, false));
					localisedCommandSender.info("ban-undone", ban.getPlayer().getName());
				} else {
					if (!this.withinTimeLimit(sender, ban.getCreatedAt())) {
						localisedCommandSender.error("undo-time-expired");
					} else {
						localisedCommandSender.error("may-not-undo-that-players-bans", this.playerName);
					}
				}
			}
		}
	}

	private boolean hasPermission(final CommandSender sender, final Timestamp time) {
		final boolean isSenderTargetingSelf = (this.playerName.equalsIgnoreCase(sender.getName())) ? true : false;
		final boolean withinTimeLimit = this.withinTimeLimit(sender, time);
		if (sender.hasPermission("banhammer.undo.own") && withinTimeLimit && isSenderTargetingSelf) {
			return true;
		}
		if (sender.hasPermission("banhammer.audit.others") && withinTimeLimit && !isSenderTargetingSelf) {
			return true;
		}
		return false;
	}

	private boolean withinTimeLimit(final CommandSender sender, final Timestamp then) {
		if (sender.hasPermission("banhammer.undo")) {
			return true;
		}
		if ((System.currentTimeMillis() - then.getTime()) <= this.undoTime) {
			return true;
		}
		return false;
	}

}
