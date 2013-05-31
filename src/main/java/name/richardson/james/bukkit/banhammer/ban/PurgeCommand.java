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

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.matchers.PlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandMatchers;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;

@CommandPermissions(permissions = { "banhammer.purge", "banhammer.purge.own", "banhammer.purge.others" })
@CommandMatchers(matchers = { PlayerRecordMatcher.class })
public class PurgeCommand extends AbstractCommand {

	private final EbeanServer database;

	private final ChoiceFormatter formatter;

	public PurgeCommand(final BanHammer plugin) {
		super();
		this.database = plugin.getDatabase();
		this.formatter = new ChoiceFormatter();
		this.formatter.setLimits(0, 1, 2);
		this.formatter.setMessage("notice.bans-purged");
		this.formatter.setFormats(this.getMessage("shared.choice.no-bans"), this.getMessage("shared.choice.one-ban"), this.getMessage("shared.choice.many-bans"));
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		if (arguments.isEmpty()) {
			sender.sendMessage(this.getMessage("error.must-specify-player"));
		} else {
			final String playerName = arguments.remove(0);
			final PlayerRecord playerRecord = PlayerRecord.find(this.database, playerName);
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
				i = BanRecord.deleteBans(this.database, playerBans);
			}

			this.formatter.setArguments(i, playerName);
			sender.sendMessage(this.formatter.getMessage());
		}
	}

}
