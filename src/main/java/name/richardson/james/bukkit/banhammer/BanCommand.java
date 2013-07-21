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
package name.richardson.james.bukkit.banhammer;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;
import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;
import name.richardson.james.bukkit.utilities.permissions.Permissions;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

@Permissions(permissions = {"banhammer.ban", "banhammer.ban.permanent"})
public class BanCommand extends AbstractCommand {

	private final List<String> immunePlayers;
	private final PlayerRecordManager playerRecordManager;
	private final Map<String, Long> limits;
	private final Server server;

	private OfflinePlayer player;
	private PlayerRecord playerRecord;
	private String reason;
	private long time;


	public BanCommand(PermissionManager permissionManager, PlayerRecordManager playerRecordManager, Map<String, Long> limits, List<String> immunePlayers, Server server) {
		super(permissionManager);
		this.playerRecordManager = playerRecordManager;
		this.limits = limits;
		this.immunePlayers = immunePlayers;
		this.server = server;
		this.registerLimitPermissions(permissionManager);
	}

	@Override
	public void execute(CommandContext context) {
		if (!setPlayer(context)) return;
		if (!setReason(context)) return;
		if (!setPlayerRecord(context)) return;
		setExpiryTime(context);
		if (!hasPermission(context.getCommandSender())) return;
		playerRecordManager.new BannedPlayerBuilder().setPlayer(player.getName()).setCreator(context.getCommandSender().getName()).setExpiryTime(time).setReason(reason).save();
		context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "player-banned", player.getName()));
	}

	private void setExpiryTime(CommandContext context) {
		if (context.hasFlag("t")) {
			time = TimeFormatter.parseTime(context.getFlag("t"));
		} else {
			time = 0;
		}
	}

	private boolean setReason(CommandContext context) {
		if (context.has(1)) {
			reason = context.getJoinedArguments(1);
			return true;
		} else {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "must-specify-reason"));
			return false;
		}
	}

	private boolean setPlayer(CommandContext context) {
		player = null;
		if (context.has(0)) context.getOfflinePlayer(0);
		if (player == null) context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "must-specify-player"));
		return (player != null);
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(player.getName());
		if (playerRecord != null && playerRecord.isBanned()) {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.WARNING, "player-is-already-banned", player.getName()));
		}
		return (playerRecord != null);
	}

	private boolean hasPermission(final CommandSender sender) {
		if (this.immunePlayers.contains(this.player.getName()) && !sender.hasPermission("banhammer.ban")) return false;
		if (sender.hasPermission("banhammer.ban.permanent")) return true;
		for (final String limitName : this.limits.keySet()) {
			final String node = "banhammer.ban." + limitName;
			if (sender.hasPermission(node) && (this.limits.get(limitName) <= this.time)) return true;
		}
		return false;
	}

	private void registerLimitPermissions(PermissionManager permissionManager) {
		if (!this.limits.isEmpty()) {
			for (final Entry<String, Long> limit : this.limits.entrySet()) {
				String name = "banhammer.ban." + limit.getKey();
				String description = getMessage("limit-permission-description", TimeFormatter.millisToLongDHMS(limit.getValue()));
				final Permission permission = new Permission(name, description, PermissionDefault.OP);
				permission.addParent(permissionManager.listPermissions().get(0), true);
				permissionManager.addPermission(permission);
			}
		}
	}

}
