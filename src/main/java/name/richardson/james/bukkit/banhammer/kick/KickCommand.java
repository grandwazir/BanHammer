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

import java.util.List;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandMatchers;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.formatters.StringFormatter;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundles;
import name.richardson.james.bukkit.utilities.matchers.OnlinePlayerMatcher;

@CommandPermissions(permissions = { "banhammer.kick" })
@CommandMatchers(matchers = { OnlinePlayerMatcher.class })
public class KickCommand extends AbstractCommand {

	/** The player who is going to be kicked */
	private Player player;

	/** The reason to give to the kicked player */
	private String reason;

	private CommandSender sender;

	/** A instance of the Bukkit server. */
	private final Server server;

	public KickCommand(final BanHammer plugin) {
		super(ResourceBundles.MESSAGES);
		this.server = plugin.getServer();
	}

	public void execute(final List<String> arguments, final CommandSender sender) {
		this.sender = sender;
		// Parse the arguments
		if (arguments.isEmpty()) {
			sender.sendMessage(this.getMessage("kickcommand.must-specify-player"));
		} else {
			this.player = this.server.getPlayer(arguments.remove(0));
			if (!arguments.isEmpty()) {
				this.reason = StringFormatter.combineString(arguments, " ");
			} else {
				this.reason = this.getMessage("kickcommand.default-reason");
			}
		}
		if (this.player == null) {
			sender.sendMessage(this.getMessage("kickcommand.must-specify-player"));
		} else {
			this.kickPlayer();
		}
		this.player = null;
		this.sender = null;
	}

	private void kickPlayer() {
		if (this.player.isOnline()) {
			this.player.kickPlayer(this.getMessage("playerlistener.kicked", this.reason, this.sender.getName()));
			this.server.broadcast(this.getMessage("kickcommand.kick-broadcast", this.player.getName(), this.sender.getName()), "banhammer.notify");
			this.server.broadcast(this.getMessage("kickcommand.reason", this.reason), "banhammer.notify");
		}
	}

}
