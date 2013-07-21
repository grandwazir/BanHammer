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

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;
import name.richardson.james.bukkit.utilities.permissions.Permissions;

import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

@Permissions(permissions = {"banhammer.pardon", "banhammer.pardon.own", "banhammer.pardon.others"})
public class PardonCommand extends AbstractCommand {

	private final BanRecordManager banRecordManager;
	private final PlayerRecordManager playerRecordManager;
	private final Server server;

	private OfflinePlayer player;
	private PlayerRecord playerRecord;

	public PardonCommand(PermissionManager permissionManager, BanRecordManager banRecordManager, PlayerRecordManager playerRecordManager, Server server) {
		super(permissionManager);
		this.banRecordManager = banRecordManager;
		this.playerRecordManager = playerRecordManager;
		this.server = server;
	}

	@Override
	public void execute(CommandContext context) {
		if (!setPlayer(context)) return;
		if (!setPlayerRecord(context)) return;
		if (!hasPermission(context.getCommandSender())) return;
		banRecordManager.delete(playerRecord.getActiveBan());
		server.broadcast(getColouredMessage(ColourScheme.Style.INFO, "player-pardoned", player.getName()), BanHammer.NOTIFY_PERMISSION_NAME);
	}

	private boolean hasPermission(CommandSender sender) {
		final boolean isSenderTargetingSelf = (playerRecord.getActiveBan().getCreator().getName().equalsIgnoreCase(sender.getName()));
		if (sender.hasPermission("banhammer.pardon.own") && isSenderTargetingSelf) return true;
		if (sender.hasPermission("banhammer.pardon.others") && !isSenderTargetingSelf) return true;
		sender.sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "unable-to-target-player", this.player.getName()));
		return false;
	}

	private boolean setPlayer(CommandContext context) {
		player = null;
		if (context.has(0)) context.getOfflinePlayer(0);
		if (player == null) context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "must-specify-player"));
		return (player != null);
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(player.getName());
		if (playerRecord == null || playerRecord.getActiveBan() == null) {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.WARNING, "player-is-not-banned", player.getName()));
		}
		return (player != null);
	}

}
