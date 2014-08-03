/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 KickCommand.java is part of BanHammer.

 BanHammer is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option) any
 later version.

 BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractSynchronousCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.PlayerMarshaller;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.argument.PlayerPositionalArgument;
import name.richardson.james.bukkit.banhammer.argument.ReasonPositionalArgument;
import name.richardson.james.bukkit.banhammer.argument.SilentSwitchArgument;

public class KickCommand extends AbstractSynchronousCommand {

	private static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	public static final String PERMISSION_ALL = "banhammer.kick";

	private final PlayerMarshaller player;
	private final Argument reason;
	private final SilentSwitchArgument silent;

	public KickCommand(final Plugin plugin, final BukkitScheduler scheduler, final Server server) {
		super(plugin, scheduler);
		this.player = PlayerPositionalArgument.getInstance(server, 0, true);
		this.reason = ReasonPositionalArgument.getInstance(1, false);
		this.silent = SilentSwitchArgument.getInstance();
		addArgument(silent);
		addArgument((Argument) player);
		addArgument(reason);
	}

	@Override
	protected void execute() {
		final String reason = (this.reason.getString() == null) ? MESSAGES.defaultKickReason() : this.reason.getString();
		final boolean silent = this.silent.isSet();
		final Set<Player> players = this.player.getPlayers();
		final String senderName = getContext().getCommandSender().getName();
		for (Player player : players) {
			if (silent) {
				addMessage(MESSAGES.playerKicked(player.getName()));
			} else {
				addMessage(MESSAGES.playerKickedBy(player.getName(), senderName));
				addMessage(MESSAGES.banReason(reason));
			}
			player.kickPlayer(MESSAGES.playerKickedNotification(reason, senderName));
		}
	}

	@Override public String getDescription() {
		return MESSAGES.kickCommandDescription();
	}

	@Override public String getName() {
		return MESSAGES.kickCommandName();
	}

	@Override public Set<String> getPermissions() {
		return new HashSet<>(Arrays.asList(PERMISSION_ALL));
	}

}
