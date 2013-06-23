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

import name.richardson.james.bukkit.banhammer.matchers.BannedPlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;

@ConsoleCommand
@CommandPermissions(permissions = {"banhammer.check"})
@CommandMatchers(matchers = {BannedPlayerRecordMatcher.class})
public class CheckCommand extends AbstractCommand {

	private final PlayerRecordManager playerRecordManager;
	private final Server server;

	public CheckCommand(final PlayerRecordManager playerRecordManager, final Server server) {
		this.playerRecordManager = playerRecordManager;
		this.server = server;
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		OfflinePlayer offlinePlayer;
		LocalisedCommandSender localisedCommandSender = new LocalisedCommandSender(sender, this.getLocalisation());
		if (arguments.isEmpty()) {
			localisedCommandSender.error("must-specify-player");
			return;
		} else {
			offlinePlayer = this.server.getOfflinePlayer(arguments.remove(0));
		}
		final PlayerRecord playerRecord = playerRecordManager.find(offlinePlayer.getName());
		if (playerRecord != null && playerRecord.isBanned()) {
			final BanRecord ban = playerRecord.getActiveBan();
			final BanSummary summary = new BanSummary(ban);
			sender.sendMessage(summary.getHeader());
			sender.sendMessage(summary.getReason());
			sender.sendMessage(summary.getLength());
			if (ban.getType() == BanRecord.Type.TEMPORARY) {
				sender.sendMessage(summary.getExpiresAt());
			}
		} else {
			localisedCommandSender.info("player-is-not-banned", offlinePlayer.getName());
		}
	}

}
