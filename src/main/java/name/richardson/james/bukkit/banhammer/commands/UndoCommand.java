/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * UndoCommand.java is part of BanHammer.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.PlayerNamePositionalArgument;

import name.richardson.james.bukkit.banhammer.record.BanRecord;
import name.richardson.james.bukkit.banhammer.record.CurrentBanRecord;
import name.richardson.james.bukkit.banhammer.record.CurrentPlayerRecord;
import name.richardson.james.bukkit.banhammer.record.PlayerRecord;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerMessages.*;

public class UndoCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.undo";
	public static final String PERMISSION_OWN = "banhammer.undo.own";
	public static final String PERMISSION_OTHERS = "banhammer.undo.others";
	public static final String PERMISSION_UNRESTRICTED = "banhammer.undo.unrestricted";
	private final Argument players;
	private final EbeanServer database;
	private final long undoTime;

	public UndoCommand(EbeanServer database, final long undoTime) {
		super(UNDO_COMMAND_NAME, UNDO_COMMAND_DESC);
		this.database = database;
		this.undoTime = undoTime;
		this.players = PlayerNamePositionalArgument.getInstance(database, 0, true, PlayerRecord.PlayerStatus.BANNED);
		addArgument(players);
	}

	@Override
	public boolean isAsynchronousCommand() {
		return false;
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL) || permissible.hasPermission(PERMISSION_OWN) || permissible.hasPermission(PERMISSION_OTHERS);
	}

	@Override
	protected void execute() {
		final Collection<String> messages = new ArrayList<String>();
		final Collection<String> playerNames = this.players.getStrings();
		final CommandSender commandSender = getContext().getCommandSender();
		for (String playerName : playerNames) {
			PlayerRecord playerRecord = CurrentPlayerRecord.find(database, playerName);
			BanRecord ban = (playerRecord == null) ? null : playerRecord.getActiveBan();
			if (playerRecord == null) {
				messages.add(PLAYER_NEVER_BEEN_BANNED.asWarningMessage(playerName));
			} else if (ban == null) {
				messages.add(PLAYER_NOT_BANNED.asWarningMessage(playerName));
			} else if (!hasPermission(commandSender, ban.getPlayer().getUuid())) {
				messages.add(UNDO_NOT_PERMITTED.asErrorMessage(ban.getCreator().getLastKnownName()));
			} else if (!withinTimeLimit(commandSender, ban.getCreatedAt().getTime())) {
				messages.add(UNDO_TIME_EXPIRED.asErrorMessage());
			} else {
				database.delete(ban);
				messages.add(UNDO_COMPLETE.asInfoMessage(playerName));
			}
		}
		commandSender.sendMessage(messages.toArray(new String[messages.size()]));
	}

	private boolean hasPermission(CommandSender sender, final UUID playerUUID) {
		final boolean isSenderTargetingSelf = (getCommandSenderUUID().compareTo(playerUUID) == 0);
		return sender.hasPermission(PERMISSION_OWN) && isSenderTargetingSelf || sender.hasPermission(PERMISSION_OTHERS) && !isSenderTargetingSelf;
	}

	private boolean withinTimeLimit(CommandSender sender, final long time) {
		return sender.hasPermission(PERMISSION_UNRESTRICTED) || (System.currentTimeMillis() - time) <= this.undoTime;
	}

	private UUID getCommandSenderUUID() {
		if (getContext().getCommandSender() instanceof Player) {
			return ((Player) getContext().getCommandSender()).getUniqueId();
		} else {
			return null;
		}
	}

}
