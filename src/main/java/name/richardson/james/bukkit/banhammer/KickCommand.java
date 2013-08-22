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
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.DefaultColourFormatter;
import name.richardson.james.bukkit.utilities.localisation.Localisation;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundleByClassLocalisation;

public class KickCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.kick";

	private static final String KICK_NOTIFICATION_KEY = "kick-notification";
	private static final String PLAYER_KICKED_KEY = "player-kicked";
	private static final String REASON_KEY = "reason";
	private static final String NO_PERMISSION_KEY = "no-permission";
	private static final String MUST_SPECIFY_PLAYER_KEY = "must-specify-player";
	private static final String DEFAULT_KICK_REASON_KEY = "default-kick-reason";

	private final Server server;
	private final Localisation localisation = new ResourceBundleByClassLocalisation(KickCommand.class);
	private final ColourFormatter colourFormatter = new DefaultColourFormatter();

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
			String message = colourFormatter.format(localisation.getMessage(KICK_NOTIFICATION_KEY), ColourFormatter.FormatStyle.ERROR, this.reason, context.getCommandSender().getName());
			Player player = server.getPlayerExact(playerName);
			player.kickPlayer(message);
			server.broadcast(colourFormatter.format(localisation.getMessage(PLAYER_KICKED_KEY), ColourFormatter.FormatStyle.ERROR, playerName, context.getCommandSender().getName()), BanHammer.NOTIFY_PERMISSION_NAME);
			server.broadcast(colourFormatter.format(localisation.getMessage(REASON_KEY), ColourFormatter.FormatStyle.WARNING, this.reason), BanHammer.NOTIFY_PERMISSION_NAME);
		} else {
			context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(NO_PERMISSION_KEY), ColourFormatter.FormatStyle.ERROR));
		}
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		if (permissible.hasPermission(PERMISSION_ALL)) return true;
		return false;
	}

	private boolean setPlayerName(CommandContext commandContext) {
		playerName = commandContext.getString(0);
		if (playerName == null || server.getPlayerExact(playerName) == null) {
			String message = colourFormatter.format(localisation.getMessage(MUST_SPECIFY_PLAYER_KEY), ColourFormatter.FormatStyle.ERROR);
			commandContext.getCommandSender().sendMessage(message);
			return false;
		} else {
			return true;
		}
	}

	private boolean setReason(CommandContext context) {
		if (context.has(1)) {
			reason = context.getJoinedArguments(1);
		} else {
			reason = localisation.getMessage(DEFAULT_KICK_REASON_KEY);
		}
		return true;
	}

}
