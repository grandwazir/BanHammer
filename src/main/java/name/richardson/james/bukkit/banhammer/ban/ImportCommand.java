/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * ImportCommand.java is part of BanHammer.
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
import name.richardson.james.bukkit.utilities.command.argument.Argument;

import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.argument.ReasonPositionalArgument;
import name.richardson.james.bukkit.banhammer.player.PlayerNotFoundException;
import name.richardson.james.bukkit.banhammer.player.PlayerRecord;

public class ImportCommand extends AbstractSynchronousCommand {

	private static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	public static final String PERMISSION_ALL = "banhammer.import";
	private final Argument reason;
	private final Server server;

	public ImportCommand(final Plugin plugin, final BukkitScheduler scheduler, final Server server) {
		super(plugin, scheduler);
		this.server = server;
		this.reason = ReasonPositionalArgument.getInstance(0, false);
	}

	@Override public String getDescription() {
		return MESSAGES.importCommandDescription();
	}

	@Override public String getName() {
		return MESSAGES.importCommandName();
	}


	@Override public Set<String> getPermissions() {
		return new HashSet<>(Arrays.asList(PERMISSION_ALL));
	}

	protected void execute() {
		final String reason = (this.reason.getString() == null) ? MESSAGES.defaultImportReason() : this.reason.getString();
		final CommandSender commandSender = getContext().getCommandSender();
		Set<OfflinePlayer> bannedPlayers = this.server.getBannedPlayers();
		try {
			PlayerRecord creatorRecord = PlayerRecord.create(commandSender.getName());
			for (OfflinePlayer player : bannedPlayers) {
				PlayerRecord playerRecord = PlayerRecord.create(player.getName());
				BanRecord banRecord = BanRecord.create(playerRecord, creatorRecord, reason);
				banRecord.save();
				player.setBanned(false);
			}
			addMessage(MESSAGES.playersImported(bannedPlayers.size()));
		} catch (PlayerNotFoundException e) {
			addMessage(MESSAGES.playerLookupException());
		}
	}

}
