/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 ExportCommand.java is part of BanHammer.

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
package name.richardson.james.bukkit.banhammer.ban;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import name.richardson.james.bukkit.utilities.command.AbstractSynchronousCommand;

import name.richardson.james.bukkit.banhammer.BanHammerMessages;
import name.richardson.james.bukkit.banhammer.BanHammerMessagesCreator;
import name.richardson.james.bukkit.banhammer.model.PlayerRecord;

public class ExportCommand extends AbstractSynchronousCommand {

	private static final BanHammerMessages MESSAGES = BanHammerMessagesCreator.getColouredMessages();
	public static final String PERMISSION_ALL = "banhammer.export";
	private final Server server;

	public ExportCommand(final Plugin plugin, final BukkitScheduler scheduler, final Server server) {
		super(plugin, scheduler);
		this.server = server;
	}

	@Override public String getDescription() {
		return MESSAGES.exportCommandDescription();
	}

	@Override public String getName() {
		return MESSAGES.exportCommandName();
	}

	@Override public Set<String> getPermissions() {
		return new HashSet<>(Arrays.asList(PERMISSION_ALL));
	}

	// It is safe to ignore the deprecation warning.
	// The Bukkit Project uses deprecation annotations in a non-standard way
	@Override @SuppressWarnings("deprecation")
	protected void execute() {
		CommandSender commandSender = getContext().getCommandSender();
		Set<PlayerRecord> players = PlayerRecord.find(PlayerRecord.Status.BANNED);
		for (PlayerRecord record : players) {
			OfflinePlayer player = server.getOfflinePlayer(record.getName());
			player.setBanned(true);
		}
		addMessage(MESSAGES.bansExported(players.size()));
	}

}
