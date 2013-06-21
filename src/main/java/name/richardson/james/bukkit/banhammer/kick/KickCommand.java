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
package name.richardson.james.bukkit.banhammer.kick;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandMatchers;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.StringFormatter;
import name.richardson.james.bukkit.utilities.localisation.LocalisedCommandSender;
import name.richardson.james.bukkit.utilities.matchers.OnlinePlayerMatcher;

@CommandPermissions(permissions = {"banhammer.kick"})
@CommandMatchers(matchers = {OnlinePlayerMatcher.class})
public class KickCommand extends AbstractCommand implements TabExecutor {

	private final Server server;
	private WeakReference<Player> player;
	private String reason;

	public KickCommand(final BanHammer plugin) {
		this.server = plugin.getServer();
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		LocalisedCommandSender localisedCommandSender = new LocalisedCommandSender(sender, this.getLocalisation());
		if (arguments.isEmpty()) {
			localisedCommandSender.error("must-specify-player");
			return;
		} else {
			this.player = new WeakReference<Player>(this.server.getPlayer(arguments.remove(0)));
			if (!arguments.isEmpty()) {
				this.reason = StringFormatter.combineString(arguments, " ");
			} else {
				this.reason = this.getLocalisation().getString("kick-default-reason");
			}
		}

		if (this.player == null) {
			localisedCommandSender.error("must-specify-player");
		} else {
			this.kickPlayer(sender);
		}

	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] arguments) {
		if (this.isAuthorized(sender)) {
			this.execute(new LinkedList<String>(Arrays.asList(arguments)), sender);
		} else {
			LocalisedCommandSender localisedCommandSender = new LocalisedCommandSender(sender, this.getLocalisation());
			localisedCommandSender.error("permission-denied");
		}
		return true;
	}

	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] arguments) {
		return this.onTabComplete(Arrays.asList(arguments), sender);
	}

	private void kickPlayer(CommandSender sender) {
		Object[] params = {this.reason, sender.getName(), player.get().getName()};
		String kickedMessage = MessageFormat.format(ColourFormatter.error(this.getLocalisation().getString("kick-notification")), params);
		this.player.get().kickPlayer(kickedMessage);
		for (Player player : this.server.getOnlinePlayers()) {
			if (player.hasPermission("banhammer.notify")) {
				LocalisedCommandSender localisedCommandSender = new LocalisedCommandSender(sender, this.getLocalisation());
				localisedCommandSender.error("player-kicked", params);
				localisedCommandSender.warning("kick-reason", params);
			}
		}
	}

}
