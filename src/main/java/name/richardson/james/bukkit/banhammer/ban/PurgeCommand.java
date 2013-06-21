/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * PurgeCommand.java is part of BanHammer.
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

import java.util.Iterator;
import java.util.List;

import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandMatchers;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.localisation.LocalisedCommandSender;

import name.richardson.james.bukkit.banhammer.matchers.PlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.BanRecordManager;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;

@CommandPermissions(permissions = {"banhammer.purge", "banhammer.purge.own", "banhammer.purge.others"})
@CommandMatchers(matchers = {PlayerRecordMatcher.class})
public class PurgeCommand extends AbstractCommand {

	private final BanRecordManager banRecordManager;
	private final ChoiceFormatter formatter;
	private final PlayerRecordManager playerRecordManager;

	public PurgeCommand(final PlayerRecordManager playerRecordManager, final BanRecordManager banRecordManager) {
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
		this.formatter = new ChoiceFormatter(this.getClass());
		this.formatter.setLimits(0, 1, 2);
		this.formatter.setMessage("bans-purged");
		this.formatter.setFormats("no-bans", "one-ban", "many-bans");
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		LocalisedCommandSender localisedCommandSender = new LocalisedCommandSender(sender, this.getLocalisation());
		if (arguments.isEmpty()) {
			localisedCommandSender.error("must-specify-player");
		} else {
			final String playerName = arguments.remove(0);
			if (playerRecordManager.exists(playerName)) {
				final PlayerRecord playerRecord = playerRecordManager.find(playerName);
				final List<BanRecord> playerBans = playerRecord.getBans();
				final Iterator<BanRecord> playerBansIter = playerBans.iterator();
				final boolean own = sender.hasPermission("banhammer.purge.own");
				final boolean others = sender.hasPermission("banhammer.purge.others");
				int i = 0;

				while (playerBansIter.hasNext()) {
					final BanRecord ban = playerBansIter.next();
					if (!own && (ban.getCreator().getName().equalsIgnoreCase(sender.getName()))) {
						playerBansIter.remove();
						continue;
					}
					if (!others && (!ban.getCreator().getName().equalsIgnoreCase(sender.getName()))) {
						playerBansIter.remove();
						continue;
					}
				}

				if (playerRecord != null) {
					i = banRecordManager.delete(playerBans);
				}

				this.formatter.setArguments(i, playerName);
				sender.sendMessage(this.formatter.getMessage());
			} else {
				localisedCommandSender.warning("player-never-banned", playerName);
			}
		}
	}

}
