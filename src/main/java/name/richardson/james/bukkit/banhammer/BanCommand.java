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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.TimeFormatter;
import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;
import name.richardson.james.bukkit.utilities.permissions.Permissions;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.ban.event.BanHammerPlayerBannedEvent;

@Permissions(permissions = {BanCommand.PERMISSION_ALL, BanCommand.PERMISSION_PERMANENT})
public class BanCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.ban";
	public static final String PERMISSION_PERMANENT = "banhammer.ban.permanent";

	private final Set<String> immunePlayers;
	private final Map<String, Long> limits;
	private final PluginManager pluginManager;
	private final PlayerRecordManager playerRecordManager;

	private String playerName;
	private PlayerRecord playerRecord;
	private String reason;
	private long time;

	public BanCommand(PermissionManager permissionManager, PluginManager pluginManager, PlayerRecordManager playerRecordManager, Map<String, Long> limits, Set<String> immunePlayers) {
		super(permissionManager);
		this.playerRecordManager = playerRecordManager;
		this.limits = limits;
		this.immunePlayers = immunePlayers;
		this.pluginManager = pluginManager;
		this.registerLimitPermissions(permissionManager);
	}

	@Override
	public void execute(CommandContext context) {
		if (!setPlayerName(context)) return;
		if (!setReason(context)) return;
		if (!setPlayerRecord(context)) return;
		setExpiryTime(context);
		if (!hasPermission(context.getCommandSender())) return;
		PlayerRecordManager.BannedPlayerBuilder bannedPlayerBuilder = playerRecordManager.new BannedPlayerBuilder();
		bannedPlayerBuilder.setPlayer(playerName).setCreator(context.getCommandSender().getName()).setExpiryTime(time).setReason(reason).save();
		context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "player-banned", playerName));
		BanHammerPlayerBannedEvent event = new BanHammerPlayerBannedEvent(bannedPlayerBuilder.getRecord(), false);
		pluginManager.callEvent(event);
	}

	private boolean hasPermission(final CommandSender sender) {
		boolean immunePlayerTargeted = this.immunePlayers.contains(this.playerName);
		if (sender.hasPermission(BanCommand.PERMISSION_ALL)) return true;
		if (!immunePlayerTargeted && sender.hasPermission(BanCommand.PERMISSION_PERMANENT)) return true;
		for (final String limitName : this.limits.keySet()) {
			final String node = BanCommand.PERMISSION_ALL + "." + limitName;
			if (!sender.hasPermission(node)) continue;
			if (!immunePlayerTargeted && (this.limits.get(limitName) >= this.time)) return true;
		}
		sender.sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "no-permission"));
		return false;
	}

	private void registerLimitPermissions(PermissionManager permissionManager) {
		if (!this.limits.isEmpty()) {
			for (final Entry<String, Long> limit : this.limits.entrySet()) {
				String name = BanCommand.PERMISSION_ALL + "." + limit.getKey();
				String description = getMessage("limit-permission-description", TimeFormatter.millisToLongDHMS(limit.getValue()));
				final Permission permission = new Permission(name, description, PermissionDefault.OP);
				permission.addParent(permissionManager.listPermissions().get(0), true);
				permissionManager.addPermission(permission);
			}
		}
	}

	private void setExpiryTime(CommandContext context) {
		if (context.hasFlag("t")) {
			time = TimeFormatter.parseTime(context.getFlag("t"));
		} else {
			time = 0;
		}
	}

	private boolean setPlayerName(CommandContext context) {
		playerName = null;
		if (context.has(0)) playerName = context.getString(0);
		if (playerName == null) {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "must-specify-player"));
			return false;
		} else {
			return true;
		}
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(playerName);
		if (playerRecord != null && playerRecord.isBanned()) {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.WARNING, "player-is-already-banned", playerName));
			return false;
		} else {
			return true;
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

}
