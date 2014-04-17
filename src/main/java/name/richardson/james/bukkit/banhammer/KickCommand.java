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

import name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation.KICK_PLAYER_NOTIFICATION;
import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation.KICK_SENDER_NOTIFICATION;

public class KickCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.kick";

	private final PlayerMarshaller player;
	private final Server server;
	private final Argument reason;
	private final SilentSwitchArgument silent;

	public KickCommand(Server server) {
		super(BanHammerLocalisation.KICK_COMMAND_NAME, BanHammerLocalisation.KICK_COMMAND_DESC);
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
		String reason = (this.reason.getString() == null) ? getLocalisation().getMessage(BanHammerLocalisation.KICK_DEFAULT_REASON) : this.reason.getString();
		boolean silent = this.silent.isSet();
		Set<Player> players = this.player.getPlayers();
		for (Player player : players) {
			final String senderName = getContext().getCommandSender().getName();
			if (silent) {
				getContext().getCommandSender().sendMessage((getLocalisation().formatAsInfoMessage(KICK_SENDER_NOTIFICATION, player.getName())));
			} else {
			 	server.broadcast(getLocalisation().formatAsWarningMessage(BanHammerLocalisation.KICK_PLAYER_KICKED, player.getName(), senderName), BanHammer.NOTIFY_PERMISSION_NAME);
				server.broadcast(getLocalisation().formatAsWarningMessage(BanHammerLocalisation.FORMATTER_REASON, reason), BanHammer.NOTIFY_PERMISSION_NAME);
			}
			player.kickPlayer(getLocalisation().formatAsErrorMessage(KICK_PLAYER_NOTIFICATION, senderName, reason));
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
