/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * CheckCommand.java is part of BanHammer.
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

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.matchers.BannedPlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandMatchers;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;

@ConsoleCommand
@CommandPermissions(permissions = { "banhammer.check" })
@CommandMatchers(matchers = { BannedPlayerRecordMatcher.class })
public class CheckCommand extends AbstractCommand {

	private final EbeanServer database;

	/** The player who we are going to check and see if they are banned */
	private OfflinePlayer player;

	private final Server server;

	public CheckCommand(final BanHammer plugin) {
		super();
		this.database = plugin.getDatabase();
		this.server = plugin.getServer();
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		if (arguments.isEmpty()) {
			sender.sendMessage(this.getMessage("misc.warning.must-specify-player"));
		} else {
			this.player = this.server.getOfflinePlayer(arguments.remove(0));
		}
		final PlayerRecord playerRecord = PlayerRecord.find(this.database, this.player.getName());
		if (playerRecord.isBanned()) {
			final BanRecord ban = playerRecord.getActiveBan();
			final BanSummary summary = new BanSummary(ban);
			sender.sendMessage(summary.getHeader());
			sender.sendMessage(summary.getReason());
			sender.sendMessage(summary.getLength());
			if (ban.getType() == BanRecord.Type.TEMPORARY) {
				sender.sendMessage(summary.getExpiresAt());
			}
		} else {
			sender.sendMessage(this.getMessage("misc.notice.player-is-not-banned", this.player.getName()));
		}

	}
}
