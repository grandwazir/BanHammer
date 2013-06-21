/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * BanCommand.java is part of BanHammer.
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
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandMatchers;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.formatters.StringFormatter;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;
import name.richardson.james.bukkit.utilities.localisation.LocalisedCommandSender;
import name.richardson.james.bukkit.utilities.matchers.OnlinePlayerMatcher;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.api.BanHandler;
import name.richardson.james.bukkit.banhammer.matchers.BanLimitMatcher;

@ConsoleCommand
@CommandMatchers(matchers = {OnlinePlayerMatcher.class, BanLimitMatcher.class})
@CommandPermissions(permissions = {"banhammer.ban", "banhammer.ban.permanent"})
public class BanCommand extends AbstractCommand implements TabExecutor {

	private final BanHandler handler;
	private final List<String> immunePlayers;
	private final Map<String, Long> limits;
	private final Server server;

	private OfflinePlayer player;
	private String reason;
	private long time;

	/**
	 * Instantiates a new BanCommand.
	 *
	 * @param plugin the plugin that this command belongs to
	 * @param limits the registered ban limits to use
	 */
	public BanCommand(final BanHammer plugin, final Map<String, Long> limits, final List<String> immunePlayers) {
		super();
		this.immunePlayers = immunePlayers;
		this.limits = limits;
		this.registerLimitPermissions();
		this.server = plugin.getServer();
		this.handler = plugin.getHandler();
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		LocalisedCommandSender localisedCommandSender = new LocalisedCommandSender(sender, this.getLocalisation());
		if (arguments.isEmpty()) {
			localisedCommandSender.error("must-specify-player");
		} else {
			this.player = this.server.getOfflinePlayer(arguments.remove(0));
			if (arguments.isEmpty()) {
				localisedCommandSender.error("must-specify-reason");
				localisedCommandSender.warning("reason-hint");
				return;
			} else {
				if (arguments.get(0).startsWith("t:")) {
					this.time = this.parseBanLength(arguments.remove(0));
				} else {
					this.time = 0;
				}
				if (arguments.isEmpty()) {
					localisedCommandSender.error("must-specify-reason");
					localisedCommandSender.warning("reason-hint");
					return;
				} else {
					this.reason = StringFormatter.combineString(arguments, " ");
				}
			}
			// now set about banning the player
			if (this.hasPermission(sender)) {
				Timestamp timestamp;
				if (this.time == 0) {
					timestamp = null;
				} else {
					timestamp = new Timestamp(System.currentTimeMillis() + this.time);
				}
				if (!this.handler.banPlayer(this.player.getName(), sender.getName(), this.reason, timestamp, true)) {
					localisedCommandSender.error("player-already-banned", this.player.getName());
				} else {
					localisedCommandSender.info("player-banned", this.player.getName());
				}
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

	private boolean hasPermission(final CommandSender sender) {
		if (this.immunePlayers.contains(this.player.getName()) && !sender.hasPermission("banhammer.ban")) {
			return false;
		}
		if (sender.hasPermission("banhammer.ban.permanent")) {
			return true;
		}
		for (final String limitName : this.limits.keySet()) {
			final String node = this.getPermissionManager().listPermissions().get(0).getName() + "." + limitName;
			if (sender.hasPermission(node) && (this.limits.get(limitName) <= this.time)) {
				return true;
			}
		}
		return false;
	}

	private long parseBanLength(final String banLength) {
		final String key = banLength.replaceAll("t:", "");
		if (this.limits.containsKey(key)) {
			return this.limits.get(key);
		} else {
			return TimeFormatter.parseTime(banLength);
		}
	}

	private void registerLimitPermissions() {
		if (!this.limits.isEmpty()) {
			final String parentPermissionName = this.getPermissionManager().listPermissions().get(0).getName();
			for (final Entry<String, Long> limit : this.limits.entrySet()) {
				String name = parentPermissionName + "." + limit.getKey();
				String description = MessageFormat.format(this.getLocalisation().getString("limit-permission-description"), TimeFormatter.millisToLongDHMS(limit.getValue()));
				final Permission permission = new Permission(name, description, PermissionDefault.OP);
				permission.addParent(parentPermissionName, true);
				this.getPermissionManager().addPermission(permission);
			}
		}
	}

}
