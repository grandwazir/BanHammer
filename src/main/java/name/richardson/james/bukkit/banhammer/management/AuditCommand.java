/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * AuditCommand.java is part of BanHammer.
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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.banhammer.matchers.CreatorPlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.persistence.BanRecordManager;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandMatchers;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.localisation.LocalisedCommandSender;

@CommandPermissions(permissions = {"banhammer.audit", "banhammer.audit.self", "banhammer.audit.others"})
@CommandMatchers(matchers = {CreatorPlayerRecordMatcher.class})
public class AuditCommand extends AbstractCommand {

	private final BanRecordManager banRecordManager;
	private final ChoiceFormatter formatter;
	private final PlayerRecordManager playerRecordManager;

	private String playerName;

	public AuditCommand(final PlayerRecordManager playerRecordManager, BanRecordManager banRecordManager) {
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
		this.formatter = new ChoiceFormatter(this.getClass());
		this.formatter.setLimits(0, 1, 2);
		this.formatter.setFormats("no-bans", "one-ban", "many-bans");
		Bukkit.getPluginManager().getPermission("banhammer.audit.self").setDefault(PermissionDefault.TRUE);
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		LocalisedCommandSender localisedCommandSender = new LocalisedCommandSender(sender, this.getLocalisation());
		if (arguments.isEmpty()) {
			this.playerName = sender.getName();
		} else {
			this.playerName = arguments.remove(0);
		}

		if (this.hasPermission(sender)) {
			if (this.playerRecordManager.exists(this.playerName)) {
				AuditSummary summary = new AuditSummary(this.playerRecordManager.find(this.playerName).getCreatedBans(), this.banRecordManager.count());
				if (summary.getTotalBanCount() != 0) {
					this.formatter.setLocalisedMessage(ColourFormatter.header(this.getLocalisation().getString("audit-header")));
					this.formatter.setArguments(summary.getTotalBanCount(), this.playerName, summary.getTotalBanCountPercentage());
					sender.sendMessage(this.formatter.getMessage());
					localisedCommandSender.header("type-summary");
					this.formatter.setLocalisedMessage(ColourFormatter.info(this.getLocalisation().getString("permanent-bans-percentage")));
					this.formatter.setArguments(summary.getPermenantBanCount(), summary.getPermenantBanCountPercentage());
					sender.sendMessage(this.formatter.getMessage());
					this.formatter.setLocalisedMessage(ColourFormatter.info(this.getLocalisation().getString("temporary-bans-percentage")));
					this.formatter.setArguments(summary.getTemporaryBanCount(), summary.getTemporaryBanCountPercentage());
					sender.sendMessage(this.formatter.getMessage());
					localisedCommandSender.header("status-summary");
					this.formatter.setLocalisedMessage(ColourFormatter.info(this.getLocalisation().getString("active-bans-percentage")));
					this.formatter.setArguments(summary.getNormalBanCount(), summary.getNormalBanCountPercentage());
					sender.sendMessage(this.formatter.getMessage());
					this.formatter.setLocalisedMessage(ColourFormatter.info(this.getLocalisation().getString("expired-bans-percentage")));
					this.formatter.setArguments(summary.getExpiredBanCount(), summary.getExpiredBanCountPercentage());
					sender.sendMessage(this.formatter.getMessage());
					this.formatter.setLocalisedMessage(ColourFormatter.info(this.getLocalisation().getString("pardoned-bans-percentage")));
					this.formatter.setArguments(summary.getPardonedBanCount(), summary.getPardonedBanCountPercentage());
					sender.sendMessage(this.formatter.getMessage());
				}
			} else {
				localisedCommandSender.info("no-bans-made", this.playerName);
			}
		} else {
			localisedCommandSender.error("permission-denied");
		}
	}

	private boolean hasPermission(final CommandSender sender) {
		final boolean isSenderCheckingSelf = (this.playerName.equalsIgnoreCase(sender.getName())) ? true : false;
		if (sender.hasPermission("banhammer.audit.self") && isSenderCheckingSelf) {
			return true;
		}
		if (sender.hasPermission("banhammer.audit.others") && !isSenderCheckingSelf) {
			return true;
		}
		return false;
	}

}
