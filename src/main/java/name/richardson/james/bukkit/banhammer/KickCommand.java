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

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.localisation.PluginLocalisation;

import name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation;

public class KickCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.kick";

	private final Server server;
	private String playerName;
	private String reason;

	public KickCommand(Server server) {
		this.server = server;
	}

	@Override
	public void execute(CommandContext context) {
		if (isAuthorised(context.getCommandSender())) {
			if (!setPlayerName(context)) return;
			if (!setReason(context)) return;
			String message = getLocalisation().formatAsErrorMessage(BanHammerLocalisation.KICK_PLAYER_NOTIFICATION, this.reason, context.getCommandSender().getName());
			Player player = server.getPlayerExact(playerName);
			player.kickPlayer(message);
		  message = getLocalisation().formatAsInfoMessage(BanHammerLocalisation.KICK_SENDER_NOTIFICATION, playerName);
			context.getCommandSender().sendMessage(message);
			boolean silent = (context.hasSwitch("s") || context.hasSwitch("silent"));
			if (!silent) {
				server.broadcast(getLocalisation().formatAsErrorMessage(BanHammerLocalisation.KICK_PLAYER_KICKED, playerName, context.getCommandSender().getName()), BanHammer.NOTIFY_PERMISSION_NAME);
				server.broadcast(getLocalisation().formatAsWarningMessage(BanHammerLocalisation.KICK_REASON_FOR_PLAYER_KICK, this.reason), BanHammer.NOTIFY_PERMISSION_NAME);
			}
		} else {
			String message = getLocalisation().formatAsErrorMessage(PluginLocalisation.COMMAND_NO_PERMISSION);
			context.getCommandSender().sendMessage(message);
		}
	}

	private boolean setReason(CommandContext context) {
		if (context.hasArgument(1)) {
			reason = context.getJoinedArguments(1);
		} else {
			reason = getLocalisation().getMessage(BanHammerLocalisation.KICK_DEFAULT_REASON);
		}
		return true;
	}

	private boolean setPlayerName(CommandContext commandContext) {
		playerName = commandContext.getString(0);
		if (playerName == null || server.getPlayerExact(playerName) == null) {
			String message = getLocalisation().formatAsErrorMessage(PluginLocalisation.COMMAND_MUST_SPECIFY_PLAYER);
			commandContext.getCommandSender().sendMessage(message);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		if (permissible.hasPermission(PERMISSION_ALL)) return true;
		return false;
	}

}
