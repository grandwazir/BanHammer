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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.banhammer.matchers.CreatorPlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandMatchers;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundles;

@CommandPermissions(permissions = { "banhammer.audit", "banhammer.audit.self", "banhammer.audit.others" })
@CommandMatchers(matchers = { CreatorPlayerRecordMatcher.class })
public class AuditCommand extends AbstractCommand {

	private final EbeanServer database;

	private final ChoiceFormatter formatter;

	private final List<BanRecord> records = new ArrayList<BanRecord>();

	private String playerName;

	public AuditCommand(final Plugin plugin) {
		super(ResourceBundles.MESSAGES);
		this.database = plugin.getDatabase();
		this.formatter = new ChoiceFormatter(ResourceBundles.MESSAGES);
		this.formatter.setLimits(0, 1, 2);
		this.formatter.setMessage("auditcommand.header");
		this.formatter.setFormats(this.getMessage("banhammer.no-bans"), this.getMessage("banhammer.one-ban"), this.getMessage("banhammer.many-bans"));
		// set banhammer.audit.own to true
		Bukkit.getPluginManager().getPermission("banhammer.audit.self").setDefault(PermissionDefault.TRUE);
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		if (arguments.isEmpty()) {
			this.playerName = sender.getName();
		} else {
			this.playerName = arguments.remove(0);
		}
		if (this.hasPermission(sender)) {
			this.records.clear();
			this.records.addAll(PlayerRecord.find(this.database, this.playerName).getCreatedBans());
			this.displayAudit(this.records, sender);
			this.records.clear();
		} else {
			sender.sendMessage(this.getMessage("permission-denied"));
		}
	}

	private void displayAudit(final List<BanRecord> bans, final CommandSender sender) {
		final int totalBans = bans.size();
		int permanentBans = 0;
		int temporaryBans = 0;
		int pardonedBans = 0;
		int activeBans = 0;
		int expiredBans = 0;
		if (totalBans != 0) {
			this.formatter.setMessage("auditcommand.header");
			this.formatter.setArguments(bans.size(), this.playerName, this.getPercentage(bans.size(), BanRecord.list(this.database).size()));
			sender.sendMessage(this.formatter.getMessage());
			final Iterator<BanRecord> banIter = bans.iterator();
			while (banIter.hasNext()) {
				final BanRecord ban = banIter.next();
				if (ban.getType() == BanRecord.Type.PERMANENT) {
					permanentBans++;
				} else {
					temporaryBans++;
				}
				if (ban.getState() == BanRecord.State.PARDONED) {
					pardonedBans++;
				} else {
					if (ban.getState() == BanRecord.State.NORMAL) {
						activeBans++;
					} else {
						if (ban.getState() == BanRecord.State.EXPIRED) {
							expiredBans++;
						}
					}
				}
			}
			sender.sendMessage(this.getMessage("auditcommand.type_summary"));
			this.formatter.setMessage("auditcommand.permanent_bans");
			this.formatter.setArguments(permanentBans, this.getPercentage(permanentBans, totalBans));
			sender.sendMessage(this.formatter.getMessage());
			this.formatter.setMessage("auditcommand.temporary_bans");
			this.formatter.setArguments(temporaryBans, this.getPercentage(temporaryBans, totalBans));
			sender.sendMessage(this.formatter.getMessage());
			sender.sendMessage(this.getMessage("auditcommand.status_summary"));
			this.formatter.setMessage("auditcommand.active_bans");
			this.formatter.setArguments(activeBans, this.getPercentage(activeBans, totalBans));
			sender.sendMessage(this.formatter.getMessage());
			this.formatter.setMessage("auditcommand.expired_bans");
			this.formatter.setArguments(expiredBans, this.getPercentage(expiredBans, totalBans));
			sender.sendMessage(this.formatter.getMessage());
			this.formatter.setMessage("auditcommand.pardoned_bans");
			this.formatter.setArguments(pardonedBans, this.getPercentage(pardonedBans, totalBans));
			sender.sendMessage(this.formatter.getMessage());
		} else {
			this.formatter.setMessage("auditcommand.header-no-percentage");
			this.formatter.setArguments(0, this.playerName);
			sender.sendMessage(this.formatter.getMessage());
		}
	}

	private float getPercentage(final int value, final int total) {
		return (float) value / total;
	}

	private boolean hasPermission(final CommandSender sender) {
		final boolean isSenderCheckingSelf = (this.playerName.equalsIgnoreCase(sender.getName())) ? true : false;
		if (sender.hasPermission("banhammer.audit.self") && isSenderCheckingSelf) { return true; }
		if (sender.hasPermission("banhammer.audit.others") && !isSenderCheckingSelf) { return true; }
		return false;
	}

}
