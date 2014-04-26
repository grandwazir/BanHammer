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

import java.util.Set;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.argument.*;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.*;

public class KickCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.kick";

	private final PlayerMarshaller player;
	private final Server server;
	private final Argument reason;
	private final SilentSwitchArgument silent;

	public KickCommand(Server server) {
		super(KICK_COMMAND_NAME, KICK_COMMAND_DESC);
		this.server = server;
		this.player = PlayerPositionalArgument.getInstance(server, 0, true);
		this.reason = ReasonPositionalArgument.getInstance(1, false);
		this.silent = SilentSwitchArgument.getInstance();
		addArgument(silent);
		addArgument(player);
		addArgument(reason);
	}

	@Override
	protected void execute() {
		String reason = (this.reason.getString() == null) ? KICK_DEFAULT_REASON.asMessage() : this.reason.getString();
		boolean silent = this.silent.isSet();
		Set<Player> players = this.player.getPlayers();
		for (Player player : players) {
			final String senderName = getContext().getCommandSender().getName();
			if (silent) {
				getContext().getCommandSender().sendMessage(KICK_SENDER_NOTIFICATION.asInfoMessage(player.getName()));
			} else {
			 	server.broadcast(KICK_PLAYER_KICKED.asErrorMessage(player.getName(), senderName), BanHammer.NOTIFY_PERMISSION_NAME);
				server.broadcast(REASON.asWarningMessage(reason), BanHammer.NOTIFY_PERMISSION_NAME);
			}
			player.kickPlayer(KICK_PLAYER_NOTIFICATION.asErrorMessage(reason, senderName));
		}
	}


	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL);
	}

	@Override
	public boolean isAsynchronousCommand() {
		return false;
	}

}
