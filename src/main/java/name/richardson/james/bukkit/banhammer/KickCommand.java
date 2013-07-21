/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * KickCommand.java is part of BanHammer.
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

import java.lang.ref.WeakReference;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;
import name.richardson.james.bukkit.utilities.permissions.Permissions;

@Permissions(permissions = {"banhammer.kick"})
public class KickCommand extends AbstractCommand {

	private final Server server;

	private WeakReference<Player> player;
	private String reason;

	public KickCommand(PermissionManager permissionManager, Server server) {
		super(permissionManager);
		this.server = server;
	}

	@Override
	public void execute(CommandContext context) {
		if (!setPlayer(context)) return;
		if (!setReason(context)) return;
		String message = getColouredMessage(ColourScheme.Style.ERROR, "kick-notification", this.reason, context.getCommandSender().getName());
		this.player.get().kickPlayer(message);
		this.server.broadcast(getColouredMessage(ColourScheme.Style.ERROR, "player-kicked", player.get().getName()), BanHammer.NOTIFY_PERMISSION_NAME);
		this.server.broadcast(getColouredMessage(ColourScheme.Style.WARNING, "kick-reason", this.reason), BanHammer.NOTIFY_PERMISSION_NAME);
	}

	private boolean setPlayer(CommandContext context) {
		if (context.has(0)) player = new WeakReference<Player>(context.getPlayer(0));
		if (player.get() == null) {
			String message = getColouredMessage(ColourScheme.Style.ERROR, "must-specify-online-player");
			context.getCommandSender().sendMessage(message);
		}
		return (player != null);
	}

	private boolean setReason(CommandContext context) {
		if (context.has(1)) {
			reason = context.getJoinedArguments(1);
		} else {
			reason = getMessage("default-kick-reason");
		}
		return true;
	}

}
