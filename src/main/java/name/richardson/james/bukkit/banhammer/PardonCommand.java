/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * PardonCommand.java is part of BanHammer.
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

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;
import name.richardson.james.bukkit.utilities.permissions.Permissions;

import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.ban.event.BanHammerPlayerPardonedEvent;

@Permissions(permissions = {PardonCommand.PERMISSION_ALL, PardonCommand.PERMISSION_OWN, PardonCommand.PERMISSION_OTHERS})
public class PardonCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.pardon";
	public static final String PERMISSION_OWN = "banhammer.pardon.own";
	public static final String PERMISSION_OTHERS = "banhammer.pardon.others";

	private final PluginManager pluginManager;
	private final BanRecordManager banRecordManager;
	private final PlayerRecordManager playerRecordManager;

	private String playerName;
	private PlayerRecord playerRecord;

	public PardonCommand(PermissionManager permissionManager, PluginManager pluginManager, BanRecordManager banRecordManager, PlayerRecordManager playerRecordManager) {
		super(permissionManager);
		this.pluginManager = pluginManager;
		this.banRecordManager = banRecordManager;
		this.playerRecordManager = playerRecordManager;
	}

	@Override
	public void execute(CommandContext context) {
		if (!setPlayer(context)) return;
		if (!setPlayerRecord(context)) return;
		if (!hasPermission(context.getCommandSender())) return;
		BanHammerPlayerPardonedEvent event = new BanHammerPlayerPardonedEvent(playerRecord.getActiveBan(), false);
		banRecordManager.delete(playerRecord.getActiveBan());
		context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.INFO, "player-pardoned", playerName));
		pluginManager.callEvent(event);
	}

	private boolean hasPermission(CommandSender sender) {
		String creatorName = playerRecord.getActiveBan().getCreator().getName();
		final boolean isSenderTargetingSelf = (creatorName.equalsIgnoreCase(sender.getName()));
		if (sender.hasPermission(PERMISSION_OWN) && isSenderTargetingSelf) return true;
		if (sender.hasPermission(PERMISSION_OTHERS) && !isSenderTargetingSelf) return true;
		sender.sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "unable-to-target-player", creatorName));
		return false;
	}

	private boolean setPlayer(CommandContext context) {
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
		if (playerRecord == null || playerRecord.getActiveBan() == null) {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.WARNING, "player-is-not-banned", playerName));
			return false;
		} else {
			return true;
		}
	}

}
