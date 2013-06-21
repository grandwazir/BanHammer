/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * PardonCommand.java is part of BanHammer.
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandMatchers;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.localisation.LocalisedCommandSender;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.matchers.BannedPlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;

@CommandPermissions(permissions = { "banhammer.pardon", "banhammer.pardon.own", "banhammer.pardon.others" })
@CommandMatchers(matchers = { BannedPlayerRecordMatcher.class })
public class PardonCommand extends AbstractCommand implements TabExecutor {

	private final PlayerRecordManager playerRecordManager;
	private final BanHandler banHandler;

	/** A instance of the Bukkit server. */
	private final Server server;

	public PardonCommand(final PlayerRecordManager playerRecordManager, final BanHandler banHandler, final Server server) {
		this.playerRecordManager = playerRecordManager;
		this.banHandler = banHandler;
		this.server = server;
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		LocalisedCommandSender localisedCommandSender = new LocalisedCommandSender(sender, this.getLocalisation());
		if (arguments.isEmpty()) {
			localisedCommandSender.error("must-specify-player");
		} else {
			final OfflinePlayer player = this.server.getOfflinePlayer(arguments.remove(0));
			if (!playerRecordManager.exists(player.getName())) {
				localisedCommandSender.error("player-is-not-banned", player.getName());
				return;
			}
			final BanRecord ban = playerRecordManager.find(player.getName()).getActiveBan();
			if (ban == null) {
				localisedCommandSender.error("player-is-not-banned", player.getName());
			} else
				if (this.hasPermission(sender, player.getName())) {
					this.banHandler.pardonPlayer(player.getName(), sender.getName(), true);
					player.setBanned(false);
					localisedCommandSender.info("player-pardoned", player.getName());
				} else {
					localisedCommandSender.error("permission-denied");
				}
		}
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] arguments) {
		LocalisedCommandSender localisedCommandSender = new LocalisedCommandSender(sender, this.getLocalisation());
		if (this.isAuthorized(sender)) {
			this.execute(new LinkedList<String>(Arrays.asList(arguments)), sender);
		} else {
			localisedCommandSender.error("permission-denied");
		}
		return true;
	}

	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] arguments) {
		return this.onTabComplete(Arrays.asList(arguments), sender);
	}

	private boolean hasPermission(final CommandSender sender, final String creatorName) {
		final boolean isSenderTargetingSelf = (creatorName.equalsIgnoreCase(sender.getName())) ? true : false;
		if (sender.hasPermission("banhammer.pardon.own") && isSenderTargetingSelf) { return true; }
		if (sender.hasPermission("banhammer.pardon.others") && !isSenderTargetingSelf) { return true; }
		return false;
	}

}
