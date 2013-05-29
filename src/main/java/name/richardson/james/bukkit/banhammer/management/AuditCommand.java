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
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.plugin.Plugin;

@ConsoleCommand
public class AuditCommand extends AbstractCommand {

	private final EbeanServer database;

	private final ChoiceFormatter formatter;

	private String playerName;

	public AuditCommand(final Plugin plugin) {
		super(plugin);
		this.database = plugin.getDatabase();
		this.formatter = new ChoiceFormatter(this.getLocalisation());
		this.formatter.setLimits(0, 1, 2);
		this.formatter.setMessage(this, "header");
		this.formatter.setFormats(this.getLocalisation().getMessage(BanHammer.class, "no-bans"), this.getLocalisation().getMessage(BanHammer.class, "one-ban"),
			this.getLocalisation().getMessage(BanHammer.class, "many-bans"));
		this.registerPermissions();
	}

	public void execute(final CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
		final List<BanRecord> bans = PlayerRecord.find(this.database, this.playerName).getCreatedBans();

		if (sender.hasPermission(this.getPermissions().get(2)) && !this.playerName.equalsIgnoreCase(sender.getName())) {
			this.displayAudit(bans, sender);
			return;
		} else
			if (!this.playerName.equalsIgnoreCase(sender.getName())) {
				throw new CommandPermissionException(null, this.getPermissions().get(2));
			}

		if (sender.hasPermission(this.getPermissions().get(1)) && this.playerName.equalsIgnoreCase(sender.getName())) {
			this.displayAudit(bans, sender);
			return;
		} else
			if (this.playerName.equalsIgnoreCase(sender.getName())) {
				throw new CommandPermissionException(null, this.getPermissions().get(1));
			}

	}

	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] arguments) {
		final List<String> list = new ArrayList<String>();
		final Set<String> temp = new TreeSet<String>();
		if (arguments.length <= 1) {
			for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (arguments.length < 1) {
					temp.add(player.getName());
				} else
					if (player.getName().startsWith(arguments[0])) {
						temp.add(player.getName());
					}
			}
			if (arguments[0].length() >= 3) {
				temp.addAll(PlayerRecord.getBanCreatorsThatStartWith(this.database, arguments[0]));
			}
		}
		list.addAll(temp);
		return list;
	}

	public void parseArguments(final String[] arguments, final CommandSender sender) throws CommandArgumentException {
		if (arguments.length == 0) {
			this.playerName = sender.getName();
		} else {
			this.playerName = arguments[0];
		}
	}

	private void displayAudit(final List<BanRecord> bans, final CommandSender sender) {
		if (bans != null) {
			this.formatter.setMessage(this, "header");
			this.formatter.setArguments(bans.size(), this.playerName, this.getPercentage(bans.size(), BanRecord.list(this.database).size()));
			sender.sendMessage(this.formatter.getMessage());
			final Iterator<BanRecord> banIter = bans.iterator();
			final int totalBans = bans.size();
			int permanentBans = 0;
			int temporaryBans = 0;
			int pardonedBans = 0;
			int activeBans = 0;
			int expiredBans = 0;
			while (banIter.hasNext()) {
				final BanRecord ban = banIter.next();
				if (ban.getType() == BanRecord.Type.PERMANENT) {
					permanentBans++;
				} else {
					temporaryBans++;
				}
				if (ban.getState() == BanRecord.State.PARDONED) {
					pardonedBans++;
				} else
					if (ban.getState() == BanRecord.State.NORMAL) {
						activeBans++;
					} else
						if (ban.getState() == BanRecord.State.EXPIRED) {
							expiredBans++;
							;
						}
			}
			sender.sendMessage(this.getLocalisation().getMessage(this, "type_summary"));
			this.formatter.setMessage(this, "permanent_bans");
			this.formatter.setArguments(permanentBans, this.getPercentage(permanentBans, totalBans));
			sender.sendMessage(this.formatter.getMessage());
			this.formatter.setMessage(this, "temporary_bans");
			this.formatter.setArguments(temporaryBans, this.getPercentage(temporaryBans, totalBans));
			sender.sendMessage(this.formatter.getMessage());
			sender.sendMessage(this.getLocalisation().getMessage(this, "status_summary"));
			this.formatter.setMessage(this, "active_bans");
			this.formatter.setArguments(activeBans, this.getPercentage(activeBans, totalBans));
			sender.sendMessage(this.formatter.getMessage());
			this.formatter.setMessage(this, "expired_bans");
			this.formatter.setArguments(expiredBans, this.getPercentage(expiredBans, totalBans));
			sender.sendMessage(this.formatter.getMessage());
			this.formatter.setMessage(this, "pardoned_bans");
			this.formatter.setArguments(pardonedBans, this.getPercentage(pardonedBans, totalBans));
			sender.sendMessage(this.formatter.getMessage());
		} else {
			this.formatter.setMessage(this, "header-no-percentage");
			this.formatter.setArguments(0, this.playerName);
			sender.sendMessage(this.formatter.getMessage());
		}
	}

	private float getPercentage(final int value, final int total) {
		// this.getLogger().debug(String.valueOf(total));
		// this.getLogger().debug(String.valueOf(value));
		// this.getLogger().debug(String.valueOf((float) value / total));
		return (float) value / total;
	}

	private void registerPermissions() {
		// add ability to view your own ban history
		final Permission own = this.getPermissionManager().createPermission(this, "own", PermissionDefault.TRUE, this.getPermissions().get(0), true);
		this.addPermission(own);
		// add ability to view the ban history of others
		final Permission others = this.getPermissionManager().createPermission(this, "others", PermissionDefault.OP, this.getPermissions().get(0), true);
		this.addPermission(others);
	}

	public void execute(List<String> arguments, CommandSender sender) {
		// TODO Auto-generated method stub

	}

}
