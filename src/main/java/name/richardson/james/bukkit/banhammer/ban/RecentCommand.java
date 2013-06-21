/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * RecentCommand.java is part of BanHammer.
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

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.localisation.LocalisedCommandSender;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.BanRecordManager;

@CommandPermissions(permissions = {"banhammer.recent"})
public class RecentCommand extends AbstractCommand {

	public static final int DEFAULT_LIMIT = 5;

	private final BanRecordManager banRecordManager;
	private final ChoiceFormatter formatter;

	private int count;

	public RecentCommand(final BanRecordManager banRecordManager) {
		this.banRecordManager = banRecordManager;
		this.formatter = new ChoiceFormatter(this.getClass());
		this.formatter.setLimits(0, 1, 2);
		this.formatter.setLocalisedMessage(ColourFormatter.header(this.getLocalisation().getString("recent-header")));
		this.formatter.setFormats("no-bans", "one-ban", "many-bans");
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		LocalisedCommandSender localisedCommandSender = new LocalisedCommandSender(sender, this.getLocalisation());
		if (arguments.isEmpty()) {
			this.count = RecentCommand.DEFAULT_LIMIT;
		} else {
			try {
				this.count = Integer.parseInt(arguments.remove(0));
			} catch (final NumberFormatException exception) {
				this.count = RecentCommand.DEFAULT_LIMIT;
			}
		}
		final List<BanRecord> bans = banRecordManager.list(count);
		if (!bans.isEmpty()) {
			this.formatter.setArguments(bans.size());
			sender.sendMessage(this.formatter.getMessage());
			for (final BanRecord ban : bans) {
				final BanSummary summary = new BanSummary(ban);
				sender.sendMessage(summary.getHeader());
				sender.sendMessage(summary.getReason());
				sender.sendMessage(summary.getLength());
				if (ban.getType() == BanRecord.Type.TEMPORARY) {
					sender.sendMessage(summary.getExpiresAt());
				}
			}
		} else {
			localisedCommandSender.info("no-bans-made-yet");
		}
	}

}
