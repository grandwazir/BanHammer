/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * ExportCommand.java is part of BanHammer.
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
package name.richardson.james.bukkit.banhammer.commands;

import java.util.Collection;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;

import name.richardson.james.bukkit.banhammer.record.PlayerRecord;
import name.richardson.james.bukkit.banhammer.utilities.formatters.BanCountChoiceFormatter;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerMessages.*;

public class ExportCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.export";
	private final ChoiceFormatter choiceFormatter;
	private final EbeanServer database;
	private final Server server;

	public ExportCommand(EbeanServer database, Server server) {
		super(EXPORT_COMMAND_NAME, EXPORT_COMMAND_DESC);
		this.database = database;
		this.server = server;
		this.choiceFormatter = new BanCountChoiceFormatter();
		this.choiceFormatter.setMessage(EXPORT_SUMMARY.asInfoMessage());
	}

	@Override
	public boolean isAsynchronousCommand() {
		return false;
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL);
	}

	// It is safe to ignore the deprecation warning.
	// The Bukkit Project uses deprecation annotations in a non-standard way
	@Override @SuppressWarnings("deprecation")
	protected void execute() {
		final CommandSender commandSender = getContext().getCommandSender();
		final Collection<PlayerRecord> players = PlayerRecord.find(database, "", PlayerRecord.PlayerStatus.BANNED);
		for (PlayerRecord record : players) {
			OfflinePlayer player = this.server.getOfflinePlayer(record.getLastKnownName());
			player.setBanned(true);
		}
		this.choiceFormatter.setArguments(players.size());
		commandSender.sendMessage(choiceFormatter.getMessage());
	}

}
