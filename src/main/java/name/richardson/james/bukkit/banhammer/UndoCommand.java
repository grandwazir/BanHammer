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
package name.richardson.james.bukkit.banhammer;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.PlayerNamePositionalArgument;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation.*;

public class UndoCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.undo";
	public static final String PERMISSION_OWN = "banhammer.undo.own";
	public static final String PERMISSION_OTHERS = "banhammer.undo.others";
	public static final String PERMISSION_UNRESTRICTED = "banhammer.undo.unrestricted";

	private final BanRecordManager banRecordManager;
	private final Argument players;
	private final PlayerRecordManager playerRecordManager;
	private final long undoTime;

	public UndoCommand(PlayerRecordManager playerRecordManager, BanRecordManager banRecordManager, final long undoTime) {
		super(BanHammerLocalisation.UNDO_COMMAND_NAME, BanHammerLocalisation.UNDO_COMMAND_DESC);
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
		this.undoTime = undoTime;
		this.players = PlayerNamePositionalArgument.getInstance(playerRecordManager, 0, true, PlayerRecordManager.PlayerStatus.BANNED);
		addArgument(players);
	}

	@Override
	protected void execute() {
		Collection<String> messages = new ArrayList<String>();
		Collection<String> players = this.players.getStrings();
		for (String playerName : players) {
			PlayerRecord record = playerRecordManager.find(playerName);
			BanRecord ban = (record == null || record.getActiveBan() == null) ? null : record.getActiveBan();
			if (record == null) {
				messages.add(getLocalisation().formatAsWarningMessage(PLAYER_NEVER_BEEN_BANNED, playerName));
			} else if (ban == null) {
				messages.add(getLocalisation().formatAsWarningMessage(PLAYER_NOT_BANNED, playerName));
			} else if (!hasPermission(getContext().getCommandSender(), ban)) {
				messages.add(getLocalisation().formatAsErrorMessage(UNDO_NOT_PERMITTED, ban.getCreator().getName()));
			} else if (!withinTimeLimit(getContext().getCommandSender(), ban)) {
				messages.add(getLocalisation().formatAsErrorMessage(UNDO_TIME_EXPIRED));
			} else {
				banRecordManager.delete(ban);
				messages.add(getLocalisation().formatAsInfoMessage(UNDO_COMPLETE, playerName));
			}
		}
		getContext().getCommandSender().sendMessage(messages.toArray(new String[messages.size()]));
	}

	private boolean hasPermission(CommandSender sender, final BanRecord ban) {
		final boolean isSenderTargetingSelf = (ban.getCreator().getName().equalsIgnoreCase(sender.getName()));
		return sender.hasPermission(PERMISSION_OWN) && isSenderTargetingSelf || sender.hasPermission(PERMISSION_OTHERS) && !isSenderTargetingSelf;
	}

	private boolean withinTimeLimit(CommandSender sender, final BanRecord ban) {
		return sender.hasPermission(PERMISSION_UNRESTRICTED) || (System.currentTimeMillis() - ban.getCreatedAt().getTime()) <= this.undoTime;
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL) || permissible.hasPermission(PERMISSION_OWN) || permissible.hasPermission(PERMISSION_OTHERS);
	}

	@Override
	public boolean isAsynchronousCommand() {
		return false;
	}

}
