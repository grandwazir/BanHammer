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
package name.richardson.james.bukkit.banhammer.commands;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.ReasonPositionalArgument;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;

import name.richardson.james.bukkit.banhammer.ban.BanRecordBuilder;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.formatters.BanCountChoiceFormatter;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerMessages.*;

public class ImportCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.import";
	private final ChoiceFormatter choiceFormatter;
	private final Argument reason;
	private final Server server;
	private final EbeanServer database;

	public ImportCommand(Server server, EbeanServer database) {
		super(IMPORT_COMMAND_NAME, IMPORT_COMMAND_DESC);
		this.server = server;
		this.database = database;
		this.reason = ReasonPositionalArgument.getInstance(0, false);
		this.choiceFormatter = new BanCountChoiceFormatter();
		this.choiceFormatter.setMessage(IMPORT_SUMMARY.asInfoMessage());
	}

	@Override
	public boolean isAsynchronousCommand() {
		return false;
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL);
	}

	private UUID getCommandSenderUUID() {
		if (getContext().getCommandSender() instanceof Player) {
			return ((Player) getContext().getCommandSender()).getUniqueId();
		} else {
			return null;
		}
	}

	@Override @SuppressWarnings("deprecation")
	protected void execute() {
		final String reason = (this.reason.getString() == null) ? IMPORT_DEFAULT_REASON.asMessage() : this.reason.getString();
		final CommandSender commandSender = getContext().getCommandSender();
		final UUID commandSenderUUID = getCommandSenderUUID();
		for (OfflinePlayer player : this.server.getBannedPlayers()) {
			BanRecordBuilder builder = new BanRecordBuilder(database, player.getName(), commandSenderUUID, reason);
			builder.save();
			player.setBanned(false);
		}
		choiceFormatter.setArguments(this.server.getBannedPlayers().size());
		commandSender.sendMessage(choiceFormatter.getMessage());
	}
}
